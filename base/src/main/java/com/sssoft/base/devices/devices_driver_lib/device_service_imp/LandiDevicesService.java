package com.sssoft.base.devices.devices_driver_lib.device_service_imp;

import android.content.Context;

import com.landicorp.android.eptapi.DeviceService;
import com.landicorp.android.eptapi.exception.ReloginException;
import com.landicorp.android.eptapi.exception.RequestException;
import com.landicorp.android.eptapi.exception.ServiceOccupiedException;
import com.landicorp.android.eptapi.exception.UnsupportMultiProcess;
import com.sssoft.base.devices.devices_driver_lib.device_control_imp.landi.LandiDeviceManager;
import com.sssoft.base.devices.devices_driver_lib.interfaces.service_interface.BankServiceInterface;
import com.sssoft.base.devices.devices_driver_lib.interfaces.service_interface.IDevicesManager;
import com.sssoft.base.iservice.IDeviceBindListener;

public class LandiDevicesService implements BankServiceInterface {

    private Context mContext;
    private final IDeviceBindListener listener;
    public LandiDevicesService(Context mContext,IDeviceBindListener listener) {
        this.listener = listener;
       this.mContext = mContext;
       bindService();
    }

    @Override
    public void bindService() {
        try {
            DeviceService.login(mContext);
            listener.onSuccess();
        } catch (RequestException e) {
            listener.onFailed();
            e.printStackTrace();
        } catch (ServiceOccupiedException e) {
            listener.onFailed();
            e.printStackTrace();
        } catch (ReloginException e) {
            listener.onFailed();
            e.printStackTrace();
        } catch (UnsupportMultiProcess e) {
            listener.onFailed();
            e.printStackTrace();
        }
    }

    @Override
    public void unBindService() {
        DeviceService.logout();
    }

    @Override
    public IDevicesManager getIDevicesManager() {
        return new LandiDeviceManager(mContext);
    }
}
