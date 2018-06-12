package com.ureaqa.ureaqamobile;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class UpdateRequest extends StringRequest {

    private static final String REQUEST_URL = "http://ureaqa.000webhostapp.com/Update.php";
    private Map<String, String> params;

    UpdateRequest(String username, int bool_pw, String new_value, String password, Response.Listener<String> listener){
        super(Request.Method.POST, REQUEST_URL, listener, null);

        params= new HashMap<>();
        params.put("username", username);
        params.put("bool_pw", bool_pw+"");
        params.put("new_value", new_value);
        params.put("password", password);

    }
    @Override
    public Map <String, String> getParams() {

        return params;
    }
}

