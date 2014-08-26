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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Align;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;


public class ConnectionPrint implements Runnable {
	Context context;
	String username;
	String userphon;
	String grade;

	
	boolean isConnected=false;
	private OutputStream outputStream=null;
	private InputStream inputStream=null;
	private Socket client;
	Thread TCPServerThread;
	private static final int WRITE_READ = 2;
	private static int writeState = 2;
	String TAG = "MainActivity";
	
	public ConnectionPrint(Context context, String username, String userphon, String grade) {
		super();
		this.context = context;
		this.username = username;
		this.userphon = userphon;
		this.grade = grade;
	}


	@Override
	public void run() {
		if(isConnected == false)
		{
			SocketAddress my_sockaddr = null;
			String ipConfic = PreferenceManager.getDefaultSharedPreferences(context)
					.getString("PrintIp", null);//PrintIp是setting.xml中设置的打印Ip
			try {
				String[] ss = ipConfic.split(":");
				InetAddress serverAddr = InetAddress.getByName(ss[0]);// TCPServer.SERVERIP
				my_sockaddr = new InetSocketAddress(serverAddr, Integer.valueOf(ss[1]));
			} catch (Exception e) {
				new Handler(Looper.getMainLooper()).post(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(context, "打印机Ip格式错误，请重新设置", Toast.LENGTH_SHORT).show();
					}
				});
				return;
			}
			
			try {
				client = new Socket();
				client.connect(my_sockaddr,5000);
				outputStream = client.getOutputStream();
				inputStream = client.getInputStream();
			} catch (UnknownHostException e) {
				Log.d(TAG, e.getMessage());
			} catch (IOException e) {
				Log.d(TAG, e.getMessage());
			}
			
			isConnected = true;
		}

		TCPServerThread = new Thread(new TCPServerThread());
		TCPServerThread.start();
		//new Thread(new InputStreamThread()).start();
		SendData(username+"    "+userphon);
		
		Bitmap printBg = Bitmap.createBitmap(384, 100,Config.RGB_565);
		Paint paint = new Paint();
		Canvas canvas = new Canvas(printBg);
		paint.setColor(Color.WHITE);
		canvas.drawRect(0, 0, 384, 100, paint);
		paint.setColor(Color.BLACK);
		paint.setTextSize(80);
		paint.setTextAlign(Align.CENTER);
		canvas.drawText(grade, 170, 60, paint);
		canvas.save();
		PrintImageOld(resizeImage(printBg,384, printBg.getHeight()));
		SendData("格瑞特儿童绘画打分");
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String time = format.format(date);
		SendData(time);
		SendData("    ");
		SendData("    ");
		SendData("    ");
		SendData("    ");
		SendData("    ");
		SendData("    ");
		SendData("    ");

	}
	
	
	
	//缩放图片
	public static Bitmap resizeImage(Bitmap bitmap, int w, int h) {
		Bitmap BitmapOrg = bitmap;
		int width = BitmapOrg.getWidth();
		int height = BitmapOrg.getHeight();
		int newWidth = w;

		float scaleWidth = ((float) newWidth) / width;
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleWidth);
		Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width,
				height, matrix, true);
		return resizedBitmap;
	}
		
	private void SendData(String strSend)
    {
    	try
		{
    		strSend=strSend+"\r\n";
			outputStream.write(strSend.getBytes("GBK"));
			outputStream.flush();				
		}
		catch (NumberFormatException e)
		{
				//	Log.d(TAG, e.getMessage());
		}
		catch (IOException e) {
				//	Log.d(TAG, e.getMessage());
		}
    }
    
    private void SendData(byte[] btSend)
    {
    	try
		{
			outputStream.write(btSend);
			outputStream.flush();
		}
		catch (NumberFormatException e)
		{
				//	Log.d(TAG, e.getMessage());
		}
		catch (IOException e) {
				//	Log.d(TAG, e.getMessage());
		}
    }
	
	public void PrintImageOld(Bitmap bitmapCode) {
		byte[] sendbuf = StartBmpToPrintCode(bitmapCode);
		byte[] sendper;
		int num = 0;
		int total = 1152;
		// 老版打印机只要用这个
		while (num != sendbuf.length) {
			if (writeState == WRITE_READ) {
				if ((sendbuf.length - num) > total) {
					sendper = new byte[total];
					System.arraycopy(sendbuf, num, sendper, 0, total);
					num = num + total;
				} else {
					sendper = new byte[sendbuf.length - num];
					System.arraycopy(sendbuf, num, sendper, 0, sendbuf.length
							- num);
					num = sendbuf.length;
				}
				try {
					Thread.sleep(1200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				SendData(sendper);
				//write(sendper);
			}
		}
		// 新版打印机只要用这个
		// write(sendbuf);
	}
		
	//图片转字节数组
	private byte[] StartBmpToPrintCode(Bitmap bitmap) {
		byte temp = 0;
		int j = 7;
		int start = 0;
		if (bitmap != null) {
			int mWidth = bitmap.getWidth();
			int mHeight = bitmap.getHeight();

			int[] mIntArray = new int[mWidth * mHeight];
			byte[] data = new byte[mWidth * mHeight];
			bitmap.getPixels(mIntArray, 0, mWidth, 0, 0, mWidth, mHeight);
			encodeYUV420SP(data, mIntArray, mWidth, mHeight);
			byte[] result = new byte[mWidth * mHeight / 8];
			for (int i = 0; i < mWidth * mHeight; i++) {
				temp = (byte) ((byte) (data[i] << j) + temp);
				j--;
				if (j < 0) {
					j = 7;
				}
				if (i % 8 == 7) {
					result[start++] = temp;
					temp = 0;
				}
			}
			if (j != 7) {
				result[start++] = temp;
			}

			int aHeight = 24 - mHeight % 24;
			byte[] add = new byte[aHeight * 48];
			byte[] nresult = new byte[mWidth * mHeight / 8 + aHeight * 48];
			System.arraycopy(result, 0, nresult, 0, result.length);
			System.arraycopy(add, 0, nresult, result.length, add.length);

			byte[] byteContent = new byte[(mWidth / 8 + 4)
					* (mHeight + aHeight)];// 打印数组
			byte[] bytehead = new byte[4];// 每行打印头
			bytehead[0] = (byte) 0x1f;
			bytehead[1] = (byte) 0x10;
			bytehead[2] = (byte) (mWidth / 8);
			bytehead[3] = (byte) 0x00;
			for (int index = 0; index < mHeight + aHeight; index++) {
				System.arraycopy(bytehead, 0, byteContent, index * 52, 4);
				System.arraycopy(nresult, index * 48, byteContent,
						index * 52 + 4, 48);

			}
			return byteContent;
		}
		return null;

	}
			
	//转换图片格式
	public void encodeYUV420SP(byte[] yuv420sp, int[] rgba, int width,
			int height) {
		////final int frameSize = width * height;
		int r, g, b, y;//, u, v;
		int index = 0;
		////int f = 0;
		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {
				r = (rgba[index] & 0xff000000) >> 24;
				g = (rgba[index] & 0xff0000) >> 16;
				b = (rgba[index] & 0xff00) >> 8;
				// rgb to yuv
				y = ((66 * r + 129 * g + 25 * b + 128) >> 8) + 16;
				/*u = ((-38 * r - 74 * g + 112 * b + 128) >> 8) + 128;
				v = ((112 * r - 94 * g - 18 * b + 128) >> 8) + 128;*/
				// clip y
				// yuv420sp[index++] = (byte) ((y < 0) ? 0 : ((y > 255) ? 255 :
				// y));
				byte temp = (byte) ((y < 0) ? 0 : ((y > 255) ? 255 : y));
				yuv420sp[index++] = temp > 0 ? (byte) 1 : (byte) 0;

				// {
				// if (f == 0) {
				// yuv420sp[index++] = 0;
				// f = 1;
				// } else {
				// yuv420sp[index++] = 1;
				// f = 0;
				// }

				// }

			}

		}
		////f = 0;
	}
	
	public void close() throws IOException{
		if(client == null)
		{
			return;
		}
		client.close();
	}

	public class TCPServerThread extends Thread 
	{  
	    public TCPServerThread() 
	    {
	    }
	    public void run()
	    {  
	    	//tvRecv.setText("start");
            byte[] buffer = new byte[1024];  
			final StringBuilder sb = new StringBuilder();
            while (true) 
            {
                try 
                {  
	    			int readSize = inputStream.read(buffer);
					//Server is stoping
					if(readSize == -1)
					{
							
						inputStream.close();
						break;
					}
					//Update the receive editText
					else if(readSize>0)
					{
						sb.append(new String(buffer,0,readSize));
	//		    							runOnUiThread(new Runnable()
	//		    							{
	//		    								public void run()
	//		    								{
	//		    								}
	//		    							});
					}
                }
                catch (IOException e)
                {
                    
                }
            }
	    }
	}
}
