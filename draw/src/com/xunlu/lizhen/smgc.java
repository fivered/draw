package com.xunlu.lizhen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class smgc extends Activity {
private	boolean flags=true; 
   @Override
   //等待完成的界面
protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	setContentView(R.layout.smgc);
	handler.post(thread);
}
   Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.arg1 == 0) {
				System.out.println("++++++++++++++++++++");
			} else {
				Intent intent = new Intent(smgc.this, xzwc.class);
				startActivity(intent);
			}
		};
	};
	Runnable thread = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Message msg = new Message();
			if (flags) {
				handler.postDelayed(thread, 3000);
				System.out.println("============================");
				msg.arg1 = 0;
				flags = false;
			} else {
				msg.arg1 = 1;
			}
			handler.sendMessage(msg);
		};

	};

}
