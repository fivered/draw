package com.xunlu.lizhen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

public class smxj extends Activity {
	private TextView tv;
	boolean flags =true;
  @Override
  //É¨ÃèÏ¸½Ú
protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	setContentView(R.layout.smxj);
	tv=(TextView)findViewById(R.id.tv);
	handler.post(thread);
}
  Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			if(msg.arg1==0){
				Intent intent=new Intent(smxj.this,hhht.class);
				startActivity(intent);	
			}	
		};
	};
	Runnable thread = new Runnable(){
		public void run() {
			Message msg = new Message();
			if(flags){
				handler.postDelayed(thread, 3000);
				msg.arg1=0;	
				flags=false;
			}else{
				handler.postDelayed(thread, 4000);
				msg.arg1=1;
			}
			
			handler.sendMessage(msg);
		};
	};
	
}
