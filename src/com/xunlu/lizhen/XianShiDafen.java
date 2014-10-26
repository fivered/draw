package com.xunlu.lizhen;

import java.io.File;
import java.io.UnsupportedEncodingException;
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
import android.widget.Toast;

import com.conn.path.AllPath;
import com.date.ListMap;
import com.fax.utils.http.HttpUtils;
import com.fax.utils.task.ResultAsyncTask;
import com.google.gson.Gson;
import com.jiexi.GetRes;
import com.thread.m.Threads;
import com.util.ImageUtil;

public class XianShiDafen extends Activity {
	private static final int CAMERA = 0;
	private static final int PICTURE = 0;
	boolean flags = true;
	private ImageView fly;
	private ImageView imageView;
	private static int size;
	private static int resultCode;
	private static Uri img;
	
	public static int getSize() {
		return size;
	}
	public static void setSize(int size) {
		XianShiDafen.size = size;
	}
	public static int getResultCode() {
		return resultCode;
	}
	public static void setResultCode(int bianhao) {
		XianShiDafen.resultCode = bianhao;
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
		imageView = (ImageView) findViewById(R.id.iv_zzsm);

		BitmapFactory.Options opt = new BitmapFactory.Options();
        Intent reqInetIntent = getIntent();
//        ArrayList<String> uris = reqInetIntent.getStringArrayListExtra("Uris"); 
		img = reqInetIntent.getData();
		Bitmap newBitmap = null;
//		Bitmap bitmap = null;
		if(img!=null){
			opt.inSampleSize = 2;
//            bitmap=BitmapFactory.decodeFile(img.getPath());
            //newBitmap = ImageUtil.zoomBitmap(bitmap, bitmap.getWidth()/4, bitmap.getHeight()/4);
            newBitmap = BitmapFactory.decodeFile(img.getPath(), opt);
            imageView.setImageBitmap(newBitmap);
		}else{
			return;
		}
//		long avgNum = 0;
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
		}

		//handler1.post(thread);
		final TranslateAnimation animation = new TranslateAnimation(1024, 0, 0,
				0);
		animation.setDuration(4000);// 设置动画持续时间
		animation.setRepeatCount(1);// 设置重复次数
		animation.setRepeatMode(Animation.REVERSE);
		fly.setAnimation(animation);
		/** 开始动画 */
		animation.startNow();
		String scanResult = reqInetIntent.getStringExtra("scan_result");
		if (scanResult!=null){
			if(scanResult.length()>0){
				final String resultCode = reqInetIntent.getExtras().getString("scan_result");

				new ResultAsyncTask<SorcererResponse>(this) {

					@Override
					protected void onPostExecuteSuc(SorcererResponse result) {
						if(result.isOk()){
							startNextActivity(Long.valueOf(result.getBase_score()));
						}else{
							Toast.makeText(getApplicationContext(), result.getMsg(), Toast.LENGTH_SHORT).show();
						}
					}

					@Override
					protected SorcererResponse doInBackground(Void... params) {
						Map<String, ContentBody> pairMap = new HashMap<String, ContentBody>();
						try {
							pairMap.put("name", new StringBody(resultCode));
							pairMap.put("username", new StringBody(getIntent().getExtras().getString("username")));
							pairMap.put("phone", new StringBody(getIntent().getExtras().getString("userphon")));
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
						pairMap.put("score_img", new FileBody(new File(img.getPath()),"image/jpeg"));
						String result = HttpUtils.reqForPost(AllPath.getMakeMaxDifferenceUrl(), pairMap);
						return new Gson().fromJson(result, SorcererResponse.class);
					}
				}.setProgressDialog().execute();
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
            	intent.putExtras(getIntent().getExtras());
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

	/**
	 * 点击右上角返回主页面的方法；
	 * @param v
	 */
	public void backhome(View v) {
		Intent intent = new Intent(this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
	
	class GetGrade implements Runnable{
        String resultCode;
        long pixCount;
        Intent intent;
        Bitmap bitmap;
        public GetGrade(String resultCode) {
			super();
			this.resultCode = resultCode;
		}
        public GetGrade(int resultCode) {
			super();
			this.resultCode = String.valueOf(resultCode);
		}
        public GetGrade(String resultCode, long pixCount, Intent intent) {
			super();
			this.resultCode = resultCode;
			this.pixCount = pixCount;
			this.intent = intent;
		}
        public GetGrade(int resultCode, long pixCount) {
			super();
			this.resultCode = String.valueOf(resultCode);
			this.pixCount = pixCount;
		}
		public GetGrade(String resultCode, Bitmap tmpBmpBitmap, Intent intent) {
			super();
			this.resultCode = resultCode;
			this.intent = intent;
			bitmap=tmpBmpBitmap;
		}
		@Override
		public void run() {
			try {
				pixCount=ImageUtil.getPixCount(bitmap);
				String path = AllPath.getMakeMaxDifferenceUrl();
			    String result = Threads.getHttpMaxCountPix(path,resultCode,pixCount);
				
				long baseImgPixCount = 0;
				long maxImgPixDifference = 0;
				JSONObject jsonObject = GetRes.getJasonObject(result);
				baseImgPixCount = jsonObject.getLong("base_score");
				maxImgPixDifference = jsonObject.getLong("max");
				Looper mainLooper = Looper.getMainLooper();
				Handler handler = new Handler(mainLooper){

					@Override
					public void handleMessage(Message msg) {
						super.handleMessage(msg);
						if(msg.what==1){
							startNextActivity((Long)msg.obj);
						}
					}
				};
				long finalGrade = ImageUtil.gradeBitmap(bitmap);//grade(baseImgPixCount, maxImgPixDifference,pixCount);
				if(finalGrade < baseImgPixCount ) finalGrade += ( baseImgPixCount - finalGrade) ;
				if(finalGrade < 60 ) finalGrade = 60;
				if(finalGrade > 100) finalGrade =100;

				Message msg = Message.obtain();
				msg.what=1;
				msg.obj=Long.valueOf(finalGrade);
				handler.sendMessage(msg);
				path = AllPath.getListUrl();
				Uri imgUri = intent.getData();
				Map<String, ContentBody> pairMap = new HashMap<String, ContentBody>();
				pairMap.put("name", new StringBody(intent.getExtras().getString("username")));
				pairMap.put("pname", new StringBody(resultCode));
				pairMap.put("phone", new StringBody(intent.getExtras().getString("userphon")));
				pairMap.put("score", new StringBody(String.valueOf(finalGrade)));
				pairMap.put("key_score", new StringBody(String.valueOf(pixCount)));
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

	public void startNextActivity(long d){
		Intent ji = new Intent(XianShiDafen.this, smjg.class);
		ji.putExtras(getIntent().getExtras());
		ji.setData(getIntent().getData());
		if(d>=60&&d<70){
			String f = d+"";
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
