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
		db = mb.getWritableDatabase();// 链接库
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

	// 绘画打分的跳转
	public void hhdf(View v) {
		Intent intent = new Intent(this, hhdf.class);
		startActivity(intent);
	}

	// 设置选项的跳页
	public void sz(View v) {
		//Intent intent = new Intent(this, ShezhiActivity.class);
		Intent intent = new Intent(this, SetUpActivity.class);
		startActivity(intent);
	}

	// 积分查询的跳转
	public void jfcx(View v) {
		Intent intent = new Intent(this, jfcx.class);
		startActivity(intent);

	}
	
	// 播放跳转
	public void play(View v) {
		Intent intent = new Intent(this, VideoListActivity.class);
		startActivity(intent);
		
	}

	/**
	 * 
	 * 判断状态栏是否显示
	 */

	public static boolean isSystemBarVisible(final Activity context) {

		int flag = context.getWindow().getDecorView().getSystemUiVisibility();

		// return (flag & View.SYSTEM_UI_FLAG_SHOW_FULLSCREEN) != 0;

		return (flag & 0x8) == 0;

	}

	/**
	 * 
	 * 设置系统栏可见性
	 */

	public static void setSystemBarVisible(final Activity context, boolean visible) {

		int flag = context.getWindow().getDecorView().getSystemUiVisibility();
		// 获取当前SystemUI显示状态

		// int fullScreen = View.SYSTEM_UI_FLAG_SHOW_FULLSCREEN;

		int fullScreen = 0x8; // 4.1 View.java的源码里面隐藏的常量SYSTEM_UI_FLAG_SHOW_FULLSCREEN，其实Eclipse里面也可以调用系统隐藏接口，重新提取下android.jar，这里就不述了。
		if (visible) { // 显示系统栏
			if ((flag & fullScreen) != 0) { // flag标志位中已经拥有全屏标志SYSTEM_UI_FLAG_SHOW_FULLSCREEN
				context.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
				// 显示系统栏
			}
		} else { // 隐藏系统栏
			if ((flag & fullScreen) == 0) { // flag标志位中不存在全屏标志SYSTEM_UI_FLAG_SHOW_FULLSCREEN
				context.getWindow().getDecorView().setSystemUiVisibility(flag | fullScreen);
			}
		}
	}
	//图片转换
	public void AddDate(Bitmap icon){
		if (icon == null) {  
            return;  
       }  
         // 最终图标要保存到浏览器的内部数据库中，系统程序均保存为SQLite格式，Browser也不例外，因为图片是二进制的所以使用字节数组存储数据库的  
         // BLOB类型  
        final ByteArrayOutputStream os = new ByteArrayOutputStream();  
         // 将Bitmap压缩成PNG编码，质量为100%存储          
         icon.compress(Bitmap.CompressFormat.PNG, 100, os);   
         // 构造SQLite的Content对象，这里也可以使用raw  
         ContentValues values = new ContentValues();   
         // 写入数据库的Browser.BookmarkColumns.TOUCH_ICON字段  
         values.put(Browser.BookmarkColumns._COUNT, os.toByteArray());           
         //DBUtil.update(....);//调用更新或者插入到数据库的方法
	}
	
	/**
	 * 获取当前程序的版本号   
	 * @return
	 * @throws Exception
	 */
	private String getVersionName() throws Exception{   
	    //获取packagemanager的实例     
	    PackageManager packageManager = getPackageManager();   
	    //getPackageName()是你当前类的包名，0代表是获取版本信息    
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
	 * 弹出对话框通知用户更新程序  
	 * 弹出对话框的步骤： 
	 *  1.创建alertDialog的builder.   
	 *  2.要给builder设置属性, 对话框的内容,样式,按钮 
	 *  3.通过builder 创建一个对话框 
	 *  4.对话框show()出来   
	 */  
	protected void showUpdataDialog(final VersionResponse result) {  
	    AlertDialog.Builder builer = new AlertDialog.Builder(this) ;   
	    builer.setTitle("版本升级");
	    String content = "";
		content = result.getMsg().getContent();
	    builer.setMessage(content);  
	    //当点确定按钮时从服务器上下载 新的apk 然后安装    
	    builer.setPositiveButton("确定", new DialogInterface.OnClickListener() {  
	    	public void onClick(DialogInterface dialog, int which) {  
	            Log.i(TAG,"下载apk,更新");
	    	    String url = "";
				url = result.getMsg().getUrl();
				
				updateDownload(url);
	        }     
	    });  
	    //当点取消按钮时进行登录
	    builer.setNegativeButton("取消", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	        	dialog.dismiss();
	        }
	    });  
	    AlertDialog dialog = builer.create();  
	    dialog.show();  
	} 

	//安装apk    
	protected void installApk(Uri uri) {
	    Intent intent = new Intent();
	    //执行动作
	    intent.setAction(Intent.ACTION_VIEW);
	    //执行的数据类型
	    intent.setDataAndType(uri, "application/vnd.android.package-archive");
	    //编者按：此处Android应为android，否则造成安装不了
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
