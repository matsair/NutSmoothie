package de.nutboyz.nutsmoothie.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * This class manages the SQLite database structure.
 * @author Mats
 */
public class MySQLiteHelper extends SQLiteOpenHelper {

    private static final String TAG = "Database";

    // Table Tasks info
    public static final String TABLE_TASKS = "Tasks";
    public static final String TABLE_TASKS_COLUMN_ID = "task_id";
    public static final String TABLE_TASKS_COLUMN_NAME = "name";
    public static final String TABLE_TASKS_COLUMN_REMINDER_RANGE = "reminder_range";

    // Table Locations info
    public static final String TABLE_LOCATIONS = "Locations";
    public static final String TABLE_LOCATIONS_COLUMN_ID = "location_id";
    public static final String TABLE_LOCATIONS_COLUMN_NAME = "name";
    public static final String TABLE_LOCATIONS_COLUMN_LATITUDE = "latitude";
    public static final String TABLE_LOCATIONS_COLUMN_LONGITUDE = "longitude";
    public static final String TABLE_LOCATIONS_COLUMN_DISTANCE = "distance";

    // Table TaskLocations info
    public static final String TABLE_TASKLOCATIONS = "TaskLocations";
    public static final String TABLE_TASKLOCATIONS_COLUMN_TASK_ID = "task_id";
    public static final String  TABLE_TASKLOCATIONS_COLUMN_LOCATION_ID = "location_id";


    public static final String DATABASE_NAME = "NutSmoothie.db";
    private static final int DATABASE_VERSION = 2;

    // Database creation sql statement
    private static final String CREATE_TABLE_TASKS = "CREATE TABLE " +
            TABLE_TASKS +
            " (" + TABLE_TASKS_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            TABLE_TASKS_COLUMN_NAME + " VARCHAR(45), " +
            TABLE_TASKS_COLUMN_REMINDER_RANGE + " DOUBLE);";

    private static final String CREATE_TABLE_LOCATIONS = "CREATE TABLE "
            + TABLE_LOCATIONS +
            " (" + TABLE_LOCATIONS_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            TABLE_LOCATIONS_COLUMN_NAME + " VARCHAR(45)," +
            TABLE_LOCATIONS_COLUMN_LATITUDE + " DOUBLE, " +
            TABLE_LOCATIONS_COLUMN_LONGITUDE + " DOUBLE, " +
            TABLE_LOCATIONS_COLUMN_DISTANCE + " DOUBLE);";

    private static final String CREATE_TABLE_TASKLOCATIONS = "CREATE TABLE "
            + TABLE_TASKLOCATIONS +
            " (" + TABLE_TASKLOCATIONS_COLUMN_TASK_ID + " INTEGER, " +
            TABLE_TASKLOCATIONS_COLUMN_LOCATION_ID + " INTEGER);";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        Log.i(TAG, "Creating database...");
        database.execSQL(CREATE_TABLE_TASKS);
        database.execSQL(CREATE_TABLE_LOCATIONS);
        database.execSQL(CREATE_TABLE_TASKLOCATIONS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKLOCATIONS);
        onCreate(db);
    }
}
