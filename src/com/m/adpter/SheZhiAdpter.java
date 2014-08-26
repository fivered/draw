package com.m.adpter;

import java.util.List;

import com.xunlu.lizhen.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.IInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SheZhiAdpter extends BaseAdapter{
    private List<Bitmap> lb = null;
    private LayoutInflater inflater;
    private Context con ;
	
    
	public void setLb(List<Bitmap> lb) {
		this.lb = lb;
	}

	public void setCon(Context con) {
		this.con = con;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		int prame  = 0;
		if(lb != null){
			prame = lb.size();
		}
		return prame;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		TextView t;
		ImageView img;
		inflater = LayoutInflater.from(con);
		arg1 = inflater.inflate(R.layout.tuyang, null);
		t = (TextView)arg1.findViewById(R.id.shechild);
		img = (ImageView)arg1.findViewById(R.id.imgzidingtu);
//		t.setText();
		img.setImageBitmap(lb.get(arg0));
		return null;
	}

}
