package com.xunlu.lizhen;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.conn.path.AllPath;
import com.google.zxing.client.android.CaptureActivity;
import com.thread.m.Threads;
import com.util.ImageUtil;

public class ShezhiActivity extends Activity {
	// private String[] s = {"打分设置","摄像头设置","服务器地址","图样","新增图样","管理自定义图样"};
	private TextView t1;
	private TextView t2;
	private TextView t3;
	private TextView t4;
	private TextView t5;
	private TextView t6;
	private LinearLayout dafen;
	private LinearLayout shexiang;
	private LinearLayout fuwuadress;
	private LinearLayout xinzeng;
	private LinearLayout ziding;
	private LinearLayout shou;
	private LinearLayout tou;
	private RelativeLayout yulan;
	private EditText adress;
	private Button tijiao ;
	private Button completebutton;
	private SurfaceView preview;
	private Camera camera;
	public static int CAMERA_Z_CODE = 1;
	public static int CAMERA_F_CODE = 2;
	long zPercent;
	SharedPreferences.Editor spfEditer;
	SharedPreferences spf;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).
//		hideSoftInputFromWindow(ShezhiActivity.this.getCurrentFocus().
//				getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS); //隱藏鍵盤
		setContentView(R.layout.activity_shezhi);
		t1 = (TextView) findViewById(R.id.dafen1);
		t2 = (TextView) findViewById(R.id.adress1);
		t3 = (TextView) findViewById(R.id.xinzeng1);
		t4 = (TextView) findViewById(R.id.zidingyi1);
		t5 = (TextView) findViewById(R.id.fanhui);
		t6 = (TextView) findViewById(R.id.toufanhui1);
		adress = (EditText)findViewById(R.id.xinadress);
		tijiao = (Button)findViewById(R.id.tijiao);
		
		shou = (LinearLayout)findViewById(R.id.shou);
		tou = (LinearLayout)findViewById(R.id.touLin);
		dafen = (LinearLayout) findViewById(R.id.dafenlayout);
//		shexiang = (LinearLayout) findViewById(R.id.updete);
		fuwuadress = (LinearLayout) findViewById(R.id.updete);
		xinzeng = (LinearLayout) findViewById(R.id.addtu);
		ziding = (LinearLayout) findViewById(R.id.ziding);
		preview = (SurfaceView)findViewById(R.id.preview);
		preview.setZOrderOnTop(true);
		preview.getHolder().setFormat(PixelFormat.TRANSLUCENT);
		yulan = (RelativeLayout)findViewById(R.id.yulan);
		yulan.setVisibility(View.GONE);
		completebutton = (Button) findViewById(R.id.completebutton);
		tijiao.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(null != adress.getText() && !"".equals(adress.getText())){
				  String path = adress.getText().toString();
				  AllPath.setGetFuadress(path);
				}
			}
		});
		
		t1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dafen.setVisibility(View.VISIBLE);
				shou.setVisibility(View.GONE);
				fuwuadress.setVisibility(View.GONE);
				xinzeng.setVisibility(View.GONE);
				ziding.setVisibility(View.GONE);
				tou.setVisibility(View.VISIBLE);
				EditText shezhiValue = (EditText)findViewById(R.id.shezhiValue);

				spf = ((Context)ShezhiActivity.this).getSharedPreferences("shezhiValue", Context.MODE_PRIVATE);
				spfEditer = spf.edit();
				String icString = spf.getString("ic", "");
				if (!icString.equals("1")) {
					spfEditer.putString("ic", "1");
					spfEditer.putInt("value", 450);
					spfEditer.commit();
				}
				int value = spf.getInt("value", 450);
				shezhiValue.setText(value + "");
				Button shezhiButton = (Button) findViewById(R.id.shezhiButton);
				shezhiButton.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						EditText shezhiValue = (EditText)findViewById(R.id.shezhiValue);
						String valueString = shezhiValue.getText().toString();
						String icString = spf.getString("ic", "");
						if (icString.equals("1")) {
							spfEditer.putInt("value", Integer.valueOf(valueString));
							spfEditer.commit();
						}
					}
				});
			}
		});
		
		t2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dafen.setVisibility(View.GONE);
				shou.setVisibility(View.GONE);
				fuwuadress.setVisibility(View.VISIBLE);
				xinzeng.setVisibility(View.GONE);
				ziding.setVisibility(View.GONE);
				tou.setVisibility(View.VISIBLE);
			}
		});
		
		t3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dafen.setVisibility(View.GONE);
				shou.setVisibility(View.GONE);
				fuwuadress.setVisibility(View.GONE);
				xinzeng.setVisibility(View.VISIBLE);
				ziding.setVisibility(View.GONE);
				tou.setVisibility(View.VISIBLE);
				findViewById(R.id.textView10).setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						xinzeng.setVisibility(View.GONE);
						yulan.setVisibility(View.VISIBLE);
						preview.setVisibility(View.VISIBLE);
						SurfaceHolder holder = preview.getHolder();
						holder.addCallback(new ShezhiActivity.SurfaceHolderCallback());
						holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
						camera = Camera.open(); 
						try {
							camera.setPreviewDisplay(holder);
						} catch (IOException e) {
							e.printStackTrace();
						}
						Camera.Parameters params = camera.getParameters();
						List<Camera.Size> sizes = params.getSupportedPreviewSizes();
						Camera.Size selected = sizes.get(0);
						params.setPreviewSize(selected.width, selected.height);
						camera.setParameters(params);
						camera.startPreview();
					}
				});
			}
		});
		
		t4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dafen.setVisibility(View.GONE);
				shou.setVisibility(View.GONE);
				fuwuadress.setVisibility(View.GONE);
				xinzeng.setVisibility(View.GONE);
				ziding.setVisibility(View.VISIBLE);
				tou.setVisibility(View.VISIBLE);
			}
		});
		t5.setOnClickListener(new OnClickListener() {//fanhui

			@Override
			public void onClick(View arg0) {
				Intent it = new Intent(ShezhiActivity.this,MainActivity.class);
				startActivity(it);
			}
		});
		
		t6.setOnClickListener(new OnClickListener() {//toufanhui

			@Override
			public void onClick(View arg0) {
				
				 shou.setVisibility(View.VISIBLE);
				 fuwuadress.setVisibility(View.GONE);
				 xinzeng.setVisibility(View.GONE);
				 ziding.setVisibility(View.GONE);
				 dafen.setVisibility(View.GONE);
				 tou.setVisibility(View.GONE);
				 
				
			}
		});
		completebutton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (camera!=null){
					camera.stopPreview();
					camera.release();
					xinzeng.setVisibility(View.VISIBLE);
					yulan.setVisibility(View.GONE);
					preview.setVisibility(View.GONE);
					Intent intent = new Intent(ShezhiActivity.this, CaptureActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					//开启照相扫码功能
					startActivityForResult(intent, ShezhiActivity.CAMERA_Z_CODE);
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.shezhi, menu);
		return true;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		
		if(resultCode==Activity.RESULT_OK)
		{
			String codeString = data.getStringExtra("scan_result");
			Uri imgUri = data.getData();
			Bitmap bitmap=BitmapFactory.decodeFile(imgUri.getPath());
			bitmap = ImageUtil.zoomBitmap(bitmap, bitmap.getWidth()/6, bitmap.getHeight()/6);
			TextView tView = (TextView) findViewById(R.id.backshowText);
			spf = ((Context)ShezhiActivity.this).getSharedPreferences("shezhiValue", 0);
			spfEditer = spf.edit();
			int value = 450;
			if (!spf.getString("ic", "").equals("1")) {
				spfEditer.putString("ic", "1");
				spfEditer.putInt("value", 450);
				spfEditer.commit();
			}
			value = spf.getInt("value", 450);
			ArrayList<String> uris = data.getStringArrayListExtra("Uris");
			if(ShezhiActivity.CAMERA_Z_CODE==requestCode ){
				try {
					
					zPercent = ImageUtil.gradeBitmap(bitmap);
				} catch (Exception e) {
					e.printStackTrace();
				}
				TextView bstTextView = (TextView)findViewById(R.id.backshowText);
				bstTextView.setText(codeString+"已录入");
				Map<String, ContentBody> pairMap = new HashMap<String, ContentBody>();
				pairMap.put("full_src", new FileBody(new File(imgUri.getPath()),"image/jpeg"));
				try {
					pairMap.put("name", new StringBody(codeString));
					pairMap.put("base_score", new StringBody(String.valueOf(zPercent)));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				new Thread(new HttpThread(pairMap)).start();
			}
		}
	}

	
	private class HttpThread implements Runnable{
		List<NameValuePair> pairList;
		Map<String, ContentBody> pairMap;
		
		public HttpThread(Map<String, ContentBody> pairMap) {
			super();
			this.pairMap = pairMap;
		}

		@Override
		public void run() {
			
			try {
				boolean isSuccess = Threads.setRegion(AllPath.getMakeMaxDifferenceUrl(), this.pairMap);
				Looper looper = Looper.getMainLooper();
				Handler handler = new showHandler(looper);
				Message msg = Message.obtain();
				if(isSuccess){
					msg.what=1;
					handler.sendMessage(msg);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	private class showHandler extends Handler{

		public showHandler() {
			super();
		}

		public showHandler(Callback callback) {
			super(callback);
		}

		public showHandler(Looper looper, Callback callback) {
			super(looper, callback);
		}

		public showHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(msg.what==1){
				Toast.makeText(ShezhiActivity.this, "已完成", Toast.LENGTH_LONG).show();
			}
		}
	}
	
	public class SurfaceHolderCallback implements SurfaceHolder.Callback{

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
		}
		
	}
}
