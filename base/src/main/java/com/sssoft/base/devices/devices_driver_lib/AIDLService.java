package com.sssoft.base.devices.devices_driver_lib;

import android.content.Context;
import android.util.Log;

import com.sssoft.base.devices.bean.BankEnum;
import com.sssoft.base.devices.bean.BankServiceConstant;
import com.sssoft.base.devices.devices_driver_lib.bank_service_imp.AbcBankService;
import com.sssoft.base.devices.devices_driver_lib.bank_service_imp.BocBankService;
import com.sssoft.base.devices.devices_driver_lib.bank_service_imp.CcbBankService;
import com.sssoft.base.devices.devices_driver_lib.bank_service_imp.IcbcBankService;
import com.sssoft.base.devices.devices_driver_lib.bank_service_imp.UmsBankService;
import com.sssoft.base.devices.devices_driver_lib.device_service_imp.LandiDevicesService;
import com.sssoft.base.devices.devices_driver_lib.device_service_imp.NewLandDevicesService;
import com.sssoft.base.devices.devices_driver_lib.interfaces.service_interface.BankServiceInterface;
import com.sssoft.base.devices.util.DriverUtil;
import com.sssoft.base.iservice.IDeviceBindListener;

import java.util.Map;

public class AIDLService {


    private static Context mContext = null;
    public static BankServiceInterface bankServiceModle;
    private static String model = android.os.Build.MODEL;

    //机器型号
    private static final String LANDI_MODE = "APOS";     //联迪
    private static final String VERIFONE = "X";     //惠尔丰
    private static final String AISION = "A90";  //艾体威尔
    private static final String NEWLAND = "N";  //新大陆
    private static final String HISENSE = "HI";  //海信
    private static final String JOLLY = "S500";   //九磊S500

    /**
     *@author wangyamin
     *@time 创建时间 2020/6/8 14:53
     * @Description  默认使用银行的服务
     *  初始化硬件设置  先检测银行服务包名 再检测设备名称
     */
    public static void init(Context context,IDeviceBindListener listener){

        mContext = context;
        if( bankServiceModle != null){
            bankServiceModle.unBindService();
        }
        Log.e("wangyamin","mode ="+model);
        BankEnum mBank = checkBankService();
        if(mBank != null){
            init(mBank,listener);
        }else{
            initDevices(mContext,listener);
        }
    }




    public static Context getmContext() {
        return mContext;
    }

    public static void initDevices(Context context, IDeviceBindListener listener) {
        switch (model){
            case LANDI_MODE:
                bankServiceModle = new LandiDevicesService(mContext,listener);
                break;
            case NEWLAND:
                bankServiceModle = new NewLandDevicesService(mContext,listener);
                break;
            default:
                Log.e("init devices err","no matched devices service ");
                break;
        }
    }
    private static void init(BankEnum bankEnum, IDeviceBindListener listener){
        switch (bankEnum){
            case CCB:
                bankServiceModle = new CcbBankService(mContext,listener);
                break;
            case BOC:
                bankServiceModle = new BocBankService(mContext,listener);
                break;
            case ABC:
                bankServiceModle = new AbcBankService(mContext,listener);
                break;
            case UMS:
                bankServiceModle = new UmsBankService(mContext,listener);
                break;
            case ICBC:
                bankServiceModle = new IcbcBankService(mContext,listener);
                break;
            default:
                Log.e("init bank service err","no matched bank service ");
                break;
        }
    }


    private static BankEnum checkBankService() {
        BankEnum bankEnum = null;
        for(Map.Entry<BankEnum,String> mapEntry : BankServiceConstant.bankMap.entrySet()){
            if(DriverUtil.checkApkExist(mContext,mapEntry.getValue())){
                bankEnum = mapEntry.getKey();
                break;
            }
        }
        return  bankEnum;
    }

    //解绑服务
    public static void unBindDeviceService() {
        if(bankServiceModle != null){
            bankServiceModle.unBindService();
        }
    }
}
