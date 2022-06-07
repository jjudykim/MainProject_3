package kr.hnu.mainproject_3;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MessageActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    String currentUserID;
    Person currentUser;
    Toolbar toolbar;
    Fragment fragment_mailbox;
    Bundle bundle;

    TextView departmentTV;
    TextView nameTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        Intent getIntent = getIntent();
        currentUserID = getIntent.getStringExtra("User");

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray jsonArray = jsonResponse.getJSONArray("response");
                    String id, password, name, department, photo;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject row = jsonArray.getJSONObject(i);
                        id = row.getString("id");
                        password = row.getString("password");
                        name = row.getString("name");
                        department = row.getString("department");
                        photo = row.getString("photo");
                        currentUser = new Person(id, password, name, department, photo);
                        departmentTV.setText(currentUser.getDepartment());
                        nameTV.setText(currentUser.getName());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        GetUserByIDRequest getUserByIDRequest = new GetUserByIDRequest(currentUserID, responseListener);
        RequestQueue requestQueue = Volley.newRequestQueue(MessageActivity.this);
        requestQueue.add(getUserByIDRequest);

        bundle = new Bundle();
        bundle.putString("User", currentUserID);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_dehaze_24);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        fragment_mailbox = new ReceiveMailboxFragment(bundle);

        onChangedFragment(2, bundle);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        departmentTV = (TextView) headerView.findViewById(R.id.nav_TV_department);
        nameTV = (TextView) headerView.findViewById(R.id.nav_TV_name);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);
                mDrawerLayout.closeDrawers();

                int id = item.getItemId();

                if (id == R.id.item_write) {
                    bundle.putString("isReply", "false");
                    onChangedFragment(1, bundle);
                } else if (id == R.id.item_receive_mailbox) {
                    onChangedFragment(2, bundle);
                } else if (id == R.id.item_send_mailbox) {
                    onChangedFragment(3, bundle);
                } else if (id == R.id.item_setting) {
                    onChangedFragment(4, bundle);
                } else if (id == R.id.item_logout) {
                    moveTaskToBack(true); // finish 이후 다른 액티비티가 뜨는 것을 막아줌
                    finishAndRemoveTask();
                    android.os.Process.killProcess(android.os.Process.myPid());
                }

                mDrawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

    }

    public void onChangedFragment(int position, Bundle bundle) {
        Fragment fragment = null;

        switch (position) {
            case 1:  // 메시지 작성
                fragment = new WriteFragment(bundle);
                toolbar.setTitleTextColor(0xFFFFFFFF);
                toolbar.setTitle("메시지 작성");
                break;
            case 2:  // 받은 메시지 함
                fragment = new ReceiveMailboxFragment(bundle);
                toolbar.setTitleTextColor(0xFFFFFFFF);
                toolbar.setTitle("받은 메시지함");
                break;
            case 3:  // 보낸 메시지 함
                fragment = new SendMailboxFragment(bundle);
                toolbar.setTitleTextColor(0xFFFFFFFF);
                toolbar.setTitle("보낸 메시지함");
                break;
            case 4:  // 개인 정보 설정
                fragment = new SettingFragment(bundle);
                toolbar.setTitleTextColor(0xFFFFFFFF);
                toolbar.setTitle("개인 정보 설정");
                break;

            // 이 아래부터는 네비게이션 메뉴에서 실행 불가능
            case 5:  // 메시지 확인
                fragment = new ReadFragment(bundle);
                toolbar.setTitleTextColor(0xFFFFFFFF);
                toolbar.setTitle("메시지 읽기");
                break;
            case 6:  // 유저 검색(메세시 작성 시)
                fragment = new FindUserFragment(bundle);
                toolbar.setTitleTextColor(0xFFFFFFFF);
                toolbar.setTitle("수신자 선택");
                break;
            default:
                break;
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public Person getCurrentUser() {
        return (Person) currentUser;
    }
}
