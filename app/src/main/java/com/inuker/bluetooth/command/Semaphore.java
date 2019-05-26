package com.inuker.bluetooth.command;

public class Semaphore {

    private static boolean isPost = false;

    public static int pend(int milis) {
        isPost = false;
        if (milis == 0) {
            while (isPost) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return 0;
        } else {
            int n = milis / 10;
            int j = 0;
            while (j++ < n) {
                if (isPost)
                    return 0;
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return 1;
        }
    }

    public static void post() {
        isPost = true;
    }
}
