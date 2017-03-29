package com.ismart.amdemo.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ismart.amdemo.entity.MemberInfo;
import com.ismart.amdemo.entity.FpListData;
import com.ismart.amdemo.util.MyApplication;

public class MemInfoDao {

	private static final String TAG = "MemInfoDao";
	private DBHelper dbOpenHelper;
	private Context context;

	public MemInfoDao(Context context) {
		this.context = context;
		this.dbOpenHelper = new DBHelper(context);
	}

	/**
	 * 添加数据
	 * 
	 * @param info
	 * @return
	 */  
	public boolean insert(MemberInfo info) { 
		String sql = "insert into MemInfo(card ,name ,sex ,phone ,id ,fpno) values(?,?,?,?,?,?)";
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		try {
			db.execSQL(sql,new Object[] { info.getCard(), info.getName(),
							info.getSex(), info.getPhone(), info.getId(),
							info.getFpno() });
		} catch (Exception e2) {
			e2.printStackTrace();
			db.close();
			return false;
		}
		db.close();
		return true;
	}

	/**
	 * 查询
	 * 
	 * @param args
	 *            数据表字段
	 * @param values
	 *            值
	 * @return
	 */
	// fpno
	public MemberInfo query(String args, String values) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		String sql = "select * from  MemInfo where " + args + " =?";
		Cursor c = null;
		MemberInfo info = null;
		try {
			c = db.rawQuery(sql, new String[] { values });

			while (c.moveToNext()) {
				info = new MemberInfo();
				info.setCard(c.getString(0));
				info.setName(c.getString(1));
				info.setSex(c.getString(2));
				info.setPhone(c.getString(3));
				info.setId(c.getString(4));
				info.setFpno(c.getString(5));
			}
			c.close();
			db.close();
			return info;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		c.close();
		db.close();
		return null;
	}

	public void querydb() {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		Cursor cursor = null;
		cursor = db.query("MemInfo", new String[] { "fpno", "name" }, null,null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			MyApplication.getInstance().gettimes().add(new FpListData(cursor.getInt(cursor.getColumnIndex("fpno")) + "", cursor.getString(cursor.getColumnIndex("name"))));
			cursor.moveToNext();
		}

		cursor.close();
		db.close();
		return;
	}
  
	public void delectdb() {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		db.delete("MemInfo", null, null);
	}

	public void delectdbname(Object object) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		db.delete("MemInfo", "name = ? ", new String[] { (String) object });
	}
}
