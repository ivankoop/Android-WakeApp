package py.com.pops.wakeapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by ivankoop on 18/4/16.
 */



public class LocationsDatabase extends SQLiteOpenHelper {


    private static final int DATABASE_VERSION = 1;


    private static final String DATABASE_NAME = "locationsDatabase";


    private static final String TABLE_LOCATIONS = "locations";

    private static final String KEY_ID = "id";
    private static final String LOCATION_NAME = "loc_name";
    private static final String LOCATION_COUNT = "loc_count";
    private static final String LOCATION_DATE = "loc_date";
    private static final String LOCATION_POINT_LAT = "loc_point1_lat";
    private static final String LOCATION_POINT_LONG = "loc_point1_long";


    public LocationsDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_LOCATION_TABLE = "CREATE TABLE " + TABLE_LOCATIONS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + LOCATION_NAME + " TEXT,"
                + LOCATION_COUNT + " INTEGER,"
                + LOCATION_DATE + " TEXT,"
                + LOCATION_POINT_LAT + " TEXT,"
                + LOCATION_POINT_LONG + " TEXT);";

        sqLiteDatabase.execSQL(CREATE_LOCATION_TABLE);


    }


    public void addLocation(String location_name, String location_latitude, String location_longitude, String full_time){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LOCATION_NAME,location_name);
        values.put(LOCATION_DATE,full_time);
        values.put(LOCATION_COUNT,1);
        values.put(LOCATION_POINT_LAT,location_latitude);
        values.put(LOCATION_POINT_LONG, location_longitude);

        db.insert(TABLE_LOCATIONS, null, values);
        db.close();
    }


    public ArrayList<SavedLocation> getLocations(){

        ArrayList<SavedLocation> locations = new ArrayList<>();

        String select_query = "SELECT * FROM " + TABLE_LOCATIONS + " ORDER BY "+KEY_ID+ " DESC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(select_query,null);

        Log.v("Cursor Object", DatabaseUtils.dumpCursorToString(cursor));

        if(cursor.moveToFirst()) {
            do {
                SavedLocation location = new SavedLocation(cursor.getInt(0),cursor.getString(1),cursor.getInt(2),
                        Double.parseDouble(cursor.getString(4)), Double.parseDouble(cursor.getString(5)), cursor.getString(3));
                location.setOn_iterator(true);
                location.setFull_date(cursor.getString(3));
                locations.add(location);
            } while (cursor.moveToNext());
        }

        return locations;
    }

    public void deleteLocation(SavedLocation location){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_LOCATIONS, KEY_ID + " = ?", new String[] { String.valueOf(location.getId())});
        db.close();
    }

    public int updateLocation(SavedLocation location){
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(LOCATION_NAME,location.getLocation_name());
        values.put(LOCATION_DATE,location.getLocation_date());
        values.put(LOCATION_COUNT,location.getLocation_count());
        values.put(LOCATION_POINT_LAT,location.getPoint2_location_latitude());
        values.put(LOCATION_POINT_LONG, location.getPoint2_location_longitude());

        return db.update(TABLE_LOCATIONS, values, KEY_ID + " = ?", new String[] { String.valueOf(location.getId()) });
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}

