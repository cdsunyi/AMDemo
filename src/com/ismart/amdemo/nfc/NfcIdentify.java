package com.ismart.amdemo.nfc;

import com.ismart.amdemo.util.MyApplication;
import com.ismart.amdemo.util.SoundPlayer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.util.Log;

@SuppressLint("NewApi")
public class NfcIdentify {
	private final static String TAG = "NfcIdentify";
	private Context mcontext;
	private final byte AUTH_KEY[] = new byte[]{(byte)0x96, (byte)0xd0, (byte)0x02, (byte)0x88, (byte)0x78, (byte)0xd5};
    private final int KEY_SECTOR = 1;
	
    public  NfcIdentify(Context context){
    	this.mcontext = context;
    }
	/** 
     * Parses the NDEF Message from the intent and prints to the TextView 
     */  
    public void processNfcIntent(Intent intent) {  
    	if (!intent.getAction().equals(NfcAdapter.ACTION_TECH_DISCOVERED)) return;

        Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);  
        for (String tech : tagFromIntent.getTechList()) {  
            Log.d(TAG, tech);  
        }  
        StringBuffer data = new StringBuffer(); 
        data.append("NFC读卡数据：\n\n");
        data.append("ID：");
        data.append(bytesToHexString(tagFromIntent.getId()) );
        data.append("\nType：");
        
        IsoDep iso = IsoDep.get(tagFromIntent);
        if (iso != null)
        {
        	Log.d(TAG, "this is isodep card.");
        	data.append("IsoDep");
        	MyApplication.getInstance().getTextView().setText(data.toString());
        } 
        MifareClassic mifare = MifareClassic.get(tagFromIntent);  
        if (mifare != null)
        {
        	//Log.d(TAG, "this is mifare card.");
        	try {  
                //Enable I/O operations to the tag from this TagTechnology object.  
                mifare.connect();  
                int type = mifare.getType();//获取TAG的类型 
                String sType = "Mifare Classic";
                switch (type) {  
                case MifareClassic.TYPE_CLASSIC:  
                	sType = "Mifare Classic";  
                    break;   
                case MifareClassic.TYPE_PLUS:  
                	sType = "Mifare Classic PLUS";  
                    break;    
                case MifareClassic.TYPE_PRO:  
                	sType = "Mifare Classic PRO";    
                    break;  
                case MifareClassic.TYPE_UNKNOWN:  
                	sType = "Mifare Classic UNKNOWN";    
                    break;  
                }    
                data.append(sType);
                /*data.append("\nSectors：");
                data.append(mifare.getSectorCount());
                data.append("\nBlocks：");
                data.append(mifare.getBlockCount());
                data.append("\nSize：");   
                data.append(mifare.getSize());
                data.append("bytes\n");*/
                    
              // boolean auth = mifare.authenticateSectorWithKeyA(KEY_SECTOR, AUTH_KEY);
             //  if (auth){ 
            	   //int blockIndex = mifare.sectorToBlock(KEY_SECTOR);
            	//   byte checkData[] = mifare.readBlock(4);     
            	   /*Log.d(TAG,""+Integer.toHexString(checkData[0])+" "+Integer.toHexString(checkData[1])+" "
            			   +Integer.toHexString(checkData[2])+" "+Integer.toHexString(checkData[3])+" "
            			   +Integer.toHexString(checkData[4])+" "+Integer.toHexString(checkData[5])+" "
            			   +Integer.toHexString(checkData[6])+" ");*/
            	  /* if (checkData[0] == 0x06){
            		   String flag = new String(checkData, 1, 6);    
            		   if (flag.equalsIgnoreCase("ISMART")){   
            			   data.append("\n认证成功");
            		   }
            		   else{
            			   Log.d(TAG,""+flag);
            			   data.append("\n认证失败");
            		   }
            	   }else{
            		   data.append("\n认证失败");
            	   }
               }else{
            	   data.append("\n认证密钥错误");
               }*/
                
            } catch (Exception e) {  
                e.printStackTrace();  
            } 
        }
      
        MyApplication.getInstance().getTextView().setText(data.toString());
    }  
    
  //字符序列转换为16进制字符串
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
} 
