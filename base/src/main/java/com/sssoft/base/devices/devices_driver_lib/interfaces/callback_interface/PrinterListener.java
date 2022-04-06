package com.sssoft.base.devices.devices_driver_lib.interfaces.callback_interface;

public interface PrinterListener {
    /**
     * 打印成功回调
     */
    void onFinish();

    /**
     * 打印失败回调
     * @param error - 错误码
     */
    void onError(String code,String detail);
}
