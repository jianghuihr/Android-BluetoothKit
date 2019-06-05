package com.hehuibio.ble.file;

import android.app.Application;

import com.hehuibio.ble.file.command.Bus;
import com.inuker.bluetooth.library.BluetoothContext;

/**
 * Created by dingjikerbo on 2016/8/27.
 */
public class MyApplication extends Application {

    private static MyApplication instance;

    public static Application getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        BluetoothContext.set(this);

        Bus.cmdProcRoll();
        Bus.cmdReceiveRoll();
    }
}
