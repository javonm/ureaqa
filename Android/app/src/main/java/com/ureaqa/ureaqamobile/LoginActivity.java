package com.ureaqa.ureaqamobile;


import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText etUsername = findViewById(R.id.etUsername);
        final EditText etPassword = findViewById(R.id.etPassword);
        final Button bLogin = findViewById(R.id.bLogin);
        final TextView tvRegister = findViewById(R.id.tvRegister);

        tvRegister.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(registerIntent);


            }
        });

        bLogin.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View v) {
                                          final String username = etUsername.getText().toString();
                                          final String password = etPassword.getText().toString();

                                          Response.Listener<String> responseListener = new Response.Listener<String>(){


                                              @Override
                                              public void onResponse(String response) {

                                                  AlertDialog.Builder error = new AlertDialog.Builder(LoginActivity.this); //returns the response and error message from php, should disappear if things go well
                                                  error.setMessage(response)
                                                          .setNegativeButton("Retry", null)
                                                          .create()
                                                          .show();

                                                  try {
                                                      JSONObject jsonResponse = new JSONObject(response);
                                                      boolean success = jsonResponse.getBoolean( "success");


                                                      if (success){
                                                          String email = jsonResponse.getString("email");
                                                          String name = jsonResponse.getString("name");
                                                          String last_name = jsonResponse.getString("last_name");
                                                          String birthday = jsonResponse.getString("birthday");
                                                          int weight = jsonResponse.getInt("weight");

                                                          Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                          intent.putExtra("email", email);
                                                          intent.putExtra("name", name);
                                                          intent.putExtra("last_name", last_name);
                                                          intent.putExtra("username", username);
                                                          intent.putExtra("birthday", birthday);
                                                          intent.putExtra("weight", weight );

                                                          LoginActivity.this.startActivity(intent);
                                                      }
                                                      else {
                                                          AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                                          builder.setMessage("Login Failed")
                                                                  .setNegativeButton("Retry", null)
                                                                  .create()
                                                                  .show();
                                                      }

                                                  } catch (JSONException e) {
                                                      e.printStackTrace();
                                                  }

                                              }
                                          };



                                          LoginRequest loginRequest = new LoginRequest(username, password, responseListener);
                                          RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                                          queue.add(loginRequest);


                                      }
                                  }
        );

    }
}

