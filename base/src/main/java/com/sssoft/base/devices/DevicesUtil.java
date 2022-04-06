package com.sssoft.base.devices;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.loong.base.R;
import com.sssoft.base.devices.bean.BankEnum;
import com.sssoft.base.devices.bean.ConstantBean;
import com.sssoft.base.devices.bean.ScanTypeEnum;
import com.sssoft.base.devices.devices_driver_lib.AIDLService;
import com.sssoft.base.devices.devices_driver_lib.interfaces.callback_interface.PrinterListener;
import com.sssoft.base.devices.devices_driver_lib.interfaces.callback_interface.ScannerListener;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.IPinpad;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.IPrinter;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.Scanner;
import com.sssoft.base.devices.devices_driver_lib.interfaces.service_interface.IDevicesManager;
import com.sssoft.base.devices.util.PrintUtil;
import com.sssoft.base.iservice.IDeviceBindListener;
import com.sssoft.base.view.SelfDialog;



/**
 *@author wangyamin
 *@time 创建时间 2021/2/23 11:22
 * @Description  设备调用工具
 *
 */
public  class DevicesUtil {

    private IDevicesManager devicesManager;
    private SelfDialog selfDialog;
    private Context mContext = null;
    private BankEnum bankEnum = null;
    private int printPage = 0, totalPages = 0;
    private String mPrintInfo = "";
    private Handler handler;
    private OnDevicesResult onDevicesResult = null;

    public DevicesUtil(Context mContext) {
        this.mContext = mContext;
        initHandler(mContext);
    }
    public DevicesUtil(Context mContext,BankEnum bankEnum) {
        this.mContext = mContext;
        this.bankEnum = bankEnum;
        initHandler(mContext);
    }


    private void initHandler(Context mContext){
        handler = new Handler(mContext.getMainLooper() ){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case ConstantBean.PrintResultCode.PRINT_ERR:
                    case ConstantBean.PrintResultCode.PRINT_NEXT:
                        showErrDialog((String)msg.obj);
                        break;
                    case ConstantBean.PrintResultCode.PRINT_OK:
                        unbindDevices();
                        onDevicesResult.onSuccess(mContext.getString(R.string.print_success));
                        break;
                    case ConstantBean.ScanHandlerCode.SUCCESS:
                        unbindDevices();
                        onDevicesResult.onSuccess((String)msg.obj);
                        break;
                    case ConstantBean.ScanHandlerCode.FINISH:
                    case ConstantBean.ScanHandlerCode.ERROR:
                        unbindDevices();
                        onDevicesResult.onFailed("xx",(String)msg.obj);
                        break;
                    default:
                        break;
                }
            }
        };

    }


    private void initDevices(IDeviceBindListener listener){
        if( devicesManager == null){
            AIDLService.init(mContext,listener);
        }
    }


    public void unbindDevices(){
        AIDLService.unBindDeviceService();
        devicesManager = null;
        handler = null;
        bankEnum = null;
    }

    /**
     *@author wangyamin
     *@time 创建时间 2021/2/23 15:12
     * @Description  bankEnum 设置为LOCAL时调用底层驱动，其他值系统自动匹配银行或设备服务
     *
     */

    /**
     *@author wangyamin
     *@time 创建时间 2021/2/23 15:12
     * @Description  bankEnum 设置为LOCAL时调用底层驱动，其他值系统自动匹配银行或设备服务
     *
     */



    public interface OnDevicesResult{
        void onSuccess(String result);
        void onFailed(String code,String reason);
    }
    /*------------------------设备调用----------------------*/
    /**
     *@author wangyamin
     *@time 创建时间 2021/2/23 15:26
     * @Description 打印操作
     *
     */

    private void showErrDialog(String info){
        selfDialog = new SelfDialog(mContext);
        selfDialog.setTitle(mContext.getString(R.string.print_tips));
        selfDialog.setMessage(info);
        selfDialog.setYesOnclickListener(mContext.getString(R.string.ok), new SelfDialog.onYesOnclickListener() {
            @Override
            public void onYesClick() {
                print(devicesManager.getPrinter(), mPrintInfo);
                selfDialog.dismiss();
            }
        });
        selfDialog.setNoOnclickListener(mContext.getString(R.string.cancel), new SelfDialog.onNoOnclickListener() {
            @Override
            public void onNoClick() {
                onDevicesResult.onSuccess(mContext.getString(R.string.cancel));
                selfDialog.dismiss();
                unbindDevices();
            }
        });
        selfDialog.setCancelable(false);
        selfDialog.setCount(6);
        selfDialog.show();
    }




    private void print(IPrinter printer, String printInfo){
        if (totalPages <= 0) {
            mPrintInfo = printInfo.replace("\n", "").replaceAll("\t", "").replaceAll("\r", "");
            totalPages = mPrintInfo.split("<cut>").length;
        }

        if (printPage<totalPages)
        {
            String[] prints = mPrintInfo.split("<cut>")[printPage].split("<br>");
            for( String s : prints){
                try {
                    PrintUtil.doPrint(s,printer);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            try{
//                printer.paperSkip(4);
                printer.startPrinter(new PrinterListener() {
                    @Override
                    public void onFinish()  {
                        Message msg = handler.obtainMessage();
                        printPage++;
                        if (printPage >= totalPages) {
                            msg.what = ConstantBean.PrintResultCode.PRINT_OK;
                            msg.obj = "打印完成";
                        } else {
                            msg.what = ConstantBean.PrintResultCode.PRINT_NEXT;
                            msg.obj = "是否继续打印第["+(printPage+1)+"]联签购单?";
                        }
                        handler.sendMessage(msg);
                    }

                    @Override
                    public void onError(String code, String detail)  {
                        Message msg = handler.obtainMessage();
                        msg.what =  ConstantBean.PrintResultCode.PRINT_ERR;
                        msg.obj = detail;
                        handler.sendMessage(msg);
                    }
                });
            }catch (Exception e){
                Message msg = handler.obtainMessage();
                msg.what =  ConstantBean.PrintResultCode.PRINT_ERR;
                msg.obj = "打印异常";
                handler.sendMessage(msg);
            }

        }
    }

    public void startPrint(String  printText,OnDevicesResult listener){
        printPage = 0;
        totalPages = 0;
        if(TextUtils.isEmpty(printText)){
            listener.onFailed("xx","打印内容为空");
        }else{
            initDevices(new IDeviceBindListener() {
                @Override
                public void onSuccess() {
                    devicesManager = AIDLService.bankServiceModle.getIDevicesManager();
                    onDevicesResult = listener;
                    print(devicesManager.getPrinter(),printText);
                }

                @Override
                public void onFailed() {
                    if( listener != null){
                        listener.onFailed("xx","初始化失败");
                    }
                }
            });
        }

    }

    private void sendScanHandler(String scanResult, int processCode){

        Message msg = new Message();
        msg.obj = scanResult;
        msg.what = processCode;
        handler.sendMessage(msg);
    }

    public void startScan(OnDevicesResult listener){
        startScan(ScanTypeEnum.SCAN_BACK, listener);
    }

    /**
     *@author wangyamin
     *@time 创建时间 2020/2/23 13:37
     * @Description 扫码操作
     *
     */
    public void startScan(ScanTypeEnum type, OnDevicesResult resultListener){
        //检查设备初始化情况
        onDevicesResult = resultListener;
        initDevices(new IDeviceBindListener() {
            @Override
            public void onSuccess() {
                devicesManager = AIDLService.bankServiceModle.getIDevicesManager();
                Scanner scanner = devicesManager.getScanner(type);
                try{
                    if(scanner == null){
                    }

                    scanner.startScan( 60, new ScannerListener() {
                        @Override
                        public void onScanResult(String barcode) {
                            sendScanHandler(barcode, ConstantBean.ScanHandlerCode.SUCCESS);
                        }

                        @Override
                        public void onError(String code, String detail) {

                            sendScanHandler(detail, ConstantBean.ScanHandlerCode.ERROR);
                        }

                        @Override
                        public void onFinish() {
                            sendScanHandler("", ConstantBean.ScanHandlerCode.FINISH);
                        }
                    });
                }catch (Exception e){
                    sendScanHandler("扫码异常", ConstantBean.ScanHandlerCode.ERROR);
                }
            }

            @Override
            public void onFailed() {
                sendScanHandler("初始化失败", ConstantBean.ScanHandlerCode.ERROR);
            }
        });
    }


    /**
     *@author wangyamin
     *@time 创建时间 2020/2/23 13:37
     * @Description 扫码操作
     *
     */
    public void startScan(ScanTypeEnum type, Handler handlerScan){
        //检查设备初始化情况
        initDevices(new IDeviceBindListener() {
            @Override
            public void onSuccess() {
                devicesManager = AIDLService.bankServiceModle.getIDevicesManager();
                Scanner scanner = devicesManager.getScanner(type);
                try{
                    if(scanner == null){
                    }
                    scanner.startScan( 60, new ScannerListener() {
                        @Override
                        public void onScanResult(String barcode) {
                            Message msg = handlerScan.obtainMessage();
                            msg.what = ConstantBean.ScanHandlerCode.SUCCESS;
                            msg.obj = barcode;
                            handlerScan.sendMessage(msg);
                        }

                        @Override
                        public void onError(String code, String detail) {
                            Message msg = handlerScan.obtainMessage();
                            msg.what = ConstantBean.ScanHandlerCode.ERROR;
                            handlerScan.sendMessage(msg);
                        }

                        @Override
                        public void onFinish() {
                            Message msg = handlerScan.obtainMessage();
                            msg.what = ConstantBean.ScanHandlerCode.FINISH;
                            handlerScan.sendMessage(msg);
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                    Message msg = handlerScan.obtainMessage();
                    msg.what = ConstantBean.ScanHandlerCode.EXCEPTION;
                    handlerScan.sendMessage(msg);
                }
            }

            @Override
            public void onFailed() {
                Message msg = handlerScan.obtainMessage();
                msg.what = ConstantBean.ScanHandlerCode.EXCEPTION;
                msg.obj = "设备初始化失败";
                handlerScan.sendMessage(msg);
            }
        });

    }

    //获取扫码对象

    public IPinpad getPinpad (){
        //检查设备初始化情况
/*
        log.info("current thread util = {}",Thread.currentThread().getName());
        initDevices(new IDeviceBindListener() {
            @Override
            public void onSuccess() {
                devicesManager = AIDLService.bankServiceModle.getIDevicesManager();
                pinpad = devicesManager.getPinpad();

            }

            @Override
            public void onFailed() {

            }
        });*/

        return devicesManager.getPinpad();

    }




}
