package com.sssoft.base.devices.devices_driver_lib.device_control_imp.ccb;

import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.ccb.deviceservice.aidl.pinpad.Constant;
import com.landicorp.android.eptapi.utils.BytesUtil;
import com.sssoft.base.devices.bean.KeyTypeEnum;
import com.sssoft.base.devices.bean.MacTypeEnum;
import com.sssoft.base.devices.devices_driver_lib.interfaces.callback_interface.PinpadListener;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.IPinpad;

public class CcbPinpad implements IPinpad {

    private com.ccb.deviceservice.aidl.pinpad.IPinpad pinpad = null;
    private PinpadListener pinpadListener;
    public static final String ISO88591 = "ISO8859-1";

    public static class KeyType {
//         pin密钥
        public static final String PIN_KEY = "PIN_KEY";
//         磁道密钥
        public static final String TRACK_KEY = "TRACK_KEY";
//         mac密钥
        public static final String MAC_KEY = "MAC_KEY";
    }


    CcbPinpad(IBinder binder)
    {
        Bundle bundle = new Bundle();
        bundle.putInt("kapId", 0);
        bundle.putString("deviceName", Constant.DeviceName.INNER);
        pinpad = com.ccb.deviceservice.aidl.pinpad.IPinpad.Stub.asInterface(binder);
    }

    @Override
    public boolean loadMainKey(int mkIndex, String key, String kcv) throws RemoteException {
        byte[] keyByte = BytesUtil.hexString2Bytes(key);
        try {
            if(keyByte.length == 8) {
                pinpad.setKeyAlgorithm(Constant.KeyAlgorithm.KA_DEA);
            } else {
                pinpad.setKeyAlgorithm(Constant.KeyAlgorithm.KA_TDEA);
            }
            Log.e("loadMainkey", "mkeyIdx = " + mkIndex);
            Log.e("loadMainkey", "keyId = " + BytesUtil.bytes2HexString(keyByte));
            if (!pinpad.loadMainKey(mkIndex, keyByte, null)) {
                return false;
            }
        } catch (RemoteException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean loadWorkKey(KeyTypeEnum keyType, int mkIndex, int wkIndex, String keyValue, String kcv) throws RemoteException {
        try {
            int keyLoadType = Constant.KeyType.MAC_KEY;
            byte[] keyByte = BytesUtil.hexString2Bytes(keyValue);
            byte[] checkValueByte = null;
            if(kcv!=null) {
                checkValueByte = BytesUtil.hexString2Bytes(kcv);
            }
            switch ( keyType){
                case MAC_KEY:
                    keyLoadType =Constant.KeyType.MAC_KEY;
                    break;
                case TDK_KEY:
                    keyLoadType =Constant.KeyType.TD_KEY;
                    break;
                case PIN_KEY:
                    keyLoadType = Constant.KeyType.PIN_KEY;
                    break;
            }
            try {
                if(keyByte.length == 8) {
                    pinpad.setKeyAlgorithm(Constant.KeyAlgorithm.KA_DEA);
                } else {
                    pinpad.setKeyAlgorithm(Constant.KeyAlgorithm.KA_TDEA);
                }
                if (!pinpad.loadWorkKey(keyLoadType, wkIndex, mkIndex, keyByte, checkValueByte)) {
                    return false;
                }
            } catch (RemoteException e) {
                return false;
            }


        } catch (Exception e) {
            e.printStackTrace();
            Log.e("loadWorkkey", e.getLocalizedMessage());
            return false;
        }
        return true;
    }

    /**
     *  计算 MAC
     *  mode ‐ 填充模式，见常量 Pinpad.MM_PADDING_MODE_* 定义
     * algorithm ‐ 算法，见常量 Pinpad.MM_ALG_* 定义
     *  encryption ‐ 对于外置 pinpad 有效，是否使用线路加密
     *  mackeyId ‐ MAC 密钥索引
     */
    @Override
    public byte[] calcMAC(MacTypeEnum macAlgorithm, int macIndex, String data) throws RemoteException {
        char tmpMacAlgorithm = Constant.KeyAlgorithm.KA_TDEA;
//        按标准的 ISO9797 算法， 计算输入数据的 MAC
        try {
            byte[] dataByte = BytesUtil.hexString2Bytes(data);
        /*    switch ( macAlgorithm){
                case MAC_CBC:
                    tmpMacAlgorithm = Constant.KeyAlgorithm.;
                    break;
                case MAC_9606:
                    tmpMacAlgorithm =Const.MacType.MAC_9606;
                    break;
                case MAC_ECB:
                    tmpMacAlgorithm = Const.MacType.MAC_ECB;
                    break;

                case MAC_X99:
                    tmpMacAlgorithm = Const.MacType.MAC_X99;
                    break;
                case MAC_X919:
                    tmpMacAlgorithm = Const.MacType.MAC_X919;
                    break;
            }*/
            pinpad.setKeyAlgorithm(tmpMacAlgorithm);
            return pinpad.calcMAC(macIndex, dataByte);
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
       return;
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
}
