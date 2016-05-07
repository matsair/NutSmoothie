package de.nutboyz.nutsmoothie;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.nutboyz.nutsmoothie.GPS.gpsService;
import de.nutboyz.nutsmoothie.commons.ListViewAdapter;
import de.nutboyz.nutsmoothie.commons.Task;
import de.nutboyz.nutsmoothie.database.TaskDataSource;

public class MainActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();

    private CheckBox checkBox;
    List<Task> taskList = new ArrayList<>();
    ListViewAdapter myAdapter;

    /***
     * The Service connection for retrieving the GPS Location
     * @Author: Jan
     */
    private ServiceConnection GpsService = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final TaskDataSource taskDataSource = new TaskDataSource(this);
        taskDataSource.open();
        taskList = taskDataSource.getAllTasks();
        taskDataSource.close();

        Collections.sort(taskList, new Comparator<Task>() {
            @Override
            public int compare(Task lhs, Task rhs) {
                return lhs.getReminderRange() > rhs.getReminderRange() ? 1 : (lhs.getReminderRange() < rhs.getReminderRange() ? -1 : 0);
            }
        });

        ListView listViewTasks = (ListView) findViewById(R.id.home_task_list);
        myAdapter = new ListViewAdapter(this, R.layout.list_row, taskList);
        listViewTasks.setAdapter(myAdapter);

        listViewTasks.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
             @Override
             public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                 AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                 dialog.setMessage("Are you sure you want to delete this task?");
                 dialog.setPositiveButton("Yes, delete", new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialog, int which) {
                         taskDataSource.open();
                         taskDataSource.deleteTask(taskList.get(position));
                         taskDataSource.close();
                         taskList.remove(position);
                         myAdapter.notifyDataSetChanged();
                         myAdapter.notifyDataSetInvalidated();
                     }
                 });
                 dialog.setNegativeButton("No, don't delete", new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialog, int which) {

                     }
                 });
                 dialog.create();
                 dialog.show();
                 return true;
             }
         });

                FloatingActionButton btn_main_addTask = (FloatingActionButton) findViewById(R.id.main_button_add);
        btn_main_addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getApplicationContext(),
                        NewTaskActivity.class);
                startActivity(i);

            }
        });


        if (checkPermissions()) {
            bindService(new Intent(getApplicationContext(), gpsService.class)
                    , GpsService,
                    Context.BIND_AUTO_CREATE);
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.refresh) {
            myAdapter.clear();
            final TaskDataSource taskDataSource = new TaskDataSource(this);
            taskDataSource.open();
            taskList = taskDataSource.getAllTasks();
            taskDataSource.close();

            Collections.sort(taskList, new Comparator<Task>() {
                @Override
                public int compare(Task lhs, Task rhs) {
                    return lhs.getReminderRange() > rhs.getReminderRange() ? 1 : (lhs.getReminderRange() < rhs.getReminderRange() ? -1 : 0);
                }
            });

            ListView listViewTasks = (ListView) findViewById(R.id.home_task_list);
            myAdapter = new ListViewAdapter(this, R.layout.list_row, taskList);
            listViewTasks.setAdapter(myAdapter);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /***
     * Checks if the permissions are given
     *
     * @return
     */
    public boolean checkPermissions() {
        //Check Permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {   //Request Permission if not granted
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION}, 10);
                return false;
            }
        }
        return true;
    }

    /***
     * Check the result of the requested permissions
     *
     * @param requestCode
     * @param permission
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permission, int[] grantResults) {
        try {
            switch (requestCode) {
                case 10:
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                        bindService(new Intent(getApplicationContext(), gpsService.class)
                                , GpsService,
                                Context.BIND_AUTO_CREATE);
                        return;
                    } else {
                        //create AlertDialog
                        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
                        myDialog.setTitle("No GPS")
                                .setMessage("Without GPS permission, we cannot determine the current Location.")
                                .setNegativeButton("No Permission", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                })
                                .setPositiveButton("Give Permission", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        checkPermissions();
                                        dialog.cancel();
                                    }
                                })
                                .create().show();
                    }
            }
        }catch (Exception e)
        {
            e.getMessage();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
