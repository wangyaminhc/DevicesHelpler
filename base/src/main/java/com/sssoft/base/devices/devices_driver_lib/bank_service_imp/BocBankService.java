package com.sssoft.base.devices.devices_driver_lib.bank_service_imp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.boc.aidl.deviceService.AidlDeviceService;
import com.sssoft.base.devices.devices_driver_lib.device_control_imp.boc.BocDeviceManager;
import com.sssoft.base.devices.devices_driver_lib.interfaces.service_interface.BankServiceInterface;
import com.sssoft.base.devices.devices_driver_lib.interfaces.service_interface.IDevicesManager;
import com.sssoft.base.iservice.IDeviceBindListener;

public class BocBankService implements BankServiceInterface {
    private static final String ACTION_DEVICE_SERVICE = "com.boc.DeviceService";
    public static final String PACKAGE_DEVICE_SERVICE = "com.boc.spos.service";
    //联迪硬件服务
    private  AidlDeviceService  deviceService = null;
    public IDevicesManager bocDevicesManager = null;
    private final IDeviceBindListener listener;

    private Context mContext;
//    public static  BocBankService bocBankService = null;
    private  ServiceConnection bocServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            deviceService = AidlDeviceService.Stub.asInterface(service);
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
            Log.e("BocDevices","binderDied");
            if(deviceService == null)
                return;
            deviceService.asBinder().unlinkToDeath(deathRecipient,0);
            deviceService = null;
            bocDevicesManager = null;
            bindService();
        }
    };

    public BocBankService(Context mContext,IDeviceBindListener listener) {
        this.listener = listener;
        this.mContext = mContext;
        bindService();
    }

    @Override
    public void bindService() {
        if(deviceService == null ){
            Intent service = new Intent(ACTION_DEVICE_SERVICE);
            service.setPackage(PACKAGE_DEVICE_SERVICE);
            mContext.bindService(service, bocServiceConnection, Context.BIND_AUTO_CREATE);

        }
    }

    @Override
    public void unBindService() {
        if(deviceService != null){
            mContext.unbindService(bocServiceConnection);
        }
        deviceService = null;
    }

    @Override
    public IDevicesManager getIDevicesManager() {
        if(bocDevicesManager == null){
            bocDevicesManager = new BocDeviceManager(deviceService);
        }
        return bocDevicesManager;
    }
}
