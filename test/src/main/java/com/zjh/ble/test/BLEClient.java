package com.zjh.ble.test;

import android.content.Context;

import com.inuker.bluetooth.library.BluetoothClient;

public class BLEClient {

    private static BluetoothClient bluetoothClient;

    public static BluetoothClient getInstance(Context context) {
        if (null == bluetoothClient) {
            synchronized (BLEClient.class) {
                if (null == bluetoothClient) {
                    bluetoothClient = new BluetoothClient(context);
                }
            }
        }
        return bluetoothClient;
    }
}
