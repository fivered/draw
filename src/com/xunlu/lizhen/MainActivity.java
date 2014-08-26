package com.xunlu.lizhen;

import java.io.ByteArrayOutputStream;
import java.io.File;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Browser;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.activity.CommActivity;
import com.conn.path.AllPath;
import com.date.CreateSQDate;
import com.date.ListMap;
import com.fax.utils.task.DownloadTask;
import com.fax.utils.task.GsonAsyncTask;
import com.fax.utils.task.HttpAsyncTask;
import com.fax.utils.task.ResultAsyncTask;
import com.google.gson.Gson;
import com.xunlu.lizhen.MainActivity.VersionResponse;
import com.xunlu.lizhen.MainActivity.VersionResponse.VersionInfo;

@SuppressLint("NewApi")
public class MainActivity extends CommActivity {
	private ImageView hhdf, jfcx;
	private ImageView sz;
	private SQLiteDatabase db = null;
	private final String TAG = "MainActivity";
	JSONObject msgObj;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int Witch = dm.widthPixels;
		int higth = dm.heightPixels;
		ListMap.setWindowHigth(higth);
		ListMap.setWindowWigth(Witch);
		CreateSQDate mb = new CreateSQDate(getApplicationContext(), "2s", null, 1);
		db = mb.getWritableDatabase();// ���ӿ�
		hhdf = (ImageView) findViewById(R.id.hhdf);
		jfcx = (ImageView) findViewById(R.id.jfcx);
		sz = (ImageView) findViewById(R.id.sz);
		new GsonAsyncTask<VersionResponse>(this, AllPath.getUpdateUrl()) {
			@Override
			protected void onPostExecuteSuc(VersionResponse result) {
				if (result.isOk()) {
					try {
						String currVersion = MainActivity.this.getVersionName();
						if(!result.getMsg().getVersion().equals(currVersion)){
							if(result.getMsg().getForce().equals("1")){
								updateDownload(result.getMsg().getUrl());
							} else if (result.getMsg().getForce().equals("0")){
								showUpdataDialog(result);
							}
						}
					} catch (Exception e) {
					}
				}
			}
		}.execute();
	}

	// �滭��ֵ���ת
	public void hhdf(View v) {
		Intent intent = new Intent(this, hhdf.class);
		startActivity(intent);
	}

	// ����ѡ�����ҳ
	public void sz(View v) {
		//Intent intent = new Intent(this, ShezhiActivity.class);
		Intent intent = new Intent(this, SetUpActivity.class);
		startActivity(intent);
	}

	// ���ֲ�ѯ����ת
	public void jfcx(View v) {
		Intent intent = new Intent(this, jfcx.class);
		startActivity(intent);

	}
	
	// ������ת
	public void play(View v) {
		Intent intent = new Intent(this, VideoListActivity.class);
		startActivity(intent);
		
	}

	/**
	 * 
	 * �ж�״̬���Ƿ���ʾ
	 */

	public static boolean isSystemBarVisible(final Activity context) {

		int flag = context.getWindow().getDecorView().getSystemUiVisibility();

		// return (flag & View.SYSTEM_UI_FLAG_SHOW_FULLSCREEN) != 0;

		return (flag & 0x8) == 0;

	}

	/**
	 * 
	 * ����ϵͳ���ɼ���
	 */

	public static void setSystemBarVisible(final Activity context, boolean visible) {

		int flag = context.getWindow().getDecorView().getSystemUiVisibility();
		// ��ȡ��ǰSystemUI��ʾ״̬

		// int fullScreen = View.SYSTEM_UI_FLAG_SHOW_FULLSCREEN;

		int fullScreen = 0x8; // 4.1 View.java��Դ���������صĳ���SYSTEM_UI_FLAG_SHOW_FULLSCREEN����ʵEclipse����Ҳ���Ե���ϵͳ���ؽӿڣ�������ȡ��android.jar������Ͳ����ˡ�
		if (visible) { // ��ʾϵͳ��
			if ((flag & fullScreen) != 0) { // flag��־λ���Ѿ�ӵ��ȫ����־SYSTEM_UI_FLAG_SHOW_FULLSCREEN
				context.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
				// ��ʾϵͳ��
			}
		} else { // ����ϵͳ��
			if ((flag & fullScreen) == 0) { // flag��־λ�в�����ȫ����־SYSTEM_UI_FLAG_SHOW_FULLSCREEN
				context.getWindow().getDecorView().setSystemUiVisibility(flag | fullScreen);
			}
		}
	}
	//ͼƬת��
	public void AddDate(Bitmap icon){
		if (icon == null) {  
            return;  
       }  
         // ����ͼ��Ҫ���浽��������ڲ����ݿ��У�ϵͳ���������ΪSQLite��ʽ��BrowserҲ�����⣬��ΪͼƬ�Ƕ����Ƶ�����ʹ���ֽ�����洢���ݿ��  
         // BLOB����  
        final ByteArrayOutputStream os = new ByteArrayOutputStream();  
         // ��Bitmapѹ����PNG���룬����Ϊ100%�洢          
         icon.compress(Bitmap.CompressFormat.PNG, 100, os);   
         // ����SQLite��Content��������Ҳ����ʹ��raw  
         ContentValues values = new ContentValues();   
         // д�����ݿ��Browser.BookmarkColumns.TOUCH_ICON�ֶ�  
         values.put(Browser.BookmarkColumns._COUNT, os.toByteArray());           
         //DBUtil.update(....);//���ø��»��߲��뵽���ݿ�ķ���
	}
	
	/**
	 * ��ȡ��ǰ����İ汾��   
	 * @return
	 * @throws Exception
	 */
	private String getVersionName() throws Exception{   
	    //��ȡpackagemanager��ʵ��     
	    PackageManager packageManager = getPackageManager();   
	    //getPackageName()���㵱ǰ��İ�����0�����ǻ�ȡ�汾��Ϣ    
	    PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);   
	    return packInfo.versionName;    
	}
	
	private void updateDownload(String url) {
		new DownloadTask(this, url) {
			@Override
			protected void onPostExecuteSuc(File file) {
				startActivity(OpenFileUtil.getOpenIntent(file.getPath()));
			}
		}.setProgressDialog().execute();
	}
	
	/** 
	 * �����Ի���֪ͨ�û����³���  
	 * �����Ի���Ĳ��裺 
	 *  1.����alertDialog��builder.   
	 *  2.Ҫ��builder��������, �Ի��������,��ʽ,��ť 
	 *  3.ͨ��builder ����һ���Ի��� 
	 *  4.�Ի���show()����   
	 */  
	protected void showUpdataDialog(final VersionResponse result) {  
	    AlertDialog.Builder builer = new AlertDialog.Builder(this) ;   
	    builer.setTitle("�汾����");
	    String content = "";
		content = result.getMsg().getContent();
	    builer.setMessage(content);  
	    //����ȷ����ťʱ�ӷ����������� �µ�apk Ȼ��װ    
	    builer.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {  
	    	public void onClick(DialogInterface dialog, int which) {  
	            Log.i(TAG,"����apk,����");
	    	    String url = "";
				url = result.getMsg().getUrl();
				
				updateDownload(url);
	        }     
	    });  
	    //����ȡ����ťʱ���е�¼
	    builer.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	        	dialog.dismiss();
	        }
	    });  
	    AlertDialog dialog = builer.create();  
	    dialog.show();  
	} 

	//��װapk    
	protected void installApk(Uri uri) {
	    Intent intent = new Intent();
	    //ִ�ж���
	    intent.setAction(Intent.ACTION_VIEW);
	    //ִ�е���������
	    intent.setDataAndType(uri, "application/vnd.android.package-archive");
	    //���߰����˴�AndroidӦΪandroid��������ɰ�װ����
	    startActivity(intent);
	}
	
	
	@SuppressWarnings("serial")
	public static class VersionResponse extends Response{
		VersionInfo msg;
		public VersionInfo getMsg() {
			return msg;
		}
		public void setMsg(VersionInfo msg) {
			this.msg = msg;
		}
		public static class VersionInfo{
			String  version;
			String url;
			String force;
			String content;
			public String getVersion() {
				return version;
			}
			public void setVersion(String version) {
				this.version = version;
			}
			public String getUrl() {
				return url;
			}
			public void setUrl(String url) {
				this.url = url;
			}
			public String getForce() {
				return force;
			}
			public void setForce(String force) {
				this.force = force;
			}
			public String getContent() {
				return content;
			}
			public void setContent(String content) {
				this.content = content;
			}
			
		}
	}
}
