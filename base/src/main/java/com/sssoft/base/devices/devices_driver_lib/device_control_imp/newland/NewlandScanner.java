package com.sssoft.base.devices.devices_driver_lib.device_control_imp.newland;

import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;

import com.newland.mtype.Device;
import com.newland.mtype.ModuleType;
import com.newland.mtype.module.common.scanner.BarcodeScanner;
import com.newland.mtype.module.common.scanner.BarcodeScannerManager;
import com.sssoft.base.devices.bean.ScanTypeEnum;
import com.sssoft.base.devices.devices_driver_lib.interfaces.callback_interface.ScannerListener;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.Scanner;
import com.sssoft.base.devices.view.ScanViewActivity;

public class NewlandScanner implements Scanner {
    public static BarcodeScanner scanner = null;
    public static ScannerListener listener = null;
    private Device device;
    private Context mContext;

    public NewlandScanner(Device device, Context mContext, ScanTypeEnum scanTypeEnum) {
        this.device = device;
        this.mContext = mContext;
        BarcodeScannerManager barcodeScannerManager=(BarcodeScannerManager)device.getStandardModule(ModuleType.COMMON_BARCODESCANNER);
        scanner = barcodeScannerManager.getDefault();
    }

    @Override
    public void startScan( int timeout, ScannerListener listener) throws RemoteException {
        NewlandScanner.listener = listener;
        Intent intent = new Intent(mContext, ScanViewActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("timeout", timeout);
        mContext.startActivity(intent);
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
