package com.xunlu.lizhen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

public class HttpUtils {
//	static final String connectFail="错误：连接失败";
	static final String NetWorkError="错误：网络异常";
	static private DefaultHttpClient httpClient;
    private static final int DEFAULT_MAX_CONNECTIONS = 30;  
    private static final int DEFAULT_SOCKET_TIMEOUT = 10 * 1000;
    private static final int DEFAULT_SOCKET_BUFFER_SIZE = 8192;
    
    private static final int DEFAILT_TRY_TIME=2;
    private static final int DEFAILT_RETRY_DELAY=1000;
    private static boolean DEBUG=true;
	public static synchronized DefaultHttpClient getHttpClient() {  
	    if(httpClient == null) {  
	        final HttpParams httpParams = new BasicHttpParams();    
	          
	        // timeout: get connections from connection pool  
	        ConnManagerParams.setTimeout(httpParams, 1000);    
	        // timeout: connect to the server  
	        HttpConnectionParams.setConnectionTimeout(httpParams, DEFAULT_SOCKET_TIMEOUT);  
	        // timeout: transfer data from server  
	        HttpConnectionParams.setSoTimeout(httpParams, DEFAULT_SOCKET_TIMEOUT);   
	          
	        // set max connections per host  
	        ConnManagerParams.setMaxConnectionsPerRoute(httpParams, new ConnPerRouteBean(10));
	        // set max total connections  
	        ConnManagerParams.setMaxTotalConnections(httpParams, DEFAULT_MAX_CONNECTIONS);  
	          
	        // use expect-continue handshake  
	        HttpProtocolParams.setUseExpectContinue(httpParams, true);  
	        // disable stale check  
	        HttpConnectionParams.setStaleCheckingEnabled(httpParams, false);  
	          
	        HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);    
	        HttpProtocolParams.setContentCharset(httpParams, HTTP.UTF_8);   
	            
//	        HttpClientParams.setRedirecting(httpParams, false);  
	          
	        // set user agent  
	        String userAgent = "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2) Gecko/20100115 Firefox/3.6";  
	        HttpProtocolParams.setUserAgent(httpParams, userAgent);
	          
	        // disable Nagle algorithm  
	        HttpConnectionParams.setTcpNoDelay(httpParams, true);   
	          
	        HttpConnectionParams.setSocketBufferSize(httpParams, DEFAULT_SOCKET_BUFFER_SIZE);    
	          
	        // scheme: http and https  
	        SchemeRegistry schemeRegistry = new SchemeRegistry();    
	        schemeRegistry.register(new Scheme("http", new PlainSocketFactory(), 80));    
//	        schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));  
	  
	        ClientConnectionManager manager = new ThreadSafeClientConnManager(httpParams, schemeRegistry);    

	        httpClient = new DefaultHttpClient(manager, httpParams);  
			DefaultHttpRequestRetryHandler handler = new DefaultHttpRequestRetryHandler(3,true); 
			httpClient.setHttpRequestRetryHandler(handler);  
	    }         
	    return httpClient;  
	}  
	public static String reqForPost(String postUrl, NameValuePair... pairs){
		List<NameValuePair> pairList=Arrays.asList(pairs);
		return reqForPost(postUrl, pairList);
	}
	public static String reqForPost(String postURL, List<NameValuePair> params) {
		if(DEBUG) Log.d("fax", "reqForPost:"+postURL);
		if(DEBUG) for(NameValuePair pair:params){
			Log.d("fax", pair.getName()+":"+pair.getValue());
		}
			HttpRequestBase httpRequest = new HttpPost(postURL);
			if (params!=null&&params.size()>0) {
				try {
					HttpEntity httpentity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
					((HttpPost) httpRequest).setEntity(httpentity);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return execute(httpRequest);
}
	public static String reqForGet(String getURL) {
		if(DEBUG) Log.d("fax", "reqForGet:"+getURL);
		HttpRequestBase httpRequest = new HttpGet(getURL);
		return execute(httpRequest);
	}
	public static String reqForPut(String putURL) {
		if(DEBUG) Log.d("fax", "reqForPut:"+putURL);
		HttpRequestBase httpRequest = new HttpPut(putURL);
		return execute(httpRequest);
	}
	public static int reqStatusCodeForPut(String putURL) {
		if(DEBUG) Log.d("fax", "reqForPut:"+putURL);
		HttpRequestBase httpRequest = new HttpPut(putURL);
		return executeForStatusCode(httpRequest);
	}
	public static int reqStatusCodeForDelete(String deleteURL) {
		if(DEBUG) Log.d("fax", "reqForDelete:"+deleteURL);
		HttpRequestBase httpRequest = new HttpDelete(deleteURL);
		return executeForStatusCode(httpRequest);
	}
	private static int executeForStatusCode(HttpRequestBase httpRequestBase){
		for(int i=0;i<DEFAILT_TRY_TIME;i++){
			try {
				return executeForStatusCodeImp(httpRequestBase);
			} catch (Exception e) {
				if(DEBUG){
					Log.e("fax", "execute error");
					e.printStackTrace();
				}
				try {
					Thread.sleep(DEFAILT_RETRY_DELAY);
				} catch (Exception e2) {
				}
			}
		}
		return -1;
	}
	private static int executeForStatusCodeImp(HttpRequestBase httpRequestBase) throws Exception{
			int code = getHttpClient().execute(httpRequestBase).getStatusLine().getStatusCode();
			if(DEBUG) Log.d("fax", "execute Code:" + code);
			return code;
	}
	private static String execute(HttpRequestBase httpRequest){
		for(int i=0;i<DEFAILT_TRY_TIME;i++){
			try {
				return executeImp(httpRequest);
			} catch (Exception e) {
				if(DEBUG){
					Log.e("fax", "execute error");
					e.printStackTrace();
				}
				try {
					Thread.sleep(DEFAILT_RETRY_DELAY);
				} catch (Exception e2) {
				}
			}
		}
		return NetWorkError;
	}
	@SuppressLint("DefaultLocale")
	private static String executeImp(HttpRequestBase httpRequest) throws Exception{
			httpRequest.addHeader("Accept-Encoding", "gzip");
			HttpResponse httpResponse = getHttpClient().execute(httpRequest);
			
			InputStream is = httpResponse.getEntity().getContent();
			try {
				String encoding = httpResponse.getEntity().getContentEncoding().getValue().toLowerCase();
				if (encoding.equals("gzip")) is = new GZIPInputStream(is);
			} catch (Exception e) {
			}
			String strResult = readInputStream(new InputStreamReader(is));
			if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK && DEBUG ) {
				Log.d("fax", "execute may Fail,Code:" + httpResponse.getStatusLine().getStatusCode()+",Entity:"+strResult);
			}
			httpRequest.abort();
			return strResult;
	}
	/**
	 *  * 从输入流中读入数据  *   * @param request  * @param response  * @param s  
	 */
	public static String readInputStream(InputStreamReader in) {
		StringBuilder sb = new StringBuilder();
		try {
			BufferedReader reader = new BufferedReader(in);
			char[] temp=new char[1024];
			int length;
			while ((length = reader.read(temp)) != -1) {
				if(Thread.currentThread().isInterrupted()) break;
				sb.append(temp,0,length);
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				in.close();
			} catch (IOException e) {
			}
		}
		return (sb.toString());
	}
	
	public static class WebviewCookieStore extends BasicCookieStore {
		Context context;
		public WebviewCookieStore(Context context){
			this.context=context;
			CookieSyncManager.createInstance(context);
		}
		@Override
		public synchronized void addCookie(Cookie cookie) {
			addCookieImpl(cookie);
		}
		public synchronized void addCookieImpl(Cookie cookie) {
			super.addCookie(cookie);

	        CookieManager cookieManager=CookieManager.getInstance();
	        String[] cookieInfo=parseCookie(cookie);
	        if(cookieInfo!=null) cookieManager.setCookie(cookieInfo[0], cookieInfo[1]);
		}
		public synchronized void addCookiesImpl(Cookie[] cookies) {
	        if (cookies != null) {
	            for (Cookie cooky : cookies) {
	            	addCookieImpl(cooky);
	            }
	        }
		}
		/**
		 * @param cookie 
		 * @return domain,head中Cookie字段
		 */
		public String[] parseCookie(Cookie cookie){
			StringBuilder sb = new StringBuilder();
			String domain = cookie.getDomain();
			if (domain == null) return null;
			sb.append(cookie.getName()).append("=").append(cookie.getValue()).append("; ");
			sb.append("path=").append(cookie.getPath() == null ? "/" : cookie.getPath()).append("; ");
			sb.append("domain=").append(domain);
			return new String[]{domain,sb.toString()};
		}
	}
}
