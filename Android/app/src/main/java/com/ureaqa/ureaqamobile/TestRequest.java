package com.ureaqa.ureaqamobile;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Javon on 2018-03-16.
 */

public class TestRequest extends StringRequest {

    private static final String TEST_REQUEST_URL = "http://ureaqa.000webhostapp.com/Test.php";
    private Map<String, String> params;

    TestRequest(String username, boolean send_receive, String timestamp, int hydration_lvl, Response.Listener<String> listener){
        super(Request.Method.POST, TEST_REQUEST_URL, listener, null);

        params= new HashMap<>();
        params.put("username", username);
        params.put("send_receive", send_receive+"");
        params.put("timestamp", timestamp);
        params.put("hydration_lvl", hydration_lvl+"");

    }
    @Override
    public Map <String, String> getParams() {

        return params;
    }


}
