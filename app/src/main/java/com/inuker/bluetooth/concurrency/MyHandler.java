package com.inuker.bluetooth.concurrency;


import android.os.Handler;
import android.os.Message;

public class MyHandler {
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (null != callback) {
                callback.handleMessage(msg);
            }
        }
    };
    private Handler.Callback callback = null;

    private static MyHandler myHandler = null;

    public static MyHandler get() {
        if (null == myHandler) {
            synchronized (MyHandler.class) {
                if (null == myHandler) {
                    myHandler = new MyHandler();
                }
            }
        }
        return myHandler;
    }

    public Handler handler() {
        return handler;
    }

    public void setCallback(Handler.Callback callback) {
        this.callback = callback;
    }
}
