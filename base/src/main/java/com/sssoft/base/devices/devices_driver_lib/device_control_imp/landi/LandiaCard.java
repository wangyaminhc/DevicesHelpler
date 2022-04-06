package com.sssoft.base.devices.devices_driver_lib.device_control_imp.landi;

import android.os.RemoteException;
import android.text.TextUtils;

import com.landicorp.android.eptapi.device.MagCardReader;
import com.landicorp.android.eptapi.exception.RequestException;
import com.sssoft.base.devices.devices_driver_lib.interfaces.callback_interface.CardListener;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.Card;

public class LandiaCard implements Card {
    private CardListener mListener;
    MagCardReader.OnSearchListener listener = new MagCardReader.OnSearchListener() {
        @Override
        public void onCrash() {
            //onDeviceServiceCrash();
        }

        @Override
        public void onFail(int code) {
                mListener.onError(code+"", getErrorDescription(code));
        }

        @Override
        public void onCardStriped(boolean[] hasTrack, String[] track) {
                if (TextUtils.isEmpty(track[1]) || !track[1].contains("=")) {// 不能为空 需有=号标志
                    mListener.onError("XX", "不符合银行卡规范");
                }else{
                    String[]  cardInfo = new String[3];
                    cardInfo[0] = getCardNo(track[1]);
                    cardInfo[1] = track[1];
                    cardInfo[2] = track[2];
                    mListener.onReadResult(cardInfo);
                }
            //displayMagCardInfo(infoBuilder.toString());
        }

        /**
         * Get msg of the error code for display
         * @param code
         * @return
         */
        public String getErrorDescription(int code) {
            switch (code) {
                case ERROR_NODATA:
                    return "no data";
                case ERROR_NEEDSTART:
                    return "need restart search";//This error never happened
                case ERROR_INVALID:
                    return "has invalid track";//
            }
            return "unknown error - " + code;
        }
    };

    public LandiaCard() {
    }

    @Override
    public void read(int timeout, CardListener listener) throws RemoteException {
        searchCard(timeout,listener);
    }

    @Override
    public void cancel() {
        stopSearch();
    }

    public void searchCard(int timeOut,CardListener listenertmp) {
        // start search card
        try {
            mListener = listenertmp;
            int enable = MagCardReader.TRK1 | MagCardReader.TRK2 | MagCardReader.TRK3;
            MagCardReader.getInstance().enableTrack(enable);
            MagCardReader.getInstance().setLRCCheckEnabled(true);
            MagCardReader.getInstance().searchCard(listener);

            //toast("Please stripe mag card!");
        } catch (RequestException e) {
            // the device service has a fatal exception
            e.printStackTrace();
            //displayInfo("request exception has ocurred");
        }
    }

    /**
     * Stop search if card searching is started
     */
    public void stopSearch() {
        try {
            MagCardReader.getInstance().stopSearch();
        } catch (RequestException e) {
            e.printStackTrace();
            //displayInfo("request exception has ocurred");
        }
    }

    public boolean isBankCard(String track2) {
        if (TextUtils.isEmpty(track2) || !track2.contains("=")) {// 不能为空 需有=号标志
            return false;
        }
        int separatorPosition = track2.indexOf("=");
        try {
            int year = Integer.parseInt(track2.substring(separatorPosition + 1, separatorPosition + 3));
            int month = Integer.parseInt(track2.substring(separatorPosition + 3, separatorPosition + 5));
            int serverCode = Integer.parseInt(track2.substring(separatorPosition + 5, separatorPosition + 8));
            for (int i = 0; i < separatorPosition; i++) { // 卡号为数字
                if (!Character.isDigit(track2.charAt(i))) {
                    return false;
                }
            }
            if (track2.length() <= 40 && track2.length() >= 21// 2磁道长度<=40 >=21
                    && separatorPosition >= 13 && separatorPosition <= 19 // 账号13到19之间
                    && (month > 0 || (month == 0 && year == 0)) && month < 13) { // 月份1到12,
                // 0000表示无失效日期
                return true;
            } else {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private String getCardNo(String track2) {
        return track2.substring(0, track2.indexOf("="));
    }

    private String getServiceCode(String track2) {
        int sepIndex = track2.indexOf('=');
        int serviceCodeIndex = sepIndex + 5;
        if (serviceCodeIndex + 3 > track2.length()) {
            return null;
        }
        return track2.substring(serviceCodeIndex, serviceCodeIndex + 3);
    }

    private String getExpiredDate(String track2) {
        int expiredDateStart = track2.indexOf('=') + 1;
        String expiredDate = track2.substring(expiredDateStart, expiredDateStart + 4);
        return expiredDate;
    }

}
