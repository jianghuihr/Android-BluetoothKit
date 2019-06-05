package com.hehuibio.ble.file.ui.activity;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.hehuibio.ble.file.R;
import com.hehuibio.ble.file.ui.adapter.DeviceListAdapter;
import com.hehuibio.ble.file.common.constants.AppConstants;
import com.hehuibio.ble.file.common.helper.ClientManager;
import com.hehuibio.ble.file.ui.view.PullRefreshListView;
import com.hehuibio.ble.file.ui.view.PullToRefreshFrameLayout;
import com.inuker.bluetooth.library.connect.listener.BleConnectStatusListener;
import com.inuker.bluetooth.library.connect.listener.BluetoothStateListener;
import com.inuker.bluetooth.library.connect.options.BleConnectOptions;
import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library.model.BleGattCharacter;
import com.inuker.bluetooth.library.model.BleGattProfile;
import com.inuker.bluetooth.library.model.BleGattService;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;
import com.inuker.bluetooth.library.utils.BluetoothLog;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.inuker.bluetooth.library.Constants.REQUEST_SUCCESS;
import static com.inuker.bluetooth.library.Constants.STATUS_CONNECTED;

public class MainActivity extends Activity {

    private final String TAG = MainActivity.class.getSimpleName();
    private static final String MAC = "B0:D5:9D:6F:E7:A5";
    private static final String PREFIX_NAME = "HH-M";
    private static final String PREFIX_SERVICE = "0000ffe0";
    private static final String PREFIX_CHARACTER = "0000ffe1";
    private PullToRefreshFrameLayout mRefreshLayout;
    private PullRefreshListView mListView;
    private DeviceListAdapter mAdapter;
    private TextView mTvTitle;
    private TextView mConnectingTV;

    private List<SearchResult> mDevices;
    private UUID mServiceUUID;
    private UUID mCharacterUUID;
    private BluetoothDevice connectDevice;
    private boolean mConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDevices = new ArrayList<>();

        mTvTitle = (TextView) findViewById(R.id.title);

        mConnectingTV = (TextView) findViewById(R.id.tv_connecting);

        mRefreshLayout = (PullToRefreshFrameLayout) findViewById(R.id.pulllayout);

        mListView = mRefreshLayout.getPullToRefreshListView();
        mAdapter = new DeviceListAdapter(this);
        mListView.setAdapter(mAdapter);
        mListView.setOnRefreshListener(new PullRefreshListView.OnRefreshListener() {

            @Override
            public void onRefresh() {
                searchDevice();  // 搜索设备
            }

        });
        mAdapter.setOnClickItemListener(new DeviceListAdapter.OnClickItemListener() {
            @Override
            public void onClickItem(SearchResult result) {
                connectDevice = result.device;
                ClientManager.getClient().registerConnectStatusListener(
                        connectDevice.getAddress(), mConnectStatusListener);

                connectDeviceIfNeeded(connectDevice);
            }
        });

        searchDevice();

        ClientManager.getClient().registerBluetoothStateListener(new BluetoothStateListener() {
            @Override
            public void onBluetoothStateChanged(boolean openOrClosed) {
                BluetoothLog.v(String.format("onBluetoothStateChanged %b", openOrClosed));
            }
        });
    }

    private final BleConnectStatusListener mConnectStatusListener = new BleConnectStatusListener() {
        @Override
        public void onConnectStatusChanged(String mac, int status) {
            BluetoothLog.v(String.format("MainActivity onConnectStatusChanged %d in %s",
                    status, Thread.currentThread().getName()));

            mConnected = (status == STATUS_CONNECTED);
        }
    };

    private void connectDeviceIfNeeded(BluetoothDevice device) {
        if (!mConnected) {
            connectDevice(device);
        }
        //断开连接

    }

    private void connectDevice(BluetoothDevice device) {
        mConnectingTV.setText("正在连接");
        mConnectingTV.setVisibility(View.VISIBLE);
        BleConnectOptions options = new BleConnectOptions.Builder()
                .setConnectRetry(3)
                .setConnectTimeout(20000)
                .setServiceDiscoverRetry(3)
                .setServiceDiscoverTimeout(10000)
                .build();

        ClientManager.getClient().connect(
                device.getAddress(),
                options, new BleConnectResponse() {
                    @Override
                    public void onResponse(int code, BleGattProfile profile) {
                        if (code == REQUEST_SUCCESS) {
                            matchService(profile);
                            mConnectingTV.setText("连接成功！");
                            mConnectingTV.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mConnectingTV.setVisibility(View.GONE);
                                }
                            }, 1000);
                        } else {
                            mConnectingTV.setText("连接失败！");
                            mConnectingTV.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mConnectingTV.setVisibility(View.GONE);

                                }
                            }, 1000);
                        }
                    }
                });
    }


    private void matchService(BleGattProfile profile) {
        List<BleGattService> services = profile.getServices();

        for (BleGattService service : services) {
            String serviceUUID = service.getUUID().toString();
            if (PREFIX_SERVICE.equals(serviceUUID.split("-")[0])) {
                matchCharacter(service);
                break;
            }
        }
    }

    private void matchCharacter(BleGattService service) {
        List<BleGattCharacter> characters = service.getCharacters();
        for (BleGattCharacter character : characters) {
            if (PREFIX_CHARACTER.equals(character.getUuid().toString().split("-")[0])) {
                mCharacterUUID = character.getUuid();
                mServiceUUID = service.getUUID();

                if (!mConnected) {
                    return;
                }
                CharacterActivity.open(this,
                        connectDevice.getName(),
                        connectDevice.getAddress(),
                        mServiceUUID,
                        mCharacterUUID);
                break;
            }
        }
    }

    private void searchDevice() {
        SearchRequest request = new SearchRequest.Builder()
                .searchBluetoothLeDevice(3000, 2).build();

        ClientManager.getClient().search(request, mSearchResponse);
    }

    private final SearchResponse mSearchResponse = new SearchResponse() {
        @Override
        public void onSearchStarted() {
            mListView.onRefreshComplete(true);
            mRefreshLayout.showState(AppConstants.LIST);
            mTvTitle.setText(R.string.string_refreshing);
            mDevices.clear();
        }

        @Override
        public void onDeviceFounded(SearchResult device) {
            if (null == device) {
                return;
            }
            if (device.getName().startsWith(PREFIX_NAME) && !mDevices.contains(device)) {
                mDevices.add(device);
                mAdapter.refresh(mDevices);
            }

            if (mDevices.size() > 0) {
                mRefreshLayout.showState(AppConstants.LIST);
            }
        }

        @Override
        public void onSearchStopped() {
            mListView.onRefreshComplete(true);
            mRefreshLayout.showState(AppConstants.LIST);

            mTvTitle.setText(R.string.devices);
        }

        @Override
        public void onSearchCanceled() {
            mListView.onRefreshComplete(true);
            mRefreshLayout.showState(AppConstants.LIST);

            mTvTitle.setText(R.string.devices);
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        ClientManager.getClient().stopSearch();
    }
}
