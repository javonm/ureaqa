package com.ureaqa.ureaqamobile;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Javon Mayhew on 2018-03-02.
 *
 */

public class RegisterRequest extends StringRequest {


    private static final String REGISTER_REQUEST_URL = "http://ureaqa.000webhostapp.com/Register.php";
    private Map<String, String> params;

    RegisterRequest(String email, String name, String last_name, String username, String birthday, int weight, String password, Response.Listener<String> listener){
        super(Method.POST, REGISTER_REQUEST_URL, listener, null);

        params= new HashMap<>();
        params.put("email", email);
        params.put("name", name);
        params.put("last_name", last_name);
        params.put("username", username);
        params.put("birthday", birthday);
        params.put("weight", weight+"");
        params.put("password", password);

    }
    @Override
    public Map <String, String> getParams() {

        return params;
    }

}
