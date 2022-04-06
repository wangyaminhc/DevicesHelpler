package com.sssoft.base.devices.devices_driver_lib.bank_service_imp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.sssoft.base.devices.devices_driver_lib.device_control_imp.abc.AbcDeviceManager;
import com.sssoft.base.devices.devices_driver_lib.interfaces.service_interface.BankServiceInterface;
import com.sssoft.base.devices.devices_driver_lib.interfaces.service_interface.IDevicesManager;
import com.sssoft.base.iservice.IDeviceBindListener;
import com.zacloud.deviceservice.aidl.IDeviceService;

public class AbcBankService implements BankServiceInterface {
    private static final String ACTION_DEVICE_SERVICE = "com.zacloud.device_service";
    public static final String PACKAGE_DEVICE_SERVICE = "com.zacloud.deviceservice";

    private IDeviceService deviceService = null;
    public IDevicesManager abcDevicesManager = null;
    private final IDeviceBindListener listener;
    private Context mContext;

    private ServiceConnection abcServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            deviceService = IDeviceService.Stub.asInterface(service);
            if( deviceService != null){
                listener.onSuccess();
                try{
                    deviceService.asBinder().linkToDeath(deathRecipient,0);
                }catch (RemoteException e){
                    e.printStackTrace();
                }
            }else{
                listener.onFailed();
            }

        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
//            deviceService = null;
        }
    };

    private  IBinder.DeathRecipient deathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            if(deviceService == null)
                return;
            deviceService.asBinder().unlinkToDeath(deathRecipient,0);
            deviceService = null;
            abcDevicesManager = null;
            bindService();
        }
    };


    public AbcBankService(Context mContext,IDeviceBindListener listener) {
        this.mContext = mContext;
        this.listener = listener;
        bindService();
    }

    @Override
    public void bindService() {
        if( deviceService == null){
            Intent service = new Intent(ACTION_DEVICE_SERVICE);
            service.setPackage(PACKAGE_DEVICE_SERVICE);
            mContext.bindService(service, abcServiceConnection, Context.BIND_AUTO_CREATE);
        }

    }

    @Override
    public void unBindService() {

        if(deviceService!=null){
            mContext.unbindService(abcServiceConnection);
        }
        deviceService = null;
    }

    @Override
    public IDevicesManager getIDevicesManager() {
        if(abcDevicesManager == null){
            abcDevicesManager = new AbcDeviceManager(deviceService);
        }
        return abcDevicesManager;
    }
}
