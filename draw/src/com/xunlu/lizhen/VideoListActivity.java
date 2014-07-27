package com.xunlu.lizhen;

import java.io.File;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;





import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class VideoListActivity extends DownloadActivity {
	ArrayList<VideoItem> videoItems=new ArrayList<VideoListActivity.VideoItem>();
	MyAdapter adapter=new MyAdapter();
	SharedPreferences sp;
	Boolean isAutoPlay = false;
	public final int PLAY_ACTIVITY = 1;
	int currPosition = -1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ListView listView=new ListView(this);
		listView.setAdapter(adapter);
		sp = getPreferences(MODE_PRIVATE);
		new ResultAsyncTask<ArrayList<VideoItem>>(this){
			@Override
			protected ArrayList<VideoItem> doInBackground(Void... params) {
				try {
					ArrayList<VideoItem> list=new ArrayList<VideoListActivity.VideoItem>();
					String jsonS=HttpUtils.reqForGet("http://draw.ideer.cn/api.php/Api/Video/list.json");
					JSONArray array=new JSONObject(jsonS).getJSONArray("msg");
					for(int i=0,length=array.length();i<length;i++){
						JSONObject item=array.getJSONObject(i);
						list.add(new VideoItem(item.getString("id"), item.getString("name"), item.getString("qiniu"), item.getString("create")));
					}
					return list;
				} catch (Exception e) {
					e.printStackTrace();
				}
//				list.add(new VideoItem(1,"视频1", "http://bcs.duapp.com/runrect/runrect.mp4", "2014-07-12 15:50:00"));
//				list.add(new VideoItem(2,"鳄鱼洗澡apk", "http://gdown.baidu.com/data/wisegame/dab808e29a3d4d2d/eyuxiaowanpiaixizao_1150.apk", "2014-07-13 15:50:00"));
//				list.add(new VideoItem("视频3", "http://bcs.duapp.com/runrect/runrect.mp4", "2014-07-14 15:50:00"));
//				list.add(new VideoItem("视频4", "http://bcs.duapp.com/runrect/runrect.mp4", "2014-07-14 15:50:00"));
//				list.add(new VideoItem("视频5", "http://bcs.duapp.com/runrect/runrect.mp4", "2014-07-14 15:50:00"));
				return null;
			}
			@Override
			protected void onPostExecute(ArrayList<VideoItem> result) {
				super.onPostExecute(result);
				if(result!=null){
					videoItems=result;
					adapter.notifyDataSetChanged();
					noticeDownloadChange();
				}
			}
		}.setProgressDialogDefault().execute();
		
		setContentView(listView);
		
	}

	@Override
	void onDownload(ArrayList<DownloadingInfo> infos) {
		Log.d("fax", "onDownload:"+infos.size());
		for(DownloadingInfo downloadingInfo:infos){
			for(VideoItem videoItem:videoItems){
				long did=sp.getLong(videoItem.getId(), -1);
				if(did>0 && did==downloadingInfo.getDId()){
					videoItem.bindDownloadingInfo(downloadingInfo);
					break;
				}
			}
		}
		if(isAutoPlay&&currPosition != -1){
			final VideoItem videoItem=adapter.getItem(currPosition);
			if(videoItem.isComplete()){
				File file = videoItem.getFile();
				Intent playIntent = OpenFileUtil.getOpenIntent(file.getPath());
				startActivityForResult(playIntent, PLAY_ACTIVITY);
			}
		}
		adapter.notifyDataSetChanged();
	}
	class MyAdapter extends BaseAdapter{
		@Override
		public int getCount() {
			return videoItems.size();
		}
		@Override
		public VideoItem getItem(int position) {
			return videoItems.get(position);
		}
		@Override
		public long getItemId(int position) {
			return 0;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView==null) convertView=View.inflate(VideoListActivity.this, R.layout.download_list_item, null);
			final VideoItem videoItem=getItem(position);
			((TextView)convertView.findViewById(R.id.download_item_text1)).setText(videoItem.title);
			((TextView)convertView.findViewById(R.id.download_item_text2)).setText(videoItem.date);
			((TextView)convertView.findViewById(R.id.download_item_filesize)).setText(videoItem.getFileSize());
			Button downBtn=(Button) convertView.findViewById(R.id.download_item_btn);
			if(videoItem.isComplete()){
				downBtn.setEnabled(true);
				downBtn.setText("播放");
				downBtn.setOnClickListener(new PlayOnClick(position, convertView, parent));
			}else if(videoItem.isDownloading()){
				downBtn.setEnabled(false);
				downBtn.setText("下载中"+videoItem.getDownloadedProgress()+"%");
			}else{
				downBtn.setEnabled(true);
				downBtn.setText("下载");
				downBtn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						v.setEnabled(false);
						long dId=startDownload(videoItem.url);
						sp.edit().putLong(videoItem.getId(), dId).commit();
					}
				});
			}
			return convertView;
		}
		private class PlayOnClick implements View.OnClickListener {
			int position;
			View convertView;
			ViewGroup parent;
			
			
			
			public PlayOnClick(int position, View convertView, ViewGroup parent) {
				super();
				this.position = position;
				this.convertView = convertView;
				this.parent = parent;
			}
			@Override
			public void onClick(View v) {
				final VideoItem videoItem=getItem(position);
				currPosition = position;
				File file = videoItem.getFile();
				Intent playIntent = OpenFileUtil.getOpenIntent(file.getPath());
				startActivityForResult(playIntent, PLAY_ACTIVITY);
			}
		}
	}
	
	ProgressDialog pd;
	final Handler handler=new Handler();
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
//		StringBuilder sb=new StringBuilder();
//		for(String key:data.getExtras().keySet()){
//			sb.append(key+"="+data.getExtras().get(key));
//		}
//		Log.d("fax", sb.toString());
		
		if(requestCode == PLAY_ACTIVITY){
			if(pd==null){
				pd=new ProgressDialog(this);
				pd.setButton(DialogInterface.BUTTON_POSITIVE, "立即播放", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						playNextVideo();
					}
				});
				pd.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});
			}
			pd.show();
			handler.postDelayed(new Runnable() {
				int time=3;
				public void run() {
					if(pd.isShowing()){
						time--;
						pd.setMessage(time+"秒后自动播放下一个视频");
						if(time<=0){
							playNextVideo();
							pd.dismiss();
						}else{
							handler.postDelayed(this, 1000);
						}
					}
				}
			}, 0);
		}
	}
	private void playNextVideo(){
		int size = adapter.getCount();
		currPosition++;
		if(currPosition>=size){
			currPosition = 0;
		}
		final VideoItem videoItem=adapter.getItem(currPosition);
		if(videoItem.isComplete()){
			File file = videoItem.getFile();
			Intent playIntent = OpenFileUtil.getOpenIntent(file.getPath());
			startActivityForResult(playIntent, PLAY_ACTIVITY);
		}else if(videoItem.isDownloading()){
			
		}else{
			long dId=startDownload(videoItem.url);
			sp.edit().putLong(videoItem.getId(), dId).commit();
			isAutoPlay = true;
		}
	}
	
	public static class VideoItem{
		String id;
		String title;
		String url;
		String date;
		DownloadingInfo downloadingInfo;
		public VideoItem(String id, String title, String url, String date) {
			this.id = id;
			this.title = title;
			this.url = url;
			this.date = date;
		}

		public String getId() {
			return id+"";
		}

		public void bindDownloadingInfo(DownloadingInfo info){
			downloadingInfo = info;
		}
		public int getDownloadedProgress() {
			if(downloadingInfo==null) return 0;
			return downloadingInfo.getDownloadProgress();
		}
		public boolean isDownloading(){
			if(downloadingInfo==null) return false;
			return downloadingInfo.isDownloading();
		}
		public boolean isComplete() {
			if(downloadingInfo==null) return false;
			return downloadingInfo.isComplete();
		}
		public String getFileSize(){
			if(downloadingInfo==null) return "";
			return sizeToString(downloadingInfo.getFileSize());
		}
		public File getFile(){
			if(downloadingInfo==null) return null;
			Log.d("fax", "downloadingInfo.getPathUri:"+downloadingInfo.getPathUri());
			return new File(Uri.parse(downloadingInfo.getPathUri()).getPath());
		}
	}
}
