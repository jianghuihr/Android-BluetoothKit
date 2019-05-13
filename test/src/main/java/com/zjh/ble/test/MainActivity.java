package com.zjh.ble.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.beacon.Beacon;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;
import com.inuker.bluetooth.library.utils.BluetoothLog;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mScanBLEDeviceTV;
    private RecyclerView mDeviceRecycler;
    private DeviceAdapter mDeviceAdapter;

    private BluetoothClient bluetoothClient = BLEClient.getInstance(getApplicationContext());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != bluetoothClient) {
            bluetoothClient.stopSearch();
            bluetoothClient.closeBluetooth();
            bluetoothClient = null;
        }
    }

    private void initView() {
        mScanBLEDeviceTV = findViewById(R.id.tv_scan_ble);
        mScanBLEDeviceTV.setOnClickListener(this);

        mDeviceRecycler = findViewById(R.id.recycler_devices);
        mDeviceRecycler.setLayoutManager(new LinearLayoutManager(
                this, LinearLayoutManager.VERTICAL, false));
        mDeviceRecycler.addItemDecoration(new DividerItemDecoration(
                this, LinearLayoutManager.HORIZONTAL));

        mDeviceAdapter = new DeviceAdapter(this);
        mDeviceRecycler.setAdapter(mDeviceAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_scan_ble:
                scanDevice();
                break;
        }
    }

    private void scanDevice() {
        final SearchRequest request = new SearchRequest.Builder()
                .searchBluetoothLeDevice(3000, 3)
                .searchBluetoothClassicDevice(5000)
                .searchBluetoothClassicDevice(2000)
                .build();
        bluetoothClient.search(request, new SearchResponse() {
            @Override
            public void onSearchStarted() {

            }

            @Override
            public void onDeviceFounded(SearchResult device) {
                Beacon beacon = new Beacon(device.scanRecord);
                BluetoothLog.i(String.format("beacon for %s\n%s", device.getAddress(), beacon.toString()));
            }

            @Override
            public void onSearchStopped() {

            }

            @Override
            public void onSearchCanceled() {

            }
        });
    }
}
