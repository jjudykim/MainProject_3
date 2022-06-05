package kr.hnu.mainproject_3;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class SettingFragment extends Fragment {
    Person user;

    TextView id;
    EditText password;
    EditText name;
    Spinner department;
    ArrayAdapter adapter;
    Uri selectedImageURI;
    Bitmap newImageBitmap;
    ImageButton btn_editImage;
    Button btn_edit;
    ActivityResultLauncher<Intent> activityResultLauncher;

    DBHelper dbHelper;
    MessageActivity activity;

    SettingFragment(Bundle bundle)
    {
        user = (Person) bundle.getSerializable("User");
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
        ViewGroup rootView = (ViewGroup) getLayoutInflater().inflate(R.layout.fragment_setting, container, false);
        btn_edit = rootView.findViewById(R.id.btn_updatePerson);
        dbHelper = new DBHelper(getContext());

        id = rootView.findViewById(R.id.text_id);
        password = rootView.findViewById(R.id.ET_newPassword);
        name = rootView.findViewById(R.id.ET_newName);
        department = rootView.findViewById(R.id.setting_spinner);
        adapter = ArrayAdapter.createFromResource(this.getContext(), R.array.department, android.R.layout.simple_spinner_dropdown_item);
        btn_editImage = rootView.findViewById(R.id.btn_editImage);
        newImageBitmap = StringBitmapUtility.getBitMapFromString(user.getPhoto());

        id.setText(user.getID());
        name.setText(user.getName());
        department.setAdapter(adapter);
        btn_editImage.setImageBitmap(newImageBitmap);
        setImage();

        btn_editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent imageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(imageIntent);
            }
        });

        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper.updatePerson(id.getText().toString(),
                        password.getText().toString(),
                        name.getText().toString(),
                        department.getSelectedItem().toString(),
                        StringBitmapUtility.getStringFromBitmap(newImageBitmap));
                Toast.makeText(getContext(), "데이터를 변경했습니다.", Toast.LENGTH_SHORT).show();
                Person temp = new Person(id.getText().toString(),
                                password.getText().toString(),
                                name.getText().toString(),
                                department.getSelectedItem().toString(),
                                StringBitmapUtility.getStringFromBitmap(newImageBitmap));
                Bundle bundleTemp = new Bundle();
                bundleTemp.putSerializable("User", temp);
                activity.onChangedFragment(2, bundleTemp);
            }
        });

        return rootView;
    }

   void setImage() {
       activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
           if(result.getResultCode() == Activity.RESULT_OK)
           {
               selectedImageURI = result.getData().getData();
               InputStream imageStream = null;
               try {
                   imageStream = getContext().getContentResolver().openInputStream(selectedImageURI);
               } catch (FileNotFoundException e) {
                   e.printStackTrace();
               }
               Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
               selectedImage = Bitmap.createScaledBitmap(selectedImage, 150, 150, true);
               newImageBitmap = selectedImage;
               btn_editImage.setImageBitmap(newImageBitmap);
           }
       });
   }
}
