package com.ureaqa.ureaqamobile;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class UploadRequest extends StringRequest {

    private static final String TEST_REQUEST_URL = "http://ureaqa.000webhostapp.com/Upload.php";
    private Map<String, String> params;

    UploadRequest(String username, String timestamp, int hydration_lvl, int hydration_feel, Response.Listener<String> listener){
        super(Method.POST, TEST_REQUEST_URL, listener, null);

        params= new HashMap<>();
        params.put("username", username);
        params.put("timestamp", timestamp);
        params.put("hydration_lvl", hydration_lvl+"");
        params.put("hydration_feel", hydration_feel+"");

    }
    @Override
    public Map <String, String> getParams() {

        return params;
    }
}
