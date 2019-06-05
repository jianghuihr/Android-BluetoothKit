package com.hehuibio.ble.file.command;

import android.os.Message;
import android.util.Log;

import com.hehuibio.ble.file.concurrency.MyHandler;
import com.hehuibio.ble.file.model.Temp;
import com.inuker.bluetooth.library.utils.ByteUtils;

import java.util.Arrays;

public class CmdReturn {

    static final String TAG = CmdReturn.class.getSimpleName();

    public static float mod;
    public static float top;
    public static float env;
    public float modLast;
    public float topLast;
    public float envLast;
    public float prm4;
    public float prm5;
    public float modLastR;
    public int tickModRx;
    public static byte code;
    public static byte[] rx = new byte[127];
    public static int lenRx;

    private static MyHandler myHandler = MyHandler.get();

    static int Byte2UINT(byte b) {
        int val;
        if (b >= 0)
            val = b;
        else
            val = (256 + b);
        return val;
    }

    public static int GetCode() {
        int code;
        code = Byte2UINT(rx[2]);
        return code;
    }

    static boolean sumCheck(byte[] dataIn, int lenIn) {
        Log.i(TAG, "sumCheck dataIn=" + ByteUtils.byteToString(dataIn) + ", lenIn=" + lenIn);
        int sum = 0;
        for (int i = 0; i < (lenIn - 1); i++) {
            sum += Byte2UINT(dataIn[i]);
            Log.i(TAG, "i=" + i + ", sum=" + sum);

        }

        Log.i(TAG, "sumCheck sum=" + sum);
        sum = sum % 0xff;
        if (dataIn[lenIn - 1] == (byte) sum) {
            return true;
        }
        return false;
    }

    public static void cmdReturnProc(byte[] dataIn, int lenIn) {
        byte cmdRet = dataIn[0];
        Log.i(TAG, "cmdRet=" + cmdRet);

        if (cmdRet == (byte) 0xB0) {
            top = (float) (Byte2UINT(dataIn[2]) + Byte2UINT(dataIn[3]) * 256) / 100.0f;
            mod = (float) (Byte2UINT(dataIn[4]) + Byte2UINT(dataIn[5]) * 256) / 100.0f;
            env = (float) (Byte2UINT(dataIn[6]) + Byte2UINT(dataIn[7]) * 256) / 100.0f;
//            Log.i(TAG, "ret=0xB0, top=" + top + "  mod=" + mod + "  env=" + env);
            Message message = new Message();
            message.what = MyHandler.MSG_WHAT_TEMP;
            message.obj = new Temp(mod, top, env);
            myHandler.receiveHandler().sendMessage(message);
        } else if (cmdRet == (byte) (Bus.cmd | 0x10)) {
            boolean ret = sumCheck(dataIn, lenIn);
            Log.i(TAG, "ret=" + ret);
            if (!ret) {
                code = 1;
            } else {
                code = dataIn[2];
                for (int i = 0; i < lenIn; i++) {
                    rx[i] = dataIn[i];
                }
                lenRx = lenIn;
                Log.i(TAG, "lenRx=" + lenRx + ", data=" + ByteUtils.byteToString(Arrays.copyOf(rx, lenRx)));
                Semaphore.post();
            }

        }
    }
}
