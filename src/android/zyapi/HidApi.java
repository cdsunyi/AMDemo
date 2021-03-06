package android.zyapi;

import com.ismart.interfaces.UidDateCallback;

import android.util.Log;


public class HidApi {
	private static HidApi mMe = null;
	
	public static final String DEV_MODEL =                   "iHID01";
	private  UidDateCallback callback; 
	private HidApi() {
	}
	
	public void setUidDateCallback(UidDateCallback callback){
        this.callback = callback;
    } 
	
	public static HidApi getInstance(){
		if (mMe == null){
			mMe = new HidApi();
		}
		return mMe;
	}
	
	private String bytesToHexString(byte[] src) {  
        StringBuilder stringBuilder = new StringBuilder("0x");  
        if (src == null || src.length <= 0) {  
            return null;  
        }    
        
        char[] buffer = new char[2];      
        for (int i = 0; i < src.length; i++) {  
            buffer[0] = Character.forDigit((src[i] >>> 4) & 0x0F, 16);  
            buffer[1] = Character.forDigit(src[i] & 0x0F, 16);  
            //System.out.println(buffer);  
            stringBuilder.append(buffer);  
        }  
        return stringBuilder.toString();  
    }
	
	private void UIDCallback(byte[] uid, int len){
		Log.d("HidApi", "uid:"+bytesToHexString(uid));
		Log.d("HidApi", "uid:"+callback);
            callback.sendUidDate(bytesToHexString(uid));          
	}   
	  
	public native String GetLibVersion();
	public native int InitDev(String devModel);
	public native int CloseDev();   
	
	static {  
		System.loadLibrary("zyapi_Hid");  
	}
}
