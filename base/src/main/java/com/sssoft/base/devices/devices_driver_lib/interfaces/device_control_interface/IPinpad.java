package com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface;

import com.sssoft.base.devices.bean.KeyTypeEnum;
import com.sssoft.base.devices.bean.MacTypeEnum;
import com.sssoft.base.devices.devices_driver_lib.interfaces.callback_interface.PinpadListener;

public interface IPinpad {
     boolean loadMainKey(int mkIndex, String key, String kcv) throws android.os.RemoteException;
     boolean loadWorkKey(KeyTypeEnum keyTypeEnum, int mkIndex, int wkIndex, String keyValue, String kcv) throws android.os.RemoteException;
     byte[] calcMAC(MacTypeEnum macAlgorithm, int macIndex, String data) throws android.os.RemoteException;
     byte[] encryptData(int keyIndex, String data) throws android.os.RemoteException;
     byte[] dencryptData(int keyIndex, String data) throws android.os.RemoteException;
     void startPininput(int pinKeyIndex, String pan, String lenLimit, PinpadListener listener) throws android.os.RemoteException;
     String getRandom() throws android.os.RemoteException;
     void cancelPininput() throws android.os.RemoteException;
     byte[] setPinpadLayout(String layout) throws android.os.RemoteException;
}
