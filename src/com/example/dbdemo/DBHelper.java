package com.example.dbdemo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper{

	private Context mContext;
	public static String TBL_LOGIN = "login";
	public static String TBL_COL_ID = "_id";
	public static String TBL_COL_UNAME = "username";
	public static String TBL_COL_PASSWORD = "password";
	public static String DB_NAME = "mydb.db";
	private static int DB_VERSION = 1;
	
	public DBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		mContext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}
	
	private void isDirectoryPresent() {
		// create directory if not in existence
		File file = new File("/data/data/" +mContext.getPackageName()+ "/databases");
		
		if(!file.isDirectory())
			file.mkdir();
	}
	
	public void createDatabaseFile(){
		
		isDirectoryPresent(); 
		File file = new File(mContext.getDatabasePath(DB_NAME).getAbsolutePath());
		Log.d(getClass().getSimpleName(), file.getAbsolutePath());
		
		if(file.exists()){
			Log.d(getClass().getSimpleName(), "File already exists");
		}
		else{
			copyDatabase(file);
		}
	}
	
	private void copyDatabase(File file) {
		try {
			file.createNewFile();   // create new file if it is not in existence
			InputStream is = mContext.getAssets().open(DB_NAME);
			OutputStream write = new FileOutputStream(file);
			
			byte[] buffer = new byte[1024];
			int length;
			while ((length = is.read(buffer)) > 0) {
				write.write(buffer, 0, length);
			}
			is.close();
			write.close();
			Log.d(getClass().getSimpleName(), "File does not exists & Newly created");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 *
	 * Always use db.execSQL for non-return queries like insert, update, delete
	 * 
	 **/
	public void insertIntoLogin(SQLiteDatabase db, String username, String password) {
		db.execSQL("insert into "+TBL_LOGIN+" (username, password) values('"+username+"','"+password+"')");
	}
	
	/**
	 * 
	 *Always user db.rawQuery for return queries like select
	 * 
	 **/
	public Cursor getLoginDetails(SQLiteDatabase db) {
		return db.rawQuery("select * from "+TBL_LOGIN, null);
	}
	
	public void updateTblLogin(SQLiteDatabase db, String username, String password) {
		db.execSQL("update "+TBL_LOGIN+" set password='"+password+"' where username=?", new String[]{username});
	}
	
	public void deleteLoginByRowId(SQLiteDatabase db, String username) {
		db.execSQL("delete from "+TBL_LOGIN+" where username=?", new String[]{username});
	}
}
