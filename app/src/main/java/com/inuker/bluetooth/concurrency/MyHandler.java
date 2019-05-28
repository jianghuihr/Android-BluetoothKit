package com.inuker.bluetooth.concurrency;


import android.os.Handler;
import android.os.Message;

public class MyHandler {

    public static final int MSG_WHAT_TEMP = 1;
    public static final int MSG_WHAT_OTHER_E3 = 2;
    public static final int MSG_WHAT_OTHER_E5 = 3;

    private Handler sendHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (null != sendCallback) {
                sendCallback.handleMessage(msg);
            }
        }
    };

    private Handler receiveHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (null != receiveCallback) {
                receiveCallback.handleMessage(msg);
            }
        }
    };

    private Handler.Callback sendCallback = null;
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

    public void setSendCallback(Handler.Callback callback) {
        this.sendCallback = callback;
    }

    public void setReceiveCallback(Handler.Callback callback) {
        this.receiveCallback = callback;
    }
}
