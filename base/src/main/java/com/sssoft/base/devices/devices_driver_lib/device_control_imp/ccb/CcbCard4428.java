package com.sssoft.base.devices.devices_driver_lib.device_control_imp.ccb;

import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.ccb.deviceservice.aidl.constant.PowerupVoltage;
import com.ccb.deviceservice.aidl.data.BytesValue;
import com.ccb.deviceservice.aidl.data.IntValue;
import com.ccb.deviceservice.aidl.synccardreader.ISIM4428CardReader;
import com.sssoft.base.devices.devices_driver_lib.interfaces.callback_interface.InsertCardListener;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.InsertCard;
import com.sssoft.base.devices.util.ByteUtil;

public class CcbCard4428 implements InsertCard {

    private ISIM4428CardReader isim4428CardReader;
    private InsertCardListener mListener;
    // 自定义错误码:接口调用失败或成功
    private static final int FAIL = 0xff;
    private static final int SUCCESS = 0x00;

    private static final byte[] SC_PWD = null/*new byte[]{ 0x01, 0x02 }*/;
    private static final int READ_LENGTH = 45;
    private static final int ADDRESS = 32;

    public CcbCard4428(IBinder binder) {
        isim4428CardReader = ISIM4428CardReader.Stub.asInterface(binder);
    }

    public boolean cardPower()  {
        int ret = powerUp();
        if (ret == SUCCESS) {
            Log.i("dispiayInfo", "card power success");
            return true;
        } else {
            Log.e("dispiayInfo", "card power fail [ret = " + ret + "]");
            return false;
        }
    }

    private int powerUp()  {
        try {
            boolean exist = isim4428CardReader.exist();
            if (!exist) {
                Log.e("dispiayInfo","card not exist");
                return -1;
            }
            BytesValue atr = new BytesValue();
            boolean ret = isim4428CardReader.powerUp(PowerupVoltage.VOL_5, atr);
            if (ret) {
                Log.e("dispiayInfo","power up fail, error = " + ret);
            } else {
                Log.i("dispiayInfo","power up success, atr = " + ByteUtil.bytes2HexString(atr.getData()));
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            Log.e("dispiayInfo","request exception has ocurred");
        }
        return 0;
    }

    @Override
    public void read(int timeout, InsertCardListener listener) throws RemoteException {
        mListener = listener;
        if(!cardPower()) {
            mListener.onError(2+"", "请插卡！");
            return;
        }
        try {
            byte[] data = readcard(SC_PWD, ADDRESS, READ_LENGTH);
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



    @Override
    public void cancel() {
        cardHalt();
    }

    public void cardHalt() {
        int ret = powerDown();
        //halt();
        if (ret == SUCCESS) {
            Log.i("dispiayInfo","card halt success");
        } else {
            Log.e("dispiayInfo","card halt fail, error = " + ret);
        }
    }

    private int powerDown() {
        if (isim4428CardReader != null) {
            try {
                if(isim4428CardReader.powerDown()) {
                    return SUCCESS;
                } else {
                    return FAIL;
                }
            } catch (RemoteException e) {
                e.printStackTrace();
                Log.e("dispiayInfo","request exception has ocurred");
                return FAIL;
            }
        }
        return SUCCESS;
    }

    private byte[] readcard(byte[] pwd, int address, int len) {
        byte[] result = null;
        int ret = FAIL;
        try {
            if (pwd != null && pwd.length > 0) {
                IntValue pwdTimes = new IntValue();
                ret = isim4428CardReader.verify(pwd, pwdTimes);
                if (ret != SUCCESS) {
                    Log.i("dispiayInfo","verify fail, error = " + ret);
                    return null;
                }
            }
            BytesValue data = new BytesValue();
            ret = isim4428CardReader.read(address, len, data);
            if (ret != SUCCESS) {
                Log.i("dispiayInfo","read fail, error = " + ret);
                return null;
            }
            return data.getData();
        } catch (RemoteException e) {
            e.printStackTrace();
            Log.i("dispiayInfo","request exception has ocurred");
        }
        return result;
    }
}
