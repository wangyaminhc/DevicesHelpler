package com.sssoft.base.devices.bean;

import java.util.HashMap;

public class ConstantBean {
    //打印相关的统一处理
    public static  HashMap<String, Integer> ALIGN_MAP = new HashMap<String, Integer>();
    public static HashMap<String, Integer> FONT_MAP = new HashMap<String, Integer>();


    private static final int FONT_MAP_SMALL = 0;
    private static final int FONT_MAP_NORMAL = 1;
    private static final int FONT_MAP_LARGE = 2;

    private static final int ALIGN_MAP_LEFT = 0;
    private static final int ALIGN_MAP_CENTER = 1;
    private static final int ALIGN_MAP_RIGHT = 2;

    public class ScanHandlerCode{
        public static final int SUCCESS = 10;
        public static final int ERROR = 11;
        public static final int FINISH = 12;
        public static final int EXCEPTION = 13;
    }

    //自定义的返回代码
    public class PrintResultCode{
        public static final int PRINT_OK =  0;
        public static final int PRINT_NEXT =  1;
        public static final int PRINT_ERR =  2;

    }

    //对齐方式
    /**
     *@author wangyamin
     *@time 创建时间 2020/11/30 10:24
     * @Description 对齐方式
     *
     */
    public static final String  ALIGN = "align";

    public static final String  ALIGN_LEFT = "left";
    public static final String  ALIGN_RIGHT = "right";
    public static final String  ALIGN_CENTER = "center";

    //大小
    /**
     *@author wangyamin
     *@time 创建时间 2020/11/30 10:24
     * @Description 字体大小
     *
     */
    public static final String  FONT = "font";

    public static final String  FONT_SMALL = "small";
    public static final String  FONT_NORMAL = "normal";
    public static final String  FONT_LARGE = "large";

    public static final String  PRINT_HEIGHT = "height";
    public static final String  PRINT_WIDTH = "width";
    public static final String  PRINT_OFFSET = "offset";



    static{
        FONT_MAP.put(FONT_SMALL, FONT_MAP_SMALL);
        FONT_MAP.put(FONT_NORMAL, FONT_MAP_NORMAL);
        FONT_MAP.put(FONT_LARGE, FONT_MAP_LARGE);

        ALIGN_MAP.put(ALIGN_LEFT, ALIGN_MAP_LEFT);
        ALIGN_MAP.put(ALIGN_RIGHT, ALIGN_MAP_RIGHT);
        ALIGN_MAP.put(ALIGN_CENTER, ALIGN_MAP_CENTER);

    }
}
