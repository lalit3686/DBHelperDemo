package com.example.dbdemo;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity{

	private DBHelper dbHelper;
	private SQLiteDatabase db;
	private ListView listView;
	private EditText et_username, et_password;
	private ArrayList<POJO> list = new ArrayList<POJO>();
	private MyAdapter adapter;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        listView = (ListView) findViewById(R.id.listView);
        et_username = (EditText) findViewById(R.id.et_username);
        et_password = (EditText) findViewById(R.id.et_password);
        
        dbHelper = new DBHelper(this);
        dbHelper.createDatabaseFile();
        Log.d("DB_PATH", getDatabasePath(DBHelper.DB_NAME).getAbsolutePath());
        
        adapter = new MyAdapter();
        listView.setAdapter(adapter);
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	db = SQLiteDatabase.openDatabase(getDatabasePath(DBHelper.DB_NAME).getAbsolutePath(), null, SQLiteDatabase.OPEN_READWRITE);
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	db.close();
    }
    
    public void MyOnClick(View view) {
    	int id = view.getId();
    	switch (id) {
		case R.id.btn_insert:
			if(!TextUtils.isEmpty(et_username.getText().toString().trim()) && !TextUtils.isEmpty(et_password.getText().toString().trim()))
			dbHelper.insertIntoLogin(db, et_username.getText().toString().trim(), et_password.getText().toString().trim());
			break;
		case R.id.btn_update:
			if(!TextUtils.isEmpty(et_username.getText().toString().trim()) && !TextUtils.isEmpty(et_password.getText().toString().trim()))
			dbHelper.updateTblLogin(db, et_username.getText().toString().trim(), et_password.getText().toString().trim());
			break;
		case R.id.btn_delete:
			if(!TextUtils.isEmpty(et_username.getText().toString().trim()))
			dbHelper.deleteLoginByRowId(db, et_username.getText().toString().trim());
			break;
		case R.id.btn_read:
			readData();
			break;
		}
	}
    
    /**
     * 
     *Bulk Insertion of data in Table
     * 
     **/
	void UsingTransaction() {
		try {
			db.beginTransaction();
			for (int i = 0; i < 8000; i++) {
				dbHelper.insertIntoLogin(db, "new_username", "new_password");
			}
			db.setTransactionSuccessful();
		} 
		catch (Exception e) {
		}
		finally{
			db.endTransaction();
		}
	}
    
    /**
     *
     *Read data from Database and updates the List.
     *
     **/
    private void readData() {
    	
    	list.clear();
    	Cursor cursor = dbHelper.getLoginDetails(db);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
        	String _id = cursor.getString(cursor.getColumnIndex(DBHelper.TBL_COL_ID));
        	String username = cursor.getString(cursor.getColumnIndex(DBHelper.TBL_COL_UNAME));
        	String password = cursor.getString(cursor.getColumnIndex(DBHelper.TBL_COL_PASSWORD));
        	list.add(new POJO(username, password));
        	
        	Log.v(DBHelper.TBL_COL_UNAME+" & "+DBHelper.TBL_COL_PASSWORD, _id+" "+username+" - "+password);
        	cursor.moveToNext();
        }
        cursor.close();
        adapter.notifyDataSetChanged();
    }
    
    class MyAdapter extends BaseAdapter
    {
		public int getCount() {
			return list.size();
		}

		public Object getItem(int position) {
			return list.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.row, null);
			TextView username = (TextView) convertView.findViewById(R.id.row_username);
			TextView password = (TextView) convertView.findViewById(R.id.row_password);
			
			username.setText(list.get(position).getUsername());
			password.setText(list.get(position).getPassword());
			
			return convertView;
		}
    }
}
