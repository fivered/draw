package com.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;

import android.R.integer;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Environment;

import com.date.ListMap;

public class ImageUtil {
	/**
	 * 压缩图片
	 * @param bitmap
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap zoomBitmap(Bitmap bitmap,int width,int height){
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Matrix matrix = new Matrix();
		float scalewidth =(float) width/w;
		float scaleheight =(float) height/h;
		matrix.postScale(scalewidth, scaleheight);
		Bitmap newbit = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
	    ListMap.getLb().put(ListMap.getIndex(), newbit);
		return newbit;
	}
	
	/** 
	 * 图片转灰度 
	 * @param bmSrc 
	 * @return 
	 * @throws Exception 
	 */  
	public static Bitmap bitmap2Gray(Bitmap bmSrc) throws Exception  
	{  
		if (bmSrc==null){
			return null;
		}
	    int w=bmSrc.getWidth(), h=bmSrc.getHeight();
	    int[] pix = new int[w * h];
	    bmSrc.getPixels(pix, 0, w, 0, 0, w, h);

	    int alpha=0xFF<<24;
	    Bitmap result= bmSrc.copy(Bitmap.Config.ARGB_8888, true);
	    for (int i = 0; i < h; i++) {
		    for (int j = 0; j < w; j++) {
			    // 获得像素的颜色
			    int color = bmSrc.getPixel(j, i);
			    int red = Color.red(color); //((color & 0x00FF0000) >> 16);
			    int green = Color.green(color); //((color & 0x0000FF00) >> 8);
			    int blue = Color.blue(color); //color & 0x000000FF;
			    int gray = (int)((float)red*0.3+(float)green*0.59+(float)blue*0.11);
			    gray = ((float)gray*1.4f) > 255f ? 255 : (int)((float)gray*1.4f);
			    int newcolor = alpha | (gray << 16) | (gray << 8) | gray;
			    result.setPixel(j, i, newcolor);
		    }
	    }
	    String path = initPath();
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();    
	    result.compress(Bitmap.CompressFormat.PNG, 100, baos);
	    //data2file(baos.toByteArray(),path);
	    return result;
	}
	
	/**
	 * 获取图中所有像素累加值
	 * @return percent
	 * @throws Exception 
	 */
	public static long getPixCount(Bitmap bm) throws Exception{
		Bitmap grayBm = ImageUtil.bitmap2Gray(bm);
		Bitmap Bm = grayBm;
		long gotPoint = 0;
		long percent = 0;
		if (Bm!=null)
		{
			int h = Bm.getHeight();
			int w = Bm.getWidth();
			for (int y = 0; y < h; y++) {
				for (int x = 0; x < w; x++) {
					int color = Bm.getPixel(x, y);
					int r = Color.red(color);
					int g = Color.green(color);
					int b = Color.blue(color);
					gotPoint+=r;
				}
			}
			percent = gotPoint ;
		}
		return percent;
	}
	
	public static String initPath(){
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		String time = format.format(date);
		// 在SD卡上创建文件夹
		File file = new File(Environment.getExternalStorageDirectory()
				+ "/myCamera/pics/gray");
		if (!file.exists()) {
			file.mkdirs();
		}
		String path = file.getPath()+ "/" + time + "gray.jpg";
		return path;
	}
	
    /**
     * 保存入磁盘
     * @param data
     * @param path
     * @throws Exception
     */
    public static void data2file(byte[] data, String path) throws Exception {
    	    FileOutputStream out = null;
			out = new FileOutputStream(path);
			out.write(data);
			out.close();
	}
	
}