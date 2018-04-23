package com.ureaqa.ureaqamobile;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Javon on 2018-03-09.
 */

public class LoginRequest extends StringRequest {

    private static final String LOGIN_REQUEST_URL = "http://ureaqa.000webhostapp.com/Login.php";
    private Map<String, String> params;

    LoginRequest(String username, String password, Response.Listener<String> listener){
        super(Request.Method.POST, LOGIN_REQUEST_URL, listener, null);

        params= new HashMap<>();
        params.put("username", username);
        params.put("password", password);

    }
    @Override
    public Map <String, String> getParams() {

        return params;
    }
}
