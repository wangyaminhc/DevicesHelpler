package com.sssoft.base.devices.devices_driver_lib.device_control_imp.abc;

import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.sssoft.base.devices.bean.KeyTypeEnum;
import com.sssoft.base.devices.bean.MacTypeEnum;
import com.sssoft.base.devices.bean.OtherUtil;
import com.sssoft.base.devices.devices_driver_lib.interfaces.callback_interface.PinpadListener;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.IPinpad;

public class AbcPinPad implements IPinpad {
    private PinpadListener mListener;
    private com.zacloud.deviceservice.aidl.IPinpad pinpad;
    private int keyLoadType; //1 MAC  2 PIN  3TDK

    public AbcPinPad(IBinder binder) {
        pinpad = com.zacloud.deviceservice.aidl.IPinpad.Stub.asInterface(binder);
    }

    @Override
    public boolean loadMainKey(int mkIndex, String key, String kcv) throws RemoteException {
        byte[] keyByte = OtherUtil.hexString2Bytes(key);
            Log.e("loadMainkey", "mkeyIdx = " + mkIndex);
            Log.e("loadMainkey", "keyId = " + OtherUtil.bytes2HexString(keyByte));
            if (!pinpad.loadMainKey(mkIndex, keyByte, null)) {
                return false;
            }
        return true;
    }

    @Override
    public boolean loadWorkKey(KeyTypeEnum keyType, int mkIndex, int wkIndex, String keyValue, String kcv) throws RemoteException {
        try {
            byte[] keyByte = OtherUtil.hexString2Bytes(keyValue);
            byte[] checkValueByte = null;
            if(kcv!=null) {
                checkValueByte = OtherUtil.hexString2Bytes(kcv);
            }
            Log.e("loadMackey", "mkeyIdx = " + mkIndex);
            Log.e("loadMackey", "keyId = " + wkIndex);
            Log.e("loadMackey", "key = " + OtherUtil.bytes2HexString(keyByte));
            switch ( keyType){
                case MAC_KEY:
                    keyLoadType =1;
                    break;
                case TDK_KEY:
                    keyLoadType =3;
                    break;
                case PIN_KEY:
                    keyLoadType = 2;
                    break;
            }
            if (!pinpad.loadWorkKey(keyLoadType, wkIndex, mkIndex, keyByte, checkValueByte)) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("loadWorkkey", e.getLocalizedMessage());
            return false;
        }
        return  true;
    }

    @Override
    public byte[] calcMAC(MacTypeEnum macAlgorithm, int macIndex, String data) throws RemoteException {
        try {
            byte[] dataByte = OtherUtil.hexString2Bytes(data);
            //byte[] dataByte = data.getBytes(ISO88591);
//            pinpad.setKeyAlgorithm(Constant.KeyAlgorithm.KA_TDEA);
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
