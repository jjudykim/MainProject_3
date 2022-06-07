package kr.hnu.mainproject_3;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class WriteMessageRequest extends StringRequest {
    final static private String URL = "http://10.0.2.2:8080/Messenger/WriteMessage.php";
    private Map<String, String> parameters;

    public WriteMessageRequest(String sender, String receiver, String title, String time, String contents, String isReply, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("WriteMessageRequest", error.getMessage());
            }
        });
        parameters = new HashMap<>();
        parameters.put("sender", sender);
        parameters.put("receiver", receiver);
        parameters.put("title", title);
        parameters.put("time", time);
        parameters.put("contents", contents);
        parameters.put("isReply", isReply);
    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }
}
