package de.nutboyz.nutsmoothie;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
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
        Log.i(TAG, "onCreate called");
        setContentView(R.layout.newtask_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        final TextView reminderLocations = (TextView) findViewById(R.id.newtask_liview_loc_list_title);
        reminderLocations.setVisibility(View.INVISIBLE);

        listView = (ListView) findViewById(R.id.newtask_liview_loc_list);

        seekbar = (SeekBar) findViewById(R.id.newtask_seek);
        reminderName = (EditText) findViewById(R.id.newtask_edtext_task);
        final TextView reminderRange = (TextView) findViewById(R.id.newtask_text_range);

        extras = getIntent().getExtras();
        if (extras != null) {
            task = new Task(Integer.valueOf(extras.getString("task")));
            List<NutLocation> nutLocationList = getTaskLocations(task);

            for (NutLocation nutLocation : nutLocationList) {
                Log.i(TAG, "Task locaton list: " + nutLocation.getName());
                locationList.add(nutLocation);
            }

            reminderName.setText(extras.getString("taskName"), TextView.BufferType.EDITABLE);
            Log.i(TAG, "EditText: " + extras.getString("taskName"));
            seekbar.setProgress(extras.getInt("taskReminderRange"));
            reminderRange.setText("Reminder range: " + extras.getInt("taskReminderRange") + "m");

            reminderLocations.setVisibility(View.VISIBLE);

            listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, locationList));

//            TaskDataSource taskDataSource = new TaskDataSource(this);
//            taskDataSource.open();
//            List<Task> taskList = taskDataSource.getAllTasks();
//            taskDataSource.close();
//
//            for(Task task : taskList){
//                if(task.getId() == this.task.getId()){
//                    reminderName = (EditText)findViewById(R.id.newtask_edtext_task);
//                    reminderName.setText(task.getName());
//                    seekbar.setProgress(task.getReminderRange());
//                }
//            }
        }
        else {
            reminderRange.setText("Reminder range:");
        }

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                               @Override
                                               public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                                   String reminderRangeText = "Reminder range: " + progress + "m";
                                                   reminderRange.setText(reminderRangeText);
                                               }

                                               @Override
                                               public void onStartTrackingTouch(SeekBar seekBar) {

                                               }

                                               @Override
                                               public void onStopTrackingTouch(SeekBar seekBar) {

                                               }
                                           });


        btn_save = (Button) findViewById(R.id.newtask_btn_save);
        btn_save.getBackground().setColorFilter(0xFFFF4081, PorterDuff.Mode.MULTIPLY);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (reminderName.getText().length() == 0) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(NewTaskActivity.this);
                    dialog.setMessage("You didn't give your task a name!");
                    dialog.setNegativeButton("Add a name", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    dialog.create();
                    dialog.show();
                }

                else {
                    if (extras != null) {
                        Task newTask = saveTaskName(reminderName.getText().toString(), seekbar.getProgress());
                        List<NutLocation> nutLocationList = getTaskLocations(task);
                        saveLocationToTask(newTask, nutLocationList);
                        if (task.getId() != newTask.getId()) {
                            TaskDataSource taskDataSource = new TaskDataSource(NewTaskActivity.this);
                            taskDataSource.open();
                            taskDataSource.deleteTask(task);
                            taskDataSource.close();
                        }
                    }
                    else {
                        Task newTask = saveTaskName(reminderName.getText().toString(), seekbar.getProgress());
                        List<NutLocation> nutLocationList = getTaskLocations(task);
                        saveLocationToTask(newTask, nutLocationList);
                    }

                    logout();
                }
            }
        });


        btn_addLocation = (Button) findViewById(R.id.newtask_btn_addLoc);
        btn_addLocation.getBackground().setColorFilter(0xFFFF4081, PorterDuff.Mode.MULTIPLY);
        btn_addLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (extras == null) {
                    Log.i(TAG, "extras is null");
                    reminderName = (EditText) findViewById(R.id.newtask_edtext_task);

                    task = saveTaskName(reminderName.getText().toString(), seekbar.getProgress());
                }
                else {
                    Log.i(TAG, "extras is not null");
                }


                Intent i = new Intent(getApplicationContext(), LocationListActivity.class);

                i.putExtra("task", task.getId());
                i.putExtra("taskName", reminderName.getText().toString());
                i.putExtra("taskReminderRange", seekbar.getProgress());
                startActivity(i);
            }
        });

        btn_cancel = (Button) findViewById(R.id.newtask_btn_cancel);
        btn_cancel.getBackground().setColorFilter(0xFFFF4081, PorterDuff.Mode.MULTIPLY);
        btn_cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (extras != null) {
                    TaskDataSource taskDataSource = new TaskDataSource(NewTaskActivity.this);
                    taskDataSource.open();
                    taskDataSource.deleteTask(task);
                    taskDataSource.close();
                }

                logout();
            }
        });
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

    public void saveLocationToTask(Task task, List<NutLocation> nutLocationList){
        TaskLocationsDataSource taskLocationsDataSource = new TaskLocationsDataSource(this);
        taskLocationsDataSource.open();

        for (NutLocation location : nutLocationList) {
            taskLocationsDataSource.addLocationToTask(task, location);
        }
        taskLocationsDataSource.close();
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