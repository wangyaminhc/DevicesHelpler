package com.sssoft.base.devices.devices_driver_lib.device_control_imp.boc;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.boc.aidl.printer.AidlPrinter;
import com.boc.aidl.printer.AidlPrinterListener;
import com.google.gson.Gson;
import com.loong.base.R;
import com.sssoft.base.devices.DevicesUtil;
import com.sssoft.base.devices.devices_driver_lib.interfaces.callback_interface.PrinterListener;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.IPrinter;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import static com.sssoft.base.devices.bean.ConstantBean.ALIGN;
import static com.sssoft.base.devices.bean.ConstantBean.ALIGN_CENTER;
import static com.sssoft.base.devices.bean.ConstantBean.ALIGN_LEFT;
import static com.sssoft.base.devices.bean.ConstantBean.FONT;
import static com.sssoft.base.devices.bean.ConstantBean.FONT_MAP;
import static com.sssoft.base.devices.bean.ConstantBean.FONT_NORMAL;
import static com.sssoft.base.devices.bean.ConstantBean.PRINT_HEIGHT;

public class BocPrinter implements IPrinter {
    private AidlPrinter iPrinter;
    private PrinterListener printerListener;
    private PrintContentBean printContentBean = new PrintContentBean() ;
    private List<PrintContentBean.SposBean> printLists = new ArrayList<>();
    public static final int QR_SIZE = 150;
    public static final int QR_SIZE_STEP = 50;
    private Context mContext;


    private static final String TEXT_TYPE = "txt";
    private static final String PIC_TYPE = "jpg";
    private static final String BAR_CODE = "one-dimension";
    private static final String QR_CODE = "two-dimension";

    public BocPrinter(IBinder binder, Context context) {
        iPrinter = AidlPrinter.Stub.asInterface(binder);
        mContext = context;
    }
    String bill="{'spos':[{'content-type':'txt','content':'\n\n','position':'left','bold':'0','height':'-1','size':'2'},"
            + "{'content-type':'txt','content':'签购单','position':'center','bold':'0','height':'-1','size':'3'},"
            + "{'content_type':'jpg','content':'','position':'center','bold':'0','height':'-1','size':'1'},"
            + "{'content-type':'txt','content':'------------------------------------------------','position':'center','bold':'0','height':'-1','size':'2'},"
            + "{'content-type':'txt','content':'持卡人存根','position':'right','bold':'0','height':'-1','size':'2'},"
            + "{'content-type':'txt','content':'------------------------------------------------','position':'center','bold':'0','height':'-1','size':'2'},"
            + "{'content-type':'txt','content':'商户：ZOE集成环境测试商户','position':'left','bold':'0','height':'-1','size':'3'},"
            + "{'content-type':'txt','content':'    商户编号：812002110030001','position':'left','bold':'0','height':'-1','size':'2'},"
            + "{'content-type':'txt','content':'    终端编号：20150909','position':'left','bold':'0','height':'-1','size':'2'},"
            + "{'content-type':'txt','content':'    操作员号：001','position':'left','bold':'0','height':'-1','size':'2'},"
            + "{'content-type':'txt','content':'------------------------------------------------','position':'center','bold':'0','height':'-1','size':'2'},"
            + "{'content-type':'txt','content':'交易：扫一扫支付','position':'left','bold':'0','height':'-1','size':'3'},"
            + "{'content-type':'txt','content':'    支付渠道：微信支付','position':'left','bold':'0','height':'-1','size':'2'},"
            + "{'content-type':'txt','content':'    日期时间：2016\\/04\\/28  15:27:51','position':'left','bold':'0','height':'-1','size':'2'},"
            + "{'content-type':'txt','content':'    参考号：000014103147','position':'left','bold':'0','height':'-1','size':'2'},"
            + "{'content-type':'one-dimension','content':'000014103147','position':'center','bold':'0','height':'1','size':'3'},"
            + "{'content-type':'txt','content':'','position':'left','bold':'0','height':'-1','size':'1'},"
            + "{'content-type':'txt','content':'','position':'left','bold':'0','height':'-1','size':'1'},"
            + "{'content-type':'txt','content':'金额：RMB  0.01','position':'left','bold':'1','height':'-1','size':'2'},"
            + "{'content-type':'txt','content':'------------------------------------------------','position':'center','bold':'0','height':'-1','size':'2'},"
            + "{'content-type':'txt','content':'备注：','position':'left','bold':'0','height':'-1','size':'2'},"
            + "{'content-type':'txt','content':'','position':'left','bold':'0','height':'-1','size':'1'},"
            + "{'content-type':'txt','content':'','position':'left','bold':'0','height':'-1','size':'1'},"
            + "{'content-type':'txt','content':'本人确认以上交易，同意将其计入本卡账户 ','position':'center','bold':'0','height':'-1','size':'1'},"
            + "{'content-type':'txt','content':'I ACKNOWLEDGE SATISFACTORY RECEIPT OF RELATIVE GOODS\\/SERVICE','position':'center','bold':'0','height':'-1','size':'1'},"
            + "{'content-type':'txt','content':'------------------------------------------------','position':'center','bold':'0','height':'-1','size':'2'},"
            + "{'content-type':'two-dimension','content':'687474703A2F2F7777772E393962696C6C2E636F6D2F617070','position':'center','bold':'0','height':'-1','size':'2'},"
            + "{'content-type':'txt','content':'\n\n\n\n','position':'center','bold':'0','height':'-1','size':'2'}]}";


    @Override
    public void addText(Bundle format, String text) throws RemoteException {
        PrintContentBean.SposBean bean = new PrintContentBean.SposBean();
        String align = ALIGN_LEFT;
        String font = FONT_NORMAL;
        if( format != null){
             align = format.getString(ALIGN,ALIGN_LEFT);
             font = format.getString(FONT,FONT_NORMAL);
        }

        bean.setContenttype(TEXT_TYPE);
        bean.setSize((FONT_MAP.get(font)+1)+"");
        bean.setContent(text);
        bean.setPosition(align);
        printLists.add(bean);
    }

    @Override
    public void addPicture(Bundle format, Bitmap bitmap) throws RemoteException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        //byte[] datas = baos.toByteArray();
        iPrinter.printBitMap(0, bitmap, 100, new AidlPrinterListener.Stub(){

            @Override
            public void onError(int i, String s) throws RemoteException {
                printerListener.onError(i+"",s);
            }

            @Override
            public void onFinish() throws RemoteException {
                printerListener.onFinish();
            }
        });
    }

    @Override
    public void addBarCode(Bundle format, String barCode) throws RemoteException {
        PrintContentBean.SposBean bean = new PrintContentBean.SposBean();
        String align = ALIGN_LEFT;
        int height = 50;
        String font =FONT_NORMAL;
        if( format != null){
            align = format.getString(ALIGN,ALIGN_LEFT);
            height = format.getInt(PRINT_HEIGHT,64);
//            font = format.getString(FONT,FONT_NORMAL);
        }
        bean.setContenttype(BAR_CODE);
        bean.setSize("2");
        bean.setContent(barCode);
        bean.setPosition(align);
//        bean.setHeight(height+"");
//        bean.setHeight("-1");
        printLists.add(bean);
    }

    @Override
    public void addQrCode(Bundle format, String qrCode) throws RemoteException {
        PrintContentBean.SposBean bean = new PrintContentBean.SposBean();
        String align = ALIGN_LEFT;
        String font =FONT_NORMAL;
        if( format != null){
            align = format.getString(ALIGN,ALIGN_LEFT);
            font = format.getString(FONT,FONT_NORMAL);
        }
        bean.setContenttype(QR_CODE);



        bean.setSize(((FONT_MAP.get(font))*QR_SIZE_STEP+QR_SIZE)+"");
        bean.setContent(qrCode);
        bean.setPosition(align);
        printLists.add(bean);
    }

    @Override
    public void paperSkip(int line) throws RemoteException {
        iPrinter.paperSkip(line);
    }

    @Override
    public void startPrinter(final PrinterListener printerLis) throws RemoteException {
        //获取需要打印的字符串
        printerListener = printerLis;
        if(printLists.size() <= 0 ){
            Log.e("getBocPrintString","print size:"+printLists.size());
            printerListener.onError("XX","打印数据为空");
        }else{
            Log.e("getBocPrintString","print size:"+printLists.size());
            Log.e("getBocPrintString","print:"+getPrintString());
//            Resources.getSystem().getDrawable(R.drawable.boclogo);
            Bitmap[] bitmap = new Bitmap[] { BitmapFactory.decodeResource(mContext.getResources(), R.drawable.boclogo) };
            iPrinter.print(getPrintString(), bitmap, new AidlPrinterListener.Stub(){
                @Override
                public void onError(int i, String s)  {
                    printerListener.onError( i+"",PrintErrorCodeBOC.ERR_MAP.get(i));
                    Log.e("getBocPrintString","onError:"+PrintErrorCodeBOC.ERR_MAP.get(i));
                    printLists.clear();
                }

                @Override
                public void onFinish() {
                    printerListener.onFinish();
                    Log.e("getBocPrintString","onFinish:");
                    printLists.clear();
                }
            });
        }
    }

    private String  getPrintString( ){
        Gson gson = new Gson();
        printContentBean.setSpos( printLists);
       return gson.toJson(printContentBean);
    }

    @Override
    public String getStatus() throws RemoteException {
        String rc = "";
        int status = iPrinter.getStatus();
        return rc;
    }

}
