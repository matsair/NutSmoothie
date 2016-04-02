package de.nutboyz.nutsmoothie.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import de.nutboyz.nutsmoothie.commons.Task;

/**
 * Contains methods for task data in database.
 * @author Mats
 */
public class TaskDataSource {

    private static final String TAG = "TaskDataSource";

    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = { MySQLiteHelper.TABLE_TASKS_COLUMN_ID,
    MySQLiteHelper.TABLE_TASKS_COLUMN_NAME, MySQLiteHelper.TABLE_TASKS_COLUMN_REMINDER_RANGE};

    public TaskDataSource(Context context) {
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
     * Adds a task to the database.
     * @param task Task to be added.
     * @return Task that was added to the database, including its ID.
     */
    public Task addTask(Task task) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.TABLE_TASKS_COLUMN_NAME, task.getName());
        if (task.getReminderRange() != 0) {
            values.put(MySQLiteHelper.TABLE_TASKS_COLUMN_REMINDER_RANGE, task.getReminderRange());
        }
        long insertId = database.insert(MySQLiteHelper.TABLE_TASKS, null, values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_TASKS,
                allColumns, MySQLiteHelper.TABLE_TASKS_COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Task newTask = cursorToTask(cursor);
        cursor.close();
        return newTask;
    }

    /**
     * Deletes a task from the database. Uses its ID as an identifier.
     * @param task Task to be deleted.
     */
    public void deleteTask(Task task) {
        int id = task.getId();
        Log.i(TAG, "Task deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_TASKS, MySQLiteHelper.TABLE_TASKS_COLUMN_ID
                + " = " + id, null);
    }

    /**
     * Returns a List of all Tasks in the database.
     * @return List of Tasks.
     */
    public List<Task> getAllTasks() {
        List<Task> tasks = new ArrayList<>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_TASKS,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Task task = cursorToTask(cursor);
            tasks.add(task);
            cursor.moveToNext();
        }

        cursor.close();
        return tasks;
    }

    private Task cursorToTask(Cursor cursor) {
        Task task = new Task();
        task.setId(cursor.getInt(0));
        task.setName(cursor.getString(1));
        task.setReminderRange(cursor.getInt(2));
        return task;
    }

}
