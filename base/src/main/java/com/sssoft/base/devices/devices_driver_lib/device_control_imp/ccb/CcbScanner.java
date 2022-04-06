package com.sssoft.base.devices.devices_driver_lib.device_control_imp.ccb;

import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import com.ccb.deviceservice.aidl.scanner.IScanner;
import com.ccb.deviceservice.aidl.scanner.OnScanListener;
import com.sssoft.base.devices.devices_driver_lib.interfaces.callback_interface.ScannerListener;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.Scanner;

public class CcbScanner implements Scanner {
    private IScanner scanner;
    private ScannerListener mListener;

    public CcbScanner(IBinder binder) {
        scanner = IScanner.Stub.asInterface(binder);
    }

    @Override
    public void startScan(int timeout, ScannerListener listener) {
        try {
            mListener = listener;
            Bundle bundle = new Bundle();
            bundle.putLong("timeout", timeout);
            bundle.putString("title", "请对准二维码或条码进行扫描");
            scanner.startScan(bundle,new OnScanListener.Stub() {
                @Override
                public void onSuccess(Bundle bundle) throws RemoteException {
                    mListener.onScanResult(bundle.getString("barcode"));
                }

                @Override
                public void onError(int error, String message){
                    if(mListener!=null)
                        mListener.onError(""+error, message);
                }

                @Override
                public void onTimeout()  {
                    if(mListener!=null)
                        mListener.onError("X2", "扫码超时");
                }

                @Override
                public void onCancel()  {
                    if(mListener!=null)
                        mListener.onError("X1", "取消扫描");
                }
            });
        } catch (RemoteException e) {
            mListener.onError("X3", "扫码异常");
        }
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
