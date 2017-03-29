package com.ismart.amdemo.util;

//Download by http://www.codefans.net
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Application;
import android.hardware.Camera;
import android.os.Looper;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import android.zyapi.FingerPrintApi;


import com.ismart.amdemo.data.MemInfoDao;
import com.ismart.amdemo.entity.FpListData;

public class MyApplication extends Application { 
	/**     
	 * 
	 * 创建全局变量 全局变量一般都比较倾向于创建一个单独的数据类文件，并使用static静态变量
	 * 这里使用了在Application中添加数据的方法实现全局变量
	 * 注意在AndroidManifest.xml中的Application节点添加android:name=".MyApplication"属性
	 */
	private static final String TAG = "MyApplication";  
    public static int isOpen;   
    public boolean isFingerprint  = false; 
    public  boolean isPreview = false;
    public static MemInfoDao mDB;
    public static FingerPrintApi mFPApi;
    public boolean  isdepress = false;
    private static MyApplication sMe;
    private TextView nfcTextView;
    private Camera mCamera;
    private List<FpListData> times1;
    private static List<Activity> activityList = new LinkedList<Activity>();
    public boolean isPressStopbtn = false;
	@Override
	public void onCreate() {
		super.onCreate();
		mDB = new MemInfoDao(this);
		mFPApi = FingerPrintApi.getInstance();
	}
	
	public MyApplication() {
		super();
		// TODO Auto-generated constructor stub
		sMe = this;
	}
	public static MyApplication getInstance() {
		if(sMe==null){
			Log.v("App"," app null");

		}
		return sMe;
	}

	public boolean getisdepress(){
		return  isdepress;
	}
	
	public void setisdepress(boolean isdepress){
		this. isdepress = isdepress;
	}
	
	public List<FpListData> gettimes(){
		return  times1;
	}
	
	public void settime( List<FpListData> times){
		this. times1 = times;
	}
	
	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		super.onTerminate();
		Log.d(TAG, "onTerminate");
		
	}
	
	public void setCamera(Camera camera){
		this.mCamera= camera;
	}
	
	public Camera getCamera(){
		return mCamera;
	}
	public void setTextView(TextView textView){
		this.nfcTextView= textView;
	}
	
	public TextView getTextView(){
		return nfcTextView;
	}
  
	private WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();

	public WindowManager.LayoutParams getMywmParams() {
		return wmParams;
	}
	
	  
	public static void addActivity(Activity activity) {
		activityList.add(activity);
	}
	public static void exit() {
	//	mFPApi.CloseDev();
		System.exit(0);
	}
}
