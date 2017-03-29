package com.ismart.amdemo.fingerprint;

import android.content.Context;
import android.os.Message;
import android.util.Log;
import android.zyapi.FingerPrintApi;

import com.ismart.amdemo.util.Constant;
import com.ismart.amdemo.util.MyApplication;

public class CheckFp extends Thread{

	private static final String TAG = "CheckFp";
	private Context context;
	
	
	public CheckFp(Context context)
	{
		this.context = context;
	}
	public void run()
	{
		Log.d(TAG, "checkFP thread run.");
		Message msg;
		try {
			int ret = 0, times = 0;
			int fpNumber = -1;
			while (true) {
				Log.d(TAG, "GetFPImage start...");
				times ++;
				ret = MyApplication.mFPApi.GetFPImage();
				Log.d(TAG, "GetFPImage result="+ret);
				if (ret == FingerPrintApi.ERR_SUCCESS) {
					break;
				} 
				Log.d(TAG, "GetFPImage->result:"+ret + "times:"+times);
				if (times >= 2) {
					Log.d(TAG, "GetFPImage overtime.");
					msg=Constant.actyList.get(0).obtainMessage(Constant.GET_IMAGE);
					msg.arg1 = 0;
					Constant.actyList.get(0).sendMessage(msg);
					RegisterFpService.isService = true;
					return;
				}
			}
			msg = Constant.actyList.get(0).obtainMessage(Constant.GET_IMAGE);
			msg.arg1 = 1;
			Constant.actyList.get(0).sendMessage(msg);
			sleep(300);
			Log.d(TAG, "IdentifyFP---> start...");
			fpNumber = MyApplication.mFPApi.IdentifyFP();//指纹识别，将采集到的指纹图像与已注册的指纹特征模版进行1：N 对比
			Log.d(TAG, "IdentifyFP---> fpNumber:"+fpNumber);
			if (fpNumber > 0) {
				msg = Constant.actyList.get(0).obtainMessage(Constant.FINGER_NUMBER);
				msg.arg1 = 1;
				msg.arg2 = fpNumber;
				Constant.actyList.get(0).sendMessage(msg);
		
			} else {
				msg = Constant.actyList.get(0).obtainMessage(Constant.FINGER_NUMBER);
				msg.arg1 = 0;
				msg.arg2 = ret;
				Constant.actyList.get(0).sendMessage(msg);
				//msg.sendToTarget();
			}
			RegisterFpService.isService = true;
		} catch (Exception e) {
		}
	}
}
