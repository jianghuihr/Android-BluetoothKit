package com.inuker.bluetooth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.widget.TextView;

import com.inuker.bluetooth.adapter.BleToolPagerAdapter;
import com.inuker.bluetooth.command.Bus;
import com.inuker.bluetooth.command.Execute;
import com.inuker.bluetooth.library.connect.response.BleNotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleWriteResponse;
import com.inuker.bluetooth.presenter.DebugPresenter;
import com.inuker.bluetooth.presenter.OtherPresenter;
import com.inuker.bluetooth.presenter.TempPresenter;
import com.inuker.bluetooth.view.PageView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.inuker.bluetooth.library.Constants.REQUEST_SUCCESS;

/**
 * Created by dingjikerbo on 2016/9/6.
 */
public class CharacterActivity extends Activity {

    private final String TAG = CharacterActivity.class.getSimpleName();
    private String mMac;
    private String mName;
    private UUID mService;
    private UUID mCharacter;

    private TextView mTvTitle;
    private ViewPager mBlePager;
    private BleToolPagerAdapter bleToolPagerAdapter;
    private TabLayout tabLayout;

    private TempPresenter tempPresenter;
    private DebugPresenter debugPresenter;
    private OtherPresenter otherPresenter;
    private List<PageView> bleToolPageList = new ArrayList<>();

    public static void open(Activity activity,
                            String name,
                            String mac,
                            UUID service,
                            UUID character) {
        Intent intent = new Intent(activity, CharacterActivity.class);
        intent.putExtra("name", name);
        intent.putExtra("mac", mac);
        intent.putExtra("service", service);
        intent.putExtra("character", character);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.character_activity);

        Intent intent = getIntent();
        mMac = intent.getStringExtra("mac");
        mName = intent.getStringExtra("name");
        mService = (UUID) intent.getSerializableExtra("service");
        mCharacter = (UUID) intent.getSerializableExtra("character");

        Bus.initConfig(new Execute(mMac, mService, mCharacter));
        //自动开启蓝牙接收
        ClientManager.getClient().notify(mMac, mService, mCharacter, mNotifyRsp);

        mTvTitle = (TextView) findViewById(R.id.title);
        mTvTitle.setText(String.format("%s", mName));

        mBlePager = (ViewPager) findViewById(R.id.pager_ble);
        mBlePager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        bleToolPagerAdapter = new BleToolPagerAdapter();
        mBlePager.setAdapter(bleToolPagerAdapter);

        tempPresenter = new TempPresenter(this);
        debugPresenter = new DebugPresenter(this);
        otherPresenter = new OtherPresenter(this);
        bleToolPageList.add(new PageView(tempPresenter.getView(), tempPresenter.getTitle()));
        bleToolPageList.add(new PageView(debugPresenter.getView(), debugPresenter.getTitle()));
        bleToolPageList.add(new PageView(otherPresenter.getView(), otherPresenter.getTitle()));
        bleToolPagerAdapter.refresh(bleToolPageList);
        mBlePager.setCurrentItem(1);

        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(mBlePager);
    }

    private final BleWriteResponse mWriteRsp = new BleWriteResponse() {
        @Override
        public void onResponse(int code) {
            if (code == REQUEST_SUCCESS) {
                CommonUtils.toast("success");
            } else {
                CommonUtils.toast("failed");
            }
        }
    };

    private final BleNotifyResponse mNotifyRsp = new BleNotifyResponse() {
        @Override
        public void onNotify(UUID service, UUID character, byte[] value) {
            if (service.equals(mService) && character.equals(mCharacter)) {
                Bus.receive(value);
            }
        }

        @Override
        public void onResponse(int code) {
            if (code == REQUEST_SUCCESS) {
                CommonUtils.toast("success");
            } else {
                CommonUtils.toast("failed");
            }
        }
    };

//    private final BleReadResponse mReadRsp = new BleReadResponse() {
//        @Override
//        public void onResponse(int code, byte[] data) {
//            if (code == REQUEST_SUCCESS) {
//                mBtnRead.setText(String.format("read: %s", ByteUtils.byteToString(data)));
//                CommonUtils.toast("success");
//            } else {
//                CommonUtils.toast("failed");
//                mBtnRead.setText("read");
//            }
//        }
//    };
//
//    private final BleWriteResponse mWriteRsp = new BleWriteResponse() {
//        @Override
//        public void onResponse(int code) {
//            if (code == REQUEST_SUCCESS) {
//                CommonUtils.toast("success");
//            } else {
//                CommonUtils.toast("failed");
//            }
//        }
//    };
//
//    private final BleNotifyResponse mNotifyRsp = new BleNotifyResponse() {
//        @Override
//        public void onNotify(UUID service, UUID character, byte[] value) {
//            if (service.equals(mService) && character.equals(mCharacter)) {
//                mBtnNotify.setText(String.format("%s", ByteUtils.byteToString(value)));
////                Log.i(TAG, "notify data=" + ByteUtils.byteToString(value));
//
//                Bus.receive(value);
//            }
//        }
//
//        @Override
//        public void onResponse(int code) {
//            if (code == REQUEST_SUCCESS) {
//                mBtnNotify.setEnabled(false);
//                mBtnUnnotify.setEnabled(true);
//                CommonUtils.toast("success");
//            } else {
//                CommonUtils.toast("failed");
//            }
//        }
//    };
//
//    private final BleUnnotifyResponse mUnnotifyRsp = new BleUnnotifyResponse() {
//        @Override
//        public void onResponse(int code) {
//            if (code == REQUEST_SUCCESS) {
//                CommonUtils.toast("success");
//                mBtnNotify.setEnabled(true);
//                mBtnUnnotify.setEnabled(false);
//            } else {
//                CommonUtils.toast("failed");
//            }
//        }
//    };
//
//    private final BleMtuResponse mMtuResponse = new BleMtuResponse() {
//        @Override
//        public void onResponse(int code, Integer data) {
//            if (code == REQUEST_SUCCESS) {
//                CommonUtils.toast("request mtu success,mtu = " + data);
//            } else {
//                CommonUtils.toast("request mtu failed");
//            }
//        }
//    };
//
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.read:
//                ClientManager.getClient().read(mMac, mService, mCharacter, mReadRsp);
//                break;
//            case R.id.write:
//                for (int i = 0; i < 200; i++) {
//                    ClientManager.getClient().write(mMac, mService, mCharacter,
////                            ByteUtils.stringToBytes(mEtInput.getText().toString()), mWriteRsp);
//                            ByteUtils.stringToBytes("1234567890abcdefghi"), mWriteRsp);
//                }
//                break;
////            case R.id.notify:
////                ClientManager.getClient().notify(mMac, mService, mCharacter, mNotifyRsp);
////                break;
//            case R.id.unnotify:
//                ClientManager.getClient().unnotify(mMac, mService, mCharacter, mUnnotifyRsp);
//                break;
//            case R.id.btn_request_mtu:
//                String mtuStr = mEtInputMtu.getText().toString();
//                if (TextUtils.isEmpty(mtuStr)) {
//                    CommonUtils.toast("MTU不能为空");
//                    return;
//                }
//                int mtu = Integer.parseInt(mtuStr);
//                if (mtu < GATT_DEF_BLE_MTU_SIZE || mtu > GATT_MAX_MTU_SIZE) {
//                    CommonUtils.toast("MTU不不在范围内");
//                    return;
//                }
//                ClientManager.getClient().requestMtu(mMac, mtu, mMtuResponse);
//                break;
//        }
//    }
//
//    private final BleConnectStatusListener mConnectStatusListener = new BleConnectStatusListener() {
//        @Override
//        public void onConnectStatusChanged(String mac, int status) {
//            BluetoothLog.v(String.format("CharacterActivity.onConnectStatusChanged status = %d", status));
//
//            if (status == STATUS_DISCONNECTED) {
//                CommonUtils.toast("disconnected");
//                mBtnRead.setEnabled(false);
//                mBtnWrite.setEnabled(false);
//                mBtnNotify.setEnabled(false);
//                mBtnUnnotify.setEnabled(false);
//
//                mTvTitle.postDelayed(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        finish();
//                    }
//                }, 300);
//            }
//        }
//    };

    @Override
    protected void onResume() {
        super.onResume();
//        ClientManager.getClient().registerConnectStatusListener(mMac, mConnectStatusListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        ClientManager.getClient().unregisterConnectStatusListener(mMac, mConnectStatusListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ClientManager.getClient().disconnect(mMac);
    }
}
