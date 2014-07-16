package com.m.adpter;

import java.io.File;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.thread.m.Threads;
import com.xunlu.lizhen.R;

public class gridAdpter extends BaseAdapter {
	private Context con;
	private LayoutInflater inflater;
	private List<SoftReference<Bitmap>> lb;
	private List<Map<String, String>> lUri;
	private List<JSONObject> lJson;
	private ImageView imageView;
	private TextView textView;
	private TextView datetextView;

	
	
	
	public List<Map<String, String>> getlUri() {
		return lUri;
	}

	public void setlUri(List<Map<String, String>> lUri) {
		this.lUri = lUri;
	}

	public List<SoftReference<Bitmap>> getListBitmap() {
		return lb;
	}

	public void setListBitmap(List<SoftReference<Bitmap>> lb) {
		this.lb = lb;
	}
	
	public void setListJson(List<JSONObject> lj) {
		this.lJson = lj;
	}

	public Context getCon() {
		return con;
	}

	public void setCon(Context con) {
		this.con = con;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		int prame = 0;
		if (lUri != null) {
			prame = lUri.size();
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
	public View getView(int index, View view, ViewGroup viewGroup) {
		inflater = LayoutInflater.from(con);
		view = inflater.inflate(R.layout.grid, null);
		imageView = (ImageView) view.findViewById(R.id.itemimage);
		textView = (TextView) view.findViewById(R.id.gridfenshu);
		datetextView = (TextView) view.findViewById(R.id.createdatetime);
		if (lUri ==null){
			return view;
		}
		Map<String, String> map = lUri.get(index);
		String path = map.get("path");
		String date = map.get("date");
		String score = map.get("score");
		textView.setText(score);
		datetextView.setText(date);
		Bitmap bitmap = BitmapFactory.decodeFile(path);
		imageView.setImageBitmap(bitmap);
		return view;
	}
	

}
