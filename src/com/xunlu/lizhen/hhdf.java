package com.xunlu.lizhen;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.activity.CommActivity;
import com.date.ListMap;
import com.google.zxing.client.android.CaptureActivity;


public class hhdf extends CommActivity {
    private View layout;
    private ImageView paizhao;
    private Boolean flag =true;
    private static final int SCANNIN_GREQUEST_CODE = 1;
    String name = "";
    String phonNumber = ""; 
    Dialog aDialog;
    
    com.xunlu.lizhen.ConnectionPrint connectionPrintRunnable;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        		WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main);
        paizhao = (ImageView)findViewById(R.id.paizhao);
        aDialog = new Dialog(this,R.style.dialog);
        aDialog.setContentView(R.layout.name_phon_dialog);
        Window dialogWindow = aDialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = 346;
        lp.height = 255;
        aDialog.onWindowAttributesChanged(lp);
        aDialog.show();
        ImageButton dialogsubmit = (ImageButton) aDialog.getWindow().findViewById(R.id.dialogsubmit);
        ImageButton dialogcancel = (ImageButton) aDialog.getWindow().findViewById(R.id.dialogcancel);
        
       
        
       
        dialogsubmit.setOnClickListener(new DialogOnClick());
        dialogcancel.setOnClickListener(new DialogOnClick());
    }

    private class DialogOnClick implements View.OnClickListener{
		
		@Override
		public void onClick(View v) {
			EditText nameEditText = (EditText) hhdf.this.aDialog.getWindow().findViewById(R.id.user_name);
			EditText phonEditText = (EditText) hhdf.this.aDialog.getWindow().findViewById(R.id.user_phon);
			hhdf.this.name = nameEditText.getText().toString();
			hhdf.this.phonNumber = phonEditText.getText().toString();
			hhdf.this.aDialog.dismiss();
		}
	}
    
    public void paizhaoClick(View v) {
		new GetErwei().run();
	}
    
    public void connection(View v) {
    	connectionPrintRunnable  = new com.xunlu.lizhen.ConnectionPrint(v.getContext(), "ssss","1234567","88");
		new Thread(connectionPrintRunnable).start();
	}
    
    
	
	@Override
	protected void onDestroy() {
		if(connectionPrintRunnable != null){
			try {
				connectionPrintRunnable.close();
			} catch (IOException e) {
				Toast.makeText(this, "关闭失败!", Toast.LENGTH_SHORT).show();
			}
		}
		super.onDestroy();
	}

		
    
    
    
    /**
     * 返回
     * @param v
     */
    public void backhome(View v) {
		finish();
	}
    
    /**
     * 子线程开启预览
     * @author android
     *
     */
    class GetErwei implements Runnable{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(flag){
				Log.i("saomiao", "dddddddddd---=== GetErWeima() ");
				//Intent intent = new Intent(hhdf.this, MipcaActivityCapture.class);
				Intent intent = new Intent(hhdf.this, CaptureActivity.class);
				intent.putExtra("username", hhdf.this.name);
				intent.putExtra("userphon", hhdf.this.phonNumber);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(intent, hhdf.SCANNIN_GREQUEST_CODE);
				ListMap.setJ(0);
				flag = false;
			}
		}
	}



	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(hhdf.SCANNIN_GREQUEST_CODE==requestCode && resultCode==Activity.RESULT_OK)
		{
			Intent it = new Intent(hhdf.this, XianShiDafen.class);
			it.putExtras(data.getExtras());
            it.setData(data.getData());
			startActivity(it);
		}
		flag = true;
	}
   
}