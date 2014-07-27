package com.xunlu.lizhen;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Handler.Callback;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.conn.path.AllPath;
import com.jiexi.GetRes;
import com.m.adpter.gridAdpter;
import com.thread.m.Threads;

public class XianshiActivity extends Activity {
   public GridView g;
   public gridAdpter ga;
   private static List<SoftReference<Bitmap>> bitMapList;
   private ImageView imageView;
   private TextView nameView;
   private TextView phoneView;
   private static SoftReference<Bitmap> srf = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_xianshi);
		g = (GridView)findViewById(R.id.serchgridview);
		imageView = (ImageView)findViewById(R.id.xianshiss);
		nameView = (TextView)findViewById(R.id.serchname);
		phoneView = (TextView)findViewById(R.id.serchphone);
		File file = new File(Environment.getExternalStorageDirectory(),
				"/myCamera/pics");
		File[] l = file.listFiles();
		bitMapList = new ArrayList<SoftReference<Bitmap>>();
		
		Intent intent = getIntent();
		String keyword = intent.getExtras().getString("keyword");
		
		Thread thread = new Thread(new HttpThread(keyword));
		thread.start();
		ga = new gridAdpter();
		ga.setCon(getApplicationContext());
		g.setAdapter(ga);
		
	}
	/**
	 * 点击右上角返回主页面的方法；
	 * @param v
	 */
	public void backhome(View v) {
		Intent intent = new Intent(this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
	
	private class HttpThread implements Runnable{
		String keyword;
		public HttpThread(String keyword) {
			super();
			this.keyword = keyword;
		}

		@Override
		public void run() {
			
			try {
				String pathString = AllPath.getSerchUrl();
				String result = Threads.getSerchStream(pathString, keyword, 9);
				JSONObject jsonObject = GetRes.getJasonObject(result);
				String flag = jsonObject.getString("flag");
				List<JSONObject> jsonList = new ArrayList<JSONObject>();
				if(flag.equals("success")){
					JSONArray jsonArray = jsonObject.getJSONArray("msg");
					Thread igThread = new Thread(new ImageGridThread(jsonArray,XianshiActivity.this));
					igThread.run();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	
	
	
	private class ImageGridThread implements Runnable {

		JSONArray jsArray;
		Context con;
		
		public ImageGridThread(JSONArray jsArray, Context con) {
			super();
			this.jsArray = jsArray;
			this.con = con;
		}
		
		

		@Override
		public void run() {
			List<Map<String, String>> uriList = new ArrayList<Map<String,String>>(); 
			for (int i = 0;i<jsArray.length();i++){
				JSONObject jsonOBJ = null;
				String url = "";
				String date = "";
				String score = "";
				try {
					jsonOBJ = jsArray.getJSONObject(i);
					if (jsonOBJ == null){
						break;
					}
					url = jsonOBJ.getString("image");
					if (url == null){
						break;
					}
					if(url.length()<1){
						break;
					}
					date = jsonOBJ.getString("create");
					score = jsonOBJ.getString("score");
					if(i==0){
						String name = jsonOBJ.getString("name");
						String phone = jsonOBJ.getString("phone");
						Looper mLooper = Looper.getMainLooper();
						Message mMsg = Message.obtain();
						Map<String, String> map = new HashMap<String, String>();
						map.put("name", name);
						map.put("phone", phone);
						mMsg.obj = map;
						
						Handler mHandler =new Handler(mLooper){
	
							@Override
							public void handleMessage(Message msg) {
								super.handleMessage(msg);
								String name = ((Map<String, String>)msg.obj).get("name");
								String phone = ((Map<String, String>)msg.obj).get("phone");
								nameView.setText(name);
								phoneView.setText(phone);
							}
							
						};
						mHandler.sendMessage(mMsg);
					}
					
					
				} catch (JSONException e1) {
					e1.printStackTrace();
					break;
				}
				
				String[] tmparray = url.split("/");
				String fileName = tmparray[tmparray.length-1];
				String path = Environment.getExternalStorageDirectory()
						+ "/myCamera/listcache/";
				File file = new File(path);
				if(!file.exists()){
					file.mkdir();
				}
				path = path+fileName;
				file = new File(path);
				if(file.exists()){
					Map<String, String> map = new HashMap<String, String>();
					map.put("path", path);
					map.put("date", date);
					map.put("score", score);
					uriList.add(map);
					Looper looper = Looper.getMainLooper();
					Message msg = Message.obtain();
					Handler handler = new ListHandle(looper);
					msg.obj = uriList;
					handler.sendMessage(msg);
		
				}else{
				
					try {
						InputStream is = Threads.getSingleImage(url);
						Bitmap bm = BitmapFactory.decodeStream(is);
						OutputStream os = new FileOutputStream(file);
						os.write(Bitmap2Bytes(bm));
						os.flush();
						os.close();
						is.close();
						bm.recycle();
						Map<String, String> map = new HashMap<String, String>();
						map.put("path", file.getPath());
						map.put("date", date);
						map.put("score", score);
						uriList.add(map);
						Looper looper = Looper.getMainLooper();
						Message msg = Message.obtain();
						Handler handler = new ListHandle(looper);
						msg.obj = uriList;
						handler.sendMessage(msg);
						
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		
		
		public byte[] Bitmap2Bytes(Bitmap bm) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
			return baos.toByteArray();
		}
		
	}
	
	
	private class ListHandle extends Handler{

		public ListHandle() {
			super();
			// TODO Auto-generated constructor stub
		}

		public ListHandle(Callback callback) {
			super(callback);
			// TODO Auto-generated constructor stub
		}

		public ListHandle(Looper looper, Callback callback) {
			super(looper, callback);
			// TODO Auto-generated constructor stub
		}

		public ListHandle(Looper looper) {
			super(looper);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			List<Map<String, String>> list = (List<Map<String, String>>) msg.obj;
			ga.setlUri(list);
			ga.notifyDataSetChanged();
		}
		
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.xianshi, menu);
		return true;
	}
	
	 public static Bitmap zoomBitmap(Bitmap bitmap,int width,int height){
			int w = bitmap.getWidth();
			int h = bitmap.getHeight();
			Matrix matrix = new Matrix();
			float scalewidth = (float)width/w;
			float scaleheight =(float) height/h;
			matrix.postScale(scalewidth, scaleheight);
			Bitmap newbit = Bitmap.createBitmap(bitmap, 0, 0, w, h,matrix,true);
			srf = new SoftReference<Bitmap>(newbit);
			bitMapList.add(srf);
			srf = null;
			return newbit;
			
		}
}
