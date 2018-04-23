package com.ureaqa.ureaqamobile;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import me.itangqi.waveloadingview.WaveLoadingView;


public class MainActivity extends AppCompatActivity {

    int hydrationLvl=94;
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
    String timestamp;
    WaveLoadingView waveHydrationLvl;
    TextView tvLastTest;
    TextView tvTitle;
    TextView tvDrinkAmount;
    Button bResults;
    boolean send_receive;
    String username;
    int weight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String last_name = intent.getStringExtra("last_name");
        int birthday = intent.getIntExtra("birthday", -1);
        username = intent.getStringExtra("username");
        weight = intent.getIntExtra("weight", -1);

        Bundle bundle = new Bundle();
        bundle.putString("username", username);
        bundle.putString("name", name);
        bundle.putInt("birthday", birthday);


        waveHydrationLvl = findViewById(R.id.waveHydrationLvl);
        bResults = findViewById(R.id.bResults);
        tvLastTest = findViewById(R.id.tvLastTest);
        tvTitle = findViewById(R.id.tvTitle);
        tvDrinkAmount = findViewById(R.id.tvDrinkAmount);

        String title = getString(R.string.results) + " > " + name + " " +last_name;
        tvTitle.setText(title);



        waveHydrationLvl.setAnimDuration(3000);
        send_receive = true;


        bResults.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                PerformTest();
            }
        });
    }

    public void PerformTest(){

        Date date = new Date();
        timestamp = sdf.format(date.getTime());



        Response.Listener<String> responseListener = new Response.Listener<String>(){


            @Override
            public void onResponse(String response) {

                /*
                AlertDialog.Builder error = new AlertDialog.Builder(MainActivity.this); //returns the response and error message from php, for debugging only
                error.setMessage(response)
                        .setNegativeButton("Great", null)
                        .create()
                        .show();
                */


                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean( "success");
                    timestamp = jsonResponse.getString( "timestamp");
                    hydrationLvl = jsonResponse.getInt( "hydration_lvl");


                    if (success){
                        //idk, update shit??

                        waveHydrationLvl.setCenterTitle(hydrationLvl+" %");
                        waveHydrationLvl.setProgressValue((int) hydrationLvl);
                        String lastTest = getString(R.string.last_test) + " " + timestamp;
                        tvLastTest.setText(lastTest);



                        //would be good to check hydrationLvl is 0>100 here
                        if (hydrationLvl > 0 && hydrationLvl < 101){
                            double hyd_factor;
                            int drinkAmount = 2*weight*(100-hydrationLvl); //assuming hydration level is exaggerated 5X BML = 1000*weight*(100-hydrationLvl)/500
                            int timeAmount;
                            Toast.makeText(getApplicationContext(), drinkAmount+"", Toast.LENGTH_SHORT).show();

                            if(hydrationLvl==100){
                                //hyd_factor=5;
                                //drinkAmount=(int)Math.round(drinkAmount/hyd_factor);
                                tvDrinkAmount.setText("You're good to go! 100% hydrated!");
                            }

                            else if (hydrationLvl >90){
                                //basically hydrated
                                hyd_factor=4;
                                drinkAmount=(int)(drinkAmount/hyd_factor);
                                tvDrinkAmount.setText("You should drink "+drinkAmount+"mL of water over the next hour.");
                            }
                            else if(hydrationLvl > 80){
                                //might want to drink a bit
                                hyd_factor=3;
                                drinkAmount=(int)(drinkAmount/hyd_factor);
                                tvDrinkAmount.setText("You should drink "+drinkAmount+"mL of water over the next ninety minutes.");

                            }
                            else if(hydrationLvl > 75){
                                hyd_factor=2.5;
                                drinkAmount=(int)((drinkAmount/hyd_factor)/1000);
                                timeAmount=drinkAmount;
                                tvDrinkAmount.setText("You should drink "+drinkAmount+"L of water over the next "+ timeAmount+" hours.");

                            }
                            else if(hydrationLvl > 50){
                                hyd_factor=2;
                                drinkAmount=(int)((drinkAmount/hyd_factor)/1000);
                                timeAmount=drinkAmount-1;
                                tvDrinkAmount.setText("You should drink "+drinkAmount+"L of water over the next "+ timeAmount+" hours.");
                            }
                            else{
                                //see a doctor
                                tvDrinkAmount.setText("You appear to be severely dehydrated. You should seek medical attention.");
                            }


                        }



                    }
                    else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                        builder.setMessage("Test Failed \n")
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
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(testRequest);


    }


}
