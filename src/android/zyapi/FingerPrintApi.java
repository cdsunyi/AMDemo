package android.zyapi;

import android.util.Log;



public class FingerPrintApi {

	private static FingerPrintApi mMe = null;
	
	public static final int ERR_SUCCESS =                   0;   //成功
	public static final int ERR_FAIL =                      -1;  //通讯错误
	public static final int ERR_OVERTIME =                  -2;  //通讯超时
	public static final int ERR_UNKNOW =                    -10; //未知设备错误
	public static final int ERR_NO_DETECT_FINGER =          -11; //没有检测到指纹
	public static final int ERR_IMAGE_QUALITY_POOR =        -12; //图像质量太差
	public static final int ERR_ALL_TMPL_EMPTY =            -13; //未登记指纹模版
	public static final int ERR_EMPTY_FP_IMAGE =            -14; //未采集指纹图像
	public static final int ERR_EMPTY_ID_NOEXIST =          -15; //不存在未注册的模版编号
	public static final int ERR_INVALID_TMPL_NO =           -16; //指纹模版编号无效
	public static final int ERR_TMPL_NOT_EMPTY =            -17; //指定模版号中已登记其他指纹模版
	public static final int ERR_FP_DUPLICATION =            -18; //该指纹已登记
	public static final int ERR_GENERALIZE =                -19; //指纹模版生成失败
	public static final int ERR_IDENTIFY =                  -20; //指纹识别错误，未发现匹配的模版
	public static final int ERR_TMPL_EMPTY =                -21; //指定指纹模版中不存在指纹数据
	public static final int ERR_MEMORY =                    -22; //外部 Flash 烧写出错
	public static final int ERR_INVALID_PARAM =             -23; //无效参数
	
	public static final String FP_MODEL =                   "iFP001";
	
	public FingerPrintApi() {
		
	}
	
	public static FingerPrintApi getInstance(){
		if (mMe == null){
			mMe = new FingerPrintApi();
		}
		return mMe;
	}
	
	public native String GetLibVersion();
	public native int InitDev(String fpModel);
	public native int CloseDev();
	public native int GetFPImage();
	public native int GetEmptyFpID();
	public native int GenFPTemplate(int fpTempNo, int bufferNo);
	public native int EnrollFP(int fpTempNo);
	public native int GetEnrollFPCount(); 
	public native int IdentifyFP();
	public native int DeleteFP(int fpTempNo);
	public native int DeleteAllFP();
	public native int ReadFPData(int fpTempNo, byte data[]);
	public native int WriteFPData(int fpTempNo, byte data[], int dataLen);
	public native int WriteMultiFPData(int fpTempNo, int fpTempNum, byte data[][], int dataLen);
	public native int SetLed(boolean open);
	public native int SetDupCheck(boolean open);
	public native int SetSecurityLevel(int level);
	public native int GetSecurityLevel();
	
	static { 
		Log.d("FingerPrintApi","~~~loadLibrary"); 
		System.loadLibrary("FingerPrintApi");
	}
}
