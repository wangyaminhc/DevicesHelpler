package com.sssoft.base.devices.devices_driver_lib.device_control_imp.icbc;

import android.os.Bundle;
import android.os.RemoteException;

import com.icbc.smartpos.deviceservice.aidl.IDeviceService;
import com.icbc.smartpos.deviceservice.aidl.IScanner;
import com.sssoft.base.devices.bean.ScanTypeEnum;
import com.sssoft.base.devices.devices_driver_lib.interfaces.callback_interface.ScannerListener;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.Scanner;

public class IcbcScanner implements Scanner {
    IScanner scanner = null;
    private ScannerListener scannerlistener;
    private IDeviceService deviceService;
    private ScanTypeEnum scanType;

    public IcbcScanner(IDeviceService deviceService, ScanTypeEnum scanType) {
//        scanner = IScanner.Stub.asInterface(binder);
        this.deviceService = deviceService;
        this.scanType = scanType;
    }

    @Override
    public void startScan( int timeout, ScannerListener listener) throws RemoteException {
            scannerlistener = listener;
            Bundle param = new Bundle();
            param.putString("upPromptString", "将二维码放入框内, 即可自动扫描");
            if(scanner == null){
                //0-前置扫码器，1-后置扫码器
                scanner = IScanner.Stub.asInterface(deviceService.getScanner( openCamera(scanType)));
            }
            scanner.startScan(param, timeout, new com.icbc.smartpos.deviceservice.aidl.ScannerListener.Stub() {

                @Override
                public void onTimeout() {
                    scannerlistener.onFinish();
                }

                @Override
                public void onSuccess(String barcode)  {
                    scannerlistener.onScanResult(barcode);
                    //scannerlistener.onFinish();
                }

                @Override
                public void onError(int error, String message){
                    scannerlistener.onError(error+"", "扫描失败:"+message);
                }

                @Override
                public void onCancel() {
                    if(scannerlistener!=null)
                        scannerlistener.onError("X1", "取消扫描");

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
        int scanType =  ScanType.BACK;

        switch (cameraId){
            case SCANF_RONT:
                scanType =ScanType.FRONT;
                break;
            case SCAN_BACK:
                scanType = ScanType.BACK;
                break;
        }
        return scanType;
    }

    public static class ScanType {
        /**
         * 前置扫码
         */
        public static final int FRONT = 0;
        /**
         * 后置扫码
         */
        public static final int BACK = 1;
        /**
         *扫描枪扫码
         */
        public static final int GUN= 2;

    }

}
