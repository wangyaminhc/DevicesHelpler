package com.sssoft.base.devices.devices_driver_lib.device_control_imp.landi;

public class CameraScannerError {
    /** 成功 */
    public static int SUCCESS = 0x00;
    /** 其他异常 */
    public static final int FAIL = 0xFF;
    public static final int INIT_DECODER_FAIL = 0x01;
    public static final int HAS_CREATED = 0x02;
    public static final int OPEN_CAMERA_FAIL = 0x03;
    public static final int LICENSE_FAIL = 0x04;
    public static final int NOT_FOUND_DECODRE = 0x05;
}
