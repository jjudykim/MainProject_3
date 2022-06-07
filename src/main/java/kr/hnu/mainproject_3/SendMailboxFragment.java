package kr.hnu.mainproject_3;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class SendMailboxFragment extends Fragment {
    Bundle bundle;

    ArrayList<RelativeLayout> arrayList_rel;
    TextView rel_sender, rel_title, rel_time;
    String currentUserID;
    LinearLayout lin;
    MessageActivity activity;

    public SendMailboxFragment(Bundle bundle) {
        currentUserID = bundle.getString("User");
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (MessageActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) getLayoutInflater().inflate(R.layout.fragment_sendmailbox, container, false);

        return rootView;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lin = view.findViewById(R.id.send_linear);
        arrayList_rel = new ArrayList<>();
        bundle = new Bundle();

        new BackgroundTask().execute();
    }

    public void AddMsgList(String sender, String title, String time, String reply) {
        RelativeLayout rel = (RelativeLayout) View.inflate(getContext(), R.layout.newmessage, null);
        rel_sender = (TextView) rel.findViewById(R.id.text_name);
        rel_sender.setText(sender);
        rel_title = (TextView) rel.findViewById(R.id.text_title);
        rel_title.setText(title);
        rel_time = (TextView) rel.findViewById(R.id.text_time);
        rel_time.setText(time);
        arrayList_rel.add(rel);

        lin.addView(rel);
    }

    void setRelOnClickListener() {
        for(int i = 0; i < arrayList_rel.size(); i++) {
            int temp = i;
            arrayList_rel.get(temp).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView tempTv = (TextView) arrayList_rel.get(temp).findViewById(R.id.text_time);
                    bundle.putString("msgTime_forWho", tempTv.getText().toString() + "/sender");
                    bundle.putString("User", currentUserID);
                    activity.onChangedFragment(5, bundle);
                }
            });
        }
    }


    class BackgroundTask extends AsyncTask<Void, Void, String> {
        String target;

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(target);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String temp;
                StringBuilder stringBuilder = new StringBuilder();
                while ((temp = bufferedReader.readLine()) != null) {
                    stringBuilder.append(temp + "\n");
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return stringBuilder.toString().trim();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            try {
                target = "http://10.0.2.2:8080/MESSENGER/MessageSendList.php?sender=" + URLEncoder.encode(currentUserID, "UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            if (s == null || getContext() == null)
                return;
            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray jsonArray = jsonObject.getJSONArray("response");
                String sender, receiver, title, time, contents, isReply;
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject row = jsonArray.getJSONObject(i);
                    sender = row.getString("sender");
                    receiver = row.getString("receiver");
                    title = row.getString("title");
                    time = row.getString("time");
                    contents = row.getString("contents");
                    isReply = row.getString("isreply");
                    Msg msg = new Msg(sender, receiver, title, time, contents, isReply);
                    AddMsgList(sender, title, time, isReply);
                }
                setRelOnClickListener();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
