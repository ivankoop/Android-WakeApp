package py.com.pops.wakeapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by ivankoop on 25/4/16.
 */

public class LatlngDatabase extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;


    private static final String DATABASE_NAME = "latlnDatabase";


    private static final String TABLE_LATLNG = "latlng";

    private static final String KEY_ID = "id";
    private static final String LATLNG_LATITUDE = "latitude";
    private static final String LATLNG_LONGITUDE = "longitude";
    private static final String LATLNG_KMH = "kmh";

    public LatlngDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String CREATE_LATLONG_TABLE = "CREATE TABLE " + TABLE_LATLNG + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + LATLNG_LATITUDE + " TEXT,"
                + LATLNG_LONGITUDE + " TEXT,"
                + LATLNG_KMH + " INTEGER);";

        sqLiteDatabase.execSQL(CREATE_LATLONG_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void addLatlng(String latlng_latitude, String latlng_longitude, int latlng_kmh){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LATLNG_LATITUDE,latlng_latitude);
        values.put(LATLNG_LONGITUDE,latlng_longitude);
        values.put(LATLNG_KMH,latlng_kmh);

        db.insert(TABLE_LATLNG,null,values);
        db.close();
    }

    public void clearLatlng(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_LATLNG, null, null);
    }


    public ArrayList<WakeappLatLngPoints> getLatlng(){

        ArrayList<WakeappLatLngPoints> latlngs = new ArrayList<>();

        String select_query = "SELECT * FROM " + TABLE_LATLNG + " ORDER BY "+KEY_ID+ " DESC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(select_query,null);

        if(cursor.moveToFirst()){
            do {
                double latitude = Double.parseDouble(cursor.getString(1));
                double longitude = Double.parseDouble(cursor.getString(2));
                WakeappLatLngPoints points = new WakeappLatLngPoints(cursor.getInt(0),latitude,longitude,cursor.getInt(3));
                latlngs.add(points);
            } while (cursor.moveToNext());
        }

        return latlngs;
    }


}
