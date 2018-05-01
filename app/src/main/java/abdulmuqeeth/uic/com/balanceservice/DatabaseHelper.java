package abdulmuqeeth.uic.com.balanceservice;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Parcel;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by Abdul Muqeeth Mohammed.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    final static String TABLE_NAME = "treasury";
    final static String YEAR ="year";
    final static String MONTH ="month";
    final static String DATE ="date";
    final static String DAY ="day";
    final static String OPENING_BAL ="openbal";
    final static String CLOSING_BAL ="closebal";

    final private static String CREATE_TABLE =
            "CREATE TABLE "+TABLE_NAME+"("+YEAR+","+MONTH+","+DATE+","+DAY+","+OPENING_BAL+","+CLOSING_BAL+")";

    final private static String NAME = "artist_db";
    final private static Integer VERSION = 1;
    final private Context mContext;

    public DatabaseHelper(Context context) {
        // Always call superclass's constructor
        super(context, NAME, null, VERSION);

        // Save the context that created DB in order to make calls on that context,
        // e.g., deleteDatabase() below.
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //
    }

    void deleteDatabase() {
        mContext.deleteDatabase(NAME);
    }

    public boolean loadData() {
        SQLiteDatabase db = this.getWritableDatabase();

        AssetManager assetManager = mContext.getAssets();

        InputStream inputStream = null;
        try {
            inputStream = assetManager.open("treasury_data.txt");
        } catch (IOException e) {
            Log.i(getClass().toString(), "CSV File Not Found");
            return false;
        }

        BufferedReader mBufferReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        db.beginTransaction();
        try {
            while((line = mBufferReader.readLine()) != null){

                String[] columns = line.split(",");
                if(columns.length != 6){
                    Log.i(getClass().toString(), "Skipped Bad Row");
                    continue;
                }
                // new contentvalue(3)
                ContentValues mContentValue = new ContentValues();
                mContentValue.put(YEAR, columns[0].trim());
                mContentValue.put(MONTH, columns[1].trim());
                mContentValue.put(DATE, columns[2].trim());
                mContentValue.put(DAY, columns[3].trim());
                mContentValue.put(OPENING_BAL, columns[4].trim());
                mContentValue.put(CLOSING_BAL, columns[5].trim());

                db.insert(TABLE_NAME, null, mContentValue);
            }
        } catch (IOException e) {
            return false;
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        return true;
    }

    public List<DailyCash> getData(int day, int month, int year, int range){
        List<DailyCash> retrievedData = null;
        String selectQuery = "Select * from "+TABLE_NAME;
        Parcel parcel;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()) {
            do {
                DailyCash dailyCash = new DailyCash(cursor.getInt(0), cursor.getInt(1), cursor.getInt(2),cursor.getString(3), cursor.getInt(5));
                retrievedData.add(dailyCash);
            } while (cursor.moveToNext());
        }
        return retrievedData;
    }
}
