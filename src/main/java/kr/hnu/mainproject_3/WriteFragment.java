package kr.hnu.mainproject_3;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WriteFragment extends Fragment {

    Bundle bundle;
    String currentUserID, bundle_str, foundUser;
    String ET_sender, ET_time;
    String sender;
    EditText ET_receiver, ET_title, ET_contents;
    String isReply_fromBundle;
    String reply_msg;
    String[] replyInfo;

    SimpleDateFormat mFormat;
    MessageActivity activity;

    public WriteFragment(Bundle bundle) {
        currentUserID = bundle.getString("User");
        bundle_str = bundle.getString("isReply");
        if(bundle.getString("SearchedUser") != null) foundUser = bundle.getString("SearchedUser");
        replyInfo = bundle_str.split("/");
        isReply_fromBundle = replyInfo[0];
        if(isReply_fromBundle.equals("true")) {
            sender = replyInfo[1];
            reply_msg = replyInfo[2];
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (MessageActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) getLayoutInflater().inflate(R.layout.fragment_write, container, false);

        return rootView;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageButton btn_findUser = view.findViewById(R.id.write_findUserBtn);
        Button btn_cancel = view.findViewById(R.id.write_cancelBtn);
        Button btn_send = view.findViewById(R.id.write_sendBtn);
        mFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        bundle = new Bundle();

        ET_sender = currentUserID;
        ET_receiver = view.findViewById(R.id.write_receiverET);
        ET_title = view.findViewById(R.id.write_titleET);
        ET_time = getTime();
        ET_contents = view.findViewById(R.id.write_contentsET);
        if(foundUser != null) {
            ET_receiver.setText(foundUser);
            ET_receiver.setEnabled(false);
        }

        if(isReply_fromBundle.equals("true")) {
            ET_receiver.setText(sender);
            ET_receiver.setEnabled(false);
            btn_findUser.setEnabled(false);
            ET_title.setText("[답장] ");
        }

        btn_findUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putString("User", currentUserID);
                bundle.putString("isReply", bundle_str);
                activity.onChangedFragment(6, bundle);
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putString("User", currentUserID);
                activity.onChangedFragment(2, bundle);
            }
        });

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String sender = ET_sender;
                String receiver = ET_receiver.getText().toString();
                String title = ET_title.getText().toString();
                String time = ET_time;
                String contents = ET_contents.getText().toString();
                String isReply = "false";

                if(receiver.equals("") || title.equals("") || contents.equals("")) {
                    Toast.makeText(getContext(), "빈 내용이 있습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(isReply_fromBundle.equals("true")) {
                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                        }
                    };

                    ReplyUpdateRequest replyUpdateRequest = new ReplyUpdateRequest(reply_msg, responseListener);
                    RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                    requestQueue.add(replyUpdateRequest);
                }

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean isSuccess = jsonResponse.getBoolean("success");
                            if (isSuccess) {
                                Toast.makeText(getContext(), "메세지를 보냈습니다.", Toast.LENGTH_SHORT).show();
                                Bundle bundle = new Bundle();
                                bundle.putString("User", currentUserID);
                                activity.onChangedFragment(2, bundle);
                            } else {
                                Log.e("WriteFragment", "WriteMessage failed");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                WriteMessageRequest writeMessageRequest = new WriteMessageRequest(sender, receiver, title, time, contents, isReply, responseListener);
                RequestQueue requestQueue = Volley.newRequestQueue(activity);
                requestQueue.add(writeMessageRequest);
            }
        });
    }

    public String getTime() {
        long now = System.currentTimeMillis();
        return mFormat.format(new Date(now));
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
