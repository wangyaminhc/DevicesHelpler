package com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface;

import com.sssoft.base.devices.devices_driver_lib.interfaces.callback_interface.AllCardListener;

public interface AllCard {
    void read(int timeout,  AllCardListener listener);
    void setM1Key(String Key);
    void cancel();
}
