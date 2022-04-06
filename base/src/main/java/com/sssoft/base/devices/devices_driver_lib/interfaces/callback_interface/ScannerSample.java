package com.sssoft.base.devices.devices_driver_lib.interfaces.callback_interface;

import android.content.Context;
import android.content.Intent;


import com.sssoft.base.devices.activity.CaptureActivity;
import com.sssoft.base.devices.devices_driver_lib.AIDLService;

import java.util.HashMap;

public class ScannerSample {
	private String qrcode;
	private final static int SCANNIN_GREQUEST_CODE = 1;
	//private AidlScanner scannerModule;
	private HashMap<Integer, Integer> rcMap = new HashMap<Integer, Integer>();
	private static ScannerListener lst;


	public static ScannerListener getLst() {
		return lst;
	}

	public static void setLst(ScannerListener lst) {
		lst = lst;
	}

	public void startScan(int scanType, int timeout, final ScannerListener listener){
		lst = listener;
		Intent intent = new Intent();
		Context sContext = AIDLService.getmContext();

		intent.setClass(sContext, CaptureActivity.class);
		//intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		//startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
		sContext.startActivity(intent);
	}
		
	public void stopScan(){
		
	}
	
}