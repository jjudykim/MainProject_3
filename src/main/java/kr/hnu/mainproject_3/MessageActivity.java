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

import com.google.android.material.navigation.NavigationView;

import java.io.Serializable;

public class MessageActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    Serializable currentUser;
    Person changed_currentUser;
    String replyInfo;
    Toolbar toolbar;
    Fragment fragment_mailbox;
    Bundle bundle;

    TextView department;
    TextView name;

   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Intent getIntent = getIntent();
        bundle = new Bundle();
        currentUser = getIntent.getSerializableExtra("User");
        changed_currentUser = (Person) currentUser;
        bundle.putSerializable("User", currentUser);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_dehaze_24);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        fragment_mailbox = new ReceiveMailboxFragment();

        toolbar.setTitle("받은 메시지함");
        toolbar.setTitleTextColor(0xFFFFFFFF);
        getSupportFragmentManager().beginTransaction().add(R.id.container, fragment_mailbox).commit();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        department = (TextView) headerView.findViewById(R.id.nav_TV_department);
        name = (TextView) headerView.findViewById(R.id.nav_TV_name);
        department.setText(changed_currentUser.getDepartment());
        name.setText(changed_currentUser.getName());
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);
                mDrawerLayout.closeDrawers();

                int id = item.getItemId();

                if(id == R.id.item_write)
                {
                    bundle.putString("isReply", "false");
                    onChangedFragment(1, bundle);
                }
                else if(id == R.id.item_receive_mailbox)
                {
                    onChangedFragment(2, null);
                }
                else if (id == R.id.item_send_mailbox)
                {
                    onChangedFragment(3, null);
                }
                else if(id == R.id.item_setting)
                {
                    onChangedFragment(4, bundle);
                }
                else if(id == R.id.item_logout)
                {
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
        if(bundle != null) {
            this.bundle = bundle;
            if(bundle.containsKey("User")) {
                changed_currentUser = (Person) this.bundle.getSerializable("User");
                department.setText(changed_currentUser.getDepartment());
                name.setText(changed_currentUser.getName());
            }
            else if (bundle.containsKey("isReply")) {
                replyInfo = (String) this.bundle.getString("isReply");
            }
        }
        switch(position) {
            case 1:  // 메시지 작성
                fragment = new WriteFragment(bundle);
                toolbar.setTitleTextColor(0xFFFFFFFF);
                toolbar.setTitle("메시지 작성");
                break;
            case 2:  // 받은 메시지 함
                fragment = new ReceiveMailboxFragment();
                toolbar.setTitleTextColor(0xFFFFFFFF);
                toolbar.setTitle("받은 메시지함");
                break;
            case 3:  // 보낸 메시지 함
                fragment = new SendMailboxFragment();
                toolbar.setTitleTextColor(0xFFFFFFFF);
                toolbar.setTitle("보낸 메시지함");
                break;
            case 4:  // 개인 정보 설정
                fragment = new SettingFragment(bundle);
                toolbar.setTitleTextColor(0xFFFFFFFF);
                toolbar.setTitle("개인 정보 설정");
                break;
//            case 5:  // 메시지 확인
//                fragment = new ReadFragment(bundle);
//                toolbar.setTitleTextColor(0xFFFFFFFF);
//                toolbar.setTitle("메시지 읽기");
//                break;
            default:
                break;
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       switch(item.getItemId()) {
           case android.R.id.home: {
               mDrawerLayout.openDrawer(GravityCompat.START);
               return true;
           }
       }
       return super.onOptionsItemSelected(item);
    }

    public Person getCurrentUser() {
       return (Person)currentUser;
    }
}
