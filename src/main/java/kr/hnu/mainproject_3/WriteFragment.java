package kr.hnu.mainproject_3;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Date;

public class WriteFragment extends Fragment {

    DBHelper dbHelper;

    String ET_sender, ET_time;
    EditText ET_receiver, ET_title, ET_contents;
    Cursor cursor;
    Boolean isReply;
    String reply_msg;
    String[] replyInfo;

    SimpleDateFormat mFormat;
    MessageActivity activity;

    public WriteFragment(Bundle bundle) {
        replyInfo = bundle.getString("isReply").split("/");
        isReply = replyInfo[0].equals("true");
        if(replyInfo.length > 1) reply_msg = replyInfo[1];
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
        ViewGroup rootView = (ViewGroup) getLayoutInflater().inflate(R.layout.fragment_write, container, false);
        Button btn_cancel = rootView.findViewById(R.id.write_cancelBtn);
        Button btn_send = rootView.findViewById(R.id.write_sendBtn);
        mFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        dbHelper = new DBHelper(this.getContext());

        ET_sender = activity.getCurrentUser().getID();
        ET_receiver = rootView.findViewById(R.id.write_receiverET);
        ET_title = rootView.findViewById(R.id.write_titleET);
        ET_time = getTime();
        ET_contents = rootView.findViewById(R.id.write_contentsET);

        if(isReply) {
            cursor = dbHelper.getCursorMessageFromMsg(reply_msg);
            if(cursor.moveToNext()) {
                ET_receiver.setText(cursor.getString(cursor.getColumnIndexOrThrow("sender")));
            }
            ET_title.setText("[답장] ");
        }

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.onChangedFragment(2, null);
            }
        });

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cursor = dbHelper.getCursorMessageFromMsg(reply_msg);
                if(cursor.moveToNext()) {
                    dbHelper.updateMsg(reply_msg, "true");
                }
                cursor = dbHelper.getCursorUserFromPerson(ET_receiver.getText().toString());
                if(cursor.moveToNext()) {
                    Msg msg = new Msg(ET_sender,
                            ET_receiver.getText().toString(),
                            ET_title.getText().toString(),
                            ET_time,
                            ET_contents.getText().toString(),
                            "false");
                    dbHelper.addMsg(msg);
                    activity.onChangedFragment(2, null);
                } else {
                    Toast.makeText(getContext(), "받는 이가 유효한 사용자가 아닙니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rootView;
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
