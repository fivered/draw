package com.activity;

import com.xunlu.lizhen.VideoListActivity;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Handler;

public class CommActivity extends Activity {
	
	Handler handler = new Handler();
	long delayMillis = 5 * 60 * 1000;//5·ÖÖÓ
	
	private Runnable playRun = new Runnable() {
		@SuppressLint("NewApi")
		@Override
		public void run() {
			if(VERSION.SDK_INT>=17 && isDestroyed()) return;
			Intent intent = new Intent(CommActivity.this, VideoListActivity.class);
			intent.putExtra("isPlay", true);
			startActivity(intent);
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		handler.removeCallbacks(playRun);
		handler.postDelayed(playRun, delayMillis);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		handler.removeCallbacks(playRun);
	}
	
	@Override
	public void onUserInteraction() {
		super.onUserInteraction();
		handler.removeCallbacks(playRun);
		handler.postDelayed(playRun, delayMillis);
	}
}
