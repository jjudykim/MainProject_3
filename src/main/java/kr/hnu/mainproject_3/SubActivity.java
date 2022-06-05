package kr.hnu.mainproject_3;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Array;

public class SubActivity extends AppCompatActivity {
    private DBHelper dbHelper;
    private Person person;
    private boolean isIdChecked;
    private Cursor cursor;
    private Spinner spinner;
    private ArrayAdapter adapter;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private Bitmap imgBitmap;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);

        spinner = findViewById(R.id.sub_spinner);
        adapter = ArrayAdapter.createFromResource(this, R.array.department, android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        dbHelper = new DBHelper(this);
        isIdChecked = false;

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

    public void mOnClick(View v) {
        EditText et_id = (EditText) findViewById(R.id.ET_id);
        EditText et_password = (EditText) findViewById(R.id.ET_password);
        EditText et_name = (EditText) findViewById(R.id.ET_name);
        Spinner sp_department = (Spinner) findViewById(R.id.sub_spinner);
        ImageButton btn_img = (ImageButton) findViewById(R.id.btn_image);

        switch (v.getId()) {
            case R.id.btn_image:
                Intent imageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(imageIntent);
                break;
            case R.id.btn_check:
                if (et_id.getText().length() != 0) {
                    cursor = dbHelper.getCursorUserFromPerson(et_id.getText().toString());
                    if (cursor.moveToNext()) {
                        Toast.makeText(SubActivity.this, "중복된 ID 입니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SubActivity.this, "Good ID", Toast.LENGTH_SHORT).show();
                        isIdChecked = true;
                    }
                } else {
                    Toast.makeText(SubActivity.this, "아이디를 입력하세요", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.btn_signIn:
                if (!isIdChecked) {
                    Toast.makeText(SubActivity.this, "아이디 체크를 진행해주세요", Toast.LENGTH_SHORT).show();
                } else if (et_password.getText().length() == 0) {
                    Toast.makeText(SubActivity.this, "비밀번호를 입력하세요", Toast.LENGTH_SHORT).show();
                } else if (et_name.getText().length() == 0) {
                    Toast.makeText(SubActivity.this, "이름을 입력하세요", Toast.LENGTH_SHORT).show();
                } else {
                    person = new Person(et_id.getText().toString(),
                            et_password.getText().toString(),
                            et_name.getText().toString(),
                            sp_department.getSelectedItem().toString(),
                            StringBitmapUtility.getStringFromBitmap(imgBitmap));

                    Intent signInIntent = new Intent();
                    signInIntent.putExtra("Person", person);
                    setResult(RESULT_OK, signInIntent);
                    finish();
                }
                break;
        }
    }


}
