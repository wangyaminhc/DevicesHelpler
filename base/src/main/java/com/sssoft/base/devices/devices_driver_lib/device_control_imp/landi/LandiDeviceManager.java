package com.sssoft.base.devices.devices_driver_lib.device_control_imp.landi;

import android.content.Context;

import com.sssoft.base.devices.bean.ScanTypeEnum;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.AllCard;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.Card;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.IPinpad;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.IPrinter;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.InsertCard;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.RfM1Card;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.Scanner;
import com.sssoft.base.devices.devices_driver_lib.interfaces.service_interface.IDevicesManager;

public class LandiDeviceManager implements IDevicesManager {
    private Context mContext;
    IPrinter printer = null;
    IPinpad pinpad = null;
    Card card = null;
    InsertCard insertCard = null;
    RfM1Card rfM1Card = null;
    AllCard allCard = null;
    Scanner scanner = null;

    public LandiDeviceManager(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public IPrinter getPrinter() {
        return new LandiPrinter(mContext);
    }

    @Override
    public IPinpad getPinpad() {
        return new LandiPinPad();
    }

    @Override
    public Card getCard() {
        return new LandiaCard();
    }

    @Override
    public InsertCard getInsertCard() {
        return new LandiaCard4428();
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
        return new LandiScanner(mContext,  scanType);
    }
}
