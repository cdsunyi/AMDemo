package com.ismart.amdemo.util;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Handler;


/**
 * 常量
 * @author Magic
 *
 */
public class Constant {

	/**http://192.168.1.213/web/att     http://183.64.1.90:8000/att*/
//	public static  String HOST_URL="http://183.64.1.90:8000/att"; 
	/**http://183.64.1.90:8000     */
	public static  String PIC_URL="";
	/**上传业务代码标识*/
	public static final String BIZCODE="bizcode";
	public static final String args = "args";
	
	public static final String BIZCODE_z001="Z001"; //人员信息查询
	public static final String BIZCODE_z002="Z002"; //上传指纹信息
	/**物理卡号*/
	public static final String CRADNUMB="0";
	/**身份证号*/
	public static final String IDNUMB="1";
	
	
	/** 错误码（0代码成功非0都是请求失败） */
	public static final String ERRORCODE = "errorcode";
	/** 错误的具体信息 标识*/
	public static final String ERRORMSG = "errormsg";
	/**errormsg 对应的信息*/
	public static String MESSAGE="";
	public static final String MyToast= "toast"; // Handler处理消息时的关键字
	public static boolean fingerTypeIn=false;     //是否在录入手指
	
	/**handler处理的返回值*/
    public static final int MESSAGE_TOAST=0;
    public static final int REGISTER_RESULT=1;
    public static final int PROGRESS_MISS=2;
    public static final int Dev_Card=3;
    public static final int CARD_NUMBER=4;
    public static final int FINGER_NUMBER=5;
    public static final int GET_IMAGE=6;
    public static final int FINGER_VERSION=7;
    public static final int TAKE_PICTURE_TIME_UPDATA = 8;
    public static final String ACTION_USB_RFID = "com.android.example.RFID";
    public static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    public static String pictureUrl;  //保存图片下载地址
    public static String GUID="guid001";
    public static SharedPreferences sp;
    /**true 表示可以刷卡 false表示不能刷卡*/
    public static boolean CARD_ALLOW=true;
    /**是否在主界面  跟CARD_ALLOW 一起用*/
    public static boolean MAIN_CARD=true;
    
    public static List<Handler> actyList=new ArrayList<Handler>();
    
    /**
	 * 方便指纹服务在哪个界面显示toast
	 * 
	 * @param acty
	 */
	public static void setHandler(Handler handler) {
		//Constant.actyList.clear();
		Constant.actyList.add(handler);
	}
}
