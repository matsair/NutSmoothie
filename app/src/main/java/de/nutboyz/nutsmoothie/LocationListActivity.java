package de.nutboyz.nutsmoothie;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import de.nutboyz.nutsmoothie.Map.Google_Map;
import de.nutboyz.nutsmoothie.commons.NutLocation;
import de.nutboyz.nutsmoothie.database.LocationDataSource;
import de.nutboyz.nutsmoothie.database.TaskDataSource;
import de.nutboyz.nutsmoothie.database.TaskLocationsDataSource;

/**
 * @author Richard
 */

public class LocationListActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();

    private Button addNewLocation;

    private int taskId;
    private String taskName;
    private int taskReminderRange;

    private ArrayList<NutLocation> locationList = new ArrayList<>();
    private ArrayAdapter<NutLocation> adapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_list_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                //TODO
            } else {
                taskId = extras.getInt("task");
                taskName = extras.getString("taskName");
                taskReminderRange = extras.getInt("taskReminderRange");
            }
        } else {
            taskId = (int) savedInstanceState.getSerializable("task");
            taskName = (String) savedInstanceState.getSerializable("taskName");
            taskReminderRange = (int) savedInstanceState.getSerializable("taskReminderRange");
        }

        addNewLocation = (Button) findViewById(R.id.add_new_location);
        addNewLocation.getBackground().setColorFilter(0xFFFF4081, PorterDuff.Mode.MULTIPLY);
        addNewLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewItem();
            }
        });

        populateListView();
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void populateListView() {
        // create listView of items
        LocationDataSource locationDataSource = new LocationDataSource(this);
        locationDataSource.open();
        List<NutLocation> locations = locationDataSource.getAllLocations();
        for (NutLocation loc : locations) {
            locationList.add(loc);
        }

        // build adapter
        buildAdapter();

        // configure the listView view
        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setLongClickable(true);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(LocationListActivity.this);
                dialog.setMessage("Are you sure you want to delete this location from the database?");
                dialog.setPositiveButton("Yes, delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LocationDataSource locationDataSource = new LocationDataSource(LocationListActivity.this);
                        locationDataSource.open();
                        locationDataSource.deleteLocation(locationList.get(position));
                        locationDataSource.close();
                        locationList.remove(position);
                        refreshListView();
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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TaskDataSource taskDataSource = new TaskDataSource(LocationListActivity.this);
                taskDataSource.open();
                TaskLocationsDataSource taskLocationsDataSource = new TaskLocationsDataSource(LocationListActivity.this);
                taskLocationsDataSource.open();
                taskLocationsDataSource.addLocationToTask(taskDataSource.getTaskFromId(taskId), locationList.get(position));
                taskDataSource.close();
                taskLocationsDataSource.close();
                Intent i = new Intent(getApplicationContext(),
                        NewTaskActivity.class);
                Log.i(TAG, "Task ID: " + taskId);
                i.putExtra("task", String.valueOf(taskId));
                i.putExtra("taskName", taskName);
                i.putExtra("taskReminderRange", taskReminderRange);
                startActivity(i);
            }
        });
    }

    public void buildAdapter(){
        adapter = new ArrayAdapter<>(
                this,               // context for the activity
                android.R.layout.simple_list_item_1,      // layout to use (create)
                locationList        // items to be displayed
        );
    }

    public void addNewItem() {
        Intent intent = new Intent(LocationListActivity.this, Google_Map.class);
        startActivity(intent);
    }

    public void refreshListView(){
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume called");
        adapter.clear();
        populateListView();
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(),
                NewTaskActivity.class);
        Log.i(TAG, "Task ID: " + taskId);
        i.putExtra("task", String.valueOf(taskId));
        i.putExtra("taskName", taskName);
        i.putExtra("taskReminderRange", taskReminderRange);
        startActivity(i);
    }
}
