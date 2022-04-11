package com.sssoft.base.devices.devices_driver_lib.device_control_imp.boc;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;

import com.boc.aidl.deviceService.AidlDeviceService;
import com.sssoft.base.devices.bean.ScanTypeEnum;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.AllCard;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.Card;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.IPinpad;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.IPrinter;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.InsertCard;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.RfM1Card;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.Scanner;
import com.sssoft.base.devices.devices_driver_lib.interfaces.service_interface.IDevicesManager;

public class BocDeviceManager implements IDevicesManager {
    public AidlDeviceService deviceBinder;
    IPrinter printer = null;
    IPinpad pinpad = null;
    Card card = null;
    InsertCard insertCard = null;
    RfM1Card rfM1Card = null;
    AllCard allCard = null;
    Scanner scanner = null;
    Context mContext = null;

    public BocDeviceManager(AidlDeviceService deviceBinder) {
        this.deviceBinder = deviceBinder;
    }
    public BocDeviceManager(AidlDeviceService deviceBinder, Context context) {
        this.deviceBinder = deviceBinder;
        mContext = context;
    }

    @Override
    public IPrinter getPrinter() {
        try {
            printer = new BocPrinter(deviceBinder.getPrinter(), mContext);
        }catch (RemoteException e){
            e.printStackTrace();
        }
        return printer;
    }

    @Override
    public IPinpad getPinpad() {
        try {
            pinpad =new BocPinPad(deviceBinder.getPinpad());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return pinpad;
    }

    @Override
    public Card getCard() {
        try {
            card = new BocMagCard(deviceBinder.getCardReader());
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
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
    public Scanner getScanner(ScanTypeEnum typeEnum) {
        try {
            scanner = new BocScanner( deviceBinder.getScan(),typeEnum );
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
        Log.e("BocDevices","getScanner boc = "+scanner);

        return scanner;
    }
}
