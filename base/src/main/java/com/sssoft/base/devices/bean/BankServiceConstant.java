package com.sssoft.base.devices.bean;


import com.sssoft.base.devices.devices_driver_lib.bank_service_imp.AbcBankService;
import com.sssoft.base.devices.devices_driver_lib.bank_service_imp.BocBankService;
import com.sssoft.base.devices.devices_driver_lib.bank_service_imp.CcbBankService;
import com.sssoft.base.devices.devices_driver_lib.bank_service_imp.IcbcBankService;
import com.sssoft.base.devices.devices_driver_lib.bank_service_imp.UmsBankService;

import java.util.HashMap;

/**
 *@author wangyamin
 *@time 创建时间 2020/11/30 9:01
 * @Description 银行的包名和银行枚举类型的对应关系
 *
 */
public class BankServiceConstant {
    //bank package
    public static HashMap<BankEnum,String> bankMap = new HashMap<>();

    static {
        bankMap.put(BankEnum.ABC, AbcBankService.PACKAGE_DEVICE_SERVICE);
        bankMap.put(BankEnum.BOC, BocBankService.PACKAGE_DEVICE_SERVICE);
        bankMap.put(BankEnum.CCB, CcbBankService.PACKAGE_DEVICE_SERVICE);
        bankMap.put(BankEnum.ICBC, IcbcBankService.PACKAGE_DEVICE_SERVICE);
        bankMap.put(BankEnum.UMS, UmsBankService.PACKAGE_DEVICE_SERVICE);
    }
}
