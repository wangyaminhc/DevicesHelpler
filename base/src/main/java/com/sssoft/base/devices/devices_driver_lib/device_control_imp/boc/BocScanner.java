package com.sssoft.base.devices.devices_driver_lib.device_control_imp.boc;

import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import com.boc.aidl.constant.Const;
import com.boc.aidl.scanner.AidlScanner;
import com.boc.aidl.scanner.AidlScannerListener;
import com.sssoft.base.devices.bean.ScanTypeEnum;
import com.sssoft.base.devices.devices_driver_lib.interfaces.callback_interface.ScannerListener;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.Scanner;

public class BocScanner implements Scanner {
    AidlScanner scanner = null;
    private ScannerListener mListener;
    private ScanTypeEnum typeEnum;

    public BocScanner(IBinder binder, ScanTypeEnum typeEnum) {
        scanner = AidlScanner.Stub.asInterface(binder);
        this.typeEnum = typeEnum;
    }

    @Override
    public void startScan( int timeout, ScannerListener listener) throws RemoteException {
        mListener = listener;
        Bundle bundle = new Bundle();
        bundle.putLong("timeout", timeout);
        bundle.putString("title", "请对准二维码或条码进行扫描");

        scanner.startScan( openCamera(typeEnum),timeout*1000, new AidlScannerListener.Stub() {

            @Override
            public void onScanResult(String[] strings)  {
                mListener.onScanResult(strings[0]);
            }

            @Override
            public void onFinish()  {
                mListener.onError("00","扫码结束");
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

    private int openCamera(ScanTypeEnum cameraId) {
        int scanType = Const.ScanType.BACK;
        switch (cameraId){
            case SCANF_RONT:
                scanType = Const.ScanType.FRONT;
                break;
            case SCAN_BACK:
                scanType = Const.ScanType.BACK;
                break;
        }

        return scanType;
    }

}
