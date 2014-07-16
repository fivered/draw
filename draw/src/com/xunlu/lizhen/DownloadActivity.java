package com.xunlu.lizhen;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

/**下载进度的监听工具 */
abstract public class DownloadActivity extends Activity {
	/**
	 * @param url 要下载的地址
	 * @return 下载的id
	 */
	public long startDownload(String url){
		long id=DownloadHandler.onDownloadStartNoStream(this, url);
		sp.edit().putBoolean(""+id, false).commit();
		noticeDownloadChange();
		return id;
	}
	public long[] getAllDownloadIds(){
		ArrayList<Long> idArray=new ArrayList<Long>();
		Map<String, ?> map=sp.getAll();
		for(Entry<String, ?> entry:map.entrySet()){
			idArray.add(Long.valueOf(entry.getKey()));
		}
		long[] ids=new long[idArray.size()];
		for(int i=0,length=ids.length;i<length;i++){
			ids[i]=idArray.get(i);
		}
		return ids;
	}
	SharedPreferences sp;
	DownloadManager downloadManager;
	Handler handler=new Handler();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sp=getSharedPreferences("download_ids", MODE_PRIVATE);
		downloadManager=(DownloadManager) getSystemService(DOWNLOAD_SERVICE);
		registerReceiver(downComplateRec, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
		getContentResolver().registerContentObserver(Uri.parse("content://downloads/my_downloads"), true, downloadObserver);
	}
	
    @Override
    protected void onDestroy() { 
        super.onDestroy(); 
          unregisterReceiver(downComplateRec);   
          getContentResolver().unregisterContentObserver(downloadObserver); 
    } 
	//下载进度的接收者
    ContentObserver downloadObserver=new ContentObserver(null) {
        @Override
        public void onChange(boolean selfChange) { 
              noticeDownloadChange();    
        }
    };
	//下载完成的接收者
	private BroadcastReceiver downComplateRec = new BroadcastReceiver() {    
        @Override   
        public void onReceive(Context context, Intent intent) {    
            //这里可以取得下载的id，这样就可以知道哪个文件下载完成了。适用与多个下载任务的监听    
//            long downId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
            noticeDownloadChange();    
        }    
    };
    protected void noticeDownloadChange() {
    	long[] dIds = getAllDownloadIds();
    	if (dIds.length == 0)
    	{
    		return;
    	}
        DownloadManager.Query query = new DownloadManager.Query();    
        query.setFilterById(dIds);
        Cursor c = downloadManager.query(query);
//        c.moveToFirst();
        int idIdx=c.getColumnIndex(DownloadManager.COLUMN_ID);
        int statusIdx=c.getColumnIndex(DownloadManager.COLUMN_STATUS);
        int titleIdx=c.getColumnIndex(DownloadManager.COLUMN_TITLE);
        int fileSizeIdx=c.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
        int bytesDLIdx=c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
        int urlIdx=c.getColumnIndex(DownloadManager.COLUMN_URI);
        int locIdx=c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
//        int reasonIdx=c.getColumnIndex(DownloadManager.COLUMN_REASON);
        final ArrayList<DownloadingInfo> infos=new ArrayList<DownloadActivity.DownloadingInfo>();
        while(c.moveToNext()) {
            int status = c.getInt(statusIdx);
            String title = c.getString(titleIdx);
            int fileSize = c.getInt(fileSizeIdx);
            int bytesDL = c.getInt(bytesDLIdx);
            long dId=c.getLong(idIdx);
//            int reason = c.getInt(reasonIdx);// Translate the pause reason to friendly text.
            
//            StringBuilder sb = new StringBuilder();
//            sb.append(title).append("\n");
//            sb.append("Downloaded").append(bytesDL).append(" / " ).append(fileSize);
//            // Display the status
//            Log.d("fax", sb.toString());
            infos.add(new DownloadingInfo(dId, c.getString(urlIdx), title, fileSize, bytesDL, status, c.getString(locIdx)));
            switch(status) {
            case DownloadManager.STATUS_PAUSED:
            case DownloadManager.STATUS_PENDING:    
            case DownloadManager.STATUS_RUNNING:    
                //正在下载，不做任何事情    
                break;    
            case DownloadManager.STATUS_SUCCESSFUL:    
                //完成    
                break;    
            case DownloadManager.STATUS_FAILED:    
                //清除已下载的内容，重新下载    
                downloadManager.remove(dId);
                sp.edit().remove(dId+"");
                break;    
            }
        }
        handler.post(new Runnable() {
			public void run() {
		        onDownload(infos);
			}
		});
    }
    abstract void onDownload(ArrayList<DownloadingInfo> infos);
    

	private static String[] sizeunits={"B","KB","MB","GB","TB"};
	public static String sizeToString(float size){
    	int i=0;
    	for (i=0;i<5;i++){
    		if(size<1024) break;
    		size=size/1024;
    	}
    	return String.format("%.1f", size)+sizeunits[i];
	}
    public static class DownloadingInfo{
    	long dId;
    	String url;
		String title;
		long fileSize;
        long bytesDL;
        int status;
        String pathUri;
		public DownloadingInfo(long dId,String url, String title, long fileSize, long bytesDL, int status, String pathUri) {
			this.dId = dId;
			this.url = url;
			this.title = title;
			this.fileSize = fileSize;
			this.bytesDL = bytesDL;
			this.status = status;
			this.pathUri = pathUri;
		}
    	public String getUrl() {
			return url;
		}
		public String getTitle() {
			return title;
		}
		public long getFileSize() {
			return fileSize;
		}
		public long getBytesDL() {
			return bytesDL;
		}
        public int getStatus() {
			return status;
		}
        public long getDId() {
			return dId;
		}
		public boolean isDownloading(){
        	return status == DownloadManager.STATUS_RUNNING || status==DownloadManager.STATUS_PENDING;
        }
        public boolean isComplete(){
        	return status == DownloadManager.STATUS_SUCCESSFUL;
        }
		public String getPathUri() {
			return pathUri;
		}
		public int getDownloadProgress(){
			if(bytesDL==0) return 0;
			if(fileSize==0) return -1;
        	return (int) (bytesDL*100l/fileSize);
        }
    }
}
