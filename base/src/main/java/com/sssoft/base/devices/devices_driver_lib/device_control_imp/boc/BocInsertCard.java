package com.sssoft.base.devices.devices_driver_lib.device_control_imp.boc;

import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import com.boc.aidl.cardreader.AidlCardReader;
import com.boc.aidl.cardreader.AidlCardReaderListener;
import com.boc.aidl.constant.Const;
import com.boc.aidl.swiper.AidlSwiper;
import com.boc.aidl.swiper.SwiperResultInfo;
import com.sssoft.base.devices.devices_driver_lib.interfaces.callback_interface.CardListener;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.Card;

public class BocInsertCard implements Card {

    private AidlCardReader iCardReader;
    private AidlSwiper swiper;
    private CardListener cardlistener;
    private int mTimeout;

    public BocInsertCard(IBinder binder) {
        iCardReader = AidlCardReader.Stub.asInterface(binder);
        swiper = AidlSwiper.Stub.asInterface(binder);
    }

    @Override
    public void read(int timeout, CardListener listener) {
        cardlistener = listener;
        mTimeout = timeout;
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    iCardReader.openCardReader(Const.OpenCardType.ICCard,
                            false,
                            false,
                            mTimeout * 1000,
                            new AidlCardReaderListener.Stub() {
                                @Override
                                public void onError(int i, String s) throws RemoteException {
                                    cardlistener.onError("" + i, s);
                                }

                                @Override
                                public void onFindingCard(int i) throws RemoteException {
                                    SwiperResultInfo info = swiper.readPlainResult(i);
                                    Bundle track = new Bundle();
                                    track.putString("PAN", info.getAcctNo());
                                    track.putString("TRACK2", info.getSecondTrackData());
                                    track.putString("TRACK3", info.getThirdTrackData());

                                    if (track == null) {
                                        cardlistener.onError("XX", "读卡失败，track信息未空");
                                        cancel();

                                    } else {
                                        String[] cardInfo = {track.getString("PAN"), track.getString("TRACK2"), track.getString("TRACK3")};
                                        cardlistener.onReadResult(cardInfo);
                                        cancel();

                                    }
                                }
                            }
                    );
                } catch (Exception e) {
                    try {
                        cardlistener.onError("XX", e.getLocalizedMessage());
                        cancel();
                    } catch (Exception e1) {
                    }
                }
            }
        }).start();
    }

    @Override
    public void cancel() {
        try {
            iCardReader.cancelCardRead();
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
