package com.sssoft.base.devices.devices_driver_lib.interfaces.service_interface;

public interface BankServiceInterface {
    void  bindService();
    void unBindService();
    IDevicesManager getIDevicesManager();
}
