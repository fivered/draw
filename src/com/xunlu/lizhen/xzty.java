package com.xunlu.lizhen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
//新增图样
public class xzty extends Activity{
  @Override
protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	setContentView(R.layout.xzty);
}
  //扫描原图
 public void next(View v){
	 Intent intent=new Intent(this,smyt.class);
	  startActivity(intent);
 } 
}
