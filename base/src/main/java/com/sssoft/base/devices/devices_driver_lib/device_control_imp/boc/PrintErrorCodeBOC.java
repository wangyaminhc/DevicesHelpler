package com.sssoft.base.devices.devices_driver_lib.device_control_imp.boc;

import java.util.HashMap;

public class PrintErrorCodeBOC {
    public static HashMap<Integer, String> ERR_MAP = new HashMap<Integer, String>();
    public static final int ERROR_NONE = 0x00;
    private static final int ERROR_DECODE = 0x01;
    private static final int ERROR_PAPERENDED = 0x02;
    private static final int ERROR_OVERHEAT = 0x03;
    private static final int ERROR_BUSY = 0x04;
    private static final int ERROR_HARDERR = 0x05;
    private static final int ERROR_PAPERJAM = 0x06;
    private static final int ERROR_OTHER = 0xff;

    static{
        ERR_MAP.put(ERROR_NONE, "状态正常");
        ERR_MAP.put(ERROR_DECODE,"打印数据解析错误" );
        ERR_MAP.put(ERROR_PAPERENDED,"缺纸,不能打印" );
        ERR_MAP.put(ERROR_OVERHEAT, "打印头过热");
        ERR_MAP.put(ERROR_BUSY, "打印机处于忙状态");
        ERR_MAP.put(ERROR_HARDERR, "硬件错误");
        ERR_MAP.put(ERROR_PAPERJAM, "卡纸");
        ERR_MAP.put(ERROR_OTHER, "其他错误");

    }
    
    
}
