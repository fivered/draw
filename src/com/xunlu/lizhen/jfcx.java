package com.xunlu.lizhen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.activity.CommActivity;

public class jfcx extends CommActivity {
	private ImageButton cha;
	//���ֲ�ѯ	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jfcx);
		cha = (ImageButton)findViewById(R.id.chaxun);
	}
	
	//���ֲ�ѯ�İ�ť����¼�
	public void btn_select(View v){
		EditText eText = (EditText) findViewById(R.id.et);
		String etString = eText.getText().toString();
		if(etString==null){
			Toast.makeText(this, "���������ݣ�", Toast.LENGTH_SHORT).show();
			return;
		}
		if(etString.equals("")){
			Toast.makeText(this, "���������ݣ�", Toast.LENGTH_SHORT).show();
			return;
		}
		Intent intent = new Intent(jfcx.this,XianshiActivity.class);
		intent.putExtra("keyword", etString);
		startActivity(intent);
	}
}
