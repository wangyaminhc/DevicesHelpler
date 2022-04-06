package com.sssoft.base.devices.devices_driver_lib.device_control_imp.newland;


import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;

import com.newland.mtype.Device;
import com.newland.mtype.ModuleType;
import com.newland.mtype.module.common.cardreader.K21CardReader;
import com.newland.mtype.module.common.swiper.K21Swiper;
import com.njabc.sdk.CardInfo;
import com.njabc.sdk.CardReaderListener;
import com.sssoft.base.devices.devices_driver_lib.interfaces.callback_interface.CardListener;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.Card;

public class NewlandCard implements Card {
    private CardListener cardlistener;
    private K21CardReader cardReader;
    private CardInfo cardInfo;
    private K21Swiper swiper;
    private int mTimeout =  60;
    private Device device;
    private Context mContext;

    public NewlandCard(Device device, Context mContext) {
        this.device = device;
        this.mContext = mContext;
        swiper = (K21Swiper) device.getStandardModule(ModuleType.COMMON_SWIPER);
        cardReader = (K21CardReader) device.getStandardModule(ModuleType.COMMON_CARDREADER);
        cardInfo = new CardInfo(device, mContext);
    }

    //磁条卡寻卡
    @Override
    public void read(int timeout, CardListener listener) throws RemoteException {
        cardlistener = listener;
        if( timeout > 0){
            mTimeout = timeout;
        }
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("supportMagCard", true);  //开启磁条卡类型
                    bundle.putBoolean("supportICCard", true);   //开启IC卡类型
                    bundle.putBoolean("supportRFCard", true);   //开启非接类型
                    cardInfo.getCardNumber(bundle, mTimeout, new CardReaderListener() {

                        // 读卡异常
                        // * <ul>
                        // * <li>ERROR_TIMEOT(1) - 读卡超时</li>
                        // * <li>ERROR_FAIL(2) - 读卡失败</li>
                        // * <li>ERROR_FALLBACK(3) -EMV降级 </li>
                        // * <li>ERROR_OTHER(100) - 其他错误</li>
                        // * </ul>
                        @Override
                        public void onError(int errorCode, String errorMsg) {
                            cardlistener.onError(errorCode + "", errorMsg);
                            cancel();

                        }

                        //读取卡号
                        @Override
                        public void onSuccess(String cardNo) {
                            String[] cardInfo = {cardNo};
                            cardlistener.onReadResult(cardInfo);
                            cancel();
                        }

                    });

                } catch (Exception e) {
                    cardlistener.onError("XX", e.getLocalizedMessage());
                    cancel();
                }

            }
        }).start();
    }

    //取消寻卡
    @Override
    public void cancel() {
        cardReader.cancelCardRead();
    }
}
