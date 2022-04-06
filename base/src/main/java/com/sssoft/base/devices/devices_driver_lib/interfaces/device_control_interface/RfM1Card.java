package com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface;

import android.os.RemoteException;

import com.sssoft.base.devices.devices_driver_lib.interfaces.callback_interface.RfM1CardListener;

public interface RfM1Card {
    void read(int block,int sector,String blockAuk,String selectAuk,  RfM1CardListener listener)throws RemoteException;
    void cancel();
}
