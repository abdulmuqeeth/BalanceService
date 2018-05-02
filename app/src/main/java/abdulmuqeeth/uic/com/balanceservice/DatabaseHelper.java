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
import java.util.ArrayList;
import java.util.List;

import abdulmuqeeth.uic.com.balancecommon.DailyCash;

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
            "CREATE TABLE "+TABLE_NAME+"( id INTEGER PRIMARY KEY AUTOINCREMENT, "+YEAR+" INTEGER,"+MONTH+" INTEGER,"+DATE+" INTEGER,"+DAY+" TEXT,"+OPENING_BAL+" INTEGER,"+CLOSING_BAL+" INTEGER )";

    final private static String NAME = "treasury_db";
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
        Log.i("Oncreate DB Helper", "called");
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
        Log.i(getClass().toString(),"Database Created");

        String selectQuery = "Select * from "+TABLE_NAME+" where "+DAY+"=? ";

        Cursor cursor = db.rawQuery(selectQuery, new String[] {"Monday"});

        if(cursor.moveToFirst()) {
            Log.i("Checking DB", ""+cursor.getInt(0)+" "+cursor.getInt(1)+" "+cursor.getInt(2)+" "+cursor.getInt(3)+" "+cursor.getString(4)+" "+cursor.getInt(5)+" "+cursor.getInt(6));
        }

        cursor.close();

        return true;
    }

    public DailyCash[] getData(int day, int month, int year, int range){

        int beginId=0;

        int i=0;

        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "Select * from "+TABLE_NAME+" where "+MONTH+"=? and "+DATE+">=? and "+YEAR+">=?";

        Cursor cursor = db.rawQuery(selectQuery, new String[]{month+"",day+"",year+""});
        Log.i("cursor count" ,""+cursor.getCount());

        if(cursor.getCount()!=0){
            if(cursor.moveToFirst()){
                beginId = cursor.getInt(0);
                Log.i("id of first" ,""+beginId);
            }
        }else {
            if(month!=12){
                selectQuery = "Select * from "+TABLE_NAME+" where "+MONTH+">=? and "+DATE+">=? and "+YEAR+">=?";
                Cursor cursor2 = db.rawQuery(selectQuery, new String[]{(month+1)+"",1+"",year+""});
                Log.i("cursor2 count" ,""+cursor2.getCount());
                if(cursor2.moveToFirst()){
                    beginId = cursor2.getInt(0);
                    Log.i("id of first now" ,""+beginId);
                }
                cursor2.close();
            }
            else {
                selectQuery = "Select * from "+TABLE_NAME+" where "+MONTH+">=? and "+DATE+">=? and "+YEAR+">=?";
                Cursor cursor3 = db.rawQuery(selectQuery, new String[]{1+"",1+"",(year+1)+""});
                Log.i("cursor3 count" ,""+cursor3.getCount());
                if(cursor3.moveToFirst()){
                    beginId = cursor3.getInt(0);
                    Log.i("id of first nowww" ,""+beginId);
                }
                cursor3.close();
            }

        }

        cursor.close();

        String selectQuery2 = "Select * from "+TABLE_NAME+" where id >="+beginId+" and id <="+(beginId+range-1);

        Cursor newCursor = db.rawQuery(selectQuery2, null);

        DailyCash[] retrievedData = new DailyCash[newCursor.getCount()];

        if(newCursor.moveToFirst()) {
            do {
                Log.i("Checking DB 2 for 900:", "i="+i+" "+newCursor.getInt(0)+" "+newCursor.getInt(1)+" "+newCursor.getInt(2)+" "+newCursor.getInt(3)+" "+newCursor.getString(4)+" "+newCursor.getInt(5)+" "+newCursor.getInt(6));
                DailyCash dailyCash = new DailyCash(newCursor.getInt(1), newCursor.getInt(2), newCursor.getInt(3), newCursor.getString(4), newCursor.getInt(6));
                retrievedData[i] = dailyCash;
                i++;
            } while (newCursor.moveToNext());

            Log.i("counter", ""+i);
        }

        return retrievedData;
    }

}
