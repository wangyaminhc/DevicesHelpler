package com.sssoft.base.devices.devices_driver_lib.device_control_imp.abc;

import android.os.RemoteException;

import com.sssoft.base.devices.bean.ScanTypeEnum;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.AllCard;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.Card;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.IPinpad;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.IPrinter;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.InsertCard;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.RfM1Card;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.Scanner;
import com.sssoft.base.devices.devices_driver_lib.interfaces.service_interface.IDevicesManager;
import com.zacloud.deviceservice.aidl.IDeviceService;


public class AbcDeviceManager implements IDevicesManager {
    IDeviceService deviceBinder = null;

    IPrinter printer = null;
    IPinpad pinpad = null;
    Card card = null;
    InsertCard insertCard = null;
    RfM1Card rfM1Card = null;
    AllCard allCard = null;
    Scanner scanner = null;

    private int scanType = 1;  //0 前置扫码 1. 后置扫码

    public AbcDeviceManager(IDeviceService binder) {
        this.deviceBinder = binder;
    }

    @Override
    public IPrinter getPrinter() {
        try {
            printer = new AbcPrinter(deviceBinder.getPrinter());
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return  printer;
    }

    @Override
    public IPinpad getPinpad() {
        return null;
    }

    @Override
    public Card getCard() {
        try {
            card = new AbcMagCard(deviceBinder.getMagCardReader());
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
        return  card;
    }


    @Override
    public InsertCard getInsertCard() {
     /*   try {
            insertCard = new AbcInsertCard(deviceBinder.getInsertCardReader());
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }*/
        return  insertCard;
    }

    @Override
    public RfM1Card getRfM1Card() {
        try {
            rfM1Card = new AbcRmf1Card(deviceBinder.getRFCardReader());
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
        return  rfM1Card;
    }

    @Override
    public AllCard getAllCard() {
        return null;
    }

    @Override
    public Scanner getScanner(ScanTypeEnum mode) {
        try {
            switch ( mode){
                case SCAN_BACK:
                    scanType = 0;
                    break;
                case SCANF_RONT:
                    scanType = 1;
                    break;
            }
            scanner = new AbcScanner(deviceBinder.getScanner(scanType) );
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return  scanner;
    }
}
