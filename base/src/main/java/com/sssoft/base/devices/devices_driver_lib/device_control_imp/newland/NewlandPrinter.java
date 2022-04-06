package com.sssoft.base.devices.devices_driver_lib.device_control_imp.newland;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.RemoteException;

import com.newland.mtype.Device;
import com.newland.mtype.ModuleType;
import com.newland.mtype.module.common.printer.PrintContext;
import com.newland.mtype.module.common.printer.Printer;
import com.newland.mtype.module.common.printer.PrinterResult;
import com.newland.mtype.module.common.printer.PrinterStatus;
import com.sssoft.base.devices.devices_driver_lib.interfaces.callback_interface.PrinterListener;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.IPrinter;
import com.sssoft.base.devices.util.Arith;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.sssoft.base.devices.bean.ConstantBean.ALIGN;
import static com.sssoft.base.devices.bean.ConstantBean.ALIGN_CENTER;
import static com.sssoft.base.devices.bean.ConstantBean.ALIGN_RIGHT;
import static com.sssoft.base.devices.bean.ConstantBean.FONT;
import static com.sssoft.base.devices.bean.ConstantBean.FONT_LARGE;
import static com.sssoft.base.devices.bean.ConstantBean.FONT_SMALL;
import static com.sssoft.base.devices.bean.ConstantBean.PRINT_HEIGHT;
import static com.sssoft.base.devices.bean.ConstantBean.PRINT_WIDTH;

public class NewlandPrinter implements IPrinter {

    private Device deviceLocal;
    private Printer printerModule;
    private PrinterResult printerResult;
    private String err = "";
    private static String scriptStr = "";
    private static Map<String,Bitmap> map=new HashMap<String, Bitmap>();

    public NewlandPrinter(Device device) {
        deviceLocal = device;
        printerModule=(Printer)deviceLocal.getStandardModule(ModuleType.COMMON_PRINTER);
        printerModule.init();
        printerModule.setLineSpace(2);
    }

    @Override
    public void addText(Bundle format, String text) throws RemoteException {
        setFontType(format);
        text = setAlgin(format, "text")+text+"\n";
        scriptStr = scriptStr + text;
        //printerResult = printerModule.print(text, 30, TimeUnit.SECONDS);
    }

    @Override
    public void addPicture(Bundle format, Bitmap bitmap) throws RemoteException {
        String fontTmp = "";
        String font = format.getString(FONT,"");
        double wb = Arith.div(bitmap.getWidth(), 400);
        if(font.equals(FONT_SMALL)){
            fontTmp = (int)(Arith.div(Arith.div(bitmap.getWidth(), wb), 4))+"*"+(int)(Arith.div(Arith.div(bitmap.getHeight(), wb), 4));
        }else if(font.equals(FONT_LARGE)){
            fontTmp = (int)(Arith.div(bitmap.getWidth(), wb))+"*"+(int)(Arith.div(bitmap.getHeight(), wb));
        }else{
            fontTmp = (int)(Arith.div(Arith.div(bitmap.getWidth(), wb), 2))+"*"+(int)(Arith.div(Arith.div(bitmap.getHeight(), wb), 2));
        }
        map.put("logo", bitmap);
        scriptStr = scriptStr + setAlgin(format, "image")+fontTmp+" path:logo\n";
    }

    @Override
    public void addBarCode(Bundle format, String barCode) throws RemoteException {
        int height = 64;
        int width = 2;
        if( format != null){
             height =  format.getInt(PRINT_HEIGHT,64);
             width =  format.getInt(PRINT_WIDTH,2);
        }

        barCode = "!barcode "+width+" "+height+"\n "+setAlgin(format, "barcode")+barCode+"\n";

        scriptStr = scriptStr + barCode;
    }

    @Override
    public void addQrCode(Bundle format, String qrCode) throws RemoteException {
        int height = 100;
        if( format!= null){
            height =  format.getInt(PRINT_HEIGHT,100);
        }
        qrCode = "!qrcode "+height+" 2\n "+setAlgin(format, "qrcode")+qrCode+"\n";
//		try {
//
//			printerResult = printerModule.printByScript(PrintContext.defaultContext(), qrCode.getBytes("GBK"), 60, TimeUnit.SECONDS);
//		} catch (UnsupportedEncodingException e) {
//			err = "print err:"+e.getLocalizedMessage();
//		}
        scriptStr = scriptStr + qrCode;
    }

    @Override
    public void paperSkip(int line) throws RemoteException {
        Bundle format = new Bundle();
        format.putString(FONT, FONT_LARGE);
        format.putString(ALIGN, ALIGN_CENTER);
        setFontType(format);
        String kg = "           ";
        String text = "";
        for (int i = 0; i < line; i++) {
            text =text + setAlgin(format, "text")+kg+"\n";
        }
        scriptStr = scriptStr + text;
    }



    @Override
    public void startPrinter(PrinterListener printerLis) throws RemoteException {
        if(!scriptStr.equals("")){
            try {
                printerResult = printerModule.printByScript(PrintContext.defaultContext(),scriptStr.getBytes("GBK"),map, 60, TimeUnit.SECONDS);
            } catch (UnsupportedEncodingException e) {
                printerLis.onError("XX", e.getLocalizedMessage());
            }
        }
        scriptStr = "";
        if(PrinterResult.SUCCESS.equals(printerResult)){
            printerLis.onFinish();
        }else{
            printerLis.onError("XX", printerResult.toString());
        }
    }

    @Override
    public String getStatus() throws RemoteException {
        PrinterStatus status = printerModule.getStatus();
        return status.toString();
    }

    public void setFontType(Bundle format){
        String font = "";
        if( format != null){
            font = format.getString(FONT,"");
        }
        if(font.equals(FONT_SMALL)){
            scriptStr = scriptStr + "!hz sn\n !asc sn\n !gray 5\n!yspace 20\n";
        }else if(font.equals(FONT_LARGE)){
            scriptStr = scriptStr + "!hz l\n !asc l\n !gray 10\n";
        }else{
            scriptStr = scriptStr + "!hz n\n !asc n !gray 1\n!yspace 6\n";
        }
    }
    public String setAlgin(Bundle format,String type){

        String align = "";
        if( format != null){
             align = format.getString(ALIGN,"");
        }
        String alginStr = "";
        if(align.equals(ALIGN_CENTER)){
            alginStr = "*"+type+" c ";
        }else if(align.equals(ALIGN_RIGHT)){
            alginStr = "*"+type+" r ";
        }else{
            alginStr = "*"+type+" l ";
        }
        return alginStr;
    }
}
