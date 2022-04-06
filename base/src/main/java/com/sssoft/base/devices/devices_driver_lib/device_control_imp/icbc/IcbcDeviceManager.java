package com.sssoft.base.devices.devices_driver_lib.device_control_imp.icbc;

import android.os.RemoteException;

import com.icbc.smartpos.deviceservice.aidl.IDeviceService;
import com.sssoft.base.devices.bean.ScanTypeEnum;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.AllCard;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.Card;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.IPinpad;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.IPrinter;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.InsertCard;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.RfM1Card;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.Scanner;
import com.sssoft.base.devices.devices_driver_lib.interfaces.service_interface.IDevicesManager;

public class IcbcDeviceManager implements IDevicesManager {
    private IDeviceService deviceBinder = null;
    IPrinter printer = null;
    IPinpad pinpad = null;
    Card card = null;
    InsertCard insertCard = null;
    RfM1Card rfM1Card = null;
    AllCard allCard = null;
    Scanner scanner = null;

    public IcbcDeviceManager(IDeviceService deviceBinder) {
        this.deviceBinder = deviceBinder;
    }

    @Override
    public IPrinter getPrinter() {
        try {
            printer = new IcbcPrinter(deviceBinder.getPrinter());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return printer;
    }

    @Override
    public IPinpad getPinpad() {
        try {
            pinpad = new IcbcPinPad( deviceBinder.getPinpad(0x05));//0x01:外置密码键盘  0x05:内置密码键盘
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return  pinpad;
    }

    @Override
    public Card getCard() {
        try {
            card = new IcbcMagCard(deviceBinder.getMagCardReader());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return  card;
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
    public Scanner getScanner(ScanTypeEnum scanType) {
        scanner = new IcbcScanner( deviceBinder, scanType);
        return scanner;
    }
}
