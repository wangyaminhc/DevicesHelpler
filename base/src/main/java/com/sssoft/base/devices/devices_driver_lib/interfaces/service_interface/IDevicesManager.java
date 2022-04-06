package com.sssoft.base.devices.devices_driver_lib.interfaces.service_interface;


import com.sssoft.base.devices.bean.ScanTypeEnum;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.AllCard;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.Card;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.IPinpad;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.IPrinter;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.InsertCard;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.RfM1Card;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.Scanner;

public interface IDevicesManager {
    IPrinter getPrinter();
    IPinpad getPinpad();
    Card getCard();
    InsertCard getInsertCard();
    RfM1Card getRfM1Card();
    AllCard getAllCard();

    /**
     *@author wangyamin
     *@time 创建时间 2020/12/3 9:30
     * @Description 获取扫码
     * @param mode  SCANF_RONT 前置扫码 SCAN_BACK后置扫码
     */
    Scanner getScanner(ScanTypeEnum mode);
}
