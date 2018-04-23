package com.ureaqa.ureaqamobile;


import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.tsongkha.spinnerdatepicker.DatePicker;
import com.tsongkha.spinnerdatepicker.DatePickerDialog;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    SimpleDateFormat simpleDateFormat;

    ToggleButton toggleButton;
    EditText etEmail, etUsername, etPassword, etPassword2, etName, etLastName, etBirthday, etWeight;
    String email, username, password, password2, name, lastName, birthday;
    int weight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etEmail = findViewById(R.id.etEmail);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etPassword2 = findViewById(R.id.etPassword2);
        etName = findViewById(R.id.etName);
        etLastName = findViewById(R.id.etLastName);
        etBirthday = findViewById(R.id.etBirthday);
        etWeight = findViewById(R.id.etWeight);
        toggleButton = findViewById(R.id.toggleButton);

        etBirthday.setShowSoftInputOnFocus(false); //prevent keyboard from popping up
        final Button bRegister = findViewById(R.id.bRegister);

        simpleDateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.US);

        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fieldChecks();

                Response.Listener<String> responseListener = new Response.Listener<String>(){


                    @Override
                    public void onResponse(String response) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this); //returns the response and error message from php, should disappear if things go well
                        builder.setMessage(response)
                                .setNegativeButton("Retry", null)
                                .create()
                                .show();

                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean( "success");
                            String error = jsonResponse.getString( "error");


                            if (success){
                                Toast.makeText(RegisterActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                RegisterActivity.this.startActivity(intent);
                            }
                            else {
                                //AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this); //commented out because of the other alert dialogue
                                builder.setMessage("Register Failed \n"+error)
                                        .setNegativeButton("Retry", null)
                                        .create()
                                        .show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                };


                AlertDialog.Builder alert = new AlertDialog.Builder(RegisterActivity.this);
                if (password2.equals(password)) {

                    if (fieldChecks()) {

                        if (isValidEmail(email)){
                            RegisterRequest registerRequest = new RegisterRequest(email, name, lastName, username, birthday, weight, password, responseListener);
                            RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                            queue.add(registerRequest);
                        }
                        else {
                            alert.setMessage("Email is not valid. \n")
                                    .setNegativeButton("Got It", null)
                                    .create()
                                    .show();
                        }

                    }
                    else {
                        alert.setMessage("Required field missing. \n")
                                .setNegativeButton("Got It", null)
                                .create()
                                .show();
                    }

                }
                else {

                    alert.setMessage("Register Failed \n"+"Passwords don't match")
                            .setNegativeButton("Retry", null)
                            .create()
                            .show();
                }



            }
        });



        etBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int year = 1970;
                int month = 0;
                int day = 1;

                if (etBirthday.getText()!=null){
                    //set spinner date to the most recent pick, instead of 1970

                    try {

                        Date date = simpleDateFormat.parse(etBirthday.getText().toString());
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(date);
                        year  = cal.get(Calendar.YEAR);
                        month = cal.get(Calendar.MONTH);
                        day   = cal.get(Calendar.DAY_OF_MONTH);
                    } catch (ParseException e) {
                        e.printStackTrace();

                    }

                }

                showDate(year, month, day, R.style.DatePickerSpinner);

            }
        });


    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {

        Calendar calendar = new GregorianCalendar(year, month, day);
        month = month +1;
        birthday = month + "/" + day + "/" + year;
        etBirthday.setText(simpleDateFormat.format(calendar.getTime()));
    }

    void showDate(int year, int monthOfYear, int dayOfMonth, int spinnerTheme) {
        new SpinnerDatePickerDialogBuilder()
                .context(RegisterActivity.this)
                .callback(RegisterActivity.this)
                .spinnerTheme(spinnerTheme)
                .defaultDate(year, monthOfYear, dayOfMonth)
                .build()
                .show();
    }

    boolean fieldChecks(){
        boolean check = false;
        email = etEmail.getText().toString();
        username = etUsername.getText().toString();
        password = etPassword.getText().toString();
        password2 = etPassword2.getText().toString();
        name = etName.getText().toString();
        lastName = etLastName.getText().toString();
        weight = Integer.parseInt(etWeight.getText().toString());

        if (email !=null && email.length() > 0){
            if (username !=null && username.length() > 0){
                if (password !=null && password.length() > 0){
                    if (name !=null && name.length() > 0){
                        if (lastName !=null && lastName.length() > 0){
                            if (etBirthday.getText()!=null && birthday !=null) {
                                if (weight > 0 && etWeight.getText().toString().trim().length() > 0){
                                    check = true;
                                    if (toggleButton.isChecked()){
                                        //kg

                                    }
                                    else{
                                        //lbs
                                        weight = (int) Math.round(weight/2.2);

                                    }

                                    Toast.makeText(this, "Entered weight: "+ weight + "kg", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                }
            }
        }

        return check;

    }

    public static boolean isValidEmail(String target) {

        return target!=null && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

}
