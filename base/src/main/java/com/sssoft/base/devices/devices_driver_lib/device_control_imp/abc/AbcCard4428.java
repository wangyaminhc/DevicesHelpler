package com.sssoft.base.devices.devices_driver_lib.device_control_imp.abc;

import android.os.IBinder;
import android.os.RemoteException;

import com.sssoft.base.devices.devices_driver_lib.interfaces.callback_interface.InsertCardListener;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.InsertCard;


public class AbcCard4428 implements InsertCard {
    public AbcCard4428(IBinder binder) {
    }


    @Override
    public void read(int timeout, InsertCardListener listener) throws RemoteException {

    }

    @Override
    public void cancel() throws RemoteException {

    }
}
