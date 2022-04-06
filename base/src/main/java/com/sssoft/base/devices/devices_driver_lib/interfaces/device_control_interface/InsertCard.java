package com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface;

import android.os.RemoteException;

import com.sssoft.base.devices.devices_driver_lib.interfaces.callback_interface.InsertCardListener;

public interface InsertCard {
    void read(int timeout,  InsertCardListener listener) throws RemoteException;
    void cancel()throws RemoteException;
}
