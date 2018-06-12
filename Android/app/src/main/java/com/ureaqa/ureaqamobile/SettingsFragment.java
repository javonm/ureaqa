package com.ureaqa.ureaqamobile;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {


    public SettingsFragment() {
            // Required empty public constructor
    }

    TextView tvTitle;
    EditText etNewPassword, etOldPassword, etNewWeight;
    Button bUpdatePassword, bUpdateWeight;
    ToggleButton toggleButton;
    String username, name, last_name;
    int update_pw;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        tvTitle = view.findViewById(R.id.tvTitle);
        etNewPassword = view.findViewById(R.id.etPasswordNew);
        etOldPassword = view.findViewById(R.id.etPasswordCurrent);
        etNewWeight = view.findViewById(R.id.etWeightNew);
        bUpdatePassword = view.findViewById(R.id.bPasswordUpdate);
        bUpdateWeight = view.findViewById(R.id.bWeightUpdate);
        toggleButton = view.findViewById(R.id.tbWeight);

        Bundle bundle = getArguments();
        assert bundle != null;
        username = bundle.getString("username");
        name = bundle.getString("name");
        last_name = bundle.getString("last_name");

        String title = "Settings > " + name + " " +last_name;
        tvTitle.setText(title);



        bUpdateWeight.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v1) {
                //do the thing
                int weight = Integer.parseInt(etNewWeight.getText().toString());
                if (weight > 0 && etNewWeight.getText().toString().trim().length() > 0){
                    if (!toggleButton.isChecked()){
                        //lbs to kg
                        weight = (int) Math.round(weight/2.2);
                    }

                    update_pw = 0;

                    String placeholder = "no password";
                    doTheThing(update_pw, weight+"", placeholder);
                    //Toast.makeText(getContext(), "Entered weight: "+ weight + "kg" + update_pw, Toast.LENGTH_SHORT).show();
                }
            }
        });

        bUpdatePassword.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v2) {
                //do the thing
                update_pw = 1;
                String oldPassword = etOldPassword.getText().toString();
                String newPassword = etNewPassword.getText().toString();

                doTheThing(update_pw, newPassword, oldPassword);
                Toast.makeText(getContext(), "updating password...", Toast.LENGTH_SHORT).show();
            }
        });


            return view;
    }

    public void doTheThing(int bool_pw, String new_value, String password){

        Response.Listener<String> responseListener = new Response.Listener<String>(){


            @Override
            public void onResponse(String response) {

                AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
                builder.setMessage(response)
                        .setNegativeButton("Retry", null)
                        .create()
                        .show();

                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean( "success");
                    String error = jsonResponse.getString("error");

                    if (success){
                        Toast.makeText(getContext(), "Updated successfully", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        builder.setMessage(error)
                                .setNegativeButton("Okay", null)
                                .create()
                                .show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };
        Toast.makeText(getContext(), "user: "+username +"bool "+ bool_pw +"value: "+ new_value +"pw: "+ password, Toast.LENGTH_LONG).show();
        UpdateRequest updateRequest = new UpdateRequest(username, bool_pw, new_value, password, responseListener);
        RequestQueue queue = Volley.newRequestQueue(Objects.requireNonNull(getContext()));
        queue.add(updateRequest);
    }

    protected void sendEmail() {
        Log.i("Send email", "");

        String[] TO = {"javon@cradletechdesign.ca"};
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");


        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "I need to reset my password");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Help! I need to get my password reset ASAP!");

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            Log.i("Finished sending email", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getContext(),"There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

}
