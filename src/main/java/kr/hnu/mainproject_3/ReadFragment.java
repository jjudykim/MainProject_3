package kr.hnu.mainproject_3;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ReadFragment extends Fragment {

    DBHelper dbHelper;
    Context context;
    Cursor cursor;
    String bundleMsg;
    String msg_time;
    boolean isForSender;
    TextView sender, receiver, title, time, contents;
    Button back, delete, reply;
    MessageActivity activity;

    public ReadFragment(Bundle bundle)
    {
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
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) getLayoutInflater().inflate(R.layout.fragment_read, container, false);

        context = this.getContext();
        dbHelper = new DBHelper(context);
        cursor = dbHelper.getCursorMessageFromMsg(msg_time);

        sender = rootView.findViewById(R.id.read_senderTV);
        receiver = rootView.findViewById(R.id.read_receiver_TV);
        title = rootView.findViewById(R.id.read_titleTV);
        time = rootView.findViewById(R.id.read_time_TV);
        contents = rootView.findViewById(R.id.read_contentsTV);
        back = rootView.findViewById(R.id.read_btn_back);
        delete = rootView.findViewById(R.id.read_btn_delete);
        reply = rootView.findViewById(R.id.read_btn_reply);

        if(isForSender) reply.setVisibility(View.INVISIBLE);

        if(cursor.moveToNext()) {
            sender.setText(cursor.getString(cursor.getColumnIndexOrThrow("sender")));
            receiver.setText(cursor.getString(cursor.getColumnIndexOrThrow("receiver")));
            title.setText(cursor.getString(cursor.getColumnIndexOrThrow("title")));
            time.setText(cursor.getString(cursor.getColumnIndexOrThrow("time")));
            contents.setText(cursor.getString(cursor.getColumnIndexOrThrow("contents")));
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isForSender) activity.onChangedFragment(3, null);
                else activity.onChangedFragment(2, null);
            }
        });

        reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("isReply", "true/" + msg_time);
                activity.onChangedFragment(1, bundle);
            }
        });

        return rootView;
    }

}
