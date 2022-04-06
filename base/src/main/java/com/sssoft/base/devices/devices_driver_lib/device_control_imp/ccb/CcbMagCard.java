package com.sssoft.base.devices.devices_driver_lib.device_control_imp.ccb;

import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import com.ccb.deviceservice.aidl.magreader.IMagCardReader;
import com.ccb.deviceservice.aidl.magreader.OnSearchListener;
import com.sssoft.base.devices.devices_driver_lib.interfaces.callback_interface.CardListener;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.Card;

public class CcbMagCard implements Card {

    private CardListener cardlistener;
    private IMagCardReader iMagCardReader;
    private int timeOut = 60;

    public CcbMagCard(IBinder binder) {
        iMagCardReader = IMagCardReader.Stub.asInterface(binder);
    }

    @Override
    public void read(int timeout, CardListener listener) throws RemoteException {
        cardlistener = listener;
        if( timeout > 0){
            timeOut = timeout;
        }
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    iMagCardReader.searchCard(timeOut, new OnSearchListener.Stub() {
                        @Override
                        public void onSuccess(Bundle track) {
                            if (track == null) {
                                cardlistener.onError("XX", "读卡失败，track信息未空");
                                cancel();

                            } else {
                                String[] cardInfo = {track.getString("PAN"), track.getString("TRACK2"), track.getString("TRACK3")};
                                cardlistener.onReadResult(cardInfo);
                                cancel();
                            }
                        }

                        @Override
                        public void onError(int error) {
                            cardlistener.onError("" + error, "读卡失败，请重试");
                            cancel();

                        }

                        @Override
                        public void onTimeout() {
                            cardlistener.onError("XX", "读卡超时");
                            cancel();

                        }
                    });
                } catch (RemoteException e) {
                    cardlistener.onError("XX", "读卡失败，请重试");
                    cancel();
                }
            }
        }).start();
    }

    @Override
    public void cancel() {
        try {
            iMagCardReader.stopSearch();
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
