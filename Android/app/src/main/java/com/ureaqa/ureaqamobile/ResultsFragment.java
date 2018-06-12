package com.ureaqa.ureaqamobile;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import me.itangqi.waveloadingview.WaveLoadingView;


public class ResultsFragment extends Fragment  {
    public ResultsFragment() {
        // Required empty public constructor
    }

    int hydrationLvl=94, weight;
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
    String timestamp, username, name, last_name;
    boolean send_receive=true;
    WaveLoadingView waveHydrationLvl;
    TextView tvLastTest, tvTitle, tvDrinkAmount, tvDrinkMsg;
    Button bResults;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_results, container, false);


        waveHydrationLvl = view.findViewById(R.id.waveHydrationLvl);
        bResults = view.findViewById(R.id.bResults);
        tvLastTest = view.findViewById(R.id.tvLastTest);
        tvTitle = view.findViewById(R.id.tvTitle);
        tvDrinkAmount = view.findViewById(R.id.tvDrinkAmount);
        tvDrinkMsg = view.findViewById(R.id.tvDrinkMsg);

        Bundle bundle = getArguments();
        username = bundle.getString("username");
        name = bundle.getString("name");
        last_name = bundle.getString("last_name");
        weight = bundle.getInt("weight",-1);

        String title = "Results > " + name + " " +last_name;
        tvTitle.setText(title);

        waveHydrationLvl.setAnimDuration(3000);

        PerformTest();

        bResults.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                PerformTest();
            }
        });


        return view;
    }


    public void PerformTest(){

        Date date = new Date();
        timestamp = sdf.format(date.getTime());



        Response.Listener<String> responseListener = new Response.Listener<String>(){


            @Override
            public void onResponse(String response) {


                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean mathNeedsToBeDone = jsonResponse.getBoolean( "success");
                    timestamp = jsonResponse.getString( "timestamp");
                    hydrationLvl = jsonResponse.getInt( "hydration_lvl");


                    if (mathNeedsToBeDone){
                        doTheMath();
                    }

                    else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
                        builder.setMessage("Test Failed")
                                .setNegativeButton("Retry", null)
                                .create()
                                .show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    }
            }
        };

        TestRequest testRequest = new TestRequest(username, send_receive, timestamp, hydrationLvl, responseListener);
        RequestQueue queue = Volley.newRequestQueue(Objects.requireNonNull(getContext()));
        queue.add(testRequest);

    }

    public void doTheMath(){

        waveHydrationLvl.setCenterTitle(hydrationLvl+"");
        waveHydrationLvl.setProgressValue(hydrationLvl);
        String lastTest = getString(R.string.last_test) + " " + timestamp;
        tvLastTest.setText(lastTest);

        //would be good to check hydrationLvl is 0>100 here

        if (hydrationLvl > 0 && hydrationLvl < 101){
            double hyd_factor = 1;
            int drinkAmount = 2*weight*(100-hydrationLvl); //assuming hydration level is exaggerated 5X BML = 1000*weight*(100-hydrationLvl)/500
            int timeAmount;

            tvDrinkMsg.setText("Suggested Water Intake");

            if(hydrationLvl==100){
                //hyd_factor=5;
                drinkAmount=50;
                tvDrinkAmount.setText(drinkAmount+" mL");
                tvDrinkMsg.setText("You're good to go!");
            }

            else if (hydrationLvl >90){
                //basically hydrated
                //hyd_factor=4;
                drinkAmount=(int)(drinkAmount/hyd_factor);
                tvDrinkAmount.setText(drinkAmount+" mL");
                //time = 60 mins?
            }
            else if(hydrationLvl > 80){
                //might want to drink a bit
                //hyd_factor=3;
                drinkAmount=(int)(drinkAmount/hyd_factor);
                tvDrinkAmount.setText(drinkAmount+" mL");
                //time = 90 mins?

            }
            else if(hydrationLvl > 75){
                //hyd_factor=2.5;
                drinkAmount=(int)((drinkAmount/hyd_factor)/1000);
                timeAmount=drinkAmount;
                tvDrinkAmount.setText(drinkAmount+" L");
                //time is a thing

            }
            else if(hydrationLvl > 50){
                //hyd_factor=2;
                drinkAmount=(int)((drinkAmount/hyd_factor)/1000);
                timeAmount=drinkAmount-1;
                tvDrinkAmount.setText(drinkAmount+" L");
                //time is a thing
            }
            else{
                //see a doctor
                tvDrinkAmount.setText("You appear to be severely dehydrated. You should seek medical attention.");
            }


        }

    }


}

