package com.sssoft.base.devices.devices_driver_lib.device_control_imp.landi;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.RemoteException;
import android.util.Log;

import com.landicorp.android.eptapi.pinpad.Pinpad;
import com.landicorp.android.eptapi.utils.BytesUtil;
import com.sssoft.base.devices.bean.KeyTypeEnum;
import com.sssoft.base.devices.bean.MacTypeEnum;
import com.sssoft.base.devices.devices_driver_lib.interfaces.callback_interface.PinpadListener;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.IPinpad;

import java.util.Arrays;

public class LandiPinPad implements IPinpad {
    private Pinpad pinpad = null;
    private PinpadListener pinpadListener;
    public static final String ISO88591 = "ISO8859-1";
    private byte[] inKeyByte;
    private int inIndex;

    public LandiPinPad() {
        pinpad = new Pinpad(0, "IPP");
    }

    @Override
    public boolean loadMainKey(int mkIndex, String key, String kcv) throws RemoteException {
        try {
            inKeyByte = BytesUtil.hexString2Bytes(key);
            inIndex = mkIndex;
            Log.e("loadMainkey", "keyByte.length=" + inKeyByte.length);
            return new PinpadExecutor() {
                @Override
                protected boolean onExecute() {
                    if (!pinpad.format()) {
                        Log.e("PinpadImpl", "pinpad format fail, error = " + pinpad.getLastError());
                        return false;
                    }

                    if (!pinpad.loadMainKey(inIndex, inKeyByte, true)) {
                        return false;
                    }
                    return true;
                }
            }.execute();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("loadMainkey", e.getLocalizedMessage());
            return false;
        }
    }

    @Override
    public boolean loadWorkKey(KeyTypeEnum keyType, int mkIndex, int wkIndex, String keyValue, String kcv) throws RemoteException {
        try {
            boolean succFlag = false;
            byte[] keyByte = BytesUtil.hexString2Bytes(keyValue);
            byte[] checkValueByte = null;
            if (kcv != null) {
                checkValueByte = BytesUtil.hexString2Bytes(kcv);
            }
            switch (keyType) {
                case MAC_KEY:
                    succFlag = loadMackey(mkIndex, wkIndex, keyByte, checkValueByte);
                    break;
                case PIN_KEY:
                    succFlag = loadPinkey(mkIndex, wkIndex, keyByte, checkValueByte);
                    break;
                default:
                    succFlag = loadMackey(mkIndex, wkIndex, keyByte, checkValueByte);
                    break;
            }

            return succFlag;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("loadWorkkey", e.getLocalizedMessage());
            return false;
        }
    }

    @Override
    public byte[] calcMAC(MacTypeEnum macAlgorithm, int macIndex, String data) throws RemoteException {
        try {
            int tmpMacAlgorithm = Pinpad.MM_ALG_ISO9797;
            byte[] dataByte = BytesUtil.hexString2Bytes(data);
            //byte[] dataByte = data.getBytes(ISO88591);
            switch ( macAlgorithm){
                case MAC_CBC:
                    tmpMacAlgorithm = Pinpad.MM_ALG_TDES_CBC;
                    break;
                case MAC_9606:
                    tmpMacAlgorithm =Pinpad.MM_ALG_ISO9797;
                    break;
                case MAC_ECB:
                    tmpMacAlgorithm = Pinpad.MM_ALG_ISO9797;
                    break;

                case MAC_X99:
                    tmpMacAlgorithm = Pinpad.MM_ALG_ISO9797;
                    break;
                case MAC_X919:
                    tmpMacAlgorithm = Pinpad.MM_ALG_ISO9797;
                    break;
            }
            return calcMAC(Pinpad.MAC_PADDING_MODE_1,
                    tmpMacAlgorithm, true, macIndex, dataByte);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("calcMAC", e.getLocalizedMessage());
            return null;
        }
    }

    @Override
    public byte[] encryptData(int keyIndex, String data) throws RemoteException {
        return new byte[0];
    }

    @Override
    public byte[] dencryptData(int keyIndex, String data) throws RemoteException {
        return new byte[0];
    }

    @Override
    public void startPininput(int pinKeyIndex, String pan, String lenLimit, PinpadListener listener) throws RemoteException {
        try {
            byte[] panBlockByte = pan.getBytes(ISO88591);
            startPinEntry(false, pinKeyIndex, panBlockByte, listener);
        } catch (Exception e) {
            Log.e("startPinEntry", e.getLocalizedMessage());
        }
    }

    @Override
    public String getRandom() throws RemoteException {
        return null;
    }

    @Override
    public void cancelPininput() throws RemoteException {

    }

    @Override
    public byte[] setPinpadLayout(String layout) throws RemoteException {
        return new byte[0];
    }

    public byte[] calcMAC(final int mode, final int algorithm, final boolean encryption, final int mackeyId, final byte[] data) {
        return new PinpadInvoker() {
            @Override
            protected byte[] onInvoke() {
                int algorithm = pinpad.isSM4Enabled() ? Pinpad.MM_ALG_SM4 : Pinpad.MM_ALG_ISO9797;
                return pinpad.calcMAC(algorithm << 8 | Pinpad.MAC_PADDING_MODE_1, mackeyId, data);
            }
        }.invoke();
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    static boolean checkEquals(byte[] checkValue, byte[] kcv) {
        if (kcv == null || kcv.length == 0) {
            return false;
        }

        if (kcv.length == checkValue.length) {
            return Arrays.equals(kcv, checkValue);
        } else {
            int cmpLen = Math.min(checkValue.length, kcv.length);
            if (kcv.length < checkValue.length) {
                return Arrays.equals(kcv, Arrays.copyOfRange(checkValue, 0, cmpLen));
            } else {
                return Arrays.equals(checkValue, Arrays.copyOfRange(kcv, 0, cmpLen));
            }
        }
    }

    public void startPinEntry(boolean isOnline, int keyId, byte[] panBlock, PinpadListener listener) {
        if (!pinpad.open()) {
            // TODO Pinpad 打开失败错误处理
            return;
        }

        byte[] lenLimit = new byte[]{0, 6};
        pinpad.setPinLengthLimit(lenLimit);
        pinpad.setPinEntryTimeout(300);
        pinpad.setTimeoutBetweenPinKeys(60);
        pinpad.setOnPinInputListener(new Pinpad.OnPinInputListener() {
            @Override
            public void onConfirm(byte[] pin, boolean isNonePin) {
                // TODO 密码输入完成事件处理
            }

            @Override
            public void onInput(int len, int key) {
                // TODO 密码输入过程事件处理
            }

            @Override
            public void onCancel() {
                // TODO 取消事件处理
            }

            @Override
            public void onError(int errorCode) {
                // TODO 出错事件处理
            }
        });

        if (isOnline) {
            pinpad.inputOnlinePin(panBlock, keyId);
        } else {
            pinpad.startOfflinePinEntry(0);
        }
    }

    // 下装 PIN 密钥
    public boolean loadPinkey(final int mainKeyId, final int keyId, final byte[] key, final byte[] checkVal) {
        return new PinpadExecutor() {
            @Override
            protected boolean onExecute() {
                if (!pinpad.loadWorkKey(mainKeyId, Pinpad.KEYTYPE_PIN_KEY, keyId, key)) {
                    return false;
                }

                if (checkVal == null || checkVal.length == 0) {
                    return true;
                }

                byte[] kcv = pinpad.calcKCV(keyId);
                boolean flag = checkEquals(checkVal, kcv);
                return flag;
            }
        }.execute();
    }

    // 下装 PIN 密钥
    public boolean loadMackey(final int mainKeyId, final int keyId, final byte[] key, final byte[] checkVal) {
        return new PinpadExecutor() {
            @Override
            protected boolean onExecute() {
                if (!pinpad.loadWorkKey(mainKeyId, Pinpad.KEYTYPE_MAC_KEY, keyId, key)) {
                    return false;
                }

                if (checkVal == null || checkVal.length == 0) {
                    return true;
                }

                byte[] kcv = pinpad.calcKCV(keyId);
                boolean flag = checkEquals(checkVal, kcv);
                return flag;
            }
        }.execute();
    }


    public static class KeyType {
        /**
         * pin密钥
         */
        public static final String PIN_KEY = "PIN_KEY";
        /**
         * 磁道密钥
         */
        public static final String TRACK_KEY = "TRACK_KEY";

        /**
         * mac密钥
         */
        public static final String MAC_KEY = "MAC_KEY";
    }

    private abstract class PinpadExecutor {
        public boolean execute() {
            if (pinpad == null || !pinpad.open()) {
                return false;
            }
            try {
                return onExecute();
            } finally {
                pinpad.close();
            }
        }

        protected abstract boolean onExecute();
    }

    private abstract class PinpadInvoker {
        public byte[] invoke() {
            if (pinpad == null || !pinpad.open()) {
                return null;
            }

            try {
                return onInvoke();
            } finally {
                pinpad.close();
            }
        }

        protected abstract byte[] onInvoke();
    }
}
