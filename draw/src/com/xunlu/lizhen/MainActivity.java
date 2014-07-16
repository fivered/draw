package com.xunlu.lizhen;

import java.io.ByteArrayOutputStream;
import com.date.CreateSQDate;
import com.date.ListMap;
import android.os.Bundle;
import android.provider.Browser;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

@SuppressLint("NewApi")
public class MainActivity extends Activity {
	private ImageView hhdf, jfcx;
	private ImageView sz;
	private SQLiteDatabase db = null;

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

	}

	// 绘画打分的跳转
	public void hhdf(View v) {
		Intent intent = new Intent(this, hhdf.class);
		startActivity(intent);
	}

	// 设置选项的跳页
	public void sz(View v) {
		Intent intent = new Intent(this, ShezhiActivity.class);
		
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

	public static void setSystemBarVisible(final Activity context,
			boolean visible) {

		int flag = context.getWindow().getDecorView().getSystemUiVisibility();
		// 获取当前SystemUI显示状态

		// int fullScreen = View.SYSTEM_UI_FLAG_SHOW_FULLSCREEN;

		int fullScreen = 0x8; // 4.1
								// View.java的源码里面隐藏的常量SYSTEM_UI_FLAG_SHOW_FULLSCREEN，其实Eclipse里面也可以调用系统隐藏接口，重新提取下android.jar，这里就不述了。
		if (visible) { // 显示系统栏
			if ((flag & fullScreen) != 0) { // flag标志位中已经拥有全屏标志SYSTEM_UI_FLAG_SHOW_FULLSCREEN

				context.getWindow().getDecorView()
						.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
				// 显示系统栏
			}
		} else { // 隐藏系统栏

			if ((flag & fullScreen) == 0) { // flag标志位中不存在全屏标志SYSTEM_UI_FLAG_SHOW_FULLSCREEN

				context.getWindow().getDecorView()
						.setSystemUiVisibility(flag | fullScreen);
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
//           DBUtil.update(....);//调用更新或者插入到数据库的方法
	}
/**	
	String []columns = {}; 
	Cursor or = db.query("majors", columns, null, null, null, null, null);
	while(or.moveToNext()){
		String name = or.getString(0);
		Log.e("dddd", or.getString(0));
		int id = or.getInt(1);
		Log.e("dddd", or.getInt(1)+"");
		Major m = new Major();
		m.setId(id);
		m.setMojorName(name);
		lm.add(m);
		Log.e("list", lm.size()+"");
	}
	
	L1Adpter l = new L1Adpter();
	l.setCon(getApplicationContext());
	l.setLm(lm);
	lv.setAdapter(l);	
}
**/

}
