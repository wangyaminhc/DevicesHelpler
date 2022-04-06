package com.sssoft.base.devices.devices_driver_lib.device_control_imp.ccb;

import android.os.Bundle;
import android.os.RemoteException;

import com.ccb.deviceservice.aidl.IDeviceService;
import com.ccb.deviceservice.aidl.constant.ICDeviceName;
import com.ccb.deviceservice.aidl.constant.SyncCardConstant;
import com.ccb.deviceservice.aidl.pinpad.Constant;
import com.sssoft.base.devices.bean.ScanTypeEnum;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.AllCard;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.Card;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.IPinpad;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.IPrinter;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.InsertCard;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.RfM1Card;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.Scanner;
import com.sssoft.base.devices.devices_driver_lib.interfaces.service_interface.IDevicesManager;

public class CcbDeviceManager implements IDevicesManager {
    public IDeviceService deviceBinder;
    IPrinter printer = null;
    IPinpad pinpad = null;
    Card card = null;
    InsertCard insertCard = null;
    RfM1Card rfM1Card = null;
    AllCard allCard = null;
    Scanner scanner = null;

    public CcbDeviceManager(IDeviceService deviceBinder) {
        this.deviceBinder = deviceBinder;
    }

    @Override
    public IPrinter getPrinter() {
        try {
            printer = new CcbPrinter(deviceBinder.getPrinter());
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
        return  printer;
    }

    @Override
    public IPinpad getPinpad() {
        Bundle bundle = new Bundle();
        bundle.putInt("kapId", 0);
        bundle.putString("deviceName", Constant.DeviceName.INNER);

        try {
            pinpad = new CcbPinpad(deviceBinder.getPinpadEx(bundle));
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
        return  pinpad;
    }

    @Override
    public Card getCard() {
        try {
            card = new CcbMagCard(deviceBinder.getMagCardReader());
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
        return  card;
    }

    @Override
    public InsertCard getInsertCard() {
        Bundle params = new Bundle();
        params.putInt("cardType", SyncCardConstant.CardType.SIM4428);
        params.putString("deviceName", ICDeviceName.USER);
        try {
            insertCard = new CcbCard4428(deviceBinder.getSyncCardReader(params)) ;
        } catch (RemoteException e) {
            insertCard = null;
        }
        return insertCard;
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
        int scanType = com.ccb.deviceservice.aidl.scanner.Constant.CameraID.BACK;
        try {
            switch (scanTypeEnum){
                case SCAN_BACK:
                    scanType = com.ccb.deviceservice.aidl.scanner.Constant.CameraID.BACK;
                    break;
                case SCANF_RONT:
                    scanType = com.ccb.deviceservice.aidl.scanner.Constant.CameraID.FRONT;
                    break;
            }
            scanner = new CcbScanner( deviceBinder.getScanner(scanType) );
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            scanner = null;
        }
        return  scanner;
    }
}
