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

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {
    ArrayList<Person> people;
    Person person;
    ActivityResultLauncher<Intent> activityResultLauncher;
    Cursor cursor;
    EditText id;
    EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        people = new ArrayList<>();
        id = (EditText) findViewById(R.id.main_ET_id);
        password = (EditText) findViewById(R.id.main_ET_password);

        try {
            String user = readLoginUser();
            String[] change_user = user.split(" ");
            if (change_user[0].equals("true")) {
                id.setText(change_user[1]);
                password.setText(change_user[2]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        init();
    }

    void init() {
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                Person returnedPerson = (Person) result.getData().getSerializableExtra("Person");
                people.add(returnedPerson);
            }
        });
    }

    public void mOnClick(View v) {
        CheckBox loginCheckBox = (CheckBox) findViewById(R.id.main_checkBox);
        switch (v.getId()) {
            case R.id.btn_login:
                final String userID = id.getText().toString();
                String userPassword = password.getText().toString();
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean isSuccess = jsonResponse.getBoolean("success");
                            if (isSuccess) {
                                String checkBox_str = loginCheckBox.isChecked() ? "true" : "false";
                                writeLoginUser(checkBox_str, userID, userPassword);
                                Intent intent_msg = new Intent(LoginActivity.this, MessageActivity.class);
                                intent_msg.putExtra("User", userID);
                                startActivity(intent_msg);
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "잘못 입력하였습니다", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                LoginRequest loginRequest = new LoginRequest(userID, userPassword, responseListener);
                RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
                requestQueue.add(loginRequest);
                break;
            case R.id.btn_signUp:
                id.setText("");
                password.setText("");
                Intent intent_register = new Intent(this, RegisterActivity.class);
                activityResultLauncher.launch(intent_register);
                break;
        }
    }

    private void writeLoginUser(String isChecked, String id, String password) {
        try {
            OutputStreamWriter oStreamWriter = new OutputStreamWriter(openFileOutput("loginUser.txt", Context.MODE_PRIVATE));
            oStreamWriter.write(isChecked + " " + id + " " + password);
            oStreamWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String readLoginUser() throws IOException {
        String fileContents = "";
        try {
            InputStream iStream = openFileInput("loginUser.txt");
            if (iStream != null) {
                InputStreamReader iStreamReader = new InputStreamReader(iStream);
                BufferedReader bufferedReader = new BufferedReader(iStreamReader);
                String temp = "";
                StringBuffer sBuffer = new StringBuffer();
                while ((temp = bufferedReader.readLine()) != null) {
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

    private void getUser(String userID) {

        new Thread() {
            @Override
            public void run() {

                BufferedReader br = null;
                URLConnection con = null;
                JSONObject result = null;
                try {
                    URL url = new URL("http://10.0.2.2:8080/MESSENGER/GetUser.php?userID=" + userID);
                    con = url.openConnection();
                    HttpURLConnection httpCon = (HttpURLConnection) con;
                    httpCon.setRequestMethod("GET");
                    httpCon.connect();
                    if (httpCon.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        result = new JSONObject(httpCon.getInputStream().toString());
                    }
                    if (result != null) {
                        if ((result.length() != 0)) {
                            String jid = result.getString("userID");
                            String jpassword = result.getString("userPassword");
                            String jname = result.getString("userName");
                            String jdepartment = result.getString("userDepartment");
                            String jphoto = result.getString("userPhoto");
                            person = new Person(jid, jpassword, jname, jdepartment, jphoto);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}