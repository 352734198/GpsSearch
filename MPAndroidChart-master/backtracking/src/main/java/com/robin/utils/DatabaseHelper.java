package com.robin.utils;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper{
	/**
	 * ��ݿ�汾
	 */
	public static final int v = 15;
	private static DatabaseHelper databaseHelper;
	private final static Object lockObj = new Object();

	private DatabaseHelper(Context context) {
		super(context, "1card1.db", null, v);
	}

	/**
	 * ����ģʽ
	 * 
	 * @param context
	 * @return
	 */
	public static DatabaseHelper getInstanece(Context context) {
		if (databaseHelper == null) {
			synchronized (lockObj) {
				if (databaseHelper == null) {
					databaseHelper = new DatabaseHelper(context);
				}
			}
		}
		return databaseHelper;
	}

	private AtomicInteger mOpenCounter = new AtomicInteger();
	private SQLiteDatabase mDatabase;

	public synchronized SQLiteDatabase openDatabase() {
		if (mOpenCounter.incrementAndGet() == 1) {
			// Opening new database
			mDatabase = getWritableDatabase();
		}
		return mDatabase;
	}

	public synchronized void closeDatabase() {
		if (mOpenCounter.decrementAndGet() == 0) {
			// Closing database
			mDatabase.close();
		}
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql;
		sql = "CREATE TABLE IF NOT EXISTS AutoRecoder"
				+ "(Id INTEGER DEFAULT '1' NOT NULL PRIMARY KEY AUTOINCREMENT,"
				+ "time TEXT  NOT NULL,"
				+ "dbvalue INTEGER DEFAULT '0')";
		db.execSQL(sql);

	}

	/**
	 * @param tableName
	 * @param values
	 * @param whereClause
	 * @param whereArgs
	 */
	public void update(String tableName, ContentValues values,
			String whereClause, String[] whereArgs) {
		SQLiteDatabase db = openDatabase();
		db.update(tableName, values, whereClause, whereArgs);
		//db.close();
		closeDatabase();
	}
	
	

	/**
	 * @param tableName
	 * @param whereClause
	 * @param whereArgs
	 */
	public void delete(String tableName, String whereClause, String[] whereArgs) {
		SQLiteDatabase db = openDatabase();
		db.delete(tableName, whereClause, whereArgs);
		closeDatabase();
	}

	public void insert(String tableName, ContentValues values) {
		SQLiteDatabase db = openDatabase();
		db.insert(tableName, null, values);
		closeDatabase();
	}

	public void insert(String tableName, List<ContentValues> list) {
		SQLiteDatabase db = openDatabase();
		db.beginTransaction();
		if (list != null) {
			for (ContentValues values : list) {
				db.insert(tableName, null, values);
			}
		}
		db.setTransactionSuccessful();
		db.endTransaction();
		closeDatabase();
	}

	public Cursor query(String sql, String[] selectionArgs) {
		SQLiteDatabase db = openDatabase();
		if (db.isOpen()) {
			Cursor c = db.rawQuery(sql, selectionArgs);
			if (!c.isClosed() && c.getCount() > 0) {
				c.moveToFirst();
			}
			//db.close();
			closeDatabase();
			return c;
		}
		//db.close();
		closeDatabase();
		return null;
	}

	public Cursor query(String sql) {
		return query(sql, null);
	}

	public void execSQL(String sql) {
		SQLiteDatabase db = openDatabase();
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion <= v) {
			String sql = "drop table AutoRecoder";
			db.execSQL(sql);
			onCreate(db);
		}
	}


}
