package com.ureaqa.ureaqamobile;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class TestFragment extends Fragment {

    public TestFragment() {
        // Required empty public constructor
    }

    int hydration_lvl, weight, hydration_feel;
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
    String timestamp, username, name, last_name;

    SeekBar seekHydLvlFeel;
    EditText etSpecGrav;
    TextView tvTimestamp, tvHydrationLvl, tvUsername, tvTitle, tvHydrationLvlGuess, tvHydrationPrompt, tvHydrationFeel;
    Button bTime, bUrine, bSendData;
    Boolean testState;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_test, container, false);

        seekHydLvlFeel = view.findViewById(R.id.sbGuessLvl);
        tvHydrationLvlGuess = view.findViewById(R.id.tvHydrationLvlGuess);
        tvHydrationFeel = view.findViewById(R.id.tvHydrationFeel);
        tvHydrationPrompt = view.findViewById(R.id.tvHydrationPrompt);

        tvTitle = view.findViewById(R.id.tvTitle);
        tvUsername = view.findViewById(R.id.tvUsername);

        etSpecGrav = view.findViewById(R.id.etSpecGrav);
        tvTimestamp = view.findViewById(R.id.tvTimestamp);
        tvHydrationLvl = view.findViewById(R.id.tvHydrationLvl);
        bTime = view.findViewById(R.id.bTime);
        bUrine = view.findViewById(R.id.bUrine);

        bSendData = view.findViewById(R.id.bSendData);
        testState = false; //not the real test

        Bundle bundle = getArguments();
        assert bundle != null;
        username = bundle.getString("username");
        name = bundle.getString("name");
        last_name = bundle.getString("last_name");

        //String title = "New Test > " + name + " " +last_name;
        //tvTitle.setText(title);
        tvUsername.setText("Username: "+username);


        final Calendar cal = Calendar.getInstance();
        timestamp = sdf.format(cal.getTime());
        tvTimestamp.setText(timestamp);


        bUrine.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sg = etSpecGrav.getText().toString();
                Double specGrav = Double.valueOf(etSpecGrav.getText().toString());

                hydration_lvl = 100 - (int) (5*(213.874230430356*specGrav - 214.870206683645)); //100 - 5x bml
                tvHydrationLvl.setText(hydration_lvl+" % ");
            }
        });

        bSendData.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (testState){
                    Toast.makeText(getContext(), "Uploading...", Toast.LENGTH_SHORT).show();
                    UpdateDatabase();
                }
                else {
                    testState = true;

                    //set new things visible
                    etSpecGrav.setVisibility(View.VISIBLE);
                    tvTimestamp.setVisibility(View.VISIBLE);
                    tvHydrationLvl.setVisibility(View.VISIBLE);
                    bTime.setVisibility(View.VISIBLE);
                    bUrine.setVisibility(View.VISIBLE);

                    //set the old things gone
                    tvHydrationLvlGuess.setVisibility(View.GONE);
                    tvHydrationFeel.setVisibility(View.GONE);
                    tvHydrationPrompt.setVisibility(View.GONE);
                    seekHydLvlFeel.setVisibility(View.GONE);

                    bSendData.setText("Update Database");
                    Toast.makeText(getContext(), "Dip urine stick & read results after 45s", Toast.LENGTH_LONG).show();

                }

            }
        });

        bTime.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                Calendar newCal = Calendar.getInstance();
                timestamp = sdf.format(newCal.getTime());
                tvTimestamp.setText(timestamp);
            }
        });


        seekHydLvlFeel.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser){
                    if (progress<30){
                        hydration_feel = progress * 5 / 3;
                    }
                    else{
                        hydration_feel = progress + 20;
                    }
                    tvHydrationLvlGuess.setText(hydration_feel+"");
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //Toast.makeText(getContext(), dht[0]+" BOII", Toast.LENGTH_SHORT).show();

                String hydPromptString;
                if (hydration_feel > 100){
                    hydPromptString = "I feel bloated and over-hydrated";

                }
                else if (hydration_feel == 100){
                    hydPromptString = "I'm optimally hydrated! Ready to race!";
                }
                else if (hydration_feel > 90){
                    hydPromptString = "I feel great! Ready to perform at my peak.";
                }
                else if (hydration_feel > 80){
                    hydPromptString = "I'm mildly dehydrated. I would expect small performance losses.";
                }
                else if (hydration_feel > 70){
                    hydPromptString = "I'm dehydrated. I do not feel ready to perform.";
                }
                else if (hydration_feel > 50){
                    hydPromptString = "I'm not feeling well. I may need to seek medical attention";
                }
                else{
                    hydPromptString = "I'm actively dying of dehydration.";
                }
                tvHydrationPrompt.setText(hydPromptString);

            }
        });


        return view;
    }



    public void UpdateDatabase(){

        Response.Listener<String> responseListener = new Response.Listener<String>(){



            @Override
            public void onResponse(String response) {
                //use this for debugging
                //Toast.makeText(getContext(), response, Toast.LENGTH_LONG).show();

                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean( "success");


                    if (success){
                        //idk, update shit??
                        Toast.makeText(getContext(), "Success!! Go Check Your Results", Toast.LENGTH_SHORT/2).show();

                    }
                    else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setMessage("Test Failed \n")
                                .setNegativeButton("Retry", null)
                                .create()
                                .show();
                    }

                } catch (JSONException e1) {
                    e1.printStackTrace();
                }


            }

        };

        UploadRequest uploadRequest = new UploadRequest(username,  timestamp, hydration_lvl, hydration_feel, responseListener);
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(uploadRequest);
    }
}
