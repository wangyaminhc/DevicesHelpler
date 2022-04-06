package com.sssoft.base.devices.devices_driver_lib.device_control_imp.abc;


import android.os.IBinder;
import android.os.RemoteException;

import com.sssoft.base.devices.devices_driver_lib.interfaces.callback_interface.ScannerListener;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.Scanner;
import com.zacloud.deviceservice.aidl.IScanner;

public class AbcScanner implements Scanner {
    IScanner scanner = null;
    private ScannerListener scannerlistener;

    public AbcScanner(IBinder binder) {
        scanner = IScanner.Stub.asInterface(binder);
    }

    @Override
    public void startScan( int timeout, ScannerListener listener) throws RemoteException {
            scannerlistener = listener;
            scanner.startScan(timeout, new com.zacloud.deviceservice.aidl.ScannerListener.Stub(){

                @Override
                public void onSuccess(String barcode) throws RemoteException {
                    scannerlistener.onScanResult(barcode);
                }

                @Override
                public void onError(int error, String message)
                        throws RemoteException {
                    scannerlistener.onError(error+"", "扫描失败:"+message);
                }

                @Override
                public void onTimeout() throws RemoteException {
                    scannerlistener.onError("XX"+"", "扫描超时");
                }

                @Override
                public void onCancel() throws RemoteException {
                    if(scannerlistener!=null){
                        scannerlistener.onError("X1", "取消扫描");
                    }
                }

            });
    }

    @Override
    public void stopScan() {
        try {
            scanner.stopScan();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
