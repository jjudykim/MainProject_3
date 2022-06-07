package kr.hnu.mainproject_3;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FindUserFragment extends Fragment {

    String currentUserID;
    String bundle_str;
    MessageActivity activity;
    Bundle bundle;
    LinearLayout lin;

    Spinner department;
    ArrayAdapter adapter;
    Button btn_cancel;
    Button btn_search;
    TextView userIdName;
    Button btn_select;
    ArrayList<String> list;
    ArrayList<RelativeLayout> arrayList_rel;

    public FindUserFragment(Bundle bundle) {
        currentUserID = bundle.getString("User");
        bundle_str = bundle.getString("isReply");
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (MessageActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) getLayoutInflater().inflate(R.layout.fragment_finduser, container, false);

        return rootView;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        list = new ArrayList<>();
        arrayList_rel = new ArrayList<>();
        bundle = new Bundle();
        lin = view.findViewById(R.id.findUser_linear);
        btn_cancel = view.findViewById(R.id.findUser_cancelBtn);
        btn_search = view.findViewById(R.id.findUser_searchBtn);
        department = view.findViewById(R.id.findUser_departmentSpin);
        adapter = ArrayAdapter.createFromResource(this.getContext(), R.array.department, android.R.layout.simple_spinner_dropdown_item);
        department.setAdapter(adapter);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putString("User", currentUserID);
                bundle.putString("isReply", bundle_str);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.container, new WriteFragment(bundle)).addToBackStack(null).commit();
            }
        });

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            JSONArray jsonArray = jsonResponse.getJSONArray("response");
                            lin.removeAllViews();
                            list.clear();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject row = jsonArray.getJSONObject(i);
                                list.add(row.getString("id") + " (" + row.getString("name") + ")");
                                AddSearchUser(i);
                            }
                            setRelOnClickListener();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                GetUserByDepRequest getUserByDepRequest = new GetUserByDepRequest(department.getSelectedItem().toString(), responseListener);
                RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                requestQueue.add(getUserByDepRequest);
            }
        });
    }

    public void AddSearchUser(int i) {
        RelativeLayout rel = (RelativeLayout) View.inflate(getContext(), R.layout.searchlist, null);
        userIdName = (TextView) rel.findViewById(R.id.searchlist_idAndName);
        userIdName.setText(list.get(i));

        arrayList_rel.add(rel);
        lin.addView(rel);
    }

    void setRelOnClickListener() {
        for(int i = 0; i < list.size(); i++) {
            int temp = i;
            arrayList_rel.get(temp).findViewById(R.id.searchlist_selectBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String[] tempStr = list.get(temp).split(" ");
                    bundle.putString("User", currentUserID);
                    bundle.putString("isReply", bundle_str);
                    bundle.putString("SearchedUser", tempStr[0]);
                    activity.onChangedFragment(1, bundle);
                }
            });
        }
    }

}
