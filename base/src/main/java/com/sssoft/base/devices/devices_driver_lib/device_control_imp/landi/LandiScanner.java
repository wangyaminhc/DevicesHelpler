package com.sssoft.base.devices.devices_driver_lib.device_control_imp.landi;

import android.app.Activity;
import android.content.Context;
import android.os.RemoteException;
import android.util.Log;

import com.landicorp.android.scan.scanDecoder.ScanDecoder;
import com.sssoft.base.devices.bean.ScanTypeEnum;
import com.sssoft.base.devices.devices_driver_lib.interfaces.callback_interface.ScannerListener;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.Scanner;

public class LandiScanner implements Scanner {
    private ScanDecoder scanDecoder;
    private ScannerListener mListener;
    private Context mContext;
    private ScanTypeEnum scanType;
    private static final String TAG = "ScannerImpl";
    private ScanDecoder.ResultCallback callback = new ScanDecoder.ResultCallback() {
        @Override
        public void onResult(String s) {
            mListener.onScanResult(s);
            close();
        }

        @Override
        public void onCancel() {
            mListener.onError("X0", "取消扫描");
            close();
        }

        @Override
        public void onTimeout() {
            mListener.onError("X0", "扫描超时");
            close();
        }
    };

    public LandiScanner(Context mContext, ScanTypeEnum scanType) {
        this.mContext = mContext;
        scanDecoder = new ScanDecoder(mContext);
        this.scanType = scanType;
    }


    public void close() {
        scanDecoder.Destroy();
    }



    private int openCamera(ScanTypeEnum cameraId) {
        int type = ScanDecoder.CAMERA_ID_BACK;
        switch (cameraId){
            case SCANF_RONT:
                type =ScanDecoder.CAMERA_ID_FRONT;
                break;
            case SCAN_BACK:
                type = ScanDecoder.CAMERA_ID_BACK;
                break;
        }

        return scanDecoder.Create(type, callback);
    }

    private String getDescribe(int error) {
        switch (error) {
            case CameraScannerError.INIT_DECODER_FAIL:
                return "init decoder failed";
            case CameraScannerError.HAS_CREATED:
                return "it created before";
            case CameraScannerError.OPEN_CAMERA_FAIL:
                return "open camera failed";
            case CameraScannerError.LICENSE_FAIL:
                return "license certified failed";
            case CameraScannerError.NOT_FOUND_DECODRE:
                return "not found decoder";
            default:
                return "unknown error";
        }
    }


    @Override
    public void startScan( int timeout, ScannerListener listener) throws RemoteException {
        mListener = listener;
        int ret = openCamera(scanType);
        if (ret != CameraScannerError.SUCCESS) {
        } else {
            ret = scanDecoder.startScanDecode((Activity)mContext, null);
            if (ret != CameraScannerError.SUCCESS) {
                String errorDes = getDescribe(ret);
                Log.i("scan err:",errorDes);
            }
        }
    }

    @Override
    public void stopScan() {
            close();
    }
}
