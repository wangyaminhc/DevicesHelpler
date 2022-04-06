package com.sssoft.base.devices.devices_driver_lib.device_control_imp.newland;


import android.os.RemoteException;

import com.newland.mtype.Device;
import com.newland.mtype.ModuleType;
import com.newland.mtype.module.common.rfcard.K21RFCardModule;
import com.newland.mtype.module.common.rfcard.RFCardType;
import com.newland.mtype.module.common.rfcard.RFKeyMode;
import com.newland.mtype.module.common.rfcard.RFResult;
import com.newland.mtype.util.ISOUtils;
import com.sssoft.base.devices.devices_driver_lib.interfaces.callback_interface.RfM1CardListener;
import com.sssoft.base.devices.devices_driver_lib.interfaces.device_control_interface.RfM1Card;
import com.sssoft.base.devices.util.SSDes;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class NewLandRfM1Card implements RfM1Card {
	private RfM1CardListener cardlistener;
	private int mTimeout = 60;
	private K21RFCardModule rfCardModule;
	private String mb0Auk;
	private String mb4Auk;
	private Device device;

	public NewLandRfM1Card(Device device){
		this.device = device;
		rfCardModule = (K21RFCardModule) device.getStandardModule(ModuleType.COMMON_RFCARDREADER);
	}

	//寻卡
	@Override
	public void read(int block, int sector, String blockAuk, String selectAuk, RfM1CardListener listener) throws RemoteException {
		cardlistener = listener;
		mb0Auk = blockAuk;
		mb4Auk = selectAuk;
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					int count = 0 ;
					String snrNo = "";
					for (int i = 0; i < mTimeout; i++) {
						try {
							List<RFCardType> cardTypeList=new ArrayList<RFCardType>();
							cardTypeList.add(RFCardType.M1CARD);
							RFResult qPResult = rfCardModule.powerOn(cardTypeList.toArray(new RFCardType[cardTypeList.size()]),1, TimeUnit.SECONDS);
							if(qPResult.getCardSerialNo()!=null){
								snrNo = ISOUtils.hexString(qPResult.getCardSerialNo());
								break;
							}
						} catch (Exception e) {
							count ++;
							continue;
						}
					}

					if(count==mTimeout){
						cardlistener.onError("XX", "读卡超时");
						cancel();

					}else{
						//外部认证0块
						byte snr[] = ISOUtils.hex2byte(snrNo);
						byte key[] = ISOUtils.hex2byte(mb0Auk);
						rfCardModule.authenticateByExtendKey(RFKeyMode.KEYA_0X60, snr, 0, key);
						//读1块
						byte output[] = rfCardModule.readDataBlock(1);
						String last16Card = convertHexToString(ISOUtils.hexString(output));
						//计算4块秘钥
						String[] miyao = ssGetKeys(last16Card, mb4Auk);
						//外部认证4块
						byte snr4[] = ISOUtils.hex2byte(snrNo);
						byte key4[] = ISOUtils.hex2byte(miyao[0]);
						rfCardModule.authenticateByExtendKey(RFKeyMode.KEYA_0X60, snr4, 4, key4);
						//读4,5,6块
						byte output4[] = rfCardModule.readDataBlock(4);
						byte output5[] = rfCardModule.readDataBlock(5);
						byte output6[] = rfCardModule.readDataBlock(6);
						String cardInfo = convertHexToString(ISOUtils.hexString(output4)+ ISOUtils.hexString(output5)+ ISOUtils.hexString(output6));
						Integer length = new Integer(cardInfo.substring(0, 2));
						String trk2 = cardInfo.substring(2, 2+length);
						String[] pans = trk2.split("=");
						String[] cardInfos = {pans[0],trk2};
//							cardlistener.onReadResult(cardInfos);
							cardlistener.onReadResult(pans[0]);
							cancel();
					}
				} catch (Exception e) {
						cardlistener.onError("XX", e.getLocalizedMessage());
					cancel();

				}

			}
		}).start();
	}


	public static String convertHexToString(String hex){
		 
		  StringBuilder sb = new StringBuilder();
		  StringBuilder temp = new StringBuilder();
	 
		  //49204c6f7665204a617661 split into two characters 49, 20, 4c...
		  for( int i=0; i<hex.length()-1; i+=2 ){
	 
		      //grab the hex in pairs
		      String output = hex.substring(i, (i + 2));
		      //convert hex to decimal
		      int decimal = Integer.parseInt(output, 16);
		      //convert the decimal to character
		      sb.append((char)decimal);
	 
		      temp.append(decimal);
		  }
		 
		  return sb.toString();
	}
	public static String[]  ssGetKeys(String pan, String key) {
		String str = "";
		String[] Keys = new String[2];
		try {
			str = SSDes.encHexStr3(pan, key);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Keys[0] = str.substring(0, 12);
		Keys[1] = str.substring(4, 16);
		return Keys;
	}
	//取消寻卡
	@Override
	public void cancel() {
		rfCardModule.powerOff(60);
	}
}
