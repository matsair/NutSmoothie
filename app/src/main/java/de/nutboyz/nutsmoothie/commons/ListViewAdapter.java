package de.nutboyz.nutsmoothie.commons;

/**
 * Created by Jojo on 21/04/16.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import de.nutboyz.nutsmoothie.R;


public class ListViewAdapter extends ArrayAdapter<Task>{

    private final String TAG = getClass().getSimpleName();

    public ListViewAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public ListViewAdapter(Context context, int resource, List<Task> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.list_row, null);
        }

        Task t = getItem(position);

        if (t != null) {
            CheckBox checkView = (CheckBox) v.findViewById(R.id.main_list_checkbox);
            TextView titleView = (TextView) v.findViewById(R.id.txtview_task_name);
            TextView distanceView = (TextView) v.findViewById(R.id.txtview_distance);

            if (checkView != null) {
                //Todo
            }

            if (titleView != null) {
                titleView.setText(t.getName());
//                Log.i(TAG, t.getName());
            }

            if (distanceView != null) {
                double distance;
                String distanceStr;
                if (t.getDistance() > 1500) {
                    distance = t.getDistance()/1000.0;
                    distanceStr = String.format("%.2f", distance) + " km";
                }
                else {
                    distance = t.getDistance();
                    distanceStr = (int) distance + " m";
                }
                if (t.getDistance() == 0) {
                    distanceView.setVisibility(View.INVISIBLE);
                }
                else {
                    distanceView.setVisibility(View.VISIBLE);
                    distanceView.setText(distanceStr);
                }
//                Log.i(TAG, String.valueOf(t.getReminderRange()));
            }
        }

        return v;
    }
}
