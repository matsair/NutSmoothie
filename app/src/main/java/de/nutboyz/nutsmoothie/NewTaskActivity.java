package de.nutboyz.nutsmoothie;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

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
public class NewTaskActivity extends Activity {

    public Button btn_save, btn_cancel, btn_addLocation;
    public EditText reminderName;
    public SeekBar seekbar;
    public ListView listView;

    private ArrayAdapter<NutLocation> adapter;
    private ArrayList<NutLocation> locationList = new ArrayList<>();

    public Task task;
    Bundle extras;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.newtask_activity);
            //  Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            // setSupportActionBar(toolbar);

            buildAdapter();

            listView = (ListView) findViewById(R.id.newtask_liview_loc_list);
            listView.setAdapter(adapter);

            seekbar = (SeekBar)findViewById(R.id.newtask_seek);

            extras = getIntent().getExtras();
            if (extras != null) {
                task = new Task(Integer.valueOf(extras.getString("task")));
                List<NutLocation> nutLocationList = getTaskLocations(task);

                List<String> stringNutLocationList = new ArrayList<String>();
                for (NutLocation nutLocation : nutLocationList)
                    stringNutLocationList.add(nutLocation.getName());

                String[] stockArr = new String[nutLocationList.size()];
                stockArr = stringNutLocationList.toArray(stockArr);

                listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, stockArr));

                TaskDataSource taskDataSource = new TaskDataSource(this);
                List<Task> taskList = taskDataSource.getAllTasks();

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
                    reminderName = (EditText) findViewById(R.id.newtask_edtext_task);

                    task = saveTaskName(reminderName.getText().toString(), seekbar.getProgress());

                    Intent i = new Intent(getApplicationContext(), LocationListActivity.class);

                    i.putExtra("task", task.getId());
                    startActivity(i);
                }
            });

            btn_cancel = (Button) findViewById(R.id.newtask_btn_cancel);
            btn_cancel.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    logout();
                }
            });
        } catch (Exception e) {
            e.getMessage();
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
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

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "NewTask Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://de.nutboyz.nutsmoothie/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "NewTask Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://de.nutboyz.nutsmoothie/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    public void buildAdapter(){
        adapter = new ArrayAdapter<>(
                this,               // context for the activity
                android.R.layout.simple_list_item_1,      // layout to use (create)
                locationList        // items to be displayed
        );
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