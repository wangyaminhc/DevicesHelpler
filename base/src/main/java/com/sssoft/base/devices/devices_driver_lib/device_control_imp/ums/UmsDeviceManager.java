package com.sssoft.base.devices.devices_driver_lib.device_control_imp.ums;

import com.sssoft.base.devices.bean.ScanTypeEnum;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.AllCard;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.Card;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.IPinpad;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.IPrinter;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.InsertCard;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.RfM1Card;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.Scanner;
import com.sssoft.base.devices.devices_driver_lib.interfaces.service_interface.IDevicesManager;

public class UmsDeviceManager implements IDevicesManager {

    @Override
    public IPrinter getPrinter() {
        return new UmsPrinter();
    }

    @Override
    public IPinpad getPinpad() {
        return null;
    }

    @Override
    public Card getCard() {
        return null;
    }

    @Override
    public InsertCard getInsertCard() {
        return null;
    }

    @Override
    public RfM1Card getRfM1Card() {
        return null;
    }

    @Override
    public AllCard getAllCard() {
        return null;
    }

    @Override
    public Scanner getScanner(ScanTypeEnum scanTypeEnum) {
        return new UmsScanner(scanTypeEnum);
    }
}
