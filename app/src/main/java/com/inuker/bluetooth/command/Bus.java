package com.inuker.bluetooth.command;

import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;

import com.inuker.bluetooth.concurrency.MyHandler;
import com.inuker.bluetooth.concurrency.ScheduleThreadPoolManager;
import com.inuker.bluetooth.library.utils.ByteUtils;
import com.inuker.bluetooth.model.Param;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

// 功能: 通信线程处理, 通信内核
// 发送线程: 根据cmd选择发送指令: 比如 Execute.qReset(1), Execute.qmove(...)
// 接收线程: 1. 字节流到帧 2. 帧到cmd返回
public class Bus {

    private static final String TAG = Bus.class.getSimpleName();
    private static byte[] rx = new byte[127];
    private static int lenRx = 0;
    private static int tick = 0;

    public static int cmd = 0x00;
    //=====
    public static int op;
    public static int val1;
    public static int val2;
    public static int val3;
    public static int val4;
    public static int val5;
    public static int val6;
    public static float f1;
    public static float f2;
    public static float f3;
    public static float f4;


    private static final int RX_BYTES_MAX = 127;

    private static Execute execute = null;

    private static Bus bus = new Bus();
    private static MyHandler myHandler = MyHandler.get();

    boolean sumCheck(byte[] dataIn, int lenIn) {
        int sum = 0;
        for (int i = 0; i < (lenIn - 1); i++) {
            sum += dataIn[i];
        }

        sum = sum % 0xff;
        return sum == dataIn[lenIn - 1];
    }

    private static void rxProc() {
        if (lenRx <= 0) {        //�������յ�����
            return;
        }
        if (tick > 6) {        // ���ճ�ʱ��Ҳ��Ϊ���ν��ս���
            CmdReturn.cmdReturnProc(rx, lenRx);
            lenRx = 0;
            tick = 0;
            Arrays.fill(rx, (byte) 0);
        } else {
            char cmdPart = (char) (rx[0] & 0xF0);
            //Log.i(TAG, "rxProc, cmdPart=" + (int) cmdPart);
            if ((cmdPart == 0xB0) || (cmdPart == 0xF0)) { // У���Ƿ���ָ���
                if (lenRx >= 2) {
                    int length = rx[1];        // ��ȡ����
                    if (length >= RX_BYTES_MAX) { // ���ȳ���  180420
                        lenRx = 0;  // ���ݶ���
                        tick = 0;
                        Arrays.fill(rx, (byte) 0);
                    } else if (length <= (lenRx - 2)) {
                        lenRx = length + 2;    // ��������ݶ���
                        CmdReturn.cmdReturnProc(rx, lenRx);
                        Log.i(TAG, "rxProc size=" + lenRx + ", data=" + ByteUtils.byteToString(Arrays.copyOf(rx, lenRx)));
                        lenRx = 0;
                        tick = 0;
                        Arrays.fill(rx, (byte) 0);
                    } else {
                        tick++;
                    }
                } else {
                    tick++;
                }
            } else {
                lenRx = 0;
                tick = 0;
            }
        }
    }

    // 发送线程执行,可阻塞
    private static void cmdProc() {
        if (cmd == 0x00) {
            return;
        }
        int code;
        switch (cmd) {
            case 0xA1:   // 运行，停止
                code = execute.qInitPreHeat(op);
                break;
            case 0xA8:   // 执行温度点
                code = execute.qTempExecute(f1);
                break;
            case 0xE1:   // 复位
                code = execute.qReset(op);
                break;
            case 0xE2:   // 运动
                code = execute.qMove(op, val1);
                break;
            case 0xE3:   // 获取参数
                Param param1 = new Param();
                param1.val1 = 0;
                Param param2 = new Param();
                param2.val1 = 0;

                code = execute.qGetParam(op, param1);
                Log.i(TAG, " 0xE3 pa= " + param1.val1);

                if (code == 0) {
                    if (op == 6) {
                        code = execute.qGetParam(7, param2);
                        Log.i(TAG, " 0xE3 pa1= " + param2.val1);
                    }
                }
                Message message = new Message();
                message.what = MyHandler.MSG_WHAT_OTHER_E3;
                message.obj = new Param(param1.val1, param2.val1);
                myHandler.sendHandler().sendMessage(message);
                break;
            case 0xE5:   // 获取传感器状态
                Param param = new Param();
                param.val1 = 0;
                code = execute.qCheckSensor(val1, param);
                Log.i(TAG, " 0xE5 pa1= " + param.val1);    // 0： 不触发；1-触发

                Message msgE5 = new Message();
                msgE5.what = MyHandler.MSG_WHAT_OTHER_E5;
                msgE5.obj = param;
                myHandler.sendHandler().sendMessage(msgE5);
                break;
            case 0xE7:   // 设置参数
                code = execute.qSetParam(op, val1);
                if (code == 0) {
                    if (op == 6) {
                        code = execute.qSetParam(7, val2);
                    }
                }
                break;
        }

        cmd = 0x00;
    }

    // 接收线程执行
    private static Runnable cmdReceiveTask = new Runnable() {
        @Override
        public void run() {
            rxProc();
            ScheduleThreadPoolManager.getInstance().schedule(cmdReceiveTask, 10, TimeUnit.MILLISECONDS);
        }
    };

    // 指令发送线程执行
    private static Runnable cmdSendTask = new Runnable() {
        @Override
        public void run() {
            cmdProc();
            ScheduleThreadPoolManager.getInstance().schedule(cmdSendTask, 10, TimeUnit.MILLISECONDS);
        }
    };

    public static void initConfig(@NonNull Execute exe) {
        execute = exe;
    }


    public static void receive(byte[] dataIn) {
        if (null == dataIn || dataIn.length <= 0) {
            return;
        }
        for (byte aDataIn : dataIn) {
            rx[lenRx++] = aDataIn;
        }
    }

    // 接收线程
    public static void cmdReceiveRoll() {
        ScheduleThreadPoolManager.getInstance().schedule(cmdReceiveTask, 0, TimeUnit.MILLISECONDS);
    }

    // 指令发送线程
    public static void cmdProcRoll() {
        ScheduleThreadPoolManager.getInstance().schedule(cmdSendTask, 0, TimeUnit.MILLISECONDS);
    }
}
