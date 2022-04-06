package com.sssoft.base.devices.util;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

public class BarCodeUtil {

	public static Bitmap creatBarCode(String contents, int widthPix, int heightPix, String imgPath) {
        try {  
        	int codeWidth = 3 + // start guard  
                    (7 * 6) + // left bars  
                    5 + // middle guard  
                    (7 * 6) + // right bars  
                    3; // end guard  
        	widthPix = Math.max(codeWidth, widthPix);
  
            BitMatrix bitMatrix = new MultiFormatWriter().encode(contents,  
                    BarcodeFormat.CODE_128, widthPix, heightPix, null);
            DriverUtil.Log(DriverUtil.LogLevel.Info,"dj","code end");
            int[] pixels = new int[widthPix * heightPix];
            // 下面这里按照条码的算法，逐个生成条码的图片，
            // 两个for循环是图片横列扫描的结果
            for (int y = 0; y < heightPix; y++) {
                for (int x = 0; x < widthPix; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * widthPix + x] = 0xff000000;
                    } else {
                        pixels[y * widthPix + x] = 0xffffffff;
                    }
                }
            }
 
            // 生成条码图片的格式，使用ARGB_8888
            Bitmap bitmap = Bitmap.createBitmap(widthPix, heightPix, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, widthPix, 0, 0, widthPix, heightPix);
            
            return bitmap;
   
        } catch (WriterException e){
            e.printStackTrace();
        } 
		return null;  
    }  
}
