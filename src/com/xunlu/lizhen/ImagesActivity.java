package com.xunlu.lizhen;

import java.util.ArrayList;

import com.fax.utils.view.TopBarContain;
import com.fax.utils.view.pager.NetImgsViewPager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

public class ImagesActivity extends Activity {
	private static final String Extra_Position = "position";
	public static void start(Activity activity,String[] imgs){
		start(activity, imgs, 0);
	}
	public static void start(Activity activity,String[] imgs, int position){
		activity.startActivity(new Intent(activity, ImagesActivity.class)
					.putExtra(String[].class.getName(), imgs)
					.putExtra(Extra_Position, position));
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final String[] imgs= getIntent().getStringArrayExtra(String[].class.getName());
		int position = getIntent().getIntExtra(Extra_Position, 0);
		
		NetImgsViewPager viewPager = new NetImgsViewPager(this);
		//viewPager.setImageZoomAble(true);//允许支持手势缩放
		final TopBarContain topBarContain = new TopBarContain(this).setTitle("1/" + imgs.length)
				.setLeftFinish("返回", 0)
				.setContentView(viewPager);
		setContentView(topBarContain);
		viewPager.setBackgroundResource(Color.BLACK);

		viewPager.setImgs(imgs);
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				topBarContain.setTitle( (position+1) +"/" + imgs.length);
			}
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}
			public void onPageScrollStateChanged(int arg0) {
			}
		});
		viewPager.setCurrentItem(position);
	}

}
