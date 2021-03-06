package com.example.capstonedesignandroid;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.example.capstonedesignandroid.Adapter.GroupPagerAdapter;
import com.example.capstonedesignandroid.Fragment.GroupFragment1;
import com.example.capstonedesignandroid.StaticMethodAndOthers.SharedPreference;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class StudyBulletinBoardActivity extends AppCompatActivity {

    String userId;
    private long backKeyPressedTime = 0;
    private Toast toast;
    protected BottomNavigationView navigationView;

    // 우선 ArrayList 객체를 ArrayAdapter 객체에 연결합니다.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_bulletin_board);

        navigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation_view);
        navigationView.setSelectedItemId(R.id.action_group);
        navigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        userId = SharedPreference.getAttribute(getApplicationContext(), "userId");

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        final ArrayList<String> list = new ArrayList<>();
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("모든스터디"));
        tabLayout.addTab(tabLayout.newTab().setText("과목별스터디"));
        tabLayout.addTab(tabLayout.newTab().setText("나의스터디"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final GroupPagerAdapter adapter = new GroupPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                GroupFragment1 fragment1 = new GroupFragment1();
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }
            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),MakeGroupActivity.class);
                startActivityForResult(intent,100);
            }
        });

    }//onCreate

    @Override
    public void onBackPressed() {
        // AlertDialog 빌더를 이용해 종료시 발생시킬 창을 띄운다
        AlertDialog.Builder alBuilder = new AlertDialog.Builder(this);
        alBuilder.setMessage("종료하시겠습니까?");

        // "예" 버튼을 누르면 실행되는 리스너
        alBuilder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    finishAffinity();
                }
                System.runFinalization();
                System.exit(0);
            }
        });
        // "아니오" 버튼을 누르면 실행되는 리스너
        alBuilder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return; // 아무런 작업도 하지 않고 돌아간다
            }
        });
        alBuilder.show(); // AlertDialog.Bulider로 만든 AlertDialog를 보여준다.
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item)
        {
            switch (item.getItemId()){
                case R.id.action_group :
                    break;
                case R.id.action_reservation :
                    Intent intent2 = new Intent(StudyBulletinBoardActivity.this, LectureroomReservationActivity.class);
                    startActivity(intent2);
                    finish();
                    break;
                case R.id.action_check :
                    Intent intent3 = new Intent(StudyBulletinBoardActivity.this, LectureroomCheckActivity.class);
                    startActivity(intent3);
                    finish();
                    break;
                case R.id.action_cafe :
                    Intent intent4 = new Intent(StudyBulletinBoardActivity.this, CafeMapActivity.class);
                    startActivity(intent4);
                    finish();
                    break;
                case R.id.action_profile :
                    Intent intent5 = new Intent(StudyBulletinBoardActivity.this, MyProfileActivity.class);
                    startActivity(intent5);
                    finish();
                    break;
            }
            return false;
        }
    };

}//MainActivity