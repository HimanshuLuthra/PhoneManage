package com.phonemanager.service;

import java.util.ArrayList;
import java.util.Iterator;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;


public class DBManager extends SQLiteOpenHelper {
	private String TAG = getClass().getSimpleName();
	private static DBManager dbManager;
	private ArrayList<ActivityDataPair> expectedDataFormat;
	private String createTableQuery;

	private DBManager(Context context) {
		super(context, PMConstants.DB_NAME, null, PMConstants.DB_VERSION);
		// need to check this function
		getWritableDatabase();
		expectedDataFormat = new ArrayList<ActivityDataPair>();
		Log.d(TAG, "Constructor() call completed");
	}

	public static DBManager getInstance(Context context) {
		if (dbManager == null)
			dbManager = new DBManager(context);
		return dbManager;
	}


	public void write(final ArrayList<ActivityDataPair> dataSet) {
		expectedDataFormat.addAll(dataSet);
		SQLiteDatabase mSqliteObject = this.getWritableDatabase();
		Iterator<ActivityDataPair> dataIterator = expectedDataFormat.iterator();
		String INSERT_QUERY = "INSERT INTO " + PMConstants.DB_TABLE + " ("
				+ PMConstants.DB_COL_ACTIVITY_NAME + ","
				+ PMConstants.DB_COL_START_TIME_STAMP + "," + PMConstants.DB_COL_END_TIME_STAMP + ") VALUES (?,?,?);";

		final SQLiteStatement statement = mSqliteObject
				.compileStatement(INSERT_QUERY);

		mSqliteObject.beginTransaction();

		Log.d(TAG, "Starting record Transaction");

		try {
			while (dataIterator.hasNext()) {
				statement.clearBindings();
				ActivityDataPair currentPair = dataIterator.next();
				statement.bindString(1, currentPair.getActivityName());
				statement.bindLong(2, currentPair.getStartTime());
				statement.bindLong(3, currentPair.getEndTime());
				Log.d(TAG, "ActivityName = "+currentPair.getActivityName()+"Time = "+currentPair.getStartTime());
				statement.execute();
				Log.d(TAG, "Statement Execute complete");
			}
			mSqliteObject.setTransactionSuccessful();
		} finally {
			mSqliteObject.endTransaction();
			Log.d(TAG, "End of Record Transaction");
		}

		expectedDataFormat.clear();
	}
	//read by negi
	/*public Cursor read(long startTime) {

		String TIME_STRING = String.valueOf(startTime);
		SQLiteDatabase mSqliteObject = this.getReadableDatabase();
		Cursor mCursor = mSqliteObject.rawQuery("SELECT * FROM "
		+ PMConstants.DB_NAME + " WHERE " + PMConstants.DB_COL_TIME_STAMP
				+ ">?",
		new String[] { TIME_STRING });
		return mCursor;
	}
	 */

	public ArrayList<ActivityDataPair> read(Long time)
	{
		Long currentTime = time;
		String queryDB = "SELECT "+PMConstants.DB_COL_ACTIVITY_NAME +","+PMConstants.DB_COL_START_TIME_STAMP + "," + PMConstants.DB_COL_END_TIME_STAMP+" FROM "+ PMConstants.DB_TABLE + " WHERE " + PMConstants.DB_COL_START_TIME_STAMP
				+ ">" + currentTime ;

		return read(queryDB);

	}

	private ArrayList<ActivityDataPair> read(String query)
	{

		ArrayList<ActivityDataPair> temp = new ArrayList<ActivityDataPair>();
		Log.d(TAG,"DBmanager read() start");
	
		SQLiteDatabase mSqliteObject = this.getReadableDatabase();
		Log.d(TAG,"DBmanager read()");
		Cursor mCursor = mSqliteObject.rawQuery(query, null );

		Log.d(TAG,"DBmanager read() after cursor");
		ActivityDataPair obj =null;
		if(mCursor!=null)
		{
		if(mCursor.moveToFirst())
		{
			do
			{
				obj = new ActivityDataPair();
				obj.setActivityName(mCursor.getString(0));
				obj.setStartTime(mCursor.getLong(1));
				obj.setEndTime(mCursor.getLong(2));
				temp.add(obj);
			}while(mCursor.moveToNext());

		}
		}

		mCursor.close();

		//TODO add the cache data here
		return temp;

	}
	
	public void deletePackageEntry(String packageName)
	{
		ArrayList<ActivityDataPair> temp = new ArrayList<ActivityDataPair>();
		SQLiteDatabase mSqliteObject = this.getWritableDatabase();
		temp = read(0L);
		for(int i=0;i<temp.size();i++)
		{
			Log.d(TAG,"DUMMY PACKAGE NAME IS "+temp.get(i).getActivityName());
			if(temp.get(i).getActivityName().equalsIgnoreCase(packageName)){
				int size = mSqliteObject.delete(PMConstants.DB_TABLE, PMConstants.DB_COL_ACTIVITY_NAME+ " ='" + packageName + "'", null);
				Log.d(TAG,"deletePackageEntry packageName-> "+packageName+" no of entries removed -> "+size);
				break;
			}
		}
		
	}

	//to delete the db
	public void clear()
	{

		SQLiteDatabase mSqliteObject = this.getWritableDatabase();
		mSqliteObject.delete(PMConstants.DB_TABLE, null, null);

	}
	@Override
	public void onCreate(SQLiteDatabase mSqliteObject) {
		Log.d(TAG, "onCreate call placed");
		createTableQuery = "CREATE TABLE IF NOT EXISTS " + PMConstants.DB_TABLE
				+ "(" + PMConstants.DB_COL_PACKAGE_NAME + " TEXT," + PMConstants.DB_COL_ACTIVITY_NAME + " TEXT,"
				+ PMConstants.DB_COL_START_TIME_STAMP + " INTEGER,"+ PMConstants.DB_COL_END_TIME_STAMP + " INTEGER);";
		mSqliteObject.execSQL(createTableQuery);
		Log.d(TAG, "Table create call placed");
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
	}
}
