package com.inuker.bluetooth.command;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;

import com.inuker.bluetooth.concurrency.MyHandler;
import com.inuker.bluetooth.concurrency.ScheduleThreadPoolManager;
import com.inuker.bluetooth.library.utils.ByteUtils;

import java.util.Arrays;
import java.util.concurrent.ScheduledFuture;
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
    private static ScheduledFuture cmdSendFuture;
    private static ScheduledFuture cmdReceiveFuture;

    private static Bus bus = new Bus();

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
                code = execute.qReset(1);
                break;
            case 0xE2:   // 运动
                code = execute.qMove(op, val1);
                break;
            case 0xE3:   // 获取参数
                Integer param1 = new Integer(0), param2 = 0;
                code = execute.qGetParam(op, param1);
                Log.i(TAG, " pa= "+ param1);

                if (code == 0) {
                    if (op == 6) {
                        code = execute.qGetParam(7, param2);
                    }
                }
                Log.i(TAG, " pa= "+ param2);
                break;
            case 0xE7:   // 设置参数
                code = execute.qSetParam(op, val1);
                break;
        }

        cmd = 0x00;
    }

    // 接收线程执行
    private static Runnable cmdReceiveRollTask = new Runnable() {
        @Override
        public void run() {
            rxProc();
            ScheduleThreadPoolManager.getInstance().schedule(cmdReceiveRollTask, 10, TimeUnit.MILLISECONDS);
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
        cmdReceiveFuture = ScheduleThreadPoolManager.getInstance().schedule(cmdReceiveRollTask, 0, TimeUnit.MILLISECONDS);
    }

    // 指令发送线程
    public static void cmdProcRoll() {
        cmdSendFuture = ScheduleThreadPoolManager.getInstance().schedule(cmdSendTask, 0, TimeUnit.MILLISECONDS);
    }
}
