package com.hehuibio.ble.file.command;

import android.util.Log;

import com.hehuibio.ble.file.common.helper.ClientManager;
import com.hehuibio.ble.file.model.Param;
import com.inuker.bluetooth.library.connect.response.BleWriteResponse;

import java.util.UUID;
//  功能：封装一条条发送指令
//  1. 字节封装 2.write发送 3.pend 4.返回值处理

public class Execute {

    private static final String TAG = Execute.class.getSimpleName();

    public String mMac;
    public UUID mService;
    public UUID mCharacter;

    public Execute(String mMac, UUID mService, UUID mCharacter) {
        this.mMac = mMac;
        this.mService = mService;
        this.mCharacter = mCharacter;
    }

    int Byte2UINT(byte b) {
        int val;

        if (b >= 0)
            val = b;
        else
            val = (256 + b);

        return val;
    }

    byte sumMake(byte[] dataIn, int lenIn) {
        int sum = 0;
        for (int i = 0; i < (lenIn - 1); i++) {
            sum += Byte2UINT(dataIn[i]);
        }
        sum = sum % 0xff;
        return (byte) sum;
    }

    void write(byte[] dataOut) {
        ClientManager.getClient().write(mMac, mService, mCharacter, dataOut,
                new BleWriteResponse() {
                    @Override
                    public void onResponse(int code) {

                    }
                });
    }

    // 运行，停止
    int qInitPreHeat(int op) {
        int code = 0;
        byte[] dataOut = new byte[5];
        dataOut[0] = (byte) 0xA1;
        dataOut[1] = 3;
        dataOut[2] = (byte) op;
        dataOut[3] = 0;
        dataOut[4] = (byte) sumMake(dataOut, dataOut.length);
        write(dataOut);
        int ret = Semaphore.pend(500);
        if (ret == 0) {
            //Log.i(TAG, "ok");
        } else {
            //Log.i(TAG, "timeout");
        }
        code = CmdReturn.GetCode();
        CmdReturn.lenRx = 0;
        Log.i(TAG, "preheat code= " + code);
        return code;
    }

    // 运行，停止
    int qTempExecute(float temp) {
        int code = 0;
        byte[] dataOut = new byte[5];
        int t = (int) (temp * 10);
        dataOut[0] = (byte) 0xA8;
        dataOut[1] = 3;
        dataOut[2] = (byte) ((t >> 0) & 0xff);
        dataOut[3] = (byte) ((t >> 8) & 0xff);
        dataOut[4] = (byte) sumMake(dataOut, dataOut.length);
        write(dataOut);
        int ret = Semaphore.pend(500);
        if (ret == 0) {
            //Log.i(TAG, "ok");
        } else {
            Log.i(TAG, "timeout");
        }
        code = CmdReturn.GetCode();
        CmdReturn.lenRx = 0;
        Log.i(TAG, "executeT code= " + code);
        return code;
    }

    int qExecFluo(int ch) {
        int code = 0;
        byte[] dataOut = new byte[4];
        dataOut[0] = (byte) 0xA9;
        dataOut[1] = 2;
        dataOut[2] = (byte) ch;
        dataOut[3] = (byte) sumMake(dataOut, dataOut.length);
        write(dataOut);
        int ret = Semaphore.pend(15000);
        if (ret == 0) {
           // Log.i(TAG, "ok");
        } else {
           // Log.i(TAG, "timeout");
        }
        code = CmdReturn.GetCode();

        CmdReturn.lenRx = 0;

        Log.i(TAG, "exGetFluo code= " + code);

        return code;
    }

    // 复位
    int qReset(int op) {
        int code = 0;
        byte[] dataOut = new byte[4];
        dataOut[0] = (byte) 0xE1;
        dataOut[1] = 0x02;
        dataOut[2] = (byte) op;
        dataOut[3] = (byte) sumMake(dataOut, dataOut.length);
        write(dataOut);
        int ret = Semaphore.pend(18000);
        if (ret == 0) {
            //Log.i(TAG, "ok");
        } else {
            //Log.i(TAG, "timeout");
        }
        code = CmdReturn.GetCode();
        CmdReturn.lenRx = 0;

        //Log.i(TAG, "code= "+code );
        return code;
    }

    // 运动
    int qMove(int op, int coordinate) {
        int code = 0;
        byte[] dataOut = new byte[6];
        dataOut[0] = (byte) 0xE2;
        dataOut[1] = 0x04;
        dataOut[2] = (byte) op;
        dataOut[3] = (byte) ((coordinate >> 0) & 0xff);
        dataOut[4] = (byte) ((coordinate >> 8) & 0xff);
        dataOut[5] = (byte) sumMake(dataOut, dataOut.length);
        write(dataOut);
        int ret = Semaphore.pend(15000);
        if (ret == 0) {
          //  Log.i(TAG, "ok");
        } else {
           // Log.i(TAG, "timeout");
        }
        code = CmdReturn.GetCode();
        CmdReturn.lenRx = 0;

        Log.i(TAG, "move code= " + code);

        return code;
    }

    int qGetParam(int op, Param param) {
        int code = 0;
        byte[] dataOut = new byte[4];
        dataOut[0] = (byte) 0xE3;
        dataOut[1] = 2;
        dataOut[2] = (byte) op;
        dataOut[3] = (byte) sumMake(dataOut, dataOut.length);
        write(dataOut);
        int ret = Semaphore.pend(500);
        if (ret == 0) {
          //  Log.i(TAG, "ok");
        } else {
          //  Log.i(TAG, "timeout");
        }
        code = CmdReturn.GetCode();

        param.val1 = Byte2UINT(CmdReturn.rx[3]) + Byte2UINT(CmdReturn.rx[4]) * 256;
        Log.i(TAG, "param1=" + param.val1);

        CmdReturn.lenRx = 0;

        Log.i(TAG, "getP code= " + code);
        return code;
    }

    // 读取传感器信息
    int qCheckSensor(int devId, Param param) {
        int code = 0;
        byte[] dataOut = new byte[4];
        dataOut[0] = (byte) 0xE5;
        dataOut[1] = 2;
        dataOut[2] = (byte) devId;
        dataOut[3] = (byte) sumMake(dataOut, dataOut.length);
        write(dataOut);
        int ret = Semaphore.pend(500);
        if (ret == 0) {
           // Log.i(TAG, "ok");
        } else {
          //  Log.i(TAG, "timeout");
        }
        code = CmdReturn.GetCode();
        param.val1 = Byte2UINT(CmdReturn.rx[3]);
        Log.i(TAG, "param1=" + param.val1);

        CmdReturn.lenRx = 0;

        Log.i(TAG, "getSensor code= " + code);

        return code;
    }

    // 运动
    int qSetParam(int op, int param) {
        int code = 0;
        byte[] dataOut = new byte[6];
        dataOut[0] = (byte) 0xE7;
        dataOut[1] = 0x04;
        dataOut[2] = (byte) op;
        dataOut[3] = (byte) ((param >> 0) & 0xff);
        dataOut[4] = (byte) ((param >> 8) & 0xff);
        dataOut[5] = (byte) sumMake(dataOut, dataOut.length);
        write(dataOut);
        int ret = Semaphore.pend(500);
        if (ret == 0) {
          //  Log.i(TAG, "ok");
        } else {
           // Log.i(TAG, "timeout");
        }
        code = CmdReturn.GetCode();
        CmdReturn.lenRx = 0;

        Log.i(TAG, "setP code= " + code);

        return code;
    }

    int qDebug(int op, Param param) {      // Param 增加  string s1, s2
        int code = 0;
        byte[] dataOut = new byte[4];
        dataOut[0] = (byte) 0xEF;
        dataOut[1] = 2;
        dataOut[2] = (byte) op;
        dataOut[3] = (byte) sumMake(dataOut, dataOut.length);
        write(dataOut);
        int ret = Semaphore.pend(500);
        if (ret == 0) {
           // Log.i(TAG, "ok");
        } else {
           // Log.i(TAG, "timeout");
        }
        code = CmdReturn.GetCode();
        if (op == 0xFE) {  // 读取版本等
            byte[] b = new byte[11];
            for (int j = 0; j < 11; j++) {
                b[j] = CmdReturn.rx[3 + j];
            }
            param.s1 = new String(b);
            param.val1 = Byte2UINT(CmdReturn.rx[14]) + Byte2UINT(CmdReturn.rx[15]) * 256;  // version
            param.val2 = Byte2UINT(CmdReturn.rx[16]) + Byte2UINT(CmdReturn.rx[17]) * 256;     // numx
        }

        Log.i(TAG, "param1=" + param.val1);


        CmdReturn.lenRx = 0;

        Log.i(TAG, "qDebug code= " + code);

        return code;
    }

    int qSwitch(int devId, int op, int time) {
        int code = 0;
        byte[] dataOut = new byte[7];
        dataOut[0] = (byte) 0xE6;
        dataOut[1] = 5;
        dataOut[2] = (byte) devId;
        dataOut[3] = (byte) op;
        dataOut[4] = (byte) ((time >> 0) & 0xff);
        dataOut[5] = (byte) ((time >> 8) & 0xff);
        dataOut[6] = (byte) sumMake(dataOut, dataOut.length);
        write(dataOut);
        int ret = Semaphore.pend(500);
        if (ret == 0) {
          //  Log.i(TAG, "ok");
        } else {
           // Log.i(TAG, "timeout");
        }
        code = CmdReturn.GetCode();

        CmdReturn.lenRx = 0;

        Log.i(TAG, "qSwitch code= " + code);

        return code;
    }


}
