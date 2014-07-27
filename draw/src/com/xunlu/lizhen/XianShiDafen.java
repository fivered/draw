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
import android.graphics.BitmapFactory.Options;
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
		// ����ɨ��
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
            //bitmap

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
//				if(uris!=null){
//					for (int i = 0; i < uris.size(); i++) {
//						String pathString = uris.get(i);
//						if (pathString==null){
//							break;
//						}
//						if(pathString.length()<1){
//							break;
//						}
//			            Bitmap tmpBmp = ImageUtil.zoomBitmap(bitmap, bitmap.getWidth()/6, bitmap.getHeight()/6);
//			            long tmp = ImageUtil.getPixCount(tmpBmp);
//						if(avgNum ==0){
//							avgNum = tmp;
//						}else{
//							avgNum = (avgNum+tmp)/2;
//						}
//					}
//				}else{
				//Bitmap tmpBmpBitmap = ImageUtil.zoomBitmap(bitmap, bitmap.getWidth()/6, bitmap.getHeight()/6);
//				opt.inSampleSize = 6;
//				Bitmap tmpBmpBitmap = BitmapFactory.decodeFile(img.getPath(), opt);
//				avgNum = ImageUtil.getPixCount(tmpBmpBitmap);
//				}
		}

		handler1.post(thread);
		final TranslateAnimation animation = new TranslateAnimation(1024, 0, 0,
				0);
		animation.setDuration(4000);// ���ö�������ʱ��
		animation.setRepeatCount(1);// �����ظ�����
		animation.setRepeatMode(Animation.REVERSE);
		// ���÷�����ִ��
		// start.setOnClickListener(new OnClickListener() {
		// public void onClick(View arg0) {
		fly.setAnimation(animation);
		/** ��ʼ���� */
		animation.startNow();
		String scanResult = reqInetIntent.getStringExtra("scan_result");
		if (scanResult!=null){
			if(scanResult.length()>0){
				Log.e("::::::", scanResult.length()+"");
				String resultCode = reqInetIntent.getExtras().getString("scan_result");

				opt.inSampleSize = 2;
				Bitmap tmpBmpBitmap = BitmapFactory.decodeFile(img.getPath(), opt);
				GetGrade getGradeThread = new GetGrade(resultCode, tmpBmpBitmap, reqInetIntent);
				new Thread(getGradeThread).start();
			}
		}
	}
	/**
	 * ���»handle
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
	 * ������ϽǷ�����ҳ��ķ�����
	 * @param v
	 */
	public void backhome(View v) {
		Intent intent = new Intent(this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
	
	
//	/**
//	 * 
//	 * @param basePixCount	 ��׼ͼ�������ۼ�ֵ
//	 * @param maxDifference  ������ֵ
//	 * @param currPixCount	 ��ǰͼ�������ۼ�ֵ
//	 * @return
//	 */
//	public long grade(long basePixCount, long maxDifference, long currPixCount){
//		double value = 0.00d;
//		long currDiff = Math.abs(basePixCount-currPixCount); //���㵱ǰ����ֵ
//		if(currDiff==maxDifference){				//�����ǰ����������������������
//			return 100;
//		} 
//		value = currDiff/(maxDifference/40); //����������͵�ǰ�����������40��ʱ���Եü���
//		value+=60;							//����60�ֵõ���С��60��ֵ
//		if(value<60){
//			value = 60.00d;
//		}
//		return (long)value;
//	}
	

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
		ji.setData(getIntent().getData());
		if(d>=60&&d<70){
			String f = d+"";
			String txt = "�������ͣ�����Ǳ��Ŷ����";
			ji.putExtra("score", f);
			ji.putExtra("txt", txt);
			ji.putExtra("wav", 2);
		}
		if(d>=70&&d<=80){
			String f = d+"";
			String txt = "���Ĳ��������츳��";
			ji.putExtra("score", f);
			ji.putExtra("txt", txt);
			ji.putExtra("wav", 3);
		}
		if(d>80){
			String f = d+"";
			String txt = "�ɼ�̫���ˣ��л��ҵ�Ǳ�ʣ�";
			ji.putExtra("score", f);
			ji.putExtra("txt", txt);
			ji.putExtra("wav", 4);
		}
		
		startActivity(ji);
	}
	
	/**
	 * ѹ��ͼƬ
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
	 * ͼƬת�Ҷ� 
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
