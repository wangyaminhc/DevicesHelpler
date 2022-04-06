package com.sssoft.base.devices.devices_driver_lib.interfaces.callback_interface;

public interface ScannerListener {
    void onScanResult(String barcode);
    void onError(String code,String detail);
    void onFinish();
}
