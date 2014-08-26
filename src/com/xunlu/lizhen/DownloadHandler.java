/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xunlu.lizhen;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Environment;
import android.webkit.CookieManager;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.Toast;

/**
 * Handle download requests
 */
@SuppressLint("NewApi")
public class DownloadHandler {
    /**
     * Notify the host application a download should be done, even if there
     * is a streaming viewer available for thise type.
     * @param activity Activity requesting the download.
     * @param url The full url to the content that should be downloaded
     * @return download id
     */
    public static long onDownloadStartNoStream(Activity activity, String url) {
    	return onDownloadStartNoStream(activity, url, null);
    }
    /**
     * Notify the host application a download should be done, even if there
     * is a streaming viewer available for thise type.
     * @param activity Activity requesting the download.
     * @param url The full url to the content that should be downloaded
     * @param mimetype The mimetype of the content reported by the server
     * @return download id
     */
    public static long onDownloadStartNoStream(Activity activity, String url, String mimetype) {
    	if(mimetype==null){
			MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
			mimetype = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(url));
    	}
        String filename = URLUtil.guessFileName(url, null, mimetype);

        // Check to see if we have an SDCard
        String status = Environment.getExternalStorageState();
        if (!status.equals(Environment.MEDIA_MOUNTED)) {
        	String title;
            String msg;

            // Check to see if the SDCard is busy, same as the music app
            if (status.equals(Environment.MEDIA_SHARED)) {
                msg = "SD 卡正忙。要允许下载，请在通知中触摸“关闭 USB 存储设备”。";
                title = "SD 卡不可用";
            } else {
                msg = "需要有 SD 卡才能下载";
                title = "无 SD 卡";
            }

            new AlertDialog.Builder(activity)
                .setTitle(title)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage(msg)
                .setPositiveButton(android.R.string.ok, null)
                .show();
            return -1;
        }

        Uri uri = Uri.parse(url);
        final DownloadManager.Request request;
        try {
            request = new DownloadManager.Request(uri);
        } catch (IllegalArgumentException e) {
            Toast.makeText(activity, "只能从“http”或“https”网址下载。", Toast.LENGTH_SHORT).show();
            return -1;
        }
        request.setMimeType(mimetype);
        
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);
        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        Uri destinationUri = Uri.withAppendedPath(Uri.fromFile(file), filename);
      //设置允许使用的网络类型，这里是移动网络和wifi都可以  
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE|DownloadManager.Request.NETWORK_WIFI);  
        //显示下载界面  
        request.setVisibleInDownloadsUi(true);
        // let this downloaded file be scanned by MediaScanner - so that it can 
        // show up in Gallery app, for example.
//        request.allowScanningByMediaScanner();
        request.setDescription(uri.getHost());
//        String cookies = CookieManager.getInstance().getCookie(url);
//        request.addRequestHeader("cookie", cookies);
//      发出通知，前台下载  
        request.setShowRunningNotification(true);
        if(VERSION.SDK_INT>=11)
        	request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        
		final DownloadManager manager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);

		long downId=manager.enqueue(request);
        Toast.makeText(activity, "正在开始下载...", Toast.LENGTH_SHORT) .show();
        
        // M: Add to start Download activity
//        Intent pageView = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);
//        pageView.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        activity.startActivity(pageView);
        
        return downId;
    }

}
