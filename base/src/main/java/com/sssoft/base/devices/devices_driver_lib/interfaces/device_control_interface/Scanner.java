package com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface;

import android.os.RemoteException;

import com.sssoft.base.devices.devices_driver_lib.interfaces.callback_interface.ScannerListener;

public interface Scanner {
    void startScan(int timeout,  ScannerListener listener) throws RemoteException;
    void stopScan();
}
