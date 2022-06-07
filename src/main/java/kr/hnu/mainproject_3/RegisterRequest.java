package kr.hnu.mainproject_3;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RegisterRequest extends StringRequest {
    final static private String URL = "http://10.0.2.2:8080/Messenger/Register.php";
    private Map<String, String> parameters;

    public RegisterRequest(String userID, String userPassword, String userName, String userDepartment, String userPhoto, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("RegisterRequest", error.getMessage());
            }
        });
        parameters = new HashMap<>();
        parameters.put("userID", userID);
        parameters.put("userPassword", userPassword);
        parameters.put("userName", userName);
        parameters.put("userDepartment", userDepartment);
        parameters.put("userPhoto", userPhoto);
    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }
}
