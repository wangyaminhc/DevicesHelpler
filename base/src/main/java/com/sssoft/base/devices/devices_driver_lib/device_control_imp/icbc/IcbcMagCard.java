package com.sssoft.base.devices.devices_driver_lib.device_control_imp.icbc;

import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import com.icbc.smartpos.deviceservice.aidl.IMagCardReader;
import com.icbc.smartpos.deviceservice.aidl.MagCardListener;
import com.sssoft.base.devices.devices_driver_lib.interfaces.callback_interface.CardListener;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.Card;

public class IcbcMagCard implements Card {
    private IMagCardReader iMagCardReader;
    private CardListener cardlistener;
    private int mTimeout = 60;

    public IcbcMagCard(IBinder binder) {
        iMagCardReader = IMagCardReader.Stub.asInterface(binder);
    }

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
                    iMagCardReader.searchCard(mTimeout, new MagCardListener.Stub() {

                        @Override
                        public void onSuccess(Bundle track)
                                throws RemoteException {
                            if(track==null){
                                cardlistener.onError("XX", "读卡失败，track信息未空");
                                cancel();

                            }else{
                                String[] cardInfo = {track.getString("PAN"),track.getString("TRACK2"),track.getString("TRACK3")};
                                if(cardInfo[0]==null || cardInfo[0].equals("") || "00000000000000000000".equals(cardInfo[0])){
                                    cardlistener.onError("XX", "读卡失败，卡号获取失败");
                                    cancel();

                                }else{
                                    cardlistener.onReadResult(cardInfo);
                                    cancel();

                                }
                            }
                        }

                        @Override
                        public void onTimeout() throws RemoteException {
                            cardlistener.onError("XX", "读卡超时");
                            cancel();

                        }

                        @Override
                        public void onError(int error, String message)
                                throws RemoteException {
                            cardlistener.onError(""+error, "读卡失败:"+message);
                            cancel();

                        }

                    });
                } catch (Exception e) {
                    try {
                        cardlistener.onError("XX", e.getLocalizedMessage());
                        cancel();
                    } catch (Exception e1) {}
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
