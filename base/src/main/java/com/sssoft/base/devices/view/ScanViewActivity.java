package com.sssoft.base.devices.view;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.Window;

import com.loong.base.R;
import com.newland.mtype.module.common.scanner.ScannerListener;
import com.sssoft.base.devices.devices_driver_lib.device_control_imp.newland.NewlandScanner;

import java.util.concurrent.TimeUnit;

public class ScanViewActivity extends Activity {
	
	private SurfaceView surfaceView;
	private Context context;
	private int timeout;
	private boolean isFinish=true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.sacn_view);
		context=this;
		timeout = getIntent().getIntExtra("timeout", 60);
		init();
	}
	
	
	
	private void init() {
		surfaceView=(SurfaceView) findViewById(R.id.surfaceView);
		startScan();
	}
	
	private void startScan(){
		try{
			NewlandScanner.scanner.initScanner(context,surfaceView,0x00);
			NewlandScanner.scanner.startScan(timeout, TimeUnit.SECONDS, new ScannerListener() {

				@Override
				public void onResponse(String[] barcodes) {
					isFinish=true;
						NewlandScanner.listener.onScanResult(barcodes[0]);
				}

				@Override
				public void onFinish() {
						NewlandScanner.listener.onFinish();
						if(isFinish){
							finish();
						}
				}
			},true);
		}catch(Exception e){
				NewlandScanner.listener.onError("XX", "scan err :"+e.getLocalizedMessage());
				finish();
		}
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		if (keyCode == KeyEvent.KEYCODE_BACK){//返回键监听
			NewlandScanner.scanner.stopScan();
			NewlandScanner.listener.onFinish();
            finish();
         }
         return super.onKeyDown(keyCode, event);
		
	}
}
