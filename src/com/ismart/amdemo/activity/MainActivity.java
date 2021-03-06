package com.ismart.amdemo.activity;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;  
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.nfc.NfcAdapter;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.Images.ImageColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.zyapi.FingerPrintApi;
import android.zyapi.HidApi;

import com.ismart.amdemo.R; 
import com.ismart.amdemo.camera.CameraInit;
import com.ismart.amdemo.entity.MemberInfo;  
import com.ismart.amdemo.entity.FpListData;
import com.ismart.amdemo.fingerprint.InitFp;
import com.ismart.amdemo.fingerprint.RegisterFpService;
import com.ismart.amdemo.nfc.NfcIdentify;
import com.ismart.amdemo.util.Constant;
import com.ismart.amdemo.util.MyApplication;
import com.ismart.amdemo.util.SoundPlayer;
import com.ismart.interfaces.UidDateCallback;

@SuppressLint("NewApi")
public class MainActivity extends Activity implements SurfaceHolder.Callback,OnClickListener {
	private static String TAG = "MainActivity";
	private Button regiBtn, btnDeleteAllFP;
	private EditText etName;
	private TextView tvVer, tvNfc;
	private Intent mServerintent;                                                                                     
	public boolean bStopRead = false;

	private SoundPlayer player;
	private NfcAdapter nfcAdapter = null;
	private PendingIntent mPendingIntent;
	private IntentFilter[] mFilters;
	private String[][] mTechLists;
	private byte mFpData[][];

	private NfcIdentify mnfcIdentify;
	private InitFp minitFp;

	private SurfaceView mPreviewSV = null;
	private SurfaceHolder mySurfaceHolder;
	private Button mPhotoImgBtn = null;
	private Camera myCamera;
	private Bitmap mBitmap = null;
	private ImageView mSetpicview;
	private Bitmap rotaBitmap;
	private LayoutInflater inflater;
	private TextView Taketimesview;
	private int Taketimes = 0;

	private long TakePicTime = 60;
	private Dialog TakePictimeDialog;
	private AlertDialog TakePicFailDialog;

	public static final int TAKE_PICTURE_TIME_UPDATA = 8;
	public static final int UID_DATA= 9;
	private Spinner timeSpinner;
	private ArrayAdapter<String> adapter;
	private View timelayout;
	private long temp = 60;
	private int selectwhich = 5;
	private int preselectwhich = 5;
	private Handler mSendHandler;
	private Message timemsg;

	private String[] mTimes = { "10秒", "20秒", "30秒", "40秒", "50秒", "1分钟",
			"2分钟", "3分钟", "4分钟", "5分钟" };
	/**
	 * 线程是否开启
	 */
	private boolean isonoff = false;
	/**
	 * 是否循环执行拍照
	 */
	private boolean iswork = false;
	private CameraInit mCameraInit;

	private View deleteFplayout;
	private ListView deleteFpListView;
	private SimpleAdapter mSimpleAdapter;
	private List<FpListData> delectFpList;
	private List<HashMap<String, Object>> deleteFphashMap;  
	private HashMap<String, Object> deleteFpmap;
	private Button mPreviewBtn;
	private HidApi mHidApi;
	private UidDateCallback callback;

	private Handler myHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {  
			case Constant.MESSAGE_TOAST:
				Toast.makeText(getApplicationContext(),msg.getData().getString("toast"), Toast.LENGTH_SHORT).show();
				break; 
			case Constant.REGISTER_RESULT:    
				if (msg.arg1 == 1) { 
					String name = etName.getText().toString();
					String finger = (String) msg.obj + "";
					MemberInfo info = new MemberInfo("", name, "", "", "",finger);
					MyApplication.mDB.insert(info);
					etName.setText("");
				}
				break;  
			case Constant.FINGER_NUMBER:
				if (msg.arg1 == 0) {
					player.playSound(SoundPlayer.ZHIWENCUOWU);
					switch (msg.arg2) {
					case FingerPrintApi.ERR_ALL_TMPL_EMPTY:
						Toast.makeText(getApplicationContext(), "未注册过指纹数据！",Toast.LENGTH_SHORT).show();
						break;
					case FingerPrintApi.ERR_IDENTIFY:
						break;
					default: 
						break;  
					}
				} else {
					player.playSound(SoundPlayer.NIHAO);
					MemberInfo info = MyApplication.mDB.query("fpno", msg.arg2+ "");
					if (info != null) {
						Toast.makeText(getApplicationContext(),info.getName() + ", 你好！", Toast.LENGTH_LONG).show();
					}
				}
				break;
			case Constant.GET_IMAGE:
				if (msg.arg1 == 0) {  
					player.playSound(SoundPlayer.CHONGANSHOUZHI);
				} else {
					player.playSound(SoundPlayer.KEY);
				}
				break;
			case Constant.FINGER_VERSION:
				break;    
			}
		}
	};

	private Handler mysetHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {  
			case TAKE_PICTURE_TIME_UPDATA:
				Taketimesview.setText(Taketimes + "");
				break;
			case UID_DATA:  
				player.playSound(11);
				String str = String.valueOf(msg.obj);   
				str = str.substring(2,str.length());
				MyApplication.getInstance().getTextView().setText("ID卡: " +str);  
				break;
			}
		}    
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		setContentView(R.layout.activity_main);
		Log.d(TAG, "MainActivity ->onCreate");

		initViews();
		setListener();

		tvVer.setText("接口版本：" + MyApplication.mFPApi.GetLibVersion());
		MyApplication.getInstance().setTextView(tvNfc);

		MyApplication.addActivity(this);

		if (nfcAdapter == null) {  
			tvNfc.setText("设备不支持NFC！");    
			return;
		}
		if (!nfcAdapter.isEnabled()) {     
			tvNfc.setText("请在系统设置中先启用NFC功能！");  
			return;
		} 
		tvNfc.setText("请刷卡...");
	}

	/**  
	 * 获取指纹信息
	 */
	private void getFpdata() {
		delectFpList.clear();
		deleteFphashMap.clear();
		MyApplication.mDB.querydb();

		for (int i = 0; i < delectFpList.size(); ++i) {    
			deleteFpmap = new HashMap<String, Object>();
			deleteFpmap.put("fpno", delectFpList.get(i).getFpno());
			deleteFpmap.put("name", delectFpList.get(i).getName());
			Log.d("MainActivity", delectFpList.size()+ delectFpList.get(i).getFpno()+ delectFpList.get(i).getName());
			deleteFphashMap.add(deleteFpmap);
		}  

	}     

	private void initViews() {
		tvVer = (TextView) findViewById(R.id.tvVersion);   
		regiBtn = (Button) findViewById(R.id.btn_register);
		btnDeleteAllFP = (Button) findViewById(R.id.btn_deleteallfp);  
		etName = (EditText) findViewById(R.id.et_name);
		tvNfc = (TextView) findViewById(R.id.tv_nfc);

		mFpData = new byte[2][498]; 
		//初始化指纹头线程
		  minitFp = new InitFp(this);    
	      minitFp.start();  
   
		player = new SoundPlayer(this);    
		Constant.sp = getSharedPreferences("config", Context.MODE_PRIVATE);

   
		mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

		IntentFilter tech = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
		mFilters = new IntentFilter[] { tech };
		mTechLists = new String[][] {new String[] { MifareClassic.class.getName() },new String[] { IsoDep.class.getName() } };
		// 获取默认的NFC控制器
		nfcAdapter = NfcAdapter.getDefaultAdapter(this);  


		mPreviewSV = (SurfaceView) findViewById(R.id.previewSV);
		mySurfaceHolder = mPreviewSV.getHolder();
		mySurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mySurfaceHolder.addCallback(this);
		

		Taketimesview = (TextView) findViewById(R.id.taketimes);
		mPhotoImgBtn = (Button) findViewById(R.id.photoBtn);
		mSetpicview = (ImageView) findViewById(R.id.open_picIcon);
		mSetpicview.setVisibility(View.INVISIBLE);
		mPreviewBtn = (Button) findViewById(R.id.preview);
		inflater = getLayoutInflater();
		mSendHandler = new Handler();

		adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, mTimes);

		delectFpList = new LinkedList<FpListData>(); 
		MyApplication.getInstance().settime(delectFpList);
		deleteFphashMap = new LinkedList<HashMap<String, Object>>();

		mSimpleAdapter = new SimpleAdapter(MainActivity.this, deleteFphashMap,R.layout.item_layout, new String[] { "fpno", "name" },new int[] { R.id.fpno_textView, R.id.name_textView });

		mCameraInit = new CameraInit(this);
		mnfcIdentify = new NfcIdentify(this);    

		mHidApi = HidApi.getInstance();
		mHidApi.InitDev(HidApi.DEV_MODEL);
  
		mHidApi.setUidDateCallback(new UidDateCallback() {

			@Override  
			public void sendUidDate(String uid) {   
				// TODO Auto-generated method stub
				Message msg = new Message();
				msg.what = UID_DATA;
				msg.obj = uid;
				mysetHandler.sendMessage(msg);
			} 
		});      
	}

	private void setListener() {
		regiBtn.setOnClickListener(BtnListener);

		btnDeleteAllFP.setOnClickListener(BtnListener);
		mPreviewBtn.setOnClickListener(this);
		mSetpicview.setOnClickListener(this);
		mPhotoImgBtn.setOnClickListener(new PhotoOnClickListener());

	}

	/**
	 * 拍照线程
	 */
	Runnable runnable = new Runnable() {  

		@Override  
		public void run() {
			// TODO Auto-generated method stub
			mSetpicview.setVisibility(View.VISIBLE);
			myCamera.takePicture(myShutterCallback, null, myJpegCallback);
			Taketimes++;
			timemsg = new Message();
			timemsg.what = TAKE_PICTURE_TIME_UPDATA; 
			mysetHandler.sendMessage(timemsg);
		}
	};

	public void surfaceChanged(SurfaceHolder holder, int format, int width,int height)
	{
		// TODO Auto-generated method stub

		Log.d(TAG, "MainActivity ->surfaceChanged");
		if(mCameraInit == null){
			mCameraInit = new CameraInit(this);
			
		}
		mCameraInit.initCamera();//初始化摄像头
	}

	public void surfaceCreated(SurfaceHolder holder) 
	{
		// TODO Auto-generated method stub   
		Log.d(TAG, "MainActivity ->surfaceCreated");
  
		try {     
			//0是前置记住   
			myCamera = Camera.open(0);// attempt to get a Camera instance   
		} 
		catch (Exception e){   
			Toast.makeText(MainActivity.this, "摄像头打开失败", Toast.LENGTH_SHORT).show();  
		} 
		MyApplication.getInstance().setCamera(myCamera);
		if (myCamera == null) {
			Toast.makeText(MainActivity.this, "摄像头打开失败", Toast.LENGTH_SHORT).show();
		} else {  
			try {  
				myCamera.setPreviewDisplay(mySurfaceHolder);

			} catch (IOException e) { 
				// TODO Auto-generated catch block
				if (null != myCamera) {
					myCamera.release();
					myCamera = null;
				}
				e.printStackTrace();
			}
		}   

	}  

	public void surfaceDestroyed(SurfaceHolder holder)
	{
		// TODO Auto-generated method stub
		Log.d(TAG, "SurfaceHolder.Callback：Surface Destroyed");
		if (null != myCamera) {
			myCamera.setPreviewCallback(null); // 在启动PreviewCallback时这个必须在前不然退出出错

			myCamera.stopPreview();
			MyApplication.getInstance().isPreview = false;
			myCamera.release();
			myCamera = null;
		}

	}  

	/**
	 * 摄像头拍照咔擦的声音
	 */
	ShutterCallback myShutterCallback = new ShutterCallback() {

		public void onShutter() {
			// TODO Auto-generated method stub
		}
	};


	PictureCallback myJpegCallback = new PictureCallback() {

		public void onPictureTaken(byte[] data, Camera camera) {
			// TODO Auto-generated method stub
			// Log.i("MainActivity", "myJpegCallback:onPictureTaken...");
			if (null != data) {
				mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
				//myCamera.stopPreview();
				MyApplication.getInstance().isPreview = false;

				rotaBitmap = Bitmap.createBitmap(mBitmap, 0, 0,1024, 768, null, false);
				Log.d(TAG,mBitmap.getWidth()+""+mBitmap.getHeight());
				// 保存图片到储存卡
				if (null != rotaBitmap) { 
					mCameraInit.saveJpeg(rotaBitmap);
					mSetpicview.setImageBitmap(rotaBitmap);  
				//	mSetpicview.setImageBitmap(reverseBitmap(rotaBitmap, 0));
				}

				//再次进入预览
				myCamera.startPreview();
				MyApplication.getInstance().isPreview = true;
				mSendHandler.removeCallbacks(runnable);

				mSendHandler.postDelayed(runnable, TakePicTime * 1000);

				if (isonoff == false) {
					mSendHandler.removeCallbacks(runnable);
				}
			} else if (null == rotaBitmap) {

				myCamera.stopPreview();
				MyApplication.getInstance().isPreview = false;
				mSendHandler.removeCallbacks(runnable);
  
				TakePicFailDialog = new AlertDialog.Builder(MainActivity.this)
				.setTitle("拍照失败！")
				.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,int which) {
						iswork = false;
						isonoff = false;
						mPhotoImgBtn.setText("开启自动拍照");
						myCamera.startPreview();
						MyApplication.getInstance().isPreview = true;
					}
				}).setCancelable(false).create();
				TakePicFailDialog.show();
			}
		}
	};

	/**
	 *  拍照按键的监听
	 */
	public class PhotoOnClickListener implements OnClickListener {

		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (MyApplication.getInstance().isPreview && myCamera != null) {
				if (isonoff == false) {

					timelayout = inflater.inflate(R.layout.timedialog, null);
					timeSpinner = (Spinner) timelayout.findViewById(R.id.timespinner);

					timeSpinner.setAdapter(adapter);
					timeSpinner.setSelection(selectwhich, true);

					TakePictimeDialog = new AlertDialog.Builder(MainActivity.this)
					.setView(timelayout)
					.setTitle("请选择拍照间隔时间")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,int which) {
							// TODO Auto-generated method stub
							TakePicTime = temp;
							isonoff = true;
							iswork = true;
							mPhotoImgBtn.setText("关闭自动拍照");
							Taketimes = 0;
							Taketimesview.setText(Taketimes+ "");
							preselectwhich = selectwhich;

							mSendHandler.postDelayed(runnable,TakePicTime * 1000);
						}
					})
					.setNegativeButton("取消",new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,int which) {
							// TODO Auto-generated method stub
							selectwhich = preselectwhich;
						}
					}).setCancelable(false).create();
					TakePictimeDialog.show();

					timeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
						@Override
						public void onItemSelected(AdapterView<?> arg0,View arg1, int arg2, long arg3) {
							// TODO Auto-generated method stub
							switch (arg2) {
							case 0:
								temp = 10;
								break;
							case 1:
								temp = 20;
								break;
							case 2:
								temp = 30;
								break;
							case 3:
								temp = 40;
								break;
							case 4:
								temp = 50;
								break;
							case 5:
								temp = 60;
								break;
							case 6:
								temp = 120;
								break;
							case 7:
								temp = 180;
								break;
							case 8:
								temp = 240;
								break;
							case 9:
								temp = 300;
								break;
							default:
								break;
							}
							selectwhich = arg2;
						}
						@Override
						public void onNothingSelected(
								AdapterView<?> parent) {
							// TODO Auto-generated method stub
						}
					});
				} else if (isonoff == true) {
					mSendHandler.removeCallbacks(runnable);
					isonoff = false;
					iswork = false;
					mPhotoImgBtn.setText("开启自动拍照");
				}
			}else if(MyApplication.getInstance().isPreview == false || myCamera == null){
				Toast.makeText(MainActivity.this, "请先开启预览", Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		Log.d("TAG", "MainActivity ->onStart");
		if (iswork == true) {
			mSendHandler.postDelayed(runnable, TakePicTime * 1000);
		}
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub  
		Log.d("TAG", "MainActivity ->onStop");
		if (iswork == true) {
			mSendHandler.removeCallbacks(runnable);
		}
		super.onStop();
	}


	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		Log.d(TAG, "MainActivity ->onResume");
		super.onResume();
		Constant.setHandler(myHandler);
		mServerintent = new Intent(MainActivity.this, RegisterFpService.class);
		startService(mServerintent);
		if (nfcAdapter != null) {
			nfcAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters,mTechLists);
		}
	}

	@Override  
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.d(TAG, "MainActivity ->onPause");
		if (nfcAdapter != null) {
			nfcAdapter.disableForegroundDispatch(this);
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		Log.d(TAG, "MainActivity ->onNewIntent");
		mnfcIdentify.processNfcIntent(intent);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "MainActivity ->onDestroy");
		MyApplication.getInstance().isFingerprint = false;
		if (nfcAdapter != null) {
		}
		MyApplication.mFPApi.CloseDev(); 
		mHidApi.CloseDev();
		MyApplication.exit();
		stopService(mServerintent);
	}
 
  

	private View.OnClickListener BtnListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (!MyApplication.getInstance().isFingerprint) {
				Toast.makeText(MainActivity.this, "未检测到指纹模块！",Toast.LENGTH_SHORT).show();
				return;  
			}
			switch (v.getId()) {
			case R.id.btn_register: // 注册页面
				String name = etName.getText().toString();
				if (name == null || name.equals("")) {
					Toast.makeText(MainActivity.this, "请先输入姓名",Toast.LENGTH_SHORT).show();
					break;
				}
				Constant.fingerTypeIn = true;

				RegisterFpService.isService = false;
				Log.d(TAG, "MainActivity ->startregisteronce");
				sendRegisterBroadcast();

				break;

			case R.id.btn_deleteallfp:  

				new AlertDialog.Builder(MainActivity.this).setTitle(R.string.string_alert_title).setItems(R.array.items_dialog,
						new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,int which) {
						// 取出响应字符串资源
						if (which == 0) {											
							deleteallFpDia(which);
						} else if (which == 1) {
							deleteFpDia(which);
						}

					}
				}).show();
				break;
			}   
		}
	};

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.open_picIcon://查看图片
			if (iswork == true) {
				mSendHandler.removeCallbacks(runnable);
				mCameraInit.enterAlbum();
			} else if (iswork == false) {
				mCameraInit.enterAlbum();
			}
			break;
		case R.id.preview:
			if(MyApplication.getInstance().isPreview == false){
				if(myCamera == null){
					myCamera =Camera.open(0); //前置
				}
				MyApplication.getInstance().setCamera(myCamera);
				mCameraInit = new CameraInit(this);
				mCameraInit.initCamera();	
				try {
					myCamera.setPreviewDisplay(mySurfaceHolder);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				myCamera.startPreview();  
				MyApplication.getInstance().isPreview  = true;   
				mPreviewBtn.setText("停止预览"+"");  
				MyApplication.getInstance().isPressStopbtn = false; 
			}else if(MyApplication.getInstance().isPreview  = true){
				if(iswork == true){   
					Toast.makeText(MainActivity.this, "正在拍照,请勿停止预览", Toast.LENGTH_SHORT).show();
				}else{  
					MyApplication.getInstance().isPressStopbtn = true;
					myCamera.stopPreview();        
					if(myCamera!= null){
						myCamera.release();
						myCamera = null;
						mCameraInit = null;  
					}
					MyApplication.getInstance().isPreview  = false;
					mPreviewBtn.setText("开始预览"+"");
				}	
			}			
			break;
		default:
			break;
		}
	}

	private void deleteFpDia(int which){

		deleteFplayout = inflater.inflate(R.layout.deletelistview,null);
		deleteFpListView = (ListView) deleteFplayout.findViewById(R.id.delete_listView);
		getFpdata();  
		deleteFpListView.setAdapter(mSimpleAdapter);
		deleteFpListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent,View view,int position,long id) {
				// TODO
				// Auto-generated  
				// method stub
				Map<String, Object> map = (Map<String, Object>) deleteFpListView.getItemAtPosition(position);
				//删除对应的指纹
				if (0 == MyApplication.mFPApi.DeleteFP(position + 1)) {
					MyApplication.mDB.delectdbname(map.get("name"));
					Toast.makeText(MainActivity.this,"删除指纹成功",Toast.LENGTH_SHORT).show();
				}
				TakePictimeDialog.cancel();
			}
		});
		TakePictimeDialog = new AlertDialog.Builder(MainActivity.this).setView(deleteFplayout).setTitle("请选择要删除的指纹").create();
		TakePictimeDialog.show();
	}

	private void deleteallFpDia(int which){

		CharSequence string_body = MainActivity.this.getResources().getString(R.string.string_alert_body);
		final String[] colors = MainActivity.this.getResources().getStringArray(R.array.items_dialog);

		new AlertDialog.Builder(MainActivity.this).setMessage(string_body+ colors[which]+ "?")

		.setNeutralButton(R.string.string_alert_ok,new DialogInterface.OnClickListener() // 确认操作
		{
			@Override
			public void onClick(DialogInterface dialog,int which) {
				//删除全部指纹
				if (MyApplication.mFPApi.DeleteAllFP() == FingerPrintApi.ERR_SUCCESS) {
					MyApplication.mDB.delectdb();Toast.makeText(MainActivity.this,"删除全部指纹成功",Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(MainActivity.this,"删除全部指纹失败！",Toast.LENGTH_SHORT).show();
				}
			}
		})
		.setNegativeButton(R.string.string_alert_cancel,new DialogInterface.OnClickListener() // 取消操作
		{
			@Override
			public void onClick(DialogInterface dialog,int which) {
				dialog.dismiss();
			}
		}).show();
	}  

	 /** 
     * 图片反转 
     *  
     * @param bm 
     * @param flag 
     *            0为水平反转，1为垂直反转 
     * @return 
     */  
    public static Bitmap reverseBitmap(Bitmap bmp, int flag) {  
        float[] floats = null;  
        switch (flag) {  
        case 0: // 水平反转  
            floats = new float[] { -1f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f };  
            break;  
        case 1: // 垂直反转  
            floats = new float[] { 1f, 0f, 0f, 0f, -1f, 0f, 0f, 0f, 1f };  
            break;  
        }  
  
        if (floats != null) {  
            Matrix matrix = new Matrix();   
            matrix.setValues(floats);  
            return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);  
        }  
  
        return null;  
    }  

	/**
	 * 注册指纹广播
	 */
	private void sendRegisterBroadcast() {
		// TODO Auto-generated method stub
		Intent registerfpIntent = new Intent();
		registerfpIntent.setAction(RegisterFpService.REGISTER_FP);
		sendBroadcast(registerfpIntent);
	}



}  
