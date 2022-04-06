package com.sssoft.base.devices.devices_driver_lib.device_control_imp.ums;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.RemoteException;

import com.sssoft.base.devices.devices_driver_lib.interfaces.callback_interface.PrinterListener;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.IPrinter;
import com.sssoft.base.devices.util.BarCodeUtil;
import com.sssoft.base.devices.util.DriverUtil;
import com.sssoft.base.devices.util.QRCodeUtil;
import com.ums.upos.sdk.exception.CallServiceException;
import com.ums.upos.sdk.exception.SdkException;
import com.ums.upos.sdk.printer.FeedCount;
import com.ums.upos.sdk.printer.FontConfig;
import com.ums.upos.sdk.printer.FontSizeEnum;
import com.ums.upos.sdk.printer.OnPrintResultListener;
import com.ums.upos.sdk.printer.PrinterManager;

import static com.sssoft.base.devices.bean.ConstantBean.ALIGN;
import static com.sssoft.base.devices.bean.ConstantBean.ALIGN_CENTER;
import static com.sssoft.base.devices.bean.ConstantBean.ALIGN_LEFT;
import static com.sssoft.base.devices.bean.ConstantBean.ALIGN_MAP;
import static com.sssoft.base.devices.bean.ConstantBean.FONT;
import static com.sssoft.base.devices.bean.ConstantBean.FONT_LARGE;
import static com.sssoft.base.devices.bean.ConstantBean.FONT_MAP;
import static com.sssoft.base.devices.bean.ConstantBean.FONT_NORMAL;
import static com.sssoft.base.devices.bean.ConstantBean.FONT_SMALL;
import static com.sssoft.base.devices.bean.ConstantBean.PRINT_OFFSET;

public class UmsPrinter implements IPrinter {
    private PrinterManager iPrinter;
    private PrinterListener printerListener;
    public UmsPrinter() {
        iPrinter = new PrinterManager();
        try {
            iPrinter.initPrinter();
        } catch (Exception e) {

        }
 
    }

    @Override
    public void addText(Bundle format, String text) throws RemoteException {
        try {
            genPrintJsonMsg(text);
        } catch (CallServiceException e) {
            e.printStackTrace();
        } catch (SdkException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addPicture(Bundle format, Bitmap bitmap) throws RemoteException {
        try {
            iPrinter.setBitmap(bitmap);
        } catch (SdkException e) {
            e.printStackTrace();
        } catch (CallServiceException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addBarCode(Bundle format, String barCode) throws RemoteException {
        Bundle formateTmp = setFormat(format);
        String align = format.getString(ALIGN,ALIGN_LEFT);
        if(align.equals(ALIGN_LEFT)){
            formateTmp.putInt(PRINT_OFFSET, 10);
        }else if(align.equals(ALIGN_CENTER)){
            formateTmp.putInt(PRINT_OFFSET, 100);
        }else{
            formateTmp.putInt(PRINT_OFFSET, 160);
        }
        int height = getBarSize(formateTmp.getString(FONT));
        try {
            iPrinter.setBitmap(BarCodeUtil.creatBarCode(barCode, 150, height, null));
        } catch (SdkException e) {
            e.printStackTrace();
        } catch (CallServiceException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void addQrCode(Bundle format, String qrCode) throws RemoteException {
        Bundle formatTmp = new Bundle();
        String align = format.getString(ALIGN,ALIGN_LEFT);
        if(align.equals(ALIGN_LEFT)){
            formatTmp.putInt(PRINT_OFFSET, 10);
        }else if(align.equals(ALIGN_CENTER)){
            formatTmp.putInt(PRINT_OFFSET, 100);
        }else{
            formatTmp.putInt(PRINT_OFFSET, 160);
        }
        int size = getQrSize(formatTmp.getString(FONT));
        try {
            iPrinter.setBitmap(QRCodeUtil.createQRBmp(qrCode, size, size, null));
        } catch (SdkException e) {
            e.printStackTrace();
        } catch (CallServiceException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void paperSkip(int line) throws RemoteException {
        FeedCount count = new FeedCount();
        count.setNum(line);
        try {
            iPrinter.feedPaper(count);
        } catch (SdkException e) {
            e.printStackTrace();
        } catch (CallServiceException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startPrinter(PrinterListener printerLis) throws RemoteException {
        printerListener = printerLis;
        try {
            iPrinter.startPrint(new OnPrintResultListener() {
                @Override
                public void onPrintResult(int i) {
                    DriverUtil.Log(DriverUtil.LogLevel.Error,"startPrinter", "onPrintResult=" + i);
                        printerListener.onFinish();
                }
            });
        } catch (SdkException e) {
            e.printStackTrace();
        } catch (CallServiceException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getStatus() throws RemoteException {
        String rc = "";
        int status = 0;
        try {
            status = iPrinter.getStatus();
        } catch (SdkException e) {
            e.printStackTrace();
        } catch (CallServiceException e) {
            e.printStackTrace();
        }
  		/*if(status ==0 ){
  			rc = "00";
  		}else if(status ==240){
  			rc = "02";
  		}else if(status ==243){
  			rc = "03";
  		}else if(status ==247){
  			rc = "04";
  		}else{
  			rc = "05";
  		}*/
        return status+"";
    }

    public Bundle setFormat(Bundle format){
        String align = format.getString(ALIGN,ALIGN_LEFT);
        String font = format.getString(FONT,FONT_NORMAL);
        Bundle formatTmp = new Bundle();
        formatTmp.putInt(ALIGN, ALIGN_MAP.get(align));
        formatTmp.putInt(FONT, FONT_MAP.get(font));
        return formatTmp;
    }

    public String genPrintJsonMsg(String textAll) throws RemoteException, CallServiceException, SdkException {
        //Log.e("doPrintJsonMsg", "textAll=" + textAll);
        String[] prints = textAll.replace("\n", "").replaceAll("\t", "").replaceAll("\r", "").split("<br>");
        FontConfig config = null;
        for (int i = 0; i < prints.length; i++) {
            config = new FontConfig();
            String text = prints[i];
            int length = 0;
            int length2 = 0;
            Bundle formatNL = new Bundle();
            //位置
            if (text.contains("<center>")) {
                formatNL.putString(ALIGN, ALIGN_CENTER);
                length = length + 8;
                length2 = length2 + 1;
            } else if (text.contains("<left>")) {
                formatNL.putString(ALIGN, ALIGN_LEFT);
                length = length + 6;
                length2 = length2 + 1;
            } else if (text.contains("<right>")) {
                formatNL.putString(ALIGN, "right");
                length = length + 7;
                length2 = length2 + 1;
            } else {
                formatNL.putString(ALIGN, ALIGN_LEFT);
            }
            //字体大小
            if (text.contains("<normal>")) {
                formatNL.putString(FONT, FONT_NORMAL);
                config.setSize(FontSizeEnum.MIDDLE);
                length = length + 8;
                length2 = length2 + 1;
            } else if (text.contains("<small>")) {
                formatNL.putString(FONT, FONT_SMALL);
                config.setSize(FontSizeEnum.SMALL);
                length = length + 7;
                length2 = length2 + 1;
            } else if (text.contains("<large>")) {
                formatNL.putString(FONT, FONT_LARGE);
                config.setSize(FontSizeEnum.BIG);
                length = length + 7;
                length2 = length2 + 1;
            } else {
                formatNL.putString(FONT, FONT_NORMAL);
                config.setSize(FontSizeEnum.MIDDLE);
            }
            if (text.contains("<barCode>")) {//条形码
                length = length + 9;
                length2 = length2 + 1;
                text = formateText(text, length, length2);
                //Log.e("doPrintJsonMsg", "jsonBarCode.text=" +  text);
                Bitmap barCode = BarCodeUtil.creatBarCode(text, getQrSize(formatNL.getString(FONT)), getBarSize(formatNL.getString(FONT)), null);
                iPrinter.setBitmap(barCode);
            } else if (text.contains("<qrCode>")) {//二维码
                length = length + 8;
                length2 = length2 + 1;
                text = formateText(text, length, length2);
                //Log.e("doPrintJsonMsg", "jsonQrCode.text=" + text);
                Bitmap qrCode = QRCodeUtil.createQRBmp(text, getQrSize(formatNL.getString(FONT)), getQrSize(formatNL.getString(FONT)), null);
                iPrinter.setBitmap(qrCode);
            } else {
                text = formateText(text, length, length2);
                iPrinter.setPrnText(text, config);
            }
        }

        return "";
    }

    public String formateText(String text,Integer length,Integer length2){
        if(text == null || text.length()<length*2+length2 || length==0){
            return text==null?"":text;
        }else{
            return text.substring(length, text.length()-length-length2);
        }
    }

    private static int getQrSize(String sizeStr) {
        int sizeInt = 1;
        switch (sizeStr) {
            case FONT_SMALL:
                sizeInt = 200;
                break;
            case FONT_NORMAL:
                sizeInt = 300;
                break;
            case FONT_LARGE:
                sizeInt = 400;
                break;
            default:
                sizeInt = 300;
                break;
        }
        return sizeInt;
    }

    private static int getBarSize(String sizeStr) {
        int sizeInt = 1;
        switch (sizeStr) {
            case FONT_SMALL:
                sizeInt = 50;
                break;
            case FONT_NORMAL:
                sizeInt = 70;
                break;
            case FONT_LARGE:
                sizeInt = 90;
                break;
            default:
                sizeInt = 70;
                break;
        }
        return sizeInt;
    }
}
