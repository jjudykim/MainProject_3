package kr.hnu.mainproject_3;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

public class GetUserByDepRequest extends StringRequest {

    static private String URL = "http://10.0.2.2:8080/Messenger/GetUserByDep.php?department=";
    //private Map<String, String> parameters;

    public GetUserByDepRequest(String department, Response.Listener<String> listener){
        super(Method.GET, URL + department, listener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("GetUserRequest", error.getMessage());
            }
        });
    }
}