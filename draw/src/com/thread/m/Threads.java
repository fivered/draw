package com.thread.m;

import java.io.InputStream;
import java.net.URI;
import java.net.URLDecoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class Threads {
	private static InputStream input = null;
	
	public static String GetINPutStream(String path,String name, long currMun) throws Exception {

		path = path+"?name="+name+"&max="+currMun;
		URI uri = new URI(path);
		HttpGet request = new HttpGet(uri);
		HttpClient httpClient = new DefaultHttpClient();
		HttpResponse response = httpClient.execute(request);
		String result="";
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			result = EntityUtils.toString(response.getEntity());
		}
		return result;
	}
	/*
	public static boolean setRegion(String path, List<NameValuePair> pairList) throws Exception {
		URI uri = new URI(path);
		HttpPost request = new HttpPost(uri);
		HttpClient httpClient = new DefaultHttpClient();
		//HttpEntity entity = new UrlEncodedFormEntity(pairList,"UTF-8");
		MultipartEntity entity = new MultipartEntity();
		//entity.addPart("", contentBody)
		//ContentBody
		//new StringBody(foodname.getText().toString().trim())
		request.setEntity(entity);
		HttpResponse response = httpClient.execute(request);
		 Log.e("fffff","----====");

		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			return true;
		}
		return false;
	}
	*/
	public static boolean setRegion(String path, Map<String, ContentBody> pairMap) throws Exception {
		URI uri = new URI(path);
		HttpPost request = new HttpPost(uri);
		HttpClient httpClient = new DefaultHttpClient();
		//HttpEntity entity = new UrlEncodedFormEntity(pairList,"UTF-8");
		MultipartEntity entity = new MultipartEntity();
		Set<String> keySet = pairMap.keySet();
		Iterator<String> itKeySetIterator = keySet.iterator();
		while (itKeySetIterator.hasNext()) {
			String key = (String) itKeySetIterator.next();
			ContentBody contentBody = pairMap.get(key);
			entity.addPart(key, contentBody);
		}
		
		request.setEntity(entity);
		HttpResponse response = httpClient.execute(request);
		String responseString = EntityUtils.toString(response.getEntity(),"UTF-8");
		for(int i = 0; i<responseString.length(); i++){
			if(responseString.charAt(i)=='{'||responseString.charAt(i)=='['){
				responseString = responseString.substring(i);
			}
		}
		URLDecoder.decode(responseString,"UTF-8");
		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			return true;
		}
		return false;
	}
	
	
	public static boolean setListResult(String path, Map<String, ContentBody> pairMap) throws Exception {
		
		URI uri = new URI(path);
		HttpPost request = new HttpPost(uri);
		HttpClient httpClient = new DefaultHttpClient();
		MultipartEntity entity = new MultipartEntity();
		Set<String> keySet = pairMap.keySet();
		Iterator<String> itKeySetIterator = keySet.iterator();
		while (itKeySetIterator.hasNext()) {
			String key = (String) itKeySetIterator.next();
			ContentBody contentBody = pairMap.get(key);
			entity.addPart(key, contentBody);
		}
		request.setEntity(entity);
		HttpResponse response = httpClient.execute(request);
		String responseString = EntityUtils.toString(response.getEntity(),"UTF-8");
		for(int i = 0; i<responseString.length(); i++){
			if(responseString.charAt(i)=='{'||responseString.charAt(i)=='['){
				responseString = responseString.substring(i);
			}
		}
		URLDecoder.decode(responseString,"UTF-8");
		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			return true;
		}
		return false;
		
	}
	
	public static String getSerchStream(String path,String keyword, int num) throws Exception {

		path = path+"?keyword="+keyword+"&num="+num;
		URI uri = new URI(path);
		HttpGet request = new HttpGet(uri);
		HttpClient httpClient = new DefaultHttpClient();
		HttpResponse response = httpClient.execute(request);
		String result="";
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			result = EntityUtils.toString(response.getEntity());
		}
		return result;
	}
	
	public static InputStream getSingleImage(String path) throws Exception {

		URI uri = new URI(path);
		HttpGet request = new HttpGet(uri);
		HttpClient httpClient = new DefaultHttpClient();
		HttpResponse response = httpClient.execute(request);
		InputStream is = null;
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			HttpEntity httpEntity =response.getEntity();
			is = httpEntity.getContent();
		}
		return is;
	}
	
	
}
