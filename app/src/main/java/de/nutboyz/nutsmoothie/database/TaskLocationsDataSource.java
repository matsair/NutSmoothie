package de.nutboyz.nutsmoothie.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import de.nutboyz.nutsmoothie.commons.NutLocation;
import de.nutboyz.nutsmoothie.commons.Task;

/**
 * Contains methods for task location data in database.
 * @author Mats
 */
public class TaskLocationsDataSource {

    private static final String TAG = "TaskLocationsDataSource";

    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = {MySQLiteHelper.TABLE_TASKLOCATIONS_COLUMN_TASK_ID,
            MySQLiteHelper.TABLE_TASKLOCATIONS_COLUMN_LOCATION_ID};

    public TaskLocationsDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }



    /**
     * Opens the database. Required before adding and accessing task location data.
     * @throws SQLException
     */
    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    /**
     * Closes the database. Should be called when access to DB is no longer needed.
     */
    public void close() {
        dbHelper.close();
    }

    /**
     * Adds a location to a task.
     * @param task
     * @param location
     */
    public void addLocationToTask(Task task, NutLocation location) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.TABLE_TASKLOCATIONS_COLUMN_TASK_ID, task.getId());
        values.put(MySQLiteHelper.TABLE_TASKLOCATIONS_COLUMN_LOCATION_ID, location.getId());
        database.insert(MySQLiteHelper.TABLE_TASKLOCATIONS, null, values);
    }

    /**
     * Deletes a location from a task.
     * @param task
     * @param location
     */
    public void deleteLocationFromTask(Task task, NutLocation location) {
        int locId = location.getId();
        database.delete(MySQLiteHelper.TABLE_TASKLOCATIONS,
                MySQLiteHelper.TABLE_TASKLOCATIONS_COLUMN_LOCATION_ID
                        + " = " + locId + " AND " +
                        MySQLiteHelper.TABLE_TASKLOCATIONS_COLUMN_TASK_ID + " = " +
                        task.getId(), null);
        Log.i(TAG, "Location " + location.getName() + " deleted from task " + task.getId());
    }

    /**
     * Returns a list of location ids corresponding to the given task.
     * @param task Task for which location ids are requested.
     * @return Integer List of location ids.
     */
    public List<Integer> getTaskLocationIds(Task task) {
        List<Integer> locationIds = new ArrayList<>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_TASKLOCATIONS,
                allColumns,
                MySQLiteHelper.TABLE_TASKLOCATIONS_COLUMN_TASK_ID + " = " + task.getId(),
                null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            locationIds.add(cursor.getInt(1));
            cursor.moveToNext();
        }

        cursor.close();
        return locationIds;
    }
}
