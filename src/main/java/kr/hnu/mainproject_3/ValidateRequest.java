package kr.hnu.mainproject_3;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class ValidateRequest extends StringRequest {
    final static private String URL = "http://10.0.2.2:8080/Messenger/ValidateUser.php";
    private Map<String, String> parameters;
    public ValidateRequest(String userID, Response.Listener<String> listener) {
        super(Request.Method.POST, URL, listener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ValidateRequest", error.getMessage());
            }
        });
        parameters = new HashMap<>();
        parameters.put("userID", userID);
    }
    @Override
    public Map<String, String> getParams() {
        return parameters;
    }
}
