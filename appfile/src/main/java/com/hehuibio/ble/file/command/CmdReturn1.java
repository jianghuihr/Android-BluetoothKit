package com.hehuibio.ble.file.command;

public class CmdReturn1 {

    public static CmdReturn host = new CmdReturn();
    private boolean isTempDebug;

//    void cmd_return_proc(byte[] dataIn, int lenIn) {// ����������֡
//        if ((null == dataIn) || (lenIn >= Bus.RX_BYTES_MAX))
//            return;
//        char cmdRet = (char) dataIn[0];
//        if (cmdRet == 0xB0) { // �¶�ˢ��
//            host.modLastR = host.modLast;
//            boolean ret = sum_check(dataIn)
//            ;
//            if (isTempDebug) {  // �¶ȵ���
//                if ((!ret) || (lenIn != 13)) { // У�����ȡ�ϴ�����
//                    host.mod = host.modLast;
//                    host.top = host.topLast;
//                    host.env = host.envLast;
//                } else {
//                    host.mod = (float) (dataIn[2] + dataIn[3] * 256) / 100.0f;
//                    host.top = (float) ((short) (dataIn[4] + dataIn[5] * 256)) / 100.0f;
//                    host.env = (float) ((short) (dataIn[6] + dataIn[7] * 256)) / 100.0f;
//                    host.prm4 = (float) ((short) (dataIn[8] + dataIn[9] * 256)) / 100.0f;
//                    host.prm5 = (float) ((short) (dataIn[10] + dataIn[11] * 256)) / 100.0f;
//                }
//            } else { // �������
//                if ((!ret) || (lenIn != 9)) { // У�����ȡ�ϴ�����
//                    host.top = host.topLast;
//                    host.mod = host.modLast;
//                    host.env = host.envLast;
//                } else {
//                    host.top = (float) (dataIn[2] + dataIn[3] * 256) / 100.0f;
//                    host.mod = (float) (dataIn[4] + dataIn[5] * 256) / 100.0f;
//                    host.env = (float) (dataIn[6] + dataIn[7] * 256) / 100.0f;
//
//                    if (host.topLast != -1) {
//                        if (Math.abs(host.top - host.topLast) > 3) { // ÿ������²�
//                            host.top = host.topLast;
//                        } else {
//                            host.topLast = host.top;
//                        }
//                    } else {
//                        host.topLast = host.top;
//                    }
//
//                    if (host.modLast != -1) {
//                        if (Math.abs(host.mod - host.modLast) > 8) {
//                            host.mod = host.modLast;
//                        } else {
//                            host.modLast = host.mod;
//                        }
//                    } else {
//                        host.modLast = host.mod;
//                    }
//
//                    if (host.envLast != -1) {
//                        if (Math.abs(host.env - host.envLast) > 10) {
//                            host.env = host.envLast;
//                        } else {
//                            host.envLast = host.env;
//                        }
//                    } else {
//                        host.envLast = host.env;
//                    }
//                }
//            }
//            host.tickModRx = obj.Tick;        // ��¼�����¶Ƚ��յĽ���
//            obj.TickLast = obj.Tick;        // ʱ���
//        } else if ((cmd | 0x10) == cmdRet) { // �Ƕ�Ӧ��ָ���
//            bool ret = sum_check((uchar *)dataIn, lenIn);
//            if (!ret) {
//                code = RET_SUM_CHECK_ERROR;    // ��������ʱ��У�����
//                ff << "sum err " << lenIn << endl;
//            } else {
//                code = dataIn[2];    // ���ص�code
//                if (code != RET_NO_ERROR) {
//                    ff << "machine 0x" << hex << code << " " << dec << lenIn << endl;
//                }
//            }
//            for (int j = 0; j < lenIn; j++)  // lenIn�Ѿ�����У��
//                rx[j] = dataIn[j];    // ��������ֵ
//            lenRx = lenIn;
//
//            SetEvent(host.m_event);    // ������
//        } else {
//            ff << "cmd err cmd=" << (int) cmd << " cmdRet=" << (int) cmdRet << " " << lenIn << endl;
//            for (int i = 0; i < lenIn; i++)
//                ff << (int) dataIn[i] << endl;
//        }
//    }

    boolean sum_check(byte[] dateIn) {
        return false;
    }
}
