package kr.hnu.mainproject_3;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
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

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class SettingFragment extends Fragment {
    String currentUserID;
    Person currentUser;

    TextView id;
    EditText password;
    EditText name;
    Spinner department;
    ArrayAdapter adapter;
    Uri selectedImageURI;
    Bitmap newImageBitmap;
    ImageButton btn_editImage;
    Button btn_edit;
    String[] array_resource;
    ActivityResultLauncher<Intent> activityResultLauncher;
    MessageActivity activity;

    SettingFragment(Bundle bundle)
    {
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
        ViewGroup rootView = (ViewGroup) getLayoutInflater().inflate(R.layout.fragment_setting, container, false);

        return rootView;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        id = view.findViewById(R.id.text_id);
        password = view.findViewById(R.id.ET_newPassword);
        name = view.findViewById(R.id.ET_newName);
        department = view.findViewById(R.id.setting_spinner);
        adapter = ArrayAdapter.createFromResource(this.getContext(), R.array.department, android.R.layout.simple_spinner_dropdown_item);
        department.setAdapter(adapter);
        btn_editImage = view.findViewById(R.id.btn_editImage);
        btn_edit = view.findViewById(R.id.btn_updatePerson);
        array_resource = getResources().getStringArray(R.array.department);

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray jsonArray = jsonResponse.getJSONArray("response");
                    String sId, sPassword, sName, sDepartment, sPhoto;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject row = jsonArray.getJSONObject(i);
                        sId = row.getString("id");
                        sPassword = row.getString("password");
                        sName = row.getString("name");
                        sDepartment = row.getString("department");
                        sPhoto = row.getString("photo");
                        currentUser = new Person(sId, sPassword, sName, sDepartment, sPhoto);
                    }
                    id.setText(currentUser.getID());
                    name.setText(currentUser.getName());
                    for(int i = 0; i < array_resource.length; i++) {
                        if(array_resource[i].equals(currentUser.getDepartment())) department.setSelection(i);
                    }
                    newImageBitmap = StringBitmapUtility.getBitMapFromString(currentUser.getPhoto());
                    btn_editImage.setImageBitmap(newImageBitmap);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        GetUserByIDRequest getUserByIDRequest = new GetUserByIDRequest(currentUserID, responseListener);
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(getUserByIDRequest);

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
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean isSuccess = jsonResponse.getBoolean("success");
                            if (isSuccess) {
                                Toast.makeText(getContext(), "데이터를 변경했습니다.", Toast.LENGTH_SHORT).show();
                                activity.departmentTV.setText(department.getSelectedItem().toString());
                                activity.nameTV.setText(name.getText().toString());
                                Bundle bundle = new Bundle();
                                bundle.putString("User", currentUserID);
                                activity.onChangedFragment(2, bundle);
                            } else {
                                Log.e("SettingFragment", "User Update failed");
                            }
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                };

                UpdateUserRequest updateUserRequest = new UpdateUserRequest(currentUserID,
                                                                password.getText().toString(),
                                                                name.getText().toString(),
                                                                department.getSelectedItem().toString(),
                                                                StringBitmapUtility.getStringFromBitmap(newImageBitmap),
                                                                responseListener);
                RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                requestQueue.add(updateUserRequest);
            }
        });
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
