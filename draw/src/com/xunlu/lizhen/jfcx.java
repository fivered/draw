package com.xunlu.lizhen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class jfcx extends Activity {
	private ImageButton cha;
	//积分查询	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jfcx);
		cha = (ImageButton)findViewById(R.id.chaxun);
	}
	
	//积分查询的按钮点击事件
	public void btn_select(View v){
		EditText eText = (EditText) findViewById(R.id.et);
		String etString = eText.getText().toString();
		if(etString==null){
			Toast.makeText(this, "请输入内容！", Toast.LENGTH_SHORT).show();
			return;
		}
		if(etString.equals("")){
			Toast.makeText(this, "请输入内容！", Toast.LENGTH_SHORT).show();
			return;
		}
		Intent intent = new Intent(jfcx.this,XianshiActivity.class);
		intent.putExtra("keyword", etString);
		startActivity(intent);
	}
}
