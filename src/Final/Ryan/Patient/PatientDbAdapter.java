package Final.Ryan.Patient;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class PatientDbAdapter {
	
	public static final String KEY_ROWID = "_id";
	public static final String KEY_PICTURE = "picture";
	public static final String KEY_NAME = "name";
	public static final String KEY_LATITUDE = "latitude";
	public static final String KEY_LONGTITUDE = "longtitude";
	

	public static final int KEY_COL_ROWID = 0;
	public static final int KEY_COL_PICTURE = 1;
	public static final int KEY_COL_NAME = 2;
	public static final int KEY_COL_LATITUDE = 3;
	public static final int KEY_COL_LONGTITUDE = 4;
    

	private static final String TAG = "PatientDbAdapter";
	private DatabaseHelper patientDbHelper;
	private SQLiteDatabase patientDb;

	private static final String DATABASE_CREATE_SEE = 
		"create table ToBeSee(_id integer primary key autoincrement, "
			+ "picture text, name text, latitude text, longtitude text);";
	private static final String DATABASE_CREATE_HISTORY = 
			"create table History(_id integer primary key autoincrement, "
				+ "picture text, name text, latitude text, longtitude text);";

	private static final String DATABASE_NAME = "patients.db";
	private static final String DATABASE_TABLE_SEE = "ToBeSee";
	private static final String DATABASE_TABLE_HISTORY = "History";
	private static final int DATABASE_VERSION = 2;

	private final Context patientContext;
	
	public PatientDbAdapter(Context ctx) {
        this.patientContext = ctx;
    }
	
	public PatientDbAdapter open() throws SQLException {
        patientDbHelper = new DatabaseHelper(patientContext);
        patientDb = patientDbHelper.getWritableDatabase();
        return this;
    }
    
    public void close() {
        patientDbHelper.close();
    }
    
    public Cursor fetchAllItems(String database_table) {

        return patientDb.query(database_table, new String[] {KEY_ROWID, KEY_PICTURE, KEY_NAME, KEY_LATITUDE,
                KEY_LONGTITUDE}, null, null, null, null, null);
    }
    
    public Cursor fetchItem(long rowId, String database_table) throws SQLException {

        Cursor mCursor =
                patientDb.query(true, database_table, new String[] {KEY_ROWID,
                        KEY_PICTURE, KEY_NAME, KEY_LATITUDE, KEY_LONGTITUDE}, KEY_ROWID + "=" + rowId, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }
    
    
    public long createItem(String database_table, String picture, String name, String latitude, String longtitude) {
        ContentValues initialValues = new ContentValues();
        
        initialValues.put(KEY_PICTURE, picture);
        initialValues.put(KEY_NAME, name);
        initialValues.put(KEY_LATITUDE, latitude);
        initialValues.put(KEY_LONGTITUDE, longtitude);
        
        return patientDb.insert(database_table, null, initialValues);
    }
    
    public boolean deleteAll(String database_table) {
    	patientDb.delete(database_table, null, null); // use the delete(String, String, String) to delete all the data.
    	return true;
    }
    
    
    public boolean deleteItem(String database_table, long rowId) {
    	String[] whereArgs = {""+rowId};
        return patientDb.delete(database_table, KEY_ROWID+ "=?", whereArgs) > 0;
    }
    
    
	public boolean isEmpty(String database_table){ // to determine that whether the patient is empty
		Cursor myCursor = patientDb.query(database_table, new String[] {KEY_ROWID, KEY_PICTURE, KEY_NAME, KEY_LATITUDE,
                KEY_LONGTITUDE}, null, null, null, null, null) ;
		
		if(myCursor.getCount() < 1){ // if there is no data then return true.
			return true;
		}
		return false;
	}
	
	public int getCount(String database_table){ // to determine that whether the patient is empty
		Cursor myCursor = patientDb.query(database_table, new String[] {KEY_ROWID, KEY_PICTURE, KEY_NAME, KEY_LATITUDE,
                KEY_LONGTITUDE}, null, null, null, null, null) ;
		
		return myCursor.getCount(); // return the count of items.
	}
	
	
	
	private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE_SEE);
            db.execSQL(DATABASE_CREATE_HISTORY);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_SEE);
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_HISTORY);
            onCreate(db);
        }
    }
	
}
