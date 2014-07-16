package com.xunlu.lizhen;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import com.date.ListMap;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class CameraView extends SurfaceView implements SurfaceHolder.Callback,
		Camera.PictureCallback {
	private SurfaceHolder holder;
	private Camera camera;
	private Camera.Parameters parameters;
	private Activity act;
	private Handler handler = new Handler();
	private Handler handler1 = new Handler();
	private Context context;
	private SurfaceView surfaceView;
	private AudioManager audio;
	private int current;
	private CameraView cameraView;
	// int i = 0;
	private ImageView ivc;
	private LinearLayout ly;
	
	// zf start
	private static String TAG = "CameraView";

	// zf end
	public CameraView(Context context, Camera cam) {
		super(context);
		camera = cam;
		surfaceView = this;
		surfaceView.setVisibility(View.INVISIBLE);
		audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		final int current = audio.getRingerMode();
		audio.setRingerMode(AudioManager.RINGER_MODE_SILENT);
		this.context = context;
		holder = getHolder();// 生成Surface Holder
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);// 指定Push Buffer

	}

	public CameraView(Context context, Activity act, ImageView ivc,
			LinearLayout ly, Camera camera) {// 在此定义一个构造方法用于拍照过后把CameraActivity给finish掉
		this(context, camera);
		this.act = act;
		this.ivc = ivc;
		this.ly = ly;
	}
	@Override
	public void surfaceCreated(final SurfaceHolder holder) {
		// TODO Auto-generated method stub
		Log.v("timer", "surfaceCreated");
	}
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		Log.v("timer", "surfaceChanged");
		Log.v(TAG, "surfaceChanged");
		if (holder.getSurface() == null) {
			// preview surface does not exist
			return;
		}
		try {
			camera.stopPreview();
		} catch (Exception e) {
			// ignore: tried to stop a non-existent preview
		}
		try {
			Log.d(TAG, holder + "" + camera);
			camera.setPreviewDisplay(holder);
			camera.startPreview();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		if (camera != null) {
			camera.stopPreview();
			camera.release();
			camera = null;
		}
	}

	public void onPictureTaken(byte[] data, Camera camera) {// 拍摄完成后保存照片

		try {

			Date date = new Date();
			SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat formats = new SimpleDateFormat("yyyyMMddHHmmss");
			String time = formats.format(date);
			// 在SD卡上创建文件夹
			File file = new File(Environment.getExternalStorageDirectory()
					+ "/myCamera/pics");
			Log.e("FFFF", file.getPath() + "GGGGGG");
			if (!file.exists()) {
				file.mkdirs();
			}
			String path = Environment.getExternalStorageDirectory()
					+ "/myCamera/pics/" + time + ".jpg";
			// if (data != null) {

			ListMap.setI(-1);
			Log.e("sssss", path + "");
			data2file(data, path);// 存到ID卡
			camera.setPreviewCallback(null);
			camera.stopPreview();
			camera.release();// 释放
			camera = null;
			ListMap.setJ(-1);
			holder.removeCallback(CameraView.this);
			audio.setRingerMode(current);

			// Thread.sleep(100);
			act.finish();
		} catch (Exception e) {

		} finally {
		}
	}

	/**
	 * 人为终止拍照，释放照相机，下一次打开
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			camera.setPreviewCallback(null);
			camera.stopPreview();
			camera.release();// 释放
			camera = null;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 *  将二进制数据转换为文件的函数
	 * @param w 二进制数据
	 * @param fileName 文件路径
	 * @throws Exception
	 */
	private void data2file(byte[] w, String fileName) throws Exception {
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(fileName);
			out.write(w);
			Bitmap bitmap = BitmapFactory.decodeByteArray(w, 0, w.length);
			ListMap.setImgsize(bitmap.getByteCount());
			ListMap.getLb().put(ListMap.getIndex(), bitmap);
			out.close();
		} catch (Exception e) {
			if (out != null)
				out.close();
			throw e;
		}
	}

	// private void uploadFile()// 拍照过后上传文件到服务器
	// {
	//
	// takephoto t = new takephoto();
	// t.GetErWeima();
	//
	// }
	class getphoto implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			camera.takePicture(null, null, new MyPictureCallback());
		}

	}

	private final class MyPictureCallback implements PictureCallback {
		public void onPictureTaken(byte[] data, Camera camera) {
			try {
				File jpgFile = new File(
						Environment.getExternalStorageDirectory(),
						System.currentTimeMillis() + ".jpg");
				Log.e("FFFFFF", jpgFile.getPath() + "****************");
				FileOutputStream outStream = new FileOutputStream(jpgFile);
				outStream.write(data);
				outStream.close();
				camera.startPreview();
				// act.finish();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
}