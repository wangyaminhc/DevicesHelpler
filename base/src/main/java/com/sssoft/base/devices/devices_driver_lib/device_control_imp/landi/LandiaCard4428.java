package com.sssoft.base.devices.devices_driver_lib.device_control_imp.landi;

import android.os.RemoteException;
import android.util.Log;

import com.landicorp.android.eptapi.card.Sim4428Driver;
import com.landicorp.android.eptapi.exception.RequestException;
import com.landicorp.android.eptapi.utils.BytesBuffer;
import com.landicorp.android.eptapi.utils.IntegerBuffer;
import com.sssoft.base.devices.devices_driver_lib.interfaces.callback_interface.InsertCardListener;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.InsertCard;
import com.sssoft.base.devices.util.ByteUtil;

public class LandiaCard4428 implements InsertCard {

    private Sim4428Driver driver;

    // 自定义错误码:接口调用失败或成功
    private static final int FAIL = 0xff;
    private static final int SUCCESS = 0x00;

    private InsertCardListener mListener;
    private static final byte[] SC_PWD = null/*new byte[]{ 0x01, 0x02 }*/;
    private static final int READ_LENGTH = 45;
    private static final int ADDRESS = 32;

    public LandiaCard4428() {
        driver = new Sim4428Driver();
    }

    @Override
    public void read(int timeout, InsertCardListener listener) throws RemoteException {
        searchCard(timeout,listener);
    }

    @Override
    public void cancel() throws RemoteException {
        stopSearch();
    }

    public boolean cardPower() {
        int ret = powerUp();
        if (ret == SUCCESS) {
            Log.i("dispiayInfo", "card power success");
            return true;
        } else {
            Log.e("dispiayInfo", "card power fail [ret = " + ret + "]");
            return false;
        }
    }

    private int powerUp() {
        int ret =FAIL;
        try {
            boolean exist = driver.exists();
            if (!exist) {
                Log.e("dispiayInfo","card not exist");
                return ret;
            }
            BytesBuffer atr = new BytesBuffer();
            ret = driver.powerUp(Sim4428Driver.VOL_5, atr);
            if (ret != SUCCESS) {
                Log.e("dispiayInfo","power up fail, error = " + ret);
            } else {
                Log.i("dispiayInfo","power up success, atr = " + ByteUtil.bytes2HexString(atr.getData()));
            }
        } catch (RequestException e) {
            e.printStackTrace();
            Log.e("dispiayInfo","request exception has ocurred");
        }
        return ret;
    }

    public void cardHalt() {
        int ret = powerDown();
        halt();
        if (ret == SUCCESS) {
            Log.i("dispiayInfo","card halt success");
        } else {
            Log.e("dispiayInfo","card halt fail, error = " + ret);
        }
    }

    private int powerDown() {
        if (driver != null) {
            try {
                return driver.powerDown();
            } catch (RequestException e) {
                e.printStackTrace();
                Log.e("dispiayInfo","request exception has ocurred");
                return FAIL;
            }
        }
        return SUCCESS;
    }

    private void halt() {
        if (driver != null) {
            try {
                driver.halt();
            } catch (RequestException e) {
                e.printStackTrace();
                Log.i("dispiayInfo","request exception has ocurred");
            }
        }
    }

    public byte[] read(byte[] pwd, int address, int len) {
        byte[] result = null;
        int ret = FAIL;
        try {
            if (pwd != null && pwd.length > 0) {
                IntegerBuffer pwdTimes = new IntegerBuffer();
                ret = driver.verify(pwd, pwdTimes);
                if (ret != SUCCESS) {
                    Log.i("dispiayInfo","verify fail, error = " + ret);
                    return null;
                }
            }
            BytesBuffer data = new BytesBuffer();
            ret = driver.read(address, len, data);
            if (ret != SUCCESS) {
                Log.i("dispiayInfo","read fail, error = " + ret);
                return null;
            }
            return data.getData();
        } catch (RequestException e) {
            e.printStackTrace();
            Log.i("dispiayInfo","request exception has ocurred");
        }
        return result;
    }

    public int write(byte[] pwd, int address, byte[] data) {
        int ret = FAIL;
        try {
            if (pwd != null && pwd.length > 0) {
                IntegerBuffer pwdTimes = new IntegerBuffer();
                ret = driver.verify(pwd, pwdTimes);
                if (ret != SUCCESS) {
                    Log.i("dispiayInfo","verify fail, error = " + ret);
                    return ret;
                }
            }
            return driver.write(Sim4428Driver.MODE_ENABLE, address, data);
        } catch (RequestException e) {
            e.printStackTrace();
            Log.i("dispiayInfo","request exception has ocurred");
        }
        return ret;
    }

    public int changePassword(byte[] originalPwd, byte[] newPwd) {
        int ret = FAIL;
        try {
            if (originalPwd != null && originalPwd.length > 0) {
                IntegerBuffer pwdTimes = new IntegerBuffer();
                ret = driver.verify(originalPwd, pwdTimes);
                if (ret != SUCCESS) {
                    Log.i("dispiayInfo","verify fail, error = " + ret);
                    return ret;
                }
                return driver.changeKey(newPwd);
            } else {
                return ret;
            }
        } catch (RequestException e) {
            e.printStackTrace();
            Log.i("dispiayInfo","request exception has ocurred");
        }
        return ret;
    }

    public void searchCard(int timeOut, InsertCardListener listenertmp) throws RemoteException {
        //Log.i("searchCard", "before searchCard");
        mListener = listenertmp;
        if(!cardPower()) {
            mListener.onError(2+"", "请插卡！");
            return;
        }
        try {
            byte[] data = read(SC_PWD, ADDRESS, READ_LENGTH);
            if(data!=null) {
                //mListener.onReadResult(getCardNo(new String(data)));
                //Log.e("searchCard", "data=" + new String(data, "ISO8859-1"));
                mListener.onReadResult(new String(data, "ISO8859-1"));
            } else {
                mListener.onError(-1+"", "读卡失败");
            }
        } catch (Exception e) {
            mListener.onError(-2+"", e.getLocalizedMessage());
        }
    }

    /**
     * Stop search if card searching is started
     */
    public void stopSearch() {
        cardHalt();
    }

    private String getCardNo(String track2) {
        return track2.substring(0, track2.indexOf("="));
    }
}
