package com.xunlu.lizhen;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.conn.path.AllPath;
import com.date.ListMap;
import com.jiexi.GetRes;
import com.thread.m.Threads;
import com.util.ImageUtil;

public class XianShiDafen extends Activity {
	private static final int CAMERA = 0;
	private static final int PICTURE = 0;
	boolean flags = true;
	private ImageView fly;
	private ImageView iv;
	private static int size;
	private static int bianhao;
	private static Uri img;
	
	public static int getSize() {
		return size;
	}
	public static void setSize(int size) {
		XianShiDafen.size = size;
	}
	public static int getBianhao() {
		return bianhao;
	}
	public static void setBianhao(int bianhao) {
		XianShiDafen.bianhao = bianhao;
	}
	private static List<Bitmap> lmap = null;
	public static List<Bitmap> getLmap() {
		return lmap;
	}
	public static void setLmap(List<Bitmap> lmap) {
		XianShiDafen.lmap = lmap;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// 正在扫描
		super.onCreate(savedInstanceState);
		setContentView(R.layout.zzsm);
		fly = (ImageView) findViewById(R.id.flys);
		iv = (ImageView) findViewById(R.id.iv_zzsm);

        Intent reqInetIntent = getIntent();
        ArrayList<String> uris = reqInetIntent.getStringArrayListExtra("Uris"); 
		img = reqInetIntent.getData();
		Bitmap newBitmap = null;
		Bitmap bitmap = null;
		if(img!=null){
            bitmap=BitmapFactory.decodeFile(img.getPath());
            newBitmap = ImageUtil.zoomBitmap(bitmap, bitmap.getWidth()/4, bitmap.getHeight()/4);
            iv.setImageBitmap(newBitmap);
            //bitmap

		}else{
			return;
		}
		long avgNum = 0;
		if (newBitmap != null)
		{

			SharedPreferences.Editor spfEditer;
			SharedPreferences spf;
			spf = ((Context)this).getSharedPreferences("shezhiValue", 0);
			spfEditer = spf.edit();
			int value = 450;
			if (spf.getString("ic", "").equals("1")) {
				value = spf.getInt("value", 450);
			}
			try {
				if(uris!=null){
					for (int i = 0; i < uris.size(); i++) {
						String pathString = uris.get(i);
						if (pathString==null){
							break;
						}
						if(pathString.length()<1){
							break;
						}
			            Bitmap tmpBmp = ImageUtil.zoomBitmap(bitmap, bitmap.getWidth()/6, bitmap.getHeight()/6);
			            long tmp = ImageUtil.getGrayPercent(tmpBmp, value);
						if(avgNum ==0){
							avgNum = tmp;
						}else{
							avgNum = (avgNum+tmp)/2;
						}
					}
				}else{
					Bitmap tmpBmpBitmap = ImageUtil.zoomBitmap(bitmap, bitmap.getWidth()/6, bitmap.getHeight()/6);
					avgNum = ImageUtil.getGrayPercent(tmpBmpBitmap, value);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		handler1.post(thread);
//		if(null != ListMap.getLb().get(0)){
//			iv.setImageBitmap(ListMap.getLb().get(0));
//		}
		final TranslateAnimation animation = new TranslateAnimation(1024, 0, 0,
				0);
		animation.setDuration(4000);// 设置动画持续时间
		animation.setRepeatCount(1);// 设置重复次数
		animation.setRepeatMode(Animation.REVERSE);
		// 设置反方向执行
		// start.setOnClickListener(new OnClickListener() {
		// public void onClick(View arg0) {
		fly.setAnimation(animation);
		/** 开始动画 */
		animation.startNow();
		String scanResult = reqInetIntent.getStringExtra("scan_result");
		if (scanResult!=null){
			if(scanResult.length()>0){
				Log.e("::::::", scanResult.length()+"");
				new Thread(new GetFen(reqInetIntent.getExtras().getString("scan_result"), avgNum, reqInetIntent)).start();
			}
		}
	}
	/**
	 * 打开新活动handle
	 */
	Handler handler1 = new Handler() {
		public void handleMessage(Message msg) {
			if(msg.what==0) {
				Log.i("XianShiDafen","");
			}else{
            	Intent intent = new Intent(XianShiDafen.this, smjg.class);
            	intent.setData(img);
				startActivity(intent);
			}
		 }
	};
	
	Runnable thread = new Runnable() {
		@Override
		public void run() {
			Message msg = new Message();
			if (flags) {
				handler1.postDelayed(thread, 4000);
				msg.arg1 = 0;
				flags = false;
			} else {
				msg.arg1 = 1;
			}
			handler1.sendMessage(msg);
		};

	};

	// protected void onStart() {
	// iv.setImageBitmap(lmap.get(0));
	// };
	/**
	 * 点击右上角返回主页面的方法；
	 * @param v
	 */
	public void backhome(View v) {
		Intent intent = new Intent(this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
	
	public long grade(long base, long difference, long curr){
		double value = 0.00d;
		long currDiff = Math.abs(base-curr);
		if(currDiff==difference){
			return 100;
		}
		value = currDiff/(difference/40);
		value+=60;
		if(value<60){
			value = 60.00d;
		}
		return (long)value;
	}
	
	// //下面是相应的回调方法：
	// @Override
	// protected void onActivityResult(int requestCode, int resultCode, Intent
	// data) {
	// super.onActivityResult(requestCode, resultCode, data);
	// Log.e("dddddd","dddddddddddd");
	// if(requestCode == CAMERA && resultCode == Activity.RESULT_OK && null !=
	// data){
	// Intent picture = new
	// Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
	// startActivityForResult(picture, PICTURE);
	// Uri selectedImage = data.getData();
	// String[] filePathColumns={MediaStore.Images.Media.DATA};
	// Cursor c = this.getContentResolver().query(selectedImage,
	// filePathColumns, null,null, null);
	// c.moveToFirst();
	// int columnIndex = c.getColumnIndex(filePathColumns[0]);
	// String picturePath= c.getString(columnIndex);
	// iv.setImageBitmap(lmap.get(0));
	// c.close();
	// //获取图片并显示
	//
	// }
	class GetFen implements Runnable{
        String bianhao;
        long percent;
        Intent intent;
        public GetFen(String bianhao) {
			super();
			this.bianhao = bianhao;
		}
        public GetFen(int bianhao) {
			super();
			this.bianhao = String.valueOf(bianhao);
		}
        public GetFen(String bianhao, long percent, Intent intent) {
			super();
			this.bianhao = bianhao;
			this.percent = percent;
			this.intent = intent;
		}
        public GetFen(int bianhao, long percent) {
			super();
			this.bianhao = String.valueOf(bianhao);
			this.percent = percent;
		}
		@Override
		public void run() {
			Log.e("genegen", "我去获取分数了-----");
			try {
				String path = AllPath.Get();
			    String result = Threads.GetINPutStream(path,bianhao,percent);
				
				long f = 0;
				long max = 0;
				JSONObject jsonObject = GetRes.getJasonObject(result);
				f = jsonObject.getLong("base_score");
				max = jsonObject.getLong("max");
				Looper mainLooper = Looper.getMainLooper();
				Handler handler = new Handler(mainLooper){

					@Override
					public void handleMessage(Message msg) {
						// TODO Auto-generated method stub
						super.handleMessage(msg);
						if(msg.what==1){
							Tiao((Long)msg.obj);
						}
					}
				};
				long gd = grade(f, max,percent);
				Message msg = Message.obtain();
				msg.what=1;
				msg.obj=Long.valueOf(gd);
				handler.sendMessage(msg);
				path = AllPath.getListUrl();
				Uri imgUri = intent.getData();
				Map<String, ContentBody> pairMap = new HashMap<String, ContentBody>();
				pairMap.put("name", new StringBody(intent.getExtras().getString("username")));
				pairMap.put("pname", new StringBody(bianhao));
				pairMap.put("phone", new StringBody(intent.getExtras().getString("userphon")));
				pairMap.put("score", new StringBody(String.valueOf(gd)));
				pairMap.put("key_score", new StringBody(String.valueOf(percent)));
				if (imgUri!=null){
					pairMap.put("image", new FileBody(new File(imgUri.getPath()),"image/jpeg"));
				}
				boolean isSuccess = Threads.setListResult(path, pairMap);
				Thread.sleep(1000);
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}	
		}
	}

	public void Tiao(long d){
		Intent ji = new Intent(XianShiDafen.this, smjg.class);
		ji.setData(getIntent().getData());
		if(d>=60&&d<70){
			String f = 60+"";
			String txt = "继续加油！很有潜力哦……";
			ji.putExtra("score", f);
			ji.putExtra("txt", txt);
			ji.putExtra("wav", 2);
		}
		if(d>=70&&d<=80){
			String f = d+"";
			String txt = "画的不错，很有天赋！";
			ji.putExtra("score", f);
			ji.putExtra("txt", txt);
			ji.putExtra("wav", 3);
		}
		if(d>80){
			String f = d+"";
			String txt = "成绩太好了，有画家的潜质！";
			ji.putExtra("score", f);
			ji.putExtra("txt", txt);
			ji.putExtra("wav", 4);
		}
		
		startActivity(ji);
	}
	
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
	 */  
	public static Bitmap bitmap2Gray(Bitmap bmSrc)  
	{  
		if (bmSrc==null){
			return null;
		}
	    int width, height;  
	    height = bmSrc.getHeight();  
	    width = bmSrc.getWidth();  
	    Bitmap bmpGray = null;  
	    bmpGray = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);  
	    Canvas c = new Canvas(bmpGray);  
	    Paint paint = new Paint();  
	    ColorMatrix cm = new ColorMatrix();  
	    cm.setSaturation(0);  
	    ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);  
	    paint.setColorFilter(f);  
	    c.drawBitmap(bmSrc, 0, 0, paint);  
	  
	    return bmpGray;  
	}
}
