package com.sssoft.base.devices.devices_driver_lib.device_service_imp;

import android.content.Context;
import android.os.Handler;

import com.newland.me.ConnUtils;
import com.newland.me.DeviceManager;
import com.newland.mtype.ConnectionCloseEvent;
import com.newland.mtype.Device;
import com.newland.mtype.event.DeviceEventListener;
import com.newland.mtypex.nseries.NSConnV100ConnParams;
import com.sssoft.base.devices.devices_driver_lib.device_control_imp.newland.NewLandDeviceManager;
import com.sssoft.base.devices.devices_driver_lib.interfaces.service_interface.BankServiceInterface;
import com.sssoft.base.devices.devices_driver_lib.interfaces.service_interface.IDevicesManager;
import com.sssoft.base.iservice.IDeviceBindListener;

public class NewLandDevicesService implements BankServiceInterface {

    private Context mContext;
    private static final String K21_DRIVER_NAME = "com.newland.me.K21Driver";
    private  DeviceManager deviceManager ;
    private  Device device;
    private final IDeviceBindListener listener;
    public NewLandDevicesService(Context mContext,IDeviceBindListener listener) {
            this.listener = listener;
            this.mContext = mContext;
            bindService();
    }

    @Override
    public void bindService() {
        deviceManager = ConnUtils.getDeviceManager();
        deviceManager.init(mContext, K21_DRIVER_NAME, new NSConnV100ConnParams(),  new DeviceEventListener<ConnectionCloseEvent>() {
            @Override
            public void onEvent(ConnectionCloseEvent event, Handler handler) {
                if (event.isSuccess()) {
                    listener.onSuccess();
                }else{
                    listener.onFailed();
                }
            }
            @Override
            public Handler getUIHandler() {
                return null;
            }
        });
        try {
            deviceManager.connect();
            device = deviceManager.getDevice();
            device.setBundle(new NSConnV100ConnParams());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void unBindService() {
        try {
            if (deviceManager != null) {
                deviceManager.disconnect();
                deviceManager = null;
            }
        } catch (Exception e) {

        }
    }

    @Override
    public IDevicesManager getIDevicesManager() {
        return new NewLandDeviceManager(device,mContext);
    }
}
