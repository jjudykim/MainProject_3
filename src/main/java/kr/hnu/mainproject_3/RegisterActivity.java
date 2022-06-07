package kr.hnu.mainproject_3;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class RegisterActivity extends AppCompatActivity {
    private Person person;
    private boolean isIdChecked;
    private Cursor cursor;
    private Spinner spinner;
    private ArrayAdapter adapter;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private Bitmap imgBitmap;
    private EditText et_id, et_password, et_name;
    private Spinner sp_department;
    private ImageButton btn_img;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        spinner = findViewById(R.id.sub_spinner);
        adapter = ArrayAdapter.createFromResource(this, R.array.department, android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        isIdChecked = false;

        et_id = (EditText) findViewById(R.id.ET_id);
        et_password = (EditText) findViewById(R.id.ET_password);
        et_name = (EditText) findViewById(R.id.ET_name);
        sp_department = (Spinner) findViewById(R.id.sub_spinner);
        btn_img = (ImageButton) findViewById(R.id.btn_image);

        imageInit();
    }

    void imageInit() {
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                Uri selectedImageURI = result.getData().getData();
                InputStream imageStream = null;
                try {
                    imageStream = getContentResolver().openInputStream(selectedImageURI);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                selectedImage = Bitmap.createScaledBitmap(selectedImage, 150, 150, true);
                ImageButton img_btn = (ImageButton) findViewById(R.id.btn_image);
                img_btn.setImageBitmap(selectedImage);
                imgBitmap = selectedImage;
            }
        });
    }

    public void onClickImage(View v) {
        Intent imageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activityResultLauncher.launch(imageIntent);
    }

    public void onClickCheck(View v) {
        String userID = et_id.getText().toString();
        if (userID.equals("")) {
            Toast.makeText(RegisterActivity.this, "아이디를 입력하세요", Toast.LENGTH_SHORT).show();
            return;
        }
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean isSuccess = jsonResponse.getBoolean("success");
                    if (isSuccess) {
                        Toast.makeText(RegisterActivity.this, "Good ID", Toast.LENGTH_SHORT).show();
                        et_id.setEnabled(false);
                        isIdChecked = true;
                    } else {
                        Toast.makeText(RegisterActivity.this, "중복된 ID 입니다.", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        ValidateRequest validateRequest = new ValidateRequest(userID, responseListener);
        RequestQueue requestQueue = Volley.newRequestQueue(RegisterActivity.this);
        requestQueue.add(validateRequest);
    }

    public void onClickRegister(View v) {
        if (!isIdChecked) {
            Toast.makeText(RegisterActivity.this, "아이디 체크를 진행해주세요", Toast.LENGTH_SHORT).show();
        }
        String userID = et_id.getText().toString();
        String userPassword = et_password.getText().toString();
        String userName = et_name.getText().toString();
        String userDepartment = sp_department.getSelectedItem().toString();
        String userPhoto = StringBitmapUtility.getStringFromBitmap(imgBitmap);
        if (userID.equals("") | userPassword.equals("") | userName.equals("") | userDepartment.equals("") | userPhoto.equals("")) {
            Toast.makeText(RegisterActivity.this, "입력하지 않은 정보가 있습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
        }
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean isSuccess = jsonResponse.getBoolean("success");
                    if (isSuccess) {
                        Toast.makeText(RegisterActivity.this, "계정 생성 성공!", Toast.LENGTH_SHORT).show();
                        Intent registerIntent = new Intent();
                        person = new Person(userID, userPassword, userName, userDepartment, userPhoto);
                        registerIntent.putExtra("Person", person);
                        setResult(RESULT_OK, registerIntent);
                        finish();
                    } else {
                        Log.e("RegisterActivity", "register failed");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RegisterRequest registerRequest = new RegisterRequest(userID, userPassword, userName, userDepartment, userPhoto, responseListener);
        RequestQueue requestQueue = Volley.newRequestQueue(RegisterActivity.this);
        requestQueue.add(registerRequest);
    }
}