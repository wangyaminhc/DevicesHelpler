package com.sssoft.base.devices.devices_driver_lib.device_control_imp.abc;

import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import com.sssoft.base.devices.devices_driver_lib.interfaces.callback_interface.CardListener;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.Card;
import com.zacloud.deviceservice.aidl.IMagCardReader;
import com.zacloud.deviceservice.aidl.MagCardListener;

public class AbcMagCard implements Card {
    private CardListener cardlistener;
    private IMagCardReader iMagCardReader;
    private int mTimeout;

    public AbcMagCard(IBinder binder) {
        iMagCardReader = IMagCardReader.Stub.asInterface(binder);
    }

    @Override
    public void read(int timeout, CardListener listener) throws RemoteException {
        cardlistener = listener;
        mTimeout = timeout;
        try {
            iMagCardReader.searchCard(mTimeout, new MagCardListener.Stub() {

                @Override
                public void onSuccess(Bundle track) {
                    if (track == null) {
                        cardlistener.onError("XX", "读卡失败，track信息未空");
                    } else {
                        String[] cardInfo = {track.getString("PAN"), track.getString("TRACK2"), track.getString("TRACK3")};
                        cardlistener.onReadResult(cardInfo);
                        cancel();
                    }
                }

                @Override
                public void onError(int error) {
                    cardlistener.onError("XX" + error, "读卡失败，请重试");
                    cancel();
                }

                @Override
                public void onTimeout() {
                    cardlistener.onError("XX", "读卡超时");
                    cancel();
                }

            });
        } catch (Exception e) {
            cardlistener.onError("XX", e.getLocalizedMessage());
        }
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
