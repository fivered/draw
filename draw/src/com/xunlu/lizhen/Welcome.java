package com.xunlu.lizhen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class Welcome extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);
		Handler handler = new Handler();
		handler.postDelayed(new HandlerRun(), 2000);
	}
	private class HandlerRun implements Runnable {
		@Override
		public void run() {
			Intent intent = new Intent();
			intent.setClass(Welcome.this, MainActivity.class);
			startActivity(intent);
			Welcome.this.finish();
		}
	}
}
