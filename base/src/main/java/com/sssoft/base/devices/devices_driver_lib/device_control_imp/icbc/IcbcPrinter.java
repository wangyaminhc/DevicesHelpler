package com.sssoft.base.devices.devices_driver_lib.device_control_imp.icbc;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.sssoft.base.devices.devices_driver_lib.interfaces.callback_interface.PrinterListener;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.IPrinter;

import java.io.ByteArrayOutputStream;

import static com.sssoft.base.devices.bean.ConstantBean.ALIGN;
import static com.sssoft.base.devices.bean.ConstantBean.ALIGN_LEFT;
import static com.sssoft.base.devices.bean.ConstantBean.ALIGN_MAP;
import static com.sssoft.base.devices.bean.ConstantBean.ALIGN_RIGHT;
import static com.sssoft.base.devices.bean.ConstantBean.FONT;
import static com.sssoft.base.devices.bean.ConstantBean.FONT_LARGE;
import static com.sssoft.base.devices.bean.ConstantBean.FONT_MAP;
import static com.sssoft.base.devices.bean.ConstantBean.FONT_NORMAL;
import static com.sssoft.base.devices.bean.ConstantBean.FONT_SMALL;
import static com.sssoft.base.devices.bean.ConstantBean.PRINT_HEIGHT;
import static com.sssoft.base.devices.bean.ConstantBean.PRINT_WIDTH;

public class IcbcPrinter implements IPrinter {
    private com.icbc.smartpos.deviceservice.aidl.IPrinter iPrinter;
    private PrinterListener printerListener;

    public IcbcPrinter(IBinder binder) {
       iPrinter = com.icbc.smartpos.deviceservice.aidl.IPrinter.Stub.asInterface(binder);
    }

    @Override
    public void addText(Bundle format, String text) throws RemoteException {
        iPrinter.addText(setFormat(format), text);
    }

    @Override
    public void addPicture(Bundle format, Bitmap bitmap) throws RemoteException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] datas = baos.toByteArray();
        iPrinter.addImage(setFormat(format), datas);
    }

    @Override
    public void addBarCode(Bundle format, String barCode) throws RemoteException {
        Bundle formateTmp = new Bundle();
        String align = format.getString(ALIGN,ALIGN_LEFT);

        if(align.equals(ALIGN_LEFT)){
            formateTmp.putInt(ALIGN, ALIGN_MAP.get(align));
        }else if(align.equals(ALIGN_RIGHT)){
            formateTmp.putInt(ALIGN, 2);
        }else{
            formateTmp.putInt(ALIGN,1);
        }

        int width = format.getInt(PRINT_WIDTH,296);
        String font = format.getString(FONT,FONT_NORMAL);
        if(FONT_LARGE.equals(font)) {
            width = 296;
        } else if(FONT_SMALL.equals(font)) {
            width = 196;
        } else {
            width = 296;
        }
        //单条条形码的宽度取值[1,8],默认为2
        formateTmp.putInt(PRINT_WIDTH, width);
        //Log.e("addBarCode", "width=" + width);

        int height = format.getInt(PRINT_HEIGHT,64);
        font = format.getString(FONT,FONT_NORMAL);
        if(FONT_LARGE.equals(font)) {
            height = 84;
        } else if(FONT_SMALL.equals(font)) {
            height = 44;
        } else {
            height = 64;
        }
        formateTmp.putInt(PRINT_HEIGHT, height);
        //Log.e("addBarCode", "height=" + height);

        iPrinter.addBarCode(formateTmp, barCode);
    }

    @Override
    public void addQrCode(Bundle format, String qrCode) throws RemoteException {

        Bundle formatTmp = new Bundle();
        String align = format.getString(ALIGN,ALIGN_LEFT);
        Log.e("addQrCode", "align=" + align);
        if(align.equals(ALIGN_LEFT)){
            formatTmp.putInt(ALIGN, 0);
        }else if(align.equals(ALIGN_RIGHT)){
            formatTmp.putInt(ALIGN, 2);
        }else{
            formatTmp.putInt(ALIGN, 1);
        }

        int height = format.getInt(PRINT_HEIGHT,200);
        String font = format.getString(FONT,FONT_NORMAL);
        if(FONT_LARGE.equals(font)) {
            height = 300;
        } else if(FONT_SMALL.equals(font)) {
            height = 200;
        } else {
            height = 250;
        }
        formatTmp.putInt("expectedHeight", height);
        //Log.e("addQrCode", "expectedHeight=" + height);

        iPrinter.addQrCode(formatTmp, qrCode);
    }

    @Override
    public void paperSkip(int line) throws RemoteException {
        iPrinter.feedLine(line+1);
    }

    @Override
    public void startPrinter(PrinterListener printerLis) throws RemoteException {
        printerListener = printerLis;
        iPrinter.startPrint(new com.icbc.smartpos.deviceservice.aidl.PrinterListener.Stub() {
            @Override
            public void onError(int error) throws RemoteException
            {
                printerListener.onError(error+"", "打印错误:" + PrintErrorCodeICBC.ERR_MAP.get(error));
            }

            @Override
            public void onFinish() throws RemoteException
            {
                printerListener.onFinish();
            }
        });
    }

    @Override
    public String getStatus() throws RemoteException {
        String rc = "";
        int status = iPrinter.getStatus();
        return status+"";
    }

    public Bundle setFormat(Bundle format){
        String align = ALIGN_LEFT;
        String font = FONT_NORMAL;
        if( format != null){
            align = format.getString(ALIGN,ALIGN_LEFT);
            font = format.getString(FONT,FONT_NORMAL);
        }

        Bundle formatTmp = new Bundle();
        formatTmp.putInt(ALIGN, ALIGN_MAP.get(align));
        formatTmp.putInt(FONT, FONT_MAP.get(font));
        return formatTmp;
    }
}
