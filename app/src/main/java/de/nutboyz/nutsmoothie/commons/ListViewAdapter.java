package de.nutboyz.nutsmoothie.commons;

/**
 * Created by Jojo on 21/04/16.
 */

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import de.nutboyz.nutsmoothie.R;


public class ListViewAdapter extends ArrayAdapter<Task>{

    // View lookup cache
    private static class ViewHolder {
        TextView name;
        TextView distance;
        CheckBox cBox;
    }

    Context context;
    View convertView;
    ArrayList<Task> data = new ArrayList<Task>();


    public ListViewAdapter(Context context, ArrayList<Task> data) {

        super(context, R.layout.activity_main, data);

        this.context = context;
        this.data = (ArrayList<Task>) data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        this.convertView = convertView;
        //Task task = data.getItem(position);


        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // 2. Get rowView from inflater
        View rowView =  inflater.inflate(R.layout.list_row, parent, false);

        // 3. Get icon,title & counter views from the rowView
        CheckBox checkView = (CheckBox) rowView.findViewById(R.id.main_list_checkbox);
        TextView titleView = (TextView) rowView.findViewById(R.id.txtview_task_name);
        TextView distanceView = (TextView) rowView.findViewById(R.id.txtview_distance);


        // 4. Set the text for textView
        titleView.setText(data.get(position).getName());
        Log.i("TEST", data.get(position).getName());
        String distance = data.get(position).getReminderRange() + " km";
        distanceView.setText(distance);
        Log.i("TEST", String.valueOf(data.get(position).getReminderRange()));


        // 5. return rowView
        return rowView;

    }


    static class RecordHolder {
        TextView txtTitle;
        ImageView imageItem;

    }

}
