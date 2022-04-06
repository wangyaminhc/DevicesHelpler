package com.sssoft.base.devices.devices_driver_lib.device_control_imp.abc;

import java.util.HashMap;

public class PrintErrorCodeAbc {
    public static HashMap<Integer, String> ERR_MAP = new HashMap<Integer, String>();
    public static final int ERROR_NONE = 0;
    private static final int ERROR_PAPERENDED = 240;
    private static final int ERROR_HARDERR = 242;
    private static final int ERROR_OVERHEAT = 243;
    private static final int ERROR_BUFOVERFLOW = 245;
    private static final int ERROR_LOWVOL = 225;
    private static final int ERROR_PAPERENDING = 244;
    private static final int ERROR_MOTORERR = 251;
    private static final int ERROR_PENOFOUND = 252;
    private static final int ERROR_PAPERJAM = 238;
    private static final int ERROR_NOBM = 246;
    private static final int ERROR_BUSY = 247;
    private static final int ERROR_BMBLACK = 248;
    private static final int ERROR_WORKON = 230;
    private static final int ERROR_LIFTHEAD = 224;
    private static final int ERROR_CUTPOSITIONERR = 226;
    private static final int ERROR_LOWTEMP = 227;

    static{
        ERR_MAP.put(ERROR_NONE, "状态正常");
        ERR_MAP.put(ERROR_PAPERENDED,"缺纸,不能打印" );
        ERR_MAP.put(ERROR_HARDERR, "硬件错误");
        ERR_MAP.put(ERROR_OVERHEAT, "打印头过热");
        ERR_MAP.put(ERROR_BUFOVERFLOW,"缓冲模式下所操作的位置超出范围");
        ERR_MAP.put(ERROR_LOWVOL,"低压保护" );
        ERR_MAP.put(ERROR_PAPERENDING, "纸张即将用尽，还允许打印");
        ERR_MAP.put(ERROR_MOTORERR, "打印机芯故障");
        ERR_MAP.put(ERROR_PENOFOUND, "自动定位没有找到对齐位置");
        ERR_MAP.put(ERROR_PAPERJAM, "卡纸");
        ERR_MAP.put(ERROR_NOBM, "没有找到黑标");
        ERR_MAP.put(ERROR_BUSY, "打印机处于忙状态");
        ERR_MAP.put(ERROR_BMBLACK, "黑标探测器检测到忙信号");
        ERR_MAP.put(ERROR_WORKON, "打印机电源处于打开状态");
        ERR_MAP.put(ERROR_LIFTHEAD, "打印头抬起");
        ERR_MAP.put(ERROR_CUTPOSITIONERR, "切纸刀不在原位");
        ERR_MAP.put(ERROR_LOWTEMP, "低温保护或AD出错");
    }
    
    
}
