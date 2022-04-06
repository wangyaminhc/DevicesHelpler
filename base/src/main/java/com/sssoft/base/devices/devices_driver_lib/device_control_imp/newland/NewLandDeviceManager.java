package com.sssoft.base.devices.devices_driver_lib.device_control_imp.newland;

import android.content.Context;

import com.newland.mtype.Device;
import com.sssoft.base.devices.bean.ScanTypeEnum;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.AllCard;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.Card;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.IPinpad;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.IPrinter;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.InsertCard;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.RfM1Card;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.Scanner;
import com.sssoft.base.devices.devices_driver_lib.interfaces.service_interface.IDevicesManager;

public class NewLandDeviceManager implements IDevicesManager {

    private Device device;
    private Context mContext;
    IPrinter printer = null;
    IPinpad pinpad = null;
    Card card = null;
    InsertCard insertCard = null;
    RfM1Card rfM1Card = null;
    AllCard allCard = null;
    Scanner scanner = null;

    public NewLandDeviceManager(Device device, Context context) {
        this.device = device;
        this.mContext = context;
    }

    @Override
    public IPrinter getPrinter() {
        return new NewlandPrinter( device);
    }

    @Override
    public IPinpad getPinpad() {
        return null;
    }

    @Override
    public Card getCard() {
        return new NewlandCard(device,mContext);
    }

    @Override
    public InsertCard getInsertCard() {
        return null;
    }

    @Override
    public RfM1Card getRfM1Card() {
        return new NewLandRfM1Card(device);
    }

    @Override
    public AllCard getAllCard() {
        return null;
    }

    @Override
    public Scanner getScanner(ScanTypeEnum typeEnum) {
        return new NewlandScanner(device,mContext,typeEnum);
    }
}
