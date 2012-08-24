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
	
	private void isDirectoryPresent(String db_path) {
		// create "databases" directory if not in existence in data/data/package_name/databases/
		File file = new File(db_path.substring(0, db_path.indexOf("/"+DB_NAME)));
		
		// check if databases folder exists or not.
		if(!file.isDirectory())
			file.mkdir();
	}
	
	public void createDatabaseFile(){
		
		// data/data/package_name/databases/db_name.db
		String db_path = mContext.getDatabasePath(DBHelper.DB_NAME).toString();
		isDirectoryPresent(db_path); 
		
		File file = new File(db_path);
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
	
	public void insertIntoLogin(SQLiteDatabase db, String username, String password) {
		db.execSQL("insert into "+TBL_LOGIN+" ("+TBL_COL_UNAME+","+TBL_COL_PASSWORD+")values('"+username+"','"+password+"')");
	}
	
	public void updateUsingUserName(SQLiteDatabase db, String username, String password) {
		db.execSQL("update "+TBL_LOGIN+" set "+TBL_COL_PASSWORD+"='"+password+"' where "+TBL_COL_UNAME+"=?", new String[]{username});
	}
	
	public void deleteByUserName(SQLiteDatabase db, String username) {
		db.execSQL("delete from "+TBL_LOGIN+" where "+TBL_COL_UNAME+"=?", new String[]{username});
	}
	
	public Cursor readAllLogin(SQLiteDatabase db) {
		return db.rawQuery("select * from "+TBL_LOGIN, null);
	}
	
	public Cursor readFromUserName(SQLiteDatabase db, String username) {
		return db.rawQuery("select * from "+TBL_LOGIN+" where "+TBL_COL_UNAME+"=?", new String[]{username});
	}
}
