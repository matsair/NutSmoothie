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

/**
 * Contains methods for location data in database.
 * @author Mats
 */
public class LocationDataSource {

    private static final String TAG = "LocationDataSource";

    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = { MySQLiteHelper.TABLE_TASKLOCATIONS_COLUMN_LOCATION_ID,
            MySQLiteHelper.TABLE_LOCATIONS_COLUMN_NAME,
            MySQLiteHelper.TABLE_LOCATIONS_COLUMN_LATITUDE,
            MySQLiteHelper.TABLE_LOCATIONS_COLUMN_LONGITUDE};

    public LocationDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    /**
     * Opens the database. Required before adding and accessing location data.
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
     * Adds the location to the database and returns the same location, with ID,
     * as a Location object.
     * @param location The location to be added to the database.
     * @return The location added to the database, with ID
     */
    public NutLocation addLocation(NutLocation location) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.TABLE_LOCATIONS_COLUMN_NAME, location.getName());
        values.put(MySQLiteHelper.TABLE_LOCATIONS_COLUMN_LATITUDE, location.getLatitude());
        values.put(MySQLiteHelper.TABLE_LOCATIONS_COLUMN_LONGITUDE, location.getLongitude());
        long insertId = database.insert(MySQLiteHelper.TABLE_LOCATIONS, null, values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_LOCATIONS,
                allColumns, MySQLiteHelper.TABLE_LOCATIONS_COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        NutLocation newLocation = cursorToLocation(cursor);
        cursor.close();
        return newLocation;
    }

    /**
     * Update the distance from the user's current location to the given location.
     * @param location Location to be updated.
     * @param distance The distance to the user's current location.
     * @return The distance
     */

    public double updateDistance(NutLocation location, double distance) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.TABLE_LOCATIONS_COLUMN_DISTANCE, distance);
        String where = MySQLiteHelper.TABLE_LOCATIONS_COLUMN_ID + "=" + location.getId();
        database.update(MySQLiteHelper.TABLE_LOCATIONS, values, where, null);
        return distance;
    }

    /**
     * Deletes the location from the database. Checks for id.
     * @param location Location to be deleted.
     */
    public void deleteLocation(NutLocation location) {
        int locId = location.getId();
        database.delete(MySQLiteHelper.TABLE_LOCATIONS, MySQLiteHelper.TABLE_LOCATIONS_COLUMN_ID
                + " = " + locId, null);
        Log.i(TAG, "Location deleted with id: " + locId);
    }

    /**
     * Returns a list of all locations in the database.
     * @return List of Locations
     */
    public ArrayList<NutLocation> getAllLocations() {
        ArrayList<NutLocation> locations = new ArrayList<>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_LOCATIONS,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            NutLocation location = cursorToLocation(cursor);
            locations.add(location);
            cursor.moveToNext();
        }

        cursor.close();
        return locations;
    }

    /**
     * Returns the location corresponding to the given id.
     * @param id
     * @return Location
     */
    public NutLocation getLocationFromId(int id) {
        Cursor cursor = database.query(MySQLiteHelper.TABLE_LOCATIONS,
                allColumns, MySQLiteHelper.TABLE_LOCATIONS_COLUMN_ID + " = " + id,
                null, null, null, null);

        cursor.moveToFirst();
        NutLocation location = cursorToLocation(cursor);

        cursor.close();
        return location;
    }

    /**
     * Returns a List of Locations from an Integer List.
     * @param idList List of location IDs to get
     * @return List of Locations corresponding to given IDs.
     */
    public List<NutLocation> getLocationsFromIntList(List<Integer> idList) {
        List<NutLocation> locationList = new ArrayList<>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_LOCATIONS,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            if (idList.contains(cursor.getInt(0))) {
                NutLocation location = cursorToLocation(cursor);
                locationList.add(location);
            }
            cursor.moveToNext();
        }

        cursor.close();
        return locationList;
    }

    private NutLocation cursorToLocation(Cursor cursor) {
        NutLocation location = new NutLocation();
        location.setId(cursor.getInt(0));
        location.setName(cursor.getString(1));
        location.setLatitude(cursor.getDouble(2));
        location.setLongitude(cursor.getDouble(3));
        return location;
    }
}
