package kr.hnu.mainproject_3;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    DBHelper dbHelper;
    ArrayList<Person> people;
    ActivityResultLauncher<Intent> activityResultLauncher;
    Cursor cursor;
    EditText id;
    EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new DBHelper(this);
        people = new ArrayList<>();

        id = (EditText) findViewById(R.id.main_ET_id);
        password = (EditText) findViewById(R.id.main_ET_password);

        try {
            String user = readLoginUser();
            String[] change_user = user.split(" ");
            if(change_user[0].equals("true")) {
                id.setText(change_user[1]);
                password.setText(change_user[2]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        init();
    }

    void init() {
        activityResultLauncher = registerForActivityResult( new ActivityResultContracts.StartActivityForResult(), result -> {
            if(result.getResultCode() == RESULT_OK) {
                Person returnedPerson = (Person) result.getData().getSerializableExtra("Person");
                people.add(returnedPerson);
                dbHelper.addPerson(returnedPerson);
            }
        });
    }

    public void mOnClick(View v) {
        CheckBox loginCheckBox = (CheckBox) findViewById(R.id.main_checkBox) ;
        switch(v.getId()) {
            case R.id.btn_login:
                Intent intent_msg = new Intent(this, MessageActivity.class);
                cursor = dbHelper.getCursorUserFromPerson(id.getText().toString(), password.getText().toString());
                if (cursor.moveToNext()){
                    Person temp = new Person(cursor.getString(cursor.getColumnIndexOrThrow("id")),
                            cursor.getString(cursor.getColumnIndexOrThrow("password")),
                            cursor.getString(cursor.getColumnIndexOrThrow("name")),
                            cursor.getString(cursor.getColumnIndexOrThrow("department")),
                            cursor.getString(cursor.getColumnIndexOrThrow("photo")));
                    String checkBox_str = loginCheckBox.isChecked() ? "true" : "false";
                    writeLoginUser(checkBox_str, id.getText().toString(), password.getText().toString());
                    intent_msg.putExtra("User", temp);
                    startActivity(intent_msg);
                } else {
                    Toast.makeText(MainActivity.this, "잘못 입력하였습니다", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_signUp:
                id.setText("");
                password.setText("");
                Intent intent_sub = new Intent(this, SubActivity.class);
                activityResultLauncher.launch(intent_sub);
                break;
        }
    }

    private void writeLoginUser(String isChecked, String id, String password) {
        try{
            OutputStreamWriter oStreamWriter = new OutputStreamWriter(openFileOutput("loginUser.txt", Context.MODE_PRIVATE));
            oStreamWriter.write(isChecked + " " + id + " " + password);
            oStreamWriter.close();
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private String readLoginUser() throws IOException{
        String fileContents = "";
        try {
            InputStream iStream = openFileInput("loginUser.txt");
            if(iStream != null) {
                InputStreamReader iStreamReader = new InputStreamReader(iStream);
                BufferedReader bufferedReader = new BufferedReader(iStreamReader);
                String temp = "";
                StringBuffer sBuffer = new StringBuffer();
                while((temp = bufferedReader.readLine()) != null) {
                    sBuffer.append(temp);
                }
                iStream.close();
                fileContents = sBuffer.toString();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return fileContents;
    }
}