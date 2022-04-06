package com.sssoft.base.devices.devices_driver_lib.device_control_imp.abc;

import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.sssoft.base.devices.bean.OtherUtil;
import com.sssoft.base.devices.devices_driver_lib.interfaces.callback_interface.RfM1CardListener;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.RfM1Card;
import com.zacloud.deviceservice.aidl.IRFCardReader;
import com.zacloud.deviceservice.aidl.RFSearchListener;

public class AbcRmf1Card implements RfM1Card {
    private RfM1CardListener cardlistener;
    private IRFCardReader irfCardReader;
    private int block;
    private int selector;
    private String keyBlock;
    private String keySelector;
    private static final int KEY_A = 0x01;
    private static final int KEY_B = 0x02;

    public AbcRmf1Card(IBinder binder) {
        irfCardReader = IRFCardReader.Stub.asInterface(binder);
    }

    @Override
    public void read(int block1,int sector,String blockAuk,String selectAuk,  RfM1CardListener cardlistener1 ) {
        cardlistener = cardlistener1;
        this.block = block1;
        this.selector = sector;
        keyBlock = blockAuk;
        keySelector = selectAuk;
        try {
            irfCardReader.searchCard(new RFSearchListener.Stub() {

                @Override
                public void onCardPass(int arg0) {
                    cardlistener.onReadResult( getCardNo(getRfCardType(arg0,1),block,selector,keyBlock,keySelector));
                    cancel();
                }

                @Override
                public void onFail(int arg0, String arg1) {
                    cardlistener.onError("XX" + arg0, "M1卡寻卡失败，请重试");
                    cancel();
                }
            });
        } catch (Exception e) {
            cardlistener.onError("XX", e.getLocalizedMessage());
            cancel();
        }
    }


    private String getRfCardType(int cardType,int flag) {
        switch (cardType) {
            case 0:
                if(flag == 0) {
                    return "S50_CARD";
                } else {
                    return "S50";
                }
            case 1:
                if(flag == 0) {
                    return "S70_CARD";
                } else {
                    return "S70";
                }
            case 2:
                if(flag == 0) {
                    return "PRO_CARD";
                } else {
                    return "PRO";
                }
            case 3:
                if(flag == 0) {
                    return "S50_PRO_CARD";
                } else {
                    return "PRO";
                }
            case 4:
                if(flag == 0) {
                    return "S70_PRO_CARD";
                } else {
                    return "PRO";
                }
            case 5:
                if(flag == 0) {
                    return "CPU_CARD";
                } else {
                    return "CPU";
                }

            default:
                return null;
        }
    }

    private String getCardNo(String cardType,int block,int sector,String blockAuk,String selectAuk) {
        byte[] aData = new byte[8];
        byte[] data = new byte[50];
        try {
            int actResult = irfCardReader.activate(cardType, aData);//激活
//            Log.i("test","actResult:"+actResult);
            byte[] keyA = OtherUtil.hexString2Bytes(blockAuk);
            byte[] keyB = OtherUtil.hexString2Bytes(selectAuk);

            int asResult = irfCardReader.authSector(sector, KEY_A, keyA);//认证扇区
            Log.i("test","asResult:"+asResult);

            int abResult = irfCardReader.authBlock(block, KEY_A, keyB);//认证块

//			int abResult = M1CardReader.authBlock(1, KEY_B, keyB);
            Log.i("test","abResult:"+abResult);

            irfCardReader.readBlock(block, data);//读取卡内数据

        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String pin = OtherUtil.bytes2HexString(aData);
        Log.i("test","M1pin:"+pin);
        String cardNo = OtherUtil.bytes2HexString(data);
        Log.i("test","M1cardNo:"+cardNo);
        return cardNo;
    }



    @Override
    public void cancel() {
        try {
            irfCardReader.stopSearch();
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
