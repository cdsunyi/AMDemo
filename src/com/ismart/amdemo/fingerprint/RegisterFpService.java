package com.ismart.amdemo.fingerprint;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
import android.zyapi.FingerPrintApi;

import com.ismart.amdemo.entity.FpListData;
import com.ismart.amdemo.util.Constant;
import com.ismart.amdemo.util.MyApplication;
import com.ismart.amdemo.util.SoundPlayer;

public class RegisterFpService extends Service {
	private static String TAG = "RegisterFpService";
	private SoundPlayer player;
	private Handler myHandler;
	private Handler sendHandler;
	private static final int SEND_REGISTER_ONCE = 0;
	private static final int SEND_REGISTER_TWICE = 1;
	private static final int GET_REGISTER_FP = 2;

	private int fpTempNo, ret, timesonce = 0, timestwice = 0, tempNo;
	private boolean iscreatfpsucceed = true;
	private boolean registertwice = false;
	private boolean isregistering = false;
	private boolean overtime = false;
	private Looper mSendLooper = null;
	private static final int MAX_TRY_TIMES = 3;
	private RegisterThread mRegisterThread = null;
	public static final String REGISTER_FP = "con.ismart1.amdemo.registerfp";
	public static final String FINGER_PRINT = "con.ismart1.amdemo.fingerprint";
	public static final String ACTION_FINGERPRINT_EINT_EVENT = "android.intent.action.FINGERPRINT_EINT_EVENT";
	private MyReceiver mReceiver;


	private int state;
	private String name;
	private CheckFp checkFP = null;
	public static boolean isService = true;
	public static boolean isRegister = false;

	private class RegisterThread extends Thread {

		public void run() {
			Looper.prepare();   
			mSendLooper = Looper.myLooper();
			Log.d("RegisterFpService", "RegisterFpService -> RegisterThread");
			myHandler = new Handler(mSendLooper) {
				public void handleMessage(Message msg) {
					// process incoming messages here }
					switch (msg.what) {
					case SEND_REGISTER_ONCE:
						RegisterOnce();
						break;
					case SEND_REGISTER_TWICE:
						RegisterTwice();
						break;
					case GET_REGISTER_FP:
						GetRegisterfp();
						break;
					}
				}
			};
			Looper.loop();
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		Log.d("RegisterFpService", "RegisterFpService -> onCreate");
		player = new SoundPlayer(this);
		sendHandler = Constant.actyList.get(0);
		mRegisterThread = new RegisterThread();
		mRegisterThread.start();
		mReceiver = new MyReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(REGISTER_FP);
		filter.addAction(ACTION_FINGERPRINT_EINT_EVENT);
		registerReceiver(mReceiver, filter);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Log.d("RegisterFpService", "RegisterFpService -> onStartCommand");

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.d("RegisterFpService", "RegisterFpService -> onDestroy");
		Stop();
	}

	private void GetRegisterfp() {
		Log.d("RegisterFpService", "RegisterFpService -> getregisterfp");
		ret = MyApplication.mFPApi.GetFPImage();
		if (ret == FingerPrintApi.ERR_SUCCESS) {

			InformUser(SoundPlayer.KEY, null);
			myHandler.removeMessages(SEND_REGISTER_ONCE);
			myHandler.removeMessages(SEND_REGISTER_TWICE);
			iscreatfpsucceed = CreatfpTemplate();
			if (iscreatfpsucceed == false) {
				return;
			}
			if (registertwice == false) {
				registertwice = true;			
				StartRegisterTwice();
			}
			overtime = false;
			isregistering = false;
			return;
		} else if (ret == FingerPrintApi.ERR_OVERTIME) {
			InformUser(SoundPlayer.ANSHOUZHI, "超时，请按手指");
			Log.d("fingerprintReceiver", "超时，请按手指");
			overtime = true;
			myHandler.removeMessages(SEND_REGISTER_ONCE);
			myHandler.sendMessageDelayed(myHandler.obtainMessage(SEND_REGISTER_ONCE), 300);
			isregistering = false;
			return;
		} else if (ret == FingerPrintApi.ERR_NO_DETECT_FINGER) {
			StopSelf(SoundPlayer.CAIJISHIBAI, "没有检测到指纹！");
			isregistering = false;
			return;
		}

		return;
	}

	private void RegisterTwice() {
		// TODO Auto-generated method stub
		Log.d("RegisterFpService", "RegisterFpService -> registertwice");
		if (timestwice > MAX_TRY_TIMES) {
			StopSelf(SoundPlayer.CAIJISHIBAI, "采集失败！");
			timestwice = 0;
			registertwice = false;  
			isregistering = false;
			myHandler.removeMessages(SEND_REGISTER_TWICE);
			return;
		} else { 
			isregistering = false;
			InformUser(SoundPlayer.ZAIANSHOUZHI, "请再按手指");
			myHandler.sendMessageDelayed(myHandler.obtainMessage(SEND_REGISTER_TWICE), 3000);
			timestwice++;
		}
	}

	private void Enrollfp() {
		// TODO Auto-generated method stub
		Log.d("RegisterFpService", "RegisterFpService -> enrollfp");
		ret = MyApplication.mFPApi.EnrollFP(fpTempNo);
		if (ret != FingerPrintApi.ERR_SUCCESS) {
			if (ret == FingerPrintApi.ERR_FP_DUPLICATION) {
				StopSelf(SoundPlayer.CAIJISHIBAI, "该指纹已登记！");
			} else if (ret == FingerPrintApi.ERR_GENERALIZE) {
				StopSelf(SoundPlayer.CAIJISHIBAI, "指纹模版生成失败！");
			} else {
				StopSelf(SoundPlayer.CAIJISHIBAI, "采集失败，请换其他手指！");
			}
		} else {
			InformUser(SoundPlayer.CAIJICHENGGONG, "采集成功");
			Message msg = myHandler.obtainMessage();
			msg.what = Constant.REGISTER_RESULT;
			msg.arg1 = 1;
			msg.obj = fpTempNo + "";
			//msg.sendToTarget();
			sendHandler.sendMessage(msg);
			StopSelf(SoundPlayer.CAIJICHENGGONG, "采集成功");
			myHandler.removeMessages(SEND_REGISTER_ONCE);
			myHandler.removeMessages(SEND_REGISTER_TWICE);
		}
	}

	private void InformUser(int sound, String text) {
		if (text != null) {
			Message msg = sendHandler.obtainMessage(Constant.MESSAGE_TOAST);
			Bundle bundle = new Bundle();
			bundle.putString(Constant.MyToast, text);
			msg.setData(bundle);
			sendHandler.sendMessage(msg);
		}
		if (sound >= 0)
			player.playSound(sound);
	}

	private void StopSelf(int sound, String text) {
		if (text != null)
			InformUser(sound, text);
		isRegister = false;
		isService = true;
		Constant.fingerTypeIn = false;
		return;
	}

	public int StartRegisterOnce() {
		Log.d("RegisterFpService", "RegisterFpService - > startregisteronce");

		myHandler.removeMessages(SEND_REGISTER_ONCE);
		myHandler.removeMessages(SEND_REGISTER_TWICE);
		MyApplication.getInstance().setisdepress(false);
		registertwice = false;
		timesonce = 0;
		timestwice = 0;
		tempNo = 0;
		isregistering = false;
		myHandler.sendMessage(myHandler.obtainMessage(SEND_REGISTER_ONCE));
		return 0;
	}

	public int Stop() {
		Log.d("RegisterFpService", "RegisterFpService -> Stop");
		if (myHandler != null) {
			myHandler.removeMessages(SEND_REGISTER_ONCE);
			myHandler.removeMessages(SEND_REGISTER_TWICE);
		}
		if (mRegisterThread != null) {
			mSendLooper.quit();
			myHandler = null;
			mRegisterThread = null;
			player = null;
		}
		if (mReceiver != null) {
			unregisterReceiver(mReceiver);
			mReceiver = null;
		}
		return 0;
	}

	private void RegisterOnce() {
		// TODO Auto-generated method stub
		Log.d("RegisterFpService", "RegisterFpService -> registeronce");
		if (timesonce > MAX_TRY_TIMES) {
			StopSelf(SoundPlayer.CAIJISHIBAI, "采集失败！");
			timesonce = 0;
			myHandler.removeMessages(SEND_REGISTER_ONCE);
			return;

		} else {

			isRegister = true;
			isService = false;
			fpTempNo = MyApplication.mFPApi.GetEmptyFpID();
			if (fpTempNo < 0) {
				StopSelf(SoundPlayer.CAIJISHIBAI, "获取空模板号失败！");
				return;
			}
			if (overtime == false) {
				InformUser(SoundPlayer.ANSHOUZHI, "请按手指");

			} else if (overtime == true) {
				InformUser(SoundPlayer.ANSHOUZHI, "超时，请按手指");
			}
			myHandler.sendMessageDelayed(myHandler.obtainMessage(SEND_REGISTER_ONCE), 3000);
			timesonce++;
		}
	}

	public void StartRegisterfp() {
		Log.d("RegisterFpService", "RegisterFpService -> startregisterfp");
		if (myHandler != null) {
			myHandler.removeMessages(SEND_REGISTER_ONCE);
			myHandler.removeMessages(SEND_REGISTER_TWICE);

		}
		myHandler.sendMessageDelayed(myHandler.obtainMessage(GET_REGISTER_FP), 300);
	}  

	private boolean CreatfpTemplate() {
		// TODO Auto-generated method stub
		Log.d("RegisterFpService", "RegisterFpService -> creatfptemplate");
		ret = MyApplication.mFPApi.GenFPTemplate(fpTempNo, tempNo);
		tempNo++;
		if (ret != FingerPrintApi.ERR_SUCCESS) {
			if (ret == FingerPrintApi.ERR_INVALID_TMPL_NO|| ret == FingerPrintApi.ERR_TMPL_NOT_EMPTY) {
				StopSelf(SoundPlayer.CAIJISHIBAI, "指纹模版编号无效或已被占用！");
				return false;
			} else {
				if (ret == FingerPrintApi.ERR_IMAGE_QUALITY_POOR) {
					InformUser(SoundPlayer.CHONGANSHOUZHI, "指纹图像质量太差！");
					return false;
				} else {
					InformUser(SoundPlayer.CHONGANSHOUZHI, "采集错误！");
					return false;
				}
			}
		}
		if (tempNo == 2) {
			Enrollfp();
			tempNo = 0;
			return true;
		}
		return true;
	}

	private void StartRegisterTwice() {
		// TODO Auto-generated method stub
		Log.d("RegisterFpService", "RegisterFpService -> startregistertwice");
		myHandler.removeMessages(SEND_REGISTER_ONCE);
		myHandler.removeMessages(SEND_REGISTER_TWICE);
		myHandler.sendMessageDelayed(myHandler.obtainMessage(SEND_REGISTER_TWICE), 300);
	}

	private class MyReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(REGISTER_FP)) {  
				Log.d("RegisterFpService", "RegisterFpService -> MyReceiver");
				StartRegisterOnce();
			} else if (intent.getAction().equals(ACTION_FINGERPRINT_EINT_EVENT)) {
				Log.d(TAG, "ACTION_FINGERPRINT_EINT_EVENT->status=" + state+ ", name=" + name + ", fingerTypeIn="+ Constant.fingerTypeIn + ", isService:" + isService);
				state = intent.getIntExtra("state", 0);
				name = intent.getStringExtra("name");
				if (state == 1 && name.equals("fingerprint") && isService && isRegister == false)
				{  
					Log.d("RegisterFpService", "RegisterFpService -> fingerprint");
					isService = false;        

					checkFP = new CheckFp(context);  
					checkFP.start();

				} else if (isRegister == true && isService == false) {
					Log.d("RegisterFpService", "RegisterFpService -> isRegister");
					if(isregistering == false){
						isregistering= true;
						StartRegisterfp();
					}
				} 
			}
		}

	}

}
