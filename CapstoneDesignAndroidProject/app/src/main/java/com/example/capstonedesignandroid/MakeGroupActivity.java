package com.example.capstonedesignandroid;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;


import com.example.capstonedesignandroid.DTO.Dummy;
import com.example.capstonedesignandroid.DTO.DummyResponse;
import com.example.capstonedesignandroid.DTO.Group;
import com.example.capstonedesignandroid.DTO.User;
import com.example.capstonedesignandroid.StaticMethodAndOthers.MyConstants;
import com.example.capstonedesignandroid.StaticMethodAndOthers.SharedPreference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static java.lang.Integer.parseInt;

public class MakeGroupActivity extends AppCompatActivity {
    EditText grouptitle, tag, body, totalnum;
    Button roommake;
    Intent intent, intent2;
    Spinner spinner_lecture;
    RadioGroup grouptype;
    RadioButton lecturechecked, allchecked;
    LinearLayout lecture_layout;
    String category = "all";
    ArrayList<String> mylectureArray = new ArrayList<>();
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_group);

        spinner_lecture = (Spinner) findViewById(R.id.spinner_lecture);
        roommake = (Button) findViewById(R.id.button_makeroom2);
        grouptype = (RadioGroup) findViewById(R.id.grouptype);
        lecturechecked= (RadioButton) findViewById(R.id.checkbox_lec);
        allchecked = (RadioButton) findViewById(R.id.checkbox_all);
        totalnum = (EditText) findViewById(R.id.totalnum);
        grouptitle = (EditText) findViewById(R.id.title);
        tag= (EditText) findViewById(R.id.tag);
        body = (EditText) findViewById(R.id.body);
        lecture_layout =findViewById(R.id.lecture_layout);

        userId = SharedPreference.getAttribute(getApplicationContext(), "userId");

        Retrofit retrofit2 = new Retrofit.Builder()
                .baseUrl(MyConstants.BASE)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GetService service = retrofit2.create(GetService.class);
        Call<User> call2 = service.getUserInfo(userId);
        CallThread2(call2);

        ArrayAdapter<String> adapter;
        List<String> list = new ArrayList<String>();
        for(String lec : mylectureArray){
            list.add(lec);
        }

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_lecture.setAdapter(adapter);

        grouptitle.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && i == KeyEvent.KEYCODE_ENTER) {
                    body.requestFocus();
                    return true;
                }
                return false;
            }
        });

        //과목별 스터디 선택할 때만 보이기
        lecture_layout.setVisibility(View.GONE);
        grouptype.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {  // 라디오 그룹 리스너 입니다.
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                RadioButton rb = (RadioButton) grouptype.findViewById(checkedId); //체크된 것을 입력받습니다.
                if( rb.getText().toString().equals("과목별 스터디") ) {
                    lecture_layout.setVisibility(View.VISIBLE);
                }
                else {
                    lecture_layout.setVisibility(View.GONE);
                    category = "all";
                }
            }
        });

        spinner_lecture.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                category = spinner_lecture.getSelectedItem().toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

        roommake.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                String title = grouptitle.getText().toString();
                String textBody = body.getText().toString();
                int studyGroupNumTot = parseInt(totalnum.getText().toString());
                String t = tag.getText().toString();

                if( ! title.equals("") && ! textBody.equals("") && ! t.equals("") && studyGroupNumTot > 1) {
                    String[] tArray = t.split("[#| |,]");
                    ArrayList<String> tagName = new ArrayList<>();
                    for (String tag : tArray)
                        if (!tag.equals(""))
                            tagName.add(tag);

                    Retrofit retrofit2 = new Retrofit.Builder()
                            .baseUrl(MyConstants.BASE)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    GroupService groupService = retrofit2.create(GroupService.class);
                    Call<DummyResponse> call2 = groupService.createStudy(userId, category, title, textBody, tagName, studyGroupNumTot);
                    CallThread(call2);

                    Toast.makeText(MakeGroupActivity.this, "즐거운 모임하세요.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), StudyBulletinBoardActivity.class);
                    startActivity(intent);
                }
                else if(studyGroupNumTot <= 0 && ! title.equals("") && ! textBody.equals("") && ! t.equals("")){
                    Toast.makeText(MakeGroupActivity.this, "올바른 숫자를 입력해주세요", Toast.LENGTH_SHORT).show();
                }
                else if(studyGroupNumTot ==1 && ! title.equals("") && ! textBody.equals("") && ! t.equals("")){
                    Toast.makeText(MakeGroupActivity.this, "모임은 혼자 하지 않아요", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(MakeGroupActivity.this, "모든 내용을 입력해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }//onCreate

    private void CallThread(Call<DummyResponse> call) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    DummyResponse dummies = call.execute().body();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("IOException: ", "IOException: ");
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (Exception e) {
        }
    }//callthread

    private void CallThread2(Call<User> call) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    User dummies = call.execute().body();
                    Log.d("getlecture", dummies.getLecture().toString());
                    for( String lec : dummies.getLecture()){
                        mylectureArray.add(lec);
                    }
                    Log.d("mylecarray", mylectureArray.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("IOException: ", "IOException: ");
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (Exception e) {
        }
    }



}
