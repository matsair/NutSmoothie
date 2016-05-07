package de.nutboyz.nutsmoothie;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.nutboyz.nutsmoothie.commons.NutLocation;
import de.nutboyz.nutsmoothie.commons.Task;
import de.nutboyz.nutsmoothie.database.LocationDataSource;
import de.nutboyz.nutsmoothie.database.TaskDataSource;
import de.nutboyz.nutsmoothie.database.TaskLocationsDataSource;

/**
 * @author Johannes
 */
public class NewTaskActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();

    public Button btn_save, btn_cancel, btn_addLocation;
    public EditText reminderName;
    public SeekBar seekbar;
    public ListView listView;

    private ArrayList<NutLocation> locationList = new ArrayList<>();

    public Task task;
    Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newtask_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView reminderLocations = (TextView) findViewById(R.id.newtask_liview_loc_list_title);
        reminderLocations.setVisibility(View.INVISIBLE);

        listView = (ListView) findViewById(R.id.newtask_liview_loc_list);

        seekbar = (SeekBar) findViewById(R.id.newtask_seek);

        extras = getIntent().getExtras();
        if (extras != null) {
            task = new Task(Integer.valueOf(extras.getString("task")));
            List<NutLocation> nutLocationList = getTaskLocations(task);

            for (NutLocation nutLocation : nutLocationList) {
                Log.i(TAG, "Task locaton list: " + nutLocation.getName());
                locationList.add(nutLocation);
            }

            reminderLocations.setVisibility(View.VISIBLE);

            listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, locationList));

            TaskDataSource taskDataSource = new TaskDataSource(this);
            taskDataSource.open();
            List<Task> taskList = taskDataSource.getAllTasks();
            taskDataSource.close();

            for(Task task : taskList){
                if(task.getId() == this.task.getId()){
                    reminderName = (EditText)findViewById(R.id.newtask_edtext_task);
                    reminderName.setText(task.getName());
                    seekbar.setProgress(task.getReminderRange());
                }
            }
        }

        final TextView seekBarValue = (TextView)findViewById(R.id.seekbar_range_text);

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                               @Override
                                               public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                                   seekBarValue.setText(String.valueOf(progress));
                                               }

                                               @Override
                                               public void onStartTrackingTouch(SeekBar seekBar) {

                                               }

                                               @Override
                                               public void onStopTrackingTouch(SeekBar seekBar) {

                                               }
                                           });


        btn_save = (Button) findViewById(R.id.newtask_btn_save);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reminderName = (EditText) findViewById(R.id.newtask_edtext_task);

                task = saveTaskName(reminderName.getText().toString(), seekbar.getProgress());

                List<NutLocation> nutLocationList = getTaskLocations(task);

                saveLocationToTask(task, nutLocationList);


                // TO-DO:
                // getList und dann gegettetes Element auswählen
                // also Nutlocation für Task

                logout();
            }
        });


        btn_addLocation = (Button) findViewById(R.id.newtask_btn_addLoc);
        btn_addLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (extras == null) {
                    reminderName = (EditText) findViewById(R.id.newtask_edtext_task);

                    task = saveTaskName(reminderName.getText().toString(), seekbar.getProgress());
                }

                Intent i = new Intent(getApplicationContext(), LocationListActivity.class);

                i.putExtra("task", task.getId());
                startActivity(i);
            }
        });

        btn_cancel = (Button) findViewById(R.id.newtask_btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {

            //TODO Delete task

            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }

    public void saveLocationToTask(Task task, List<NutLocation> nutLocationList){
        TaskLocationsDataSource taskLocationsDataSource = new TaskLocationsDataSource(this);

        for (NutLocation location : nutLocationList) {
            taskLocationsDataSource.addLocationToTask(task, location);
        }
    }


   public Task saveTaskName(String reminder, int seekbar) {
        TaskDataSource taskDataSource;

        taskDataSource = new TaskDataSource(this);
        taskDataSource.open();

        Task task = taskDataSource.addTask(new Task(reminder, seekbar));

        taskDataSource.close();

        return task;
    }


    public List<NutLocation> getTaskLocations(Task task) {

        TaskLocationsDataSource taskLocationsDataSource = new TaskLocationsDataSource(this);
        taskLocationsDataSource.open();
        List<Integer> taskIdList = taskLocationsDataSource.getTaskLocationIds(task);
        taskLocationsDataSource.close();

        LocationDataSource locationDataSource = new LocationDataSource(this);
        locationDataSource.open();
        List<NutLocation> tasks = locationDataSource.getLocationsFromIntList(taskIdList);
        locationDataSource.close();

        return tasks;
    }

    public void logout() {
        Intent i = new Intent(getApplicationContext(),
                MainActivity.class);
        i.putExtra("finish", true);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // To clean up all act
        startActivity(i);
        finish();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }



/*
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }


    save = (Button) findViewById(R.id.buttonHome);
    save.setOnClickListener(new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            Intent i = new Intent(getApplicationContext(),
                    HomeActivity.class);
       //     i.putExtra("myUserId", myUserId);
            startActivity(i);
        }
    });
*/

}