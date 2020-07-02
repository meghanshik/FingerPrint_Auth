package com.meghanshi.fingerprintlogin;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.Map;

public class AddDate extends StringRequest {


    private static final String REGISTER_REQUEST_URL = "http://192.168.1.13/Webs/Insert.php";
    private Map<String, String> params;

    public AddDate(String date, Response.Listener<String> listener) {
        super(Method.POST, REGISTER_REQUEST_URL, listener, null);

        params.put("date", date);
    }

    public Map<String, String> getParams() {
        return params;
    }
}
