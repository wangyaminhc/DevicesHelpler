package com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface;

import android.os.RemoteException;

import com.sssoft.base.devices.devices_driver_lib.interfaces.callback_interface.CardListener;

public interface Card {
    void read(int timeout,  CardListener listener) throws RemoteException;
    void cancel();
}
