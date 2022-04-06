package com.sssoft.base.devices.devices_driver_lib.device_control_imp.abc;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;


import com.sssoft.base.devices.devices_driver_lib.interfaces.callback_interface.PrinterListener;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.IPrinter;
import com.sssoft.base.devices.util.Arith;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static com.sssoft.base.devices.bean.ConstantBean.ALIGN;
import static com.sssoft.base.devices.bean.ConstantBean.ALIGN_CENTER;
import static com.sssoft.base.devices.bean.ConstantBean.ALIGN_LEFT;
import static com.sssoft.base.devices.bean.ConstantBean.ALIGN_MAP;
import static com.sssoft.base.devices.bean.ConstantBean.ALIGN_RIGHT;
import static com.sssoft.base.devices.bean.ConstantBean.FONT;
import static com.sssoft.base.devices.bean.ConstantBean.FONT_LARGE;
import static com.sssoft.base.devices.bean.ConstantBean.FONT_MAP;
import static com.sssoft.base.devices.bean.ConstantBean.FONT_NORMAL;
import static com.sssoft.base.devices.bean.ConstantBean.FONT_SMALL;
import static com.sssoft.base.devices.bean.ConstantBean.PRINT_HEIGHT;
import static com.sssoft.base.devices.bean.ConstantBean.PRINT_OFFSET;
import static com.sssoft.base.devices.bean.ConstantBean.PRINT_WIDTH;
import static com.sssoft.base.devices.devices_driver_lib.device_control_imp.abc.PrintErrorCodeAbc.ERR_MAP;


public class AbcPrinter implements IPrinter {

    private com.zacloud.deviceservice.aidl.IPrinter iPrinter;
    private PrinterListener printerListener;

    public AbcPrinter(IBinder binder) {
        iPrinter = com.zacloud.deviceservice.aidl.IPrinter.Stub.asInterface(binder);
    }

    @Override
    public void addText(Bundle format, String text) throws RemoteException {
        iPrinter.addText(setFormat(format), text);
    }

    @Override
    public void addPicture(Bundle format, Bitmap bitmap) throws RemoteException {
        double wb = Arith.div(bitmap.getWidth(), 400);
        String font = format.getString(FONT,"");
        Bundle format3 = new Bundle();
        if(font.equals(FONT_SMALL)){
            format3.putInt(PRINT_WIDTH, (int)(Arith.div(Arith.div(bitmap.getWidth(), wb), 4)));
            format3.putInt(PRINT_HEIGHT, (int)(Arith.div(Arith.div(bitmap.getHeight(), wb), 4)));
        }else if(font.equals(FONT_LARGE)){
            format3.putInt(PRINT_WIDTH, (int)(Arith.div(Arith.div(bitmap.getWidth(), wb), 1)));
            format3.putInt(PRINT_HEIGHT, (int)(Arith.div(Arith.div(bitmap.getHeight(), wb), 1)));
        }else{
            format3.putInt(PRINT_WIDTH, (int)(Arith.div(Arith.div(bitmap.getWidth(), wb), 2)));
            format3.putInt(PRINT_HEIGHT, (int)(Arith.div(Arith.div(bitmap.getHeight(), wb), 2)));
        }
        String align = format.getString(ALIGN,"");
        if(align.equals(ALIGN_CENTER)){
            format3.putInt(PRINT_OFFSET, (int)(Arith.div(400-format3.getInt(PRINT_WIDTH),2)));
        }else if(align.equals(ALIGN_RIGHT)){
            format3.putInt(PRINT_OFFSET, 400-format3.getInt(PRINT_WIDTH));
        }else{
            format3.putInt(PRINT_OFFSET, 0);
        }
        Bitmap bitmapTmp = getBitmap(bitmap, format3.getInt(PRINT_WIDTH), format3.getInt(PRINT_HEIGHT));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmapTmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        try {
            baos.flush();
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] datas = baos.toByteArray();
        iPrinter.addImage(format3, datas);
    }

    @Override
    public void addBarCode(Bundle format, String barCode) throws RemoteException {
        Bundle formateTmp = setFormat(format);
        formateTmp.putInt(PRINT_HEIGHT,64);
        formateTmp.putInt(PRINT_WIDTH,300);
        iPrinter.addBarCode(formateTmp, barCode);
    }

    @Override
    public void addQrCode(Bundle format, String qrCode) throws RemoteException {
        Bundle formateTmp = setFormat(format);
        formateTmp.putInt(PRINT_HEIGHT,100);
        iPrinter.addQrCode(formateTmp, qrCode);
    }

    @Override
    public void paperSkip(int line) throws RemoteException {
        iPrinter.feedLine(line);
    }

    @Override
    public void startPrinter(PrinterListener printerLis) throws RemoteException {
        this.printerListener = printerLis;
        int status = iPrinter.getStatus();
        if(status == PrintErrorCodeAbc.ERROR_NONE){
            iPrinter.startPrint(new com.zacloud.deviceservice.aidl.PrinterListener.Stub() {
                @Override
                public void onError(int error) throws RemoteException
                {
                    printerListener.onError(error+"", "打印错误,:" + ERR_MAP.get(error));
                }

                @Override
                public void onFinish() throws RemoteException
                {
                    printerListener.onFinish();
                }
            });
        }else{
            Log.i("test", "打印失败:"+ERR_MAP.get(status));
            printerListener.onError(status+"", "打印错误,:" + ERR_MAP.get(status));
        }

    }

    @Override
    public String getStatus() throws RemoteException {
        String rc = "";
        /*int status = iPrinter.getStatus();
        return status + ERR_MAP.get(status);*/
        return iPrinter.getStatus()+"";
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

    public Bitmap getBitmap(Bitmap bm,int newWidth,int newHeight){
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix,
                true);
        return newbm;
    }
}
