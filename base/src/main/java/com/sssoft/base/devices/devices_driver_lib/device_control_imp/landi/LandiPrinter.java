package com.sssoft.base.devices.devices_driver_lib.device_control_imp.landi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.RemoteException;

import com.landicorp.android.eptapi.device.Printer;
import com.landicorp.android.eptapi.exception.RequestException;
import com.landicorp.android.eptapi.utils.ImageTransformer;
import com.landicorp.android.eptapi.utils.QrCode;
import com.sssoft.base.devices.devices_driver_lib.interfaces.callback_interface.PrinterListener;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.IPrinter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.landicorp.android.eptapi.device.Printer.Format.HZ_DOT24x24;
import static com.landicorp.android.eptapi.utils.QrCode.ECLEVEL_Q;
import static com.sssoft.base.devices.bean.ConstantBean.ALIGN;
import static com.sssoft.base.devices.bean.ConstantBean.ALIGN_CENTER;
import static com.sssoft.base.devices.bean.ConstantBean.ALIGN_RIGHT;
import static com.sssoft.base.devices.bean.ConstantBean.FONT;
import static com.sssoft.base.devices.bean.ConstantBean.FONT_LARGE;
import static com.sssoft.base.devices.bean.ConstantBean.FONT_SMALL;

public class LandiPrinter implements IPrinter {

    private Context mContext;
    private Printer.Progress progress;
    private List<Printer.Step> stepList;
    private Bundle bundle;
    private String inString;
    private Bitmap inBitmap;
    private int inline;
    private PrinterListener listener;
    public LandiPrinter(Context mContext) {
        this.mContext = mContext;
        stepList = new ArrayList<Printer.Step>();
    }

    @Override
    public void addText( Bundle format,  String text) throws RemoteException {
        bundle = format;
        inString = text;
        stepList.add(new Printer.Step() {
            @Override
            public void doPrint(Printer printer) throws Exception {
                printer.setFormat(setFontType(bundle));
                printer.setAutoTrunc(false);
                printer.printText(setAlgin(bundle), inString+"\n");
            }
        });
    }

    @Override
    public void addPicture( Bundle format,  Bitmap bitmap) throws RemoteException {
        bundle = format;
        inBitmap = bitmap;
        stepList.add(new Printer.Step() {
            @Override
            public void doPrint(Printer printer) throws Exception {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                inBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                InputStream inputStream = new ByteArrayInputStream(baos.toByteArray());
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                final int MAX_WIDTH = getPrinterWidth();
                if (bitmap.getWidth() > MAX_WIDTH) {
                    bitmap = scaleBitmap(bitmap, bundle.getInt("offset",0), MAX_WIDTH);
                    if (bitmap == null) {
                        return;
                    }
                }
                ByteArrayOutputStream outputStream = ImageTransformer.convert1BitBmp(bitmap);
                inputStream = new ByteArrayInputStream(outputStream.toByteArray());
                printer.printImage(Printer.Alignment.LEFT, inputStream);
                // 若是打印大位图，需使用printer.printMonochromeBmp接口
                inputStream.close();
//                printer.printMonochromeBmp(0, outputStream.toByteArray());
                outputStream.close();

                // printer.printMonochromeBmp(offset, path)接口打印位图，需将sdcard文件路径进行转义，否则底层打印机无法找到对应文件
//                String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/pay.bmp";
//                filePath = convertBmpAbsolutePath(Build.MODEL, filePath);
//                printer.printMonochromeBmp(0, filePath);
            }
        });
    }

    @Override
    public void addBarCode(Bundle format, String barCode) throws RemoteException {
        bundle = format;
        inString = barCode;
        stepList.add(new Printer.Step() {
            @Override
            public void doPrint(Printer printer) throws Exception {
                printer.printBarCode(setAlgin(bundle),inString);
            }
        });
    }

    @Override
    public void addQrCode(Bundle format, String qrCode) throws RemoteException {
        bundle = format;
        inString = qrCode;
        stepList.add(new Printer.Step() {
            @Override
            public void doPrint(Printer printer) throws Exception {
                printer.printQrCode(setAlgin(bundle),
                        new QrCode(inString, ECLEVEL_Q),
                        200);
            }
        });
    }

    @Override
    public void paperSkip(int line) throws RemoteException {
        inline = line;
        stepList.add(new Printer.Step() {
            @Override
            public void doPrint(Printer printer) throws Exception {
                printer.feedLine(inline);
            }
        });
    }

    @Override
    public void startPrinter(PrinterListener printerLis) throws RemoteException {
        listener = printerLis;
        progress = new Printer.Progress() {
            @Override
            public void doPrint(Printer printer) throws Exception {
                // never call
            }

            @Override
            public void onFinish(int error){
                stepList.clear();
                if (error == Printer.ERROR_NONE) {
                        listener.onFinish();
                } else {
                    listener.onError( error+"","打印错误:"+PrintErrorCodeLanDi.ERR_MAP.get(error));
                }
            }

            @Override
            public void onCrash() {
                stepList.clear();
            }
        };
        for (Printer.Step step : stepList) {
            progress.addStep(step);
        }
        try {
            progress.start();
        } catch (RequestException e) {
            e.printStackTrace();
            printerLis.onError("05", "打印错误，请检查");
        }
    }

    @Override
    public String getStatus() throws RemoteException {
        try {
            int status = Printer.getInstance().getStatus();
            return  status+"";
        } catch (RequestException e) {
            e.printStackTrace();
        }
        return "err";
    }

    public Printer.Format setFontType(Bundle format){
        String font = "";
        if(format != null){
             font = format.getString(FONT,"");
        }
        Printer.Format formatTmp = new Printer.Format();
        if(font.equals(FONT_SMALL)){
            formatTmp.setAscScale(Printer.Format.ASC_SC1x1);
            formatTmp.setAscSize(Printer.Format.ASC_DOT16x8);
            formatTmp.setHzScale(Printer.Format.HZ_SC1x1);
            formatTmp.setHzSize(Printer.Format.HZ_DOT16x16);
        }else if(font.equals(FONT_LARGE)){
            formatTmp.setAscScale(Printer.Format.ASC_SC2x2);
            formatTmp.setAscSize(Printer.Format.ASC_DOT24x12);
            formatTmp.setHzScale(Printer.Format.HZ_SC2x2);
            formatTmp.setHzSize(HZ_DOT24x24);
        }else{
            formatTmp.setAscScale(Printer.Format.ASC_SC1x1);
            formatTmp.setAscSize(Printer.Format.ASC_DOT24x12);
            formatTmp.setHzScale(Printer.Format.HZ_SC1x1);
            formatTmp.setHzSize(Printer.Format.HZ_DOT24x24);
        }
        return formatTmp;
    }
    public Printer.Alignment setAlgin(Bundle format){
        String align = "";
        if(format != null){
            align = format.getString(ALIGN,"");
        }
        Printer.Alignment alignment = Printer.Alignment.LEFT;
        if(align.equals(ALIGN_CENTER)){
            alignment = Printer.Alignment.CENTER;
        }else if(align.equals(ALIGN_RIGHT)){
            alignment = Printer.Alignment.RIGHT;
        }else{
            alignment = Printer.Alignment.LEFT;
        }
        return alignment;
    }

    private int getPrinterWidth() {
        int width = Printer.getInstance().getValidWidth();
        if (width <= 0) {
            return 384;
        }
        return width;
    }

    private Bitmap scaleBitmap(Bitmap bm, int offset, int maxWidth) {
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 设置想要的大小
        int newWidth = maxWidth - offset;
        if (newWidth <= 0) {
            return null;
        }
        int newHeight = height;
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newbmp = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbmp;
    }
}
