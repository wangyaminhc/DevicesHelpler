package com.sssoft.base.devices.devices_driver_lib.device_control_imp.icbc;

import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.icbc.smartpos.deviceservice.aidl.PinInputListener;
import com.sssoft.base.devices.bean.KeyTypeEnum;
import com.sssoft.base.devices.bean.MacTypeEnum;
import com.sssoft.base.devices.devices_driver_lib.interfaces.callback_interface.PinpadListener;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.IPinpad;

public class IcbcPinPad implements IPinpad {

    private PinpadListener pinpadlistener;
    private com.icbc.smartpos.deviceservice.aidl.IPinpad iPinpad;

    public IcbcPinPad(IBinder binder) {
        iPinpad = com.icbc.smartpos.deviceservice.aidl.IPinpad.Stub.asInterface(binder);
    }

    @Override
    public boolean loadMainKey(int mkIndex, String key, String kcv) throws RemoteException {
        byte[] keyBytes = key.getBytes();
        byte[] kcvByte = null;
        if(kcv != null) {
            kcvByte = kcv.getBytes();
        }
        try {
            return iPinpad.loadMainKey(mkIndex, keyBytes, kcvByte);
        } catch (Exception e) {
            Log.e("APinpadSample", "loadMainKey"+e.getLocalizedMessage());
            return false;
        }
    }

    /**
     *
     * @param keyType 工作密钥类型，1：MAC key，2：PIN key，3：TDK key
     * @param mkIndex 主密钥的索引
     * @param wkIndex 下装工作密钥存储的索引
     * @param keyValue 密钥数据
     * @param kcv 校验值KCV
     * @return
     * @throws RemoteException
     */
    @Override
    public boolean loadWorkKey(KeyTypeEnum keyType, int mkIndex, int wkIndex, String keyValue, String kcv) throws RemoteException {
        try {
            return  iPinpad.loadWorkKey(getWorkKeyType(keyType), mkIndex, wkIndex, hexString2ByteArray(keyValue), null);
        } catch (RemoteException e) {
            Log.e("APinpadSample", "loadWorkKey"+e.getLocalizedMessage());
            return false;
        }
    }

    @Override
    public byte[] calcMAC(MacTypeEnum macAlgorithm, int macIndex, String data) throws RemoteException {
        return iPinpad.calcMAC(macIndex, hexString2ByteArray(data));
    }

    @Override
    public byte[] encryptData(int keyIndex, String data) throws RemoteException {
        return iPinpad.encryptTrackData(0, keyIndex, hexString2ByteArray(data));
    }

    @Override
    public byte[] dencryptData(int keyIndex, String data) throws RemoteException {
        return null;
    }

    @Override
    public void startPininput(int pinKeyIndex, String pan, String lenLimit, PinpadListener listener) throws RemoteException {
        Bundle param = new Bundle();
        param.putBoolean("isOnline", false);
        param.putString("Pan", pan);
        byte[] pinLimit = lenLimit.getBytes();
        param.putByteArray("pinLimit", pinLimit);
        param.putLong("Timeout", 60);
        param.putString("promoptString", "请输入密码");

        pinpadlistener = listener;
        iPinpad.startPinInput(pinKeyIndex, param, new PinInputListener.Stub() {
            @Override
            public void onInput(int i, int i1) throws RemoteException {

            }

            @Override
            public void onConfirm(byte[] bytes, boolean b) throws RemoteException {
                pinpadlistener.onPinpadResult(new String(bytes));
            }

            @Override
            public void onCancel() throws RemoteException {
                pinpadlistener.onError("XX", "取消密码输入");
            }

            @Override
            public void onError(int i) throws RemoteException {
                pinpadlistener.onError("XX", "密码输入错误");
            }
        });
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

    private int getWorkKeyType(KeyTypeEnum keyType)
    {
        int type = 3;
        switch (keyType){
            case MAC_KEY:
                type = 1;
                break;
            case PIN_KEY:
                type = 2;
                break;
        }

        return type;
    }

    private byte[] hexString2ByteArray(String hexStr) {
        if(hexStr == null) {
            return null;
        } else if(hexStr.length() % 2 != 0) {
            return null;
        } else {
            byte[] data = new byte[hexStr.length() / 2];

            for(int i = 0; i < hexStr.length() / 2; ++i) {
                char hc = hexStr.charAt(2 * i);
                char lc = hexStr.charAt(2 * i + 1);
                byte hb = hexChar2Byte(hc);
                byte lb = hexChar2Byte(lc);
                if(hb < 0 || lb < 0) {
                    return null;
                }

                int n = hb << 4;
                data[i] = (byte)(n + lb);
            }

            return data;
        }
    }
    private byte hexChar2Byte(char c) {
        return c >= 48 && c <= 57?(byte)(c - 48):(c >= 97 && c <= 102?(byte)(c - 97 + 10):(c >= 65 && c <= 70?(byte)(c - 65 + 10):-1));
    }

}
