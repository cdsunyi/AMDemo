package com.ismart.amdemo.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper{

	private static final String DB_NAME="finger.db";
	private static int DB_VERSION=1;
	private static final String CREATE_TBL="create table MemInfo(card text,name text,sex text,phone text,id text,fpno text)";
	
	public DBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub 
		db.execSQL(CREATE_TBL);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS "+CREATE_TBL);
		onCreate(db);
	}
}
