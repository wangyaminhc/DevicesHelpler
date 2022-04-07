package com.sssoft.base.devices.devices_driver_lib.bank_service_imp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.icbc.smartpos.deviceservice.aidl.IDeviceService;
import com.sssoft.base.devices.devices_driver_lib.device_control_imp.icbc.IcbcDeviceManager;
import com.sssoft.base.devices.devices_driver_lib.interfaces.service_interface.BankServiceInterface;
import com.sssoft.base.devices.devices_driver_lib.interfaces.service_interface.IDevicesManager;
import com.sssoft.base.iservice.IDeviceBindListener;

public class IcbcBankService implements BankServiceInterface {

    private static final String ACTION_DEVICE_SERVICE = "com.icbc.smartpos.device_service";
    public static final String PACKAGE_DEVICE_SERVICE = "com.icbc.smartpos.deviceservice";
    //联迪硬件服务
    private IDeviceService deviceService = null;
    public  IDevicesManager icbcDevicesManager = null;
    private final IDeviceBindListener listener;
    private Context mContext;

    private ServiceConnection icbcServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            deviceService = IDeviceService.Stub.asInterface(service);

            if( deviceService != null){
                try{
                    deviceService.asBinder().linkToDeath(deathRecipient,0);
                }catch (RemoteException e){
                    e.printStackTrace();
                }
                listener.onSuccess();
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
            Log.e("Devices","binderDied");
            if(deviceService == null){
                return;
            }
            deviceService.asBinder().unlinkToDeath(deathRecipient,0);
            deviceService = null;
            icbcDevicesManager = null;
            bindService();
        }
    };

    public IcbcBankService(Context mContext,IDeviceBindListener listener) {
        this.mContext = mContext;
        this.listener = listener;
        bindService();
    }

    @Override
    public void bindService(){
        if(deviceService == null ){
            Intent service = new Intent(ACTION_DEVICE_SERVICE);
            service.setPackage(PACKAGE_DEVICE_SERVICE);
            mContext.bindService(service, icbcServiceConnection, Context.BIND_AUTO_CREATE);

        }
    }

    @Override
    public void unBindService() {
        if(deviceService != null){
            mContext.unbindService(icbcServiceConnection);
        }
        deviceService = null;
    }

    @Override
    public IDevicesManager getIDevicesManager() {
        if(icbcDevicesManager == null){
            icbcDevicesManager = new IcbcDeviceManager(deviceService);
        }
        return icbcDevicesManager;
    }

}
