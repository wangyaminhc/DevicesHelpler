package com.sssoft.base.devices.devices_driver_lib.interfaces.callback_interface;

public interface PinpadListener {
    void onPinpadResult(String pinpadInfo);

    void onError(String code,String detail);

    void onFinish();
}
