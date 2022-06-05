package kr.hnu.mainproject_3;

import android.content.Context;
import android.database.Cursor;
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

public class SendMailboxFragment extends Fragment {
    DBHelper dbHelper;
    Context context;
    Cursor cursor;
    Bundle bundle;

    String text_sender, text_title, text_time;
    TextView rel_sender, rel_title, rel_time;
    Person currentUser;
    LinearLayout lin;
    MessageActivity activity;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (MessageActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) getLayoutInflater().inflate(R.layout.fragment_sendmailbox, container, false);
        currentUser = activity.getCurrentUser();
        lin = rootView.findViewById(R.id.send_linear);

        context = this.getContext();
        bundle = new Bundle();
        dbHelper = new DBHelper(context);
        cursor = dbHelper.getCursorSendMessageFromMsg(currentUser);

        while(cursor.moveToNext()) {
            text_sender = cursor.getString(cursor.getColumnIndexOrThrow("sender"));
            text_title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
            text_time = cursor.getString(cursor.getColumnIndexOrThrow("time"));
            AddMsgList(text_sender, text_title, text_time);
        }

        return rootView;
    }

    public void AddMsgList(String sender, String title, String time) {
        RelativeLayout rel = (RelativeLayout) View.inflate(context, R.layout.newmessage, null);
        rel_sender = (TextView) rel.findViewById(R.id.text_name);
        rel_sender.setText(sender);
        rel_title = (TextView) rel.findViewById(R.id.text_title);
        rel_title.setText(title);
        rel_time = (TextView) rel.findViewById(R.id.text_time);
        rel_time.setText(time);

        rel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putString("msgTime_forWho", rel_time.getText().toString() + "/sender");
                Fragment fragment = new ReadFragment(bundle);
                activity.toolbar.setTitleTextColor(0xFFFFFFFF);
                activity.toolbar.setTitle("메시지 읽기");
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
            }
        });

        lin.addView(rel);
    }
}
