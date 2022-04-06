package com.sssoft.base.devices.devices_driver_lib.interfaces.callback_interface;

public interface CardListener {
    void onReadResult( String[] cardInfo);
    void onError(String code,String detail);
    void onFinish();

}
