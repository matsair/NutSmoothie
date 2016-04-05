package de.nutboyz.nutsmoothie.Map;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import de.nutboyz.nutsmoothie.R;

/**
 * Created by Jan on 02.04.16.
 */
public class inputDialog extends DialogFragment {

    private Button btn_ok;
    private Button btn_cancel;
    private EditText location_name;
    private inputDialog_Interface communicator;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        communicator = (inputDialog_Interface) activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.input_dialog,null);

        //der eingegebene location name
        location_name = (EditText) view.findViewById(R.id.inpDialog_editText);

        //OnClick Handler für Cancel
        btn_cancel = (Button) view.findViewById(R.id.inpDialog_btn_cancel);
        btn_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"No location name specified.",Toast.LENGTH_SHORT)
                        .show();
                dismiss();
            }
        });

        //OnClick handler für Location Name speichern.
        btn_ok = (Button) view.findViewById(R.id.inpDialog_btn_ok);
        btn_ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (location_name.getText().length() != 0) {
                        //Gib nur den eingegebenen location namen zurück
                        communicator.save_data(location_name.getText().toString());
                        Toast.makeText(getContext(),"Location name " + location_name.getText().toString() + " stored.",Toast.LENGTH_LONG)
                                .show();
                        dismiss();
                    } else {
                        AlertDialog.Builder myAlert = new AlertDialog.Builder(getContext());
                        myAlert.setTitle("No Location Name")
                                .setMessage("You did not specified any location name. Please specify a name.")
                                .setNeutralButton("Ok", null)
                                .show();
                    }
                }catch (Exception e)
                {
                    e.getMessage();
                }
            }
        });

        return view;
    }
}
