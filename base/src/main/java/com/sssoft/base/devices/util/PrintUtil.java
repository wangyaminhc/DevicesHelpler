package com.sssoft.base.devices.util;

import android.os.Bundle;

import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.IPrinter;


public class PrintUtil {
    public static void doPrint(String text, IPrinter printer) throws Exception{
        int length = 0;
        int length2 = 0;
        Bundle formatNL = new Bundle();
        if(text.contains("<center>")){
            formatNL.putString("align", "center");
            length = length + 8;
            length2 = length2 + 1;
        }else if(text.contains("<left>")){
            formatNL.putString("align", "left");
            length = length + 6;
            length2 = length2 + 1;
        }else if(text.contains("<right>")){
            formatNL.putString("align", "right");
            length = length + 7;
            length2 = length2 + 1;
        }else{
            formatNL.putString("align", "left");
        }

        if(text.contains("<normal>")){
            formatNL.putString("font", "normal");
            length = length + 8;
            length2 = length2 + 1;
        }else if(text.contains("<small>")){
            formatNL.putString("font", "small");
            length = length + 7;
            length2 = length2 + 1;
        }else if(text.contains("<large>") ){
            formatNL.putString("font", "large");
            length = length + 7;
            length2 = length2 + 1;
        }else{
            formatNL.putString("font", "normal");
        }
        if(text.contains("<barCode>")){
            length = length + 9;
            length2 = length2 + 1;
            text = formateText(text, length,length2);
            printer.addBarCode(formatNL, text);
        }else if(text.contains("<qrCode>")){
            length = length + 8;
            length2 = length2 + 1;
            text = formateText(text, length,length2);
            printer.addQrCode(formatNL, text);
        }else{
            text = formateText(text, length,length2);
            printer.addText(formatNL, text);
        }
    }


    public static String formateText(String text,Integer length,Integer length2){
        if(text == null || text.length()<length*2+length2 || length==0){
            return text==null?"":text;
        }else{
            return text.substring(length, text.length()-length-length2);
        }
    }

}
