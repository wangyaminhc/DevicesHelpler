package com.sssoft.base.devices.devices_driver_lib.device_control_imp.boc;

import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.boc.aidl.constant.Const;
import com.boc.aidl.pinpad.AidlPinpad;
import com.boc.aidl.pinpad.AidlPinpadListener;
import com.landicorp.android.eptapi.utils.BytesUtil;
import com.sssoft.base.devices.bean.KeyTypeEnum;
import com.sssoft.base.devices.bean.MacTypeEnum;
import com.sssoft.base.devices.devices_driver_lib.interfaces.callback_interface.PinpadListener;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.IPinpad;

import static com.boc.aidl.constant.Const.KeyType.MAC_KEY;
import static com.boc.aidl.constant.Const.KeyType.PIN_KEY;
import static com.boc.aidl.constant.Const.KeyType.TRACK_KEY;

public class BocPinPad implements IPinpad {
    private PinpadListener mListener;
    private AidlPinpad pinpad;
    public BocPinPad(IBinder binder) {
        pinpad = AidlPinpad.Stub.asInterface(binder);
    }

    @Override
    public boolean loadMainKey(int mkIndex, String key, String kcv) throws RemoteException {
        try {
            return pinpad.loadMainKey(mkIndex, key, kcv);
        } catch (RemoteException e) {
            e.printStackTrace();
            Log.e("loadMainKey", e.getLocalizedMessage());
            return false;
        }
    }

    @Override
    public boolean loadWorkKey(KeyTypeEnum keyType, int mkIndex, int wkIndex, String keyValue, String kcv) throws RemoteException {
        String keyLoadType = "";
        try {
            Log.e("loadMackey", "mkeyIdx = " + mkIndex);
            Log.e("loadMackey", "keyId = " + wkIndex);
            Log.e("loadMackey", "key = " + keyValue);
            switch ( keyType){
                case MAC_KEY:
                    keyLoadType =MAC_KEY;
                    break;
                case TDK_KEY:
                    keyLoadType =TRACK_KEY;
                    break;
                case PIN_KEY:
                    keyLoadType = PIN_KEY;
                    break;
            }
            return pinpad.loadWorkKey(keyLoadType, mkIndex, wkIndex, keyValue, kcv);
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public byte[] calcMAC(MacTypeEnum macAlgorithm, int macIndex, String data) throws RemoteException {
         int tmpMacAlgorithm = Const.MacType.MAC_X99;
        byte[] data_byte = BytesUtil.hexString2Bytes(data);
        try {
            switch ( macAlgorithm){
                case MAC_CBC:
                    tmpMacAlgorithm = Const.MacType.MAC_CBC;
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
            }
            return pinpad.calcMAC(tmpMacAlgorithm, macIndex, data_byte);
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public byte[] encryptData(int keyIndex, String data) throws RemoteException {
        byte[] data_byte = BytesUtil.hexString2Bytes(data);
        try {
            return pinpad.encryptData(keyIndex, data_byte);
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public byte[] dencryptData(int keyIndex, String data) throws RemoteException {
        byte[] data_byte = BytesUtil.hexString2Bytes(data);
        try {
            return pinpad.dencryptData(keyIndex,data_byte);
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void startPininput(int pinKeyIndex, String pan, String lenLimit, PinpadListener listener) throws RemoteException {
        byte[] lenLimit_byte = BytesUtil.hexString2Bytes(lenLimit);
        try {
            mListener = listener;
            pinpad.startPininput(pinKeyIndex, pan, lenLimit_byte, new AidlPinpadListener.Stub() {
                @Override
                public void onKeyDown(int i, int i1) throws RemoteException {

                }

                @Override
                public void onPinRslt(byte[] bytes) throws RemoteException {

                }

                @Override
                public void onError(int i, String s) throws RemoteException {

                }

                @Override
                public void onCancel() throws RemoteException {

                }
            });
        } catch (Exception e) {

        }
    }

    @Override
    public String getRandom() throws RemoteException {
        try {
            return pinpad.getRandom();
        } catch (Exception e){
            return null;
        }
    }

    @Override
    public void cancelPininput() throws RemoteException {
        try {
            pinpad.cancelPininput();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public byte[] setPinpadLayout(String layout) throws RemoteException {
        byte[] layout_byte = BytesUtil.hexString2Bytes(layout);
        try {
            return pinpad.setPinpadLayout(layout_byte);
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }
}
