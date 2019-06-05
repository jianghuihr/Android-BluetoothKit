package com.hehuibio.ble.file.concurrency;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class MyHandler {

    private final String TAG = MyHandler.class.getSimpleName();
    public static final int MSG_WHAT_TEMP = 1;
    public static final int MSG_WHAT_OTHER_E3 = 2;
    public static final int MSG_WHAT_OTHER_E5 = 3;
    public static final int MSG_WHAT_OTHER_EF = 4;

    private Handler sendHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            for (Callback callback : sendCallbackList) {
                callback.handleMessage(msg);
            }
        }
    };

    private Handler receiveHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (null != receiveCallback) {
                receiveCallback.handleMessage(msg);
            }
        }
    };

    private List<Handler.Callback> sendCallbackList = new ArrayList<>();
    private Handler.Callback receiveCallback = null;

    private static MyHandler myHandler = null;

    private MyHandler() {
    }

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

    public Handler sendHandler() {
        return sendHandler;
    }

    public Handler receiveHandler() {
        return receiveHandler;
    }

    public void addSendCallback(@NonNull Handler.Callback callback) {
        sendCallbackList.add(callback);
    }

    public void setReceiveCallback(Handler.Callback callback) {
        this.receiveCallback = callback;
    }
}
