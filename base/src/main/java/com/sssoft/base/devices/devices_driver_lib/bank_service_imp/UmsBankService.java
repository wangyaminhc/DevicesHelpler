package com.sssoft.base.devices.devices_driver_lib.bank_service_imp;

import android.content.Context;

import com.sssoft.base.devices.devices_driver_lib.device_control_imp.ums.UmsDeviceManager;
import com.sssoft.base.devices.devices_driver_lib.interfaces.service_interface.BankServiceInterface;
import com.sssoft.base.devices.devices_driver_lib.interfaces.service_interface.IDevicesManager;
import com.sssoft.base.devices.util.DriverUtil;
import com.sssoft.base.iservice.IDeviceBindListener;
import com.ums.upos.sdk.exception.SdkException;
import com.ums.upos.sdk.system.BaseSystemManager;
import com.ums.upos.sdk.system.OnServiceStatusListener;

public class UmsBankService implements BankServiceInterface {
    public  IDevicesManager umsDevicesManager = null;
    private Context mContext;
    public static final String PACKAGE_DEVICE_SERVICE = "com.ums.upos.uapi";
    private final IDeviceBindListener listener;

    public UmsBankService(Context mContext, IDeviceBindListener listener) {
        this.mContext = mContext;
        this.listener = listener;
        bindService();
    }

    @Override
    public void bindService() {
        try {
            BaseSystemManager.getInstance().deviceServiceLogin(
                    mContext, null, "99999998",//设备ID，生产找后台配置
                    new OnServiceStatusListener() {
                        @Override
                        public void onStatus(int arg0) {//arg0可见ServiceResult.java
                            if (0 == arg0 || 2 == arg0 || 100 == arg0) {//0：登录成功，有相关参数；2：登录成功，无相关参数；100：重复登录。
                                DriverUtil.Log(DriverUtil.LogLevel.Debug,"AIDLService.isBnak", "UmsInit ok");
                                listener.onSuccess();
                            }else{
                                listener.onFailed();
                            }
                        }
                    });
        } catch (Exception e) {
            DriverUtil.Log(DriverUtil.LogLevel.Debug,"AIDLService.isBnak", "UmsInit exception");
            listener.onFailed();
        }
    }

    @Override
    public void unBindService() {
        //登出，以免占用U架构服务
        try {
            BaseSystemManager.getInstance().deviceServiceLogout();
        } catch (SdkException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public IDevicesManager getIDevicesManager() {
        if(umsDevicesManager == null){
            umsDevicesManager = new UmsDeviceManager();
        }
        return umsDevicesManager;
    }
}
