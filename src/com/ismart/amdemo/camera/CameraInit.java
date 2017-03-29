package com.ismart.amdemo.camera;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.ismart.amdemo.activity.MainActivity;
import com.ismart.amdemo.util.MyApplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class CameraInit {
	private String savePath = "/storage/sdcard0/DCIM/CameraInit/";
	private Uri mUri;

	private File folder;
	private String jpegName;
	private File JpgfilePath;
	private Intent fullscreen;

	public static final int TAKE_PICTURE_TIME_UPDATA = 8;

	private Context context;

	public CameraInit(Context context)
	{
		this.context = context;
	}
	// 初始化摄像头
	public void initCamera() {
		if (MyApplication.getInstance().isPreview) {
			MyApplication.getInstance().getCamera().stopPreview();
		}
		if (null != MyApplication.getInstance().getCamera()) {
			Camera.Parameters myParam = MyApplication.getInstance().getCamera().getParameters();
			  
			myParam.setPictureFormat(PixelFormat.JPEG);  
			MyApplication.getInstance().getCamera().setParameters(myParam);
			if(MyApplication.getInstance().isPressStopbtn == false){
				MyApplication.getInstance().getCamera().startPreview();
				MyApplication.getInstance().isPreview = true;  
			}else{
				MyApplication.getInstance().isPreview = false; 
			}
		
		}
	}  

	public void saveJpeg(Bitmap bm) {
		folder = new File(savePath);
		if (!folder.exists())
		{
			folder.mkdir();
		}
		jpegName = savePath + "123.jpg";   
		try {
			FileOutputStream fout = new FileOutputStream(jpegName);
			bm.compress(Bitmap.CompressFormat.JPEG, 100, fout);
			fout.flush();
			fout.close();   
			Log.d("CameraInit", "saveJpeg：存储完毕！");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Toast.makeText(context, "相片存储失败", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}

	public void enterAlbum() {

		JpgfilePath = new File(savePath + "123.jpg");
		fullscreen = new Intent(Intent.ACTION_VIEW);
		mUri = Uri.parse("file://" + JpgfilePath.getPath());
		fullscreen.setDataAndType(mUri, "image/*");
		context.startActivity(fullscreen);
	} 
}
