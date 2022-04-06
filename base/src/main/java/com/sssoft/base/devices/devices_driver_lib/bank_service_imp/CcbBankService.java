package com.sssoft.base.devices.devices_driver_lib.bank_service_imp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;

import com.ccb.deviceservice.aidl.IDeviceService;
import com.sssoft.base.devices.devices_driver_lib.interfaces.service_interface.BankServiceInterface;
import com.sssoft.base.devices.devices_driver_lib.device_control_imp.ccb.CcbDeviceManager;
import com.sssoft.base.devices.devices_driver_lib.interfaces.service_interface.IDevicesManager;
import com.sssoft.base.iservice.IDeviceBindListener;

public class CcbBankService implements BankServiceInterface {

    private Context mContext;

    public  IDevicesManager ccbDevicesManager = null;
    private static final String ACTION_DEVICE_SERVICE = "com.ccb.device_service";
    public static final String PACKAGE_DEVICE_SERVICE = "com.ccb.deviceservice";
    private  IDeviceService deviceService;
    private final IDeviceBindListener listener;

    public CcbBankService(Context mContext, IDeviceBindListener listener){
        this.mContext = mContext;
        this.listener = listener;
        bindService();
    }

    private  ServiceConnection ccbConnection = new ServiceConnection(){

        @Override
        public void onServiceDisconnected(ComponentName name){
//            deviceService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service)
        {
            deviceService = IDeviceService.Stub.asInterface(service);
            boolean isLogin = login();
            try {
                deviceService.asBinder().linkToDeath(deathRecipient,0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            if( isLogin){
                listener.onSuccess();
            }else {
                listener.onFailed();
            }

        }
    };

    private  IBinder.DeathRecipient deathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            if(deviceService == null)
                return;
            deviceService.asBinder().unlinkToDeath(deathRecipient,0);
            deviceService = null;
            ccbDevicesManager = null;
            bindService();
        }
    };

    private  boolean login() {
        boolean ret = false;
        try {
             ret = deviceService.lock(null, new Binder());

        } catch (RemoteException e) {
            e.printStackTrace();
            return  ret;
        }
        return  ret;
    }
    private  void logout() {
        try {
            deviceService.unlock();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void bindService() {
        if(deviceService == null){
            Intent service = new Intent(ACTION_DEVICE_SERVICE);
            service.setPackage(PACKAGE_DEVICE_SERVICE);
            mContext.bindService(service, ccbConnection, Context.BIND_AUTO_CREATE);
        }

    }

    @Override
    public void unBindService() {
        if(deviceService!=null){
            logout();
            mContext.unbindService(ccbConnection);
        }
        deviceService = null;
    }

    @Override
    public IDevicesManager getIDevicesManager() {
        if( ccbDevicesManager == null){
            ccbDevicesManager = new CcbDeviceManager(deviceService);
        }
        return ccbDevicesManager;
    }
}
