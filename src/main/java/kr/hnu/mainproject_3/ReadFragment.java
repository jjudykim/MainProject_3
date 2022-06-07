package kr.hnu.mainproject_3;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ReadFragment extends Fragment {

    String bundleMsg;
    String msg_time;
    String currentUserID;
    boolean isForSender;
    TextView sender, receiver, title, time, contents;
    Button back, delete, reply;
    String sSender, sReceiver, sTitle, sTime, sContents, sIsReply;
    MessageActivity activity;

    public ReadFragment(Bundle bundle)
    {
        currentUserID = bundle.getString("User");
        bundleMsg = bundle.getString("msgTime_forWho");
        String[] msg_split = bundleMsg.split("/");
        if(msg_split[1].equals("sender")) {
            isForSender = true;
        } else if(msg_split[1].equals("receiver")) {
            isForSender = false;
        }
        msg_time = msg_split[0];
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (MessageActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) getLayoutInflater().inflate(R.layout.fragment_read, container, false);

        return rootView;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sender = view.findViewById(R.id.read_senderTV);
        receiver = view.findViewById(R.id.read_receiver_TV);
        title = view.findViewById(R.id.read_titleTV);
        time = view.findViewById(R.id.read_time_TV);
        contents = view.findViewById(R.id.read_contentsTV);

        back = view.findViewById(R.id.read_btn_back);
        delete = view.findViewById(R.id.read_btn_delete);
        reply = view.findViewById(R.id.read_btn_reply);

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray jsonArray = jsonResponse.getJSONArray("response");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject row = jsonArray.getJSONObject(i);
                        sSender = row.getString("sender");
                        sReceiver = row.getString("receiver");
                        sTitle = row.getString("title");
                        sTime = row.getString("time");
                        sContents = row.getString("contents");
                        sIsReply = row.getString("isreply");
                    }
                    sender.setText(sSender);
                    receiver.setText(sReceiver);
                    title.setText(sTitle);
                    time.setText(sTime);
                    contents.setText(sContents);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        GetMessageRequest getMessageRequest = new GetMessageRequest(msg_time, responseListener);
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(getMessageRequest);

        if(isForSender) reply.setVisibility(View.INVISIBLE);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("User", currentUserID);
                if(isForSender) activity.onChangedFragment(3, bundle);
                else activity.onChangedFragment(2, bundle);
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean isSuccess = jsonResponse.getBoolean("success");
                            if (isSuccess) {
                                Toast.makeText(getContext(), "메시지를 삭제했습니다.", Toast.LENGTH_SHORT).show();
                                Bundle bundle = new Bundle();
                                bundle.putString("User", currentUserID);
                                if(isForSender) activity.onChangedFragment(3, bundle);
                                else activity.onChangedFragment(2, bundle);
                            } else {
                                Log.e("ReadFragment", "DeleteMessage failed");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                DeleteMessageRequest DeleteMessageRequest = new DeleteMessageRequest(sTime, responseListener);
                RequestQueue requestQueue = Volley.newRequestQueue(activity);
                requestQueue.add(DeleteMessageRequest);
            }
        });

        reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("isReply", "true/" + sSender + "/" + msg_time);
                bundle.putString("User", currentUserID);
                activity.onChangedFragment(1, bundle);
            }
        });
    }

}
