package com.sssoft.base.devices.devices_driver_lib.device_control_imp.ccb;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import com.ccb.deviceservice.aidl.printer.OnPrintListener;
import com.sssoft.base.devices.devices_driver_lib.interfaces.callback_interface.PrinterListener;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.IPrinter;

import java.io.ByteArrayOutputStream;

import static com.sssoft.base.devices.bean.ConstantBean.ALIGN;
import static com.sssoft.base.devices.bean.ConstantBean.ALIGN_CENTER;
import static com.sssoft.base.devices.bean.ConstantBean.ALIGN_LEFT;
import static com.sssoft.base.devices.bean.ConstantBean.ALIGN_MAP;
import static com.sssoft.base.devices.bean.ConstantBean.FONT;
import static com.sssoft.base.devices.bean.ConstantBean.FONT_MAP;
import static com.sssoft.base.devices.bean.ConstantBean.FONT_NORMAL;
import static com.sssoft.base.devices.bean.ConstantBean.PRINT_HEIGHT;
import static com.sssoft.base.devices.bean.ConstantBean.PRINT_OFFSET;
import static com.sssoft.base.devices.bean.ConstantBean.PRINT_WIDTH;

public class CcbPrinter implements IPrinter {
    private com.ccb.deviceservice.aidl.printer.IPrinter iPrinter;

    CcbPrinter(IBinder binder){
        iPrinter = com.ccb.deviceservice.aidl.printer.IPrinter.Stub.asInterface(binder);
        try {
            iPrinter.setGray(8);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
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
        Bundle formateTmp = setFormat(format);
        String align = format.getString(ALIGN,ALIGN_LEFT);
        int height = format.getInt(PRINT_HEIGHT,64);
        if(align.equals(ALIGN_LEFT)){
            formateTmp.putInt(PRINT_OFFSET, 10);
        }else if(align.equals(ALIGN_CENTER)){
            formateTmp.putInt(PRINT_OFFSET, 100);
        }else{
            formateTmp.putInt(PRINT_OFFSET, 160);
        }
        format.putInt(PRINT_WIDTH, 296);
        formateTmp.putInt(PRINT_HEIGHT, height);
        iPrinter.addBarCode(formateTmp, barCode);
    }

    @Override
    public void addQrCode(Bundle format, String qrCode) throws RemoteException {
        Bundle formatTmp = new Bundle();
        String align = format.getString(ALIGN,ALIGN_LEFT);
        int height = format.getInt(PRINT_HEIGHT,200);
        if(align.equals(ALIGN_LEFT)){
            formatTmp.putInt(PRINT_OFFSET, 10);
        }else if(align.equals(ALIGN_CENTER)){
            formatTmp.putInt(PRINT_OFFSET, 100);
        }else{
            formatTmp.putInt(PRINT_OFFSET, 160);
        }
        formatTmp.putInt("expectedHeight", height);
        iPrinter.addQrCode(formatTmp, qrCode);
    }

    @Override
    public void paperSkip(int line) throws RemoteException {
        iPrinter.feedLine(line);
    }

    @Override
    public void startPrinter(final PrinterListener printerLis) throws RemoteException {
        iPrinter.startPrint(new OnPrintListener.Stub() {
            @Override
            public void onFinish() {
                printerLis.onFinish();
            }

            @Override
            public void onError(int error){
                printerLis.onError(error+"", "打印错误:" + PrintErrorCodeCCB.ERR_MAP.get(error));
            }
        });
    }

    @Override
    public String getStatus() throws RemoteException {
        String rc = "";
        int status = iPrinter.getStatus();
        return rc;
    }
}
