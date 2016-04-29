package de.nutboyz.nutsmoothie;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.List;

import de.nutboyz.nutsmoothie.Map.Google_Map;
import de.nutboyz.nutsmoothie.commons.ListViewAdapter;
import de.nutboyz.nutsmoothie.commons.Task;
import de.nutboyz.nutsmoothie.database.TaskDataSource;

public class MainActivity extends AppCompatActivity {

    private GoogleApiClient client;
    List<Task> taskList = new ArrayList<Task>();
    ListViewAdapter myAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TaskDataSource taskDataSource = new TaskDataSource(this);
        taskDataSource.open();
        taskList = taskDataSource.getAllTasks();
        for (Task task : taskList) {
            taskDataSource.deleteTask(task);
        }
        taskDataSource.addTask(new Task("Buy nuts", 100));
        taskDataSource.addTask(new Task("Buy smoothie", 100));
        taskDataSource.close();

        myAdapter = new ListViewAdapter(this,
                (ArrayList<Task>) taskList);

        ListView listViewTasks = (ListView) findViewById(R.id.home_task_list);

        listViewTasks.setAdapter(myAdapter);


        listViewTasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long arg3) {
                Toast.makeText(getApplicationContext(), " " +position , Toast.LENGTH_LONG).show();
                taskList.remove(position);
                myAdapter.notifyDataSetChanged();
                myAdapter.notifyDataSetInvalidated();
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
        if (id == R.id.action_settings) {
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
                        //call the onClick method of the button if permissions are granted
                        //get_map.performClick();
                        Intent intent = new Intent(MainActivity.this, Google_Map.class);
                        startActivity(intent);
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
