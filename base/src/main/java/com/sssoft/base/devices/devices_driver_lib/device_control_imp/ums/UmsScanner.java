package com.sssoft.base.devices.devices_driver_lib.device_control_imp.ums;

import android.os.Bundle;
import android.os.RemoteException;

import com.sssoft.base.devices.bean.ScanTypeEnum;
import com.sssoft.base.devices.devices_driver_lib.interfaces.callback_interface.ScannerListener;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.Scanner;
import com.sssoft.base.devices.util.DriverUtil;
import com.ums.upos.sdk.scanner.OnScanListener;
import com.ums.upos.sdk.scanner.ScannerConfig;
import com.ums.upos.sdk.scanner.ScannerManager;

public class UmsScanner implements Scanner {
    private ScannerListener mListener;
    private ScannerManager scanner;
    private ScanTypeEnum scanTypeEnum;

    public UmsScanner(ScanTypeEnum scanTyp) {
        scanner = new ScannerManager();
        scanTypeEnum = scanTyp;
    }

    @Override
    public void startScan( int timeout, ScannerListener listener) throws RemoteException {
        int scanType = 1;
        try {
            mListener = listener;
            Bundle bundle = new Bundle();
            switch (scanTypeEnum){
                case SCAN_BACK:
                    scanType = 1;
                    break;
                case SCANF_RONT:
                    scanType = 0;
                    break;
            }
            bundle.putInt(ScannerConfig.COMM_SCANNER_TYPE, scanType);//后置扫码1；前置扫码0
            bundle.putBoolean(ScannerConfig.COMM_ISCONTINUOUS_SCAN, false);
            scanner.stopScan();
            scanner.initScanner(bundle);
            scanner.startScan(timeout * 1000, new OnScanListener() {
                @Override
                public void onScanResult(int i, byte[] bytes) {
                    //防止用户未扫描直接返回，导致bytes为空
                        if (bytes != null && !bytes.equals("")) {
                            DriverUtil.Log(DriverUtil.LogLevel.Error,"startScan", "code=" + new String(bytes));
                            mListener.onScanResult(new String(bytes));
                        } else {
                            mListener.onError("XX", "扫码错误");
                        }
                }
            });
        } catch (Exception e) {
            DriverUtil.Log(DriverUtil.LogLevel.Info,"startScan", "arg5:");
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
