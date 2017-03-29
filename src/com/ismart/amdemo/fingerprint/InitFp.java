package com.ismart.amdemo.fingerprint;

import android.content.Context;

import com.ismart.amdemo.util.MyApplication;


public class InitFp extends Thread{
	private static final String TAG = "InitFp";
	private Context context;
	
	
	public InitFp(Context context)
	{
		this.context = context;
	}
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		try {
			sleep(1000); 
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (MyApplication.mFPApi.InitDev(MyApplication.mFPApi.FP_MODEL) == 0)
		{
			MyApplication.getInstance().isFingerprint = true;
		}else{
			MyApplication.getInstance().isFingerprint = false;
		}
	}
}
