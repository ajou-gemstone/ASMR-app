package com.example.capstonedesignandroid;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.capstonedesignandroid.Adapter.ReservationAdapter;
import com.example.capstonedesignandroid.DTO.DummyLectureRoomReservationState;
import com.example.capstonedesignandroid.DTO.DummyLectureroomInfo;
import com.example.capstonedesignandroid.DTO.DummyReservationId;
import com.example.capstonedesignandroid.DTO.LectureRoomReservationState;
import com.example.capstonedesignandroid.Fragment.LectureroomReservationCanlendar;
import com.example.capstonedesignandroid.StaticMethodAndOthers.DefinedMethod;
import com.example.capstonedesignandroid.StaticMethodAndOthers.MyConstants;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class LectureroomReservationActivity extends AppCompatActivity {

    private Fragment lectureroomReservationCanlendarFragment;
    public boolean dataSelected = false;
    public Date reserveDate;
    public Date currentDate;
    private Button calendarReserveButton;
    private TextView reserveTimeTextView;
    private LinearLayout LectureBuildingFilterLayout;
    private CheckBox reserveTimeAllCheckbox;
    private Spinner reserveStartTimeSpinner;
    private Spinner reserveEndTimeSpinner;
    private ArrayList eightToTwentyoneTimeArrayList = new ArrayList<>();
    private long nowTime;
    private boolean isFCFS = true;
    private TextView reserveTypeTextView;
    private ImageButton reserveRandomButton;
    private ImageButton reserveButton;
    private RecyclerView recyclerView;
    private ArrayList<String> list;
    private RecyclerView recyclerViewFCFS;
    private RecyclerView recyclerViewReservation;
    private LinearLayout recyclerPPDLayout;
    private LinearLayout lectureRoomLayout;
    private CheckBox LectureBuildingAllCheckbox;
    private ArrayList<CheckBox> LectureBuildingCheckboxArrayList;
    private boolean checkControl = false;
    private LinearLayout reserveTimeSpinnerInnerLayout;
    private ArrayList<DummyLectureRoomReservationState> dummyLectureRoomReservationList;
    private String startTime;
    private String lastTime;
    private HorizontalScrollView lectureRoomScroll;
    private Boolean firstClick = false;
    private Boolean secondClick = false;
    private int firstTag =-1;
    private int secondTag =-1;
    private int currentPosition = -1;
    private View currentPositionView;
    private int startTimePosition;
    private int lastTimePosition;
    private ImageButton reserveDetermineButton;
    private Retrofit retrofit;
    private boolean dummyLectureRoomReservationState_state = false;
    private DummyReservationId reservationid;
    private ArrayList<LectureRoomReservationState> lectureRoomReservationStateArrayList;
    protected BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lectureroom_reservation);

        navigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation_view);
        navigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        retrofit = new Retrofit.Builder()
                .baseUrl(MyConstants.BASE)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //프래그먼트는 뷰와 다르게 context를 매개변수로 넣어줄 필요가 없다.
        lectureroomReservationCanlendarFragment = new LectureroomReservationCanlendar();
        calendarReserveButton = findViewById(R.id.calendarReserveButton);
        reserveTimeTextView = findViewById(R.id.reserveTimeTextView);
        FrameLayout reservation_calendar_container = findViewById(R.id.reservation_calendar_container);
        reservation_calendar_container.bringToFront();//최상단의 view로 보여주도록 한다.

        reserveTypeTextView = findViewById(R.id.reserveTypeTextView);

        calendarReserveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //프래그먼트 추가하거나 할떄는 여러개 명령을 한꺼번에 쓸 수 있으므로
                //beginTransaction을 사용함
                //fragment1를 R.id.container에 넣어달라(add 또는 replace, replace는 기존에있던걸 대체 해줌)
                //트랜잭션안에서 수행되는것이므로 마지막에 꼭 commit을 해줘야 실행이된다.
                getSupportFragmentManager().beginTransaction().replace(R.id.reservation_calendar_container, lectureroomReservationCanlendarFragment).commit();
                /*getSupportFragmentManager().beginTransaction().add(R.id.container, fragment1).commit();*/
            }
        });

        //현재시간 보여주기
        dataSelected = true;
        nowTime = System.currentTimeMillis();
        reserveDate = new Date(nowTime);
        //시, 분, 초를 없앤 년,월,일의 Date
        reserveDate = DefinedMethod.getDate(DefinedMethod.getYear(reserveDate), DefinedMethod.getMonth(reserveDate),DefinedMethod.getDay(reserveDate));
        currentDate = new Date();
        currentDate = reserveDate;

        reserveTimeTextView.setText(""+DefinedMethod.getYear(currentDate)+"-"+Math.addExact(DefinedMethod.getMonth(currentDate), 1)+"-"+DefinedMethod.getDay(currentDate));
        //--------------------------------------------

        //모든 강의실 checkBox처리
        LectureBuildingAllCheckbox = findViewById(R.id.LectureBuildingAllCheckbox);
        LectureBuildingAllCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(checkControl == true){
                    checkControl = false;
                    return;
                }
                if (isChecked) {
                    //옆의 모든 checkBox를 check해준다.
                    for (CheckBox eachCheckbox : LectureBuildingCheckboxArrayList) {
                        eachCheckbox.setChecked(true);
                    }
                } else {
                    //옆의 모든 checkBox를 uncheck해준다.
                    for (CheckBox eachCheckbox : LectureBuildingCheckboxArrayList) {
                        eachCheckbox.setChecked(false);
                    }
                }
            }
        });

        //checkbox 동적 생성, margin을 준다.
        LectureBuildingCheckboxArrayList = new ArrayList<CheckBox>();
        LectureBuildingFilterLayout = (LinearLayout)findViewById(R.id.LectureBuildingFilterLayout);
        LinearLayout.LayoutParams checkboxLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        checkboxLayoutParams.leftMargin = 40;
        for(String buildingName: MyConstants.LectureBuildingList){
            CheckBox checkBox = new CheckBox(getApplicationContext());
            checkBox.setLayoutParams(checkboxLayoutParams);
            checkBox.setText(buildingName);
            checkBox.setId(View.generateViewId());//generate view Id로 뷰의 id를 생성해준다.
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {//모든 건물물 풀어준다.
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if(isChecked){
                    }
                    else{
                        //모든 건물 checkBox를 uncheck해준다.
                        if(LectureBuildingAllCheckbox.isChecked()){
                            checkControl = true;
                            LectureBuildingAllCheckbox.setChecked(false);
                        }
                    }
                }
            });
            LectureBuildingFilterLayout.addView(checkBox);
            LectureBuildingCheckboxArrayList.add(checkBox);
        }

        reserveTimeSpinnerInnerLayout = findViewById(R.id.reserveTimeSpinnerInnerLayout);

        //모든 시간대 checkBox처리
        reserveTimeAllCheckbox = findViewById(R.id.reserveTimeAllCheckbox);
        reserveTimeAllCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    reserveStartTimeSpinner.setEnabled(false);
                    reserveEndTimeSpinner.setEnabled(false);
                    reserveTimeSpinnerInnerLayout.setBackgroundColor(Color.argb(51,17,17,17));
                }else{
                    reserveStartTimeSpinner.setEnabled(true);
                    reserveEndTimeSpinner.setEnabled(true);
                    reserveTimeSpinnerInnerLayout.setBackgroundColor(Color.argb(0,255,255,255));
                }
            }
        });

        //spinner, ArrayAdapter를 이용하여 구현한다.
        eightToTwentyoneTimeArrayList = DefinedMethod.declareEightToTwentyoneTimeArrayList(eightToTwentyoneTimeArrayList);

        reserveStartTimeSpinner = findViewById(R.id.reserveStartTimeSpinner);
        reserveEndTimeSpinner = findViewById(R.id.reserveEndTimeSpinner);
        ArrayAdapter arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, eightToTwentyoneTimeArrayList);
        reserveStartTimeSpinner.setAdapter(arrayAdapter);
        reserveStartTimeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(),eightToTwentyoneTimeArrayList.get(i)+"가 선택되었습니다.",
                        Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        reserveEndTimeSpinner.setAdapter(arrayAdapter);
        reserveEndTimeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(),eightToTwentyoneTimeArrayList.get(i)+"가 선택되었습니다.",
                        Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        reserveRandomButton = findViewById(R.id.reserveRandomButton);
        reserveButton = findViewById(R.id.reserveButton);

        reserveButton.setOnClickListener(new View.OnClickListener() {
            //-------DB 조회-------
            //입력: 날짜(하나), 건물(리스트), 시작시간(하나), 종료시간(하나), //강의실 예약 인원 수(하나)
            //입력: {date: "YYYY-M-D", building: "성호관 율곡관 연암관" startTime: "0" lastTime: "3"}
            //출력: {lectureroom: "성101", stateList: "R 0 0 0 1 L"}
            //출력: {lectureroom: "성103", stateList: "R A A A L L"}

            @Override
            public void onClick(View view){
                //날짜, 강의실 등의 데이터를 서버에 전달하여 필터링을 거쳐 목록을 받는다.
                //쿼리
                //날짜
                int year = DefinedMethod.getYear(reserveDate);
                int month = DefinedMethod.getMonth(reserveDate) + 1;
                int day = DefinedMethod.getDay(reserveDate);
                String date = "" + year + "-" + month + "-" + day;
                //건물
                ArrayList<String> buildingArr = new ArrayList<String>();
                for(CheckBox eachCheckBox : LectureBuildingCheckboxArrayList){
                    if(eachCheckBox.isChecked()){
                        buildingArr.add((String) eachCheckBox.getText());
                    }
                }
                if(buildingArr.size() == 0){
                    Toast.makeText(getApplicationContext(), "건물을 최소한 하나 선택해주세요", Toast.LENGTH_LONG).show();
                    return;
                }
                String[] buildingArray = new String[buildingArr.size()];
                buildingArray = buildingArr.toArray(buildingArray);


                //사용 시간대
                //모든 시간
                //순서가 바뀌는 경우도 따로 오류 처리
                if(reserveTimeAllCheckbox.isChecked()){
                    startTime = "7:00";//position 0
                    startTimePosition = 0;
                    lastTime = "20:30";//position 27
                    lastTimePosition = 27;
                }else{
                    //시작 시간
                    startTime = reserveStartTimeSpinner.getSelectedItem().toString();
                    startTimePosition = reserveStartTimeSpinner.getSelectedItemPosition();
                    //마지막 시간
                    lastTime = reserveEndTimeSpinner.getSelectedItem().toString();
                    lastTimePosition = reserveEndTimeSpinner.getSelectedItemPosition();
                }

                Log.d("retrofittt", "date:"+date+ " building:"+ buildingArray[0] + " " + buildingArray[1] + "..." +
                        " startTime:" + startTimePosition + " lastTime:"+ lastTimePosition);

                //서버 DB에서 목록을 가져온다.
                GetService service = retrofit.create(GetService.class);
                //retrofit service에 정의된 method를 사용하여
                Call<List<DummyLectureRoomReservationState>> call = service.getReservationList(date, buildingArray, startTimePosition, lastTimePosition);

                //비동기 호출
//                call.enqueue(dummyLectureRoomReservationState);

                //동기 호출, network를 사용한 thread는 main thread에서 처리를 할 수 없기 때문에
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            List<DummyLectureRoomReservationState> dummies = call.execute().body();
                            dummyLectureRoomReservationList = new ArrayList<DummyLectureRoomReservationState>(dummies);
                            dummyLectureRoomReservationState_state = true;
                            Log.d("run: ", "run: ");
                        } catch (IOException e) {
                            e.printStackTrace();
                            dummyLectureRoomReservationState_state = false;
                            Log.d("IOException: ", "IOException: ");
                        }
                    }
                });
                thread.start();
                try {
                    thread.join();
                } catch (Exception e) {
                    // TODO: handle exception
                }

                //-----------------------------------------------------------
                //mockup 데이터 받기
                if(dummyLectureRoomReservationState_state == false){
                    //DB에서 아래와 같은 정보를 dummy list로 가져왔다고 가정한다.
                    startTimePosition = 2;
                    lastTimePosition = 7;
                    DummyLectureRoomReservationState dummyLectureRoomReservation1 = new DummyLectureRoomReservationState("성101", "R 0 0 0 1 L");
                    DummyLectureRoomReservationState dummyLectureRoomReservation2 = new DummyLectureRoomReservationState("성102", "L L L L L L");
                    DummyLectureRoomReservationState dummyLectureRoomReservation3 = new DummyLectureRoomReservationState("성103", "R R A A A L");
                    DummyLectureRoomReservationState dummyLectureRoomReservation4 = new DummyLectureRoomReservationState("성104", "1 R 0 L 2 3");
                    DummyLectureRoomReservationState dummyLectureRoomReservation5 = new DummyLectureRoomReservationState("성105", "1 R R L R 4");
                    DummyLectureRoomReservationState dummyLectureRoomReservation6 = new DummyLectureRoomReservationState("성106", "0 0 0 L 2 3");
                    DummyLectureRoomReservationState dummyLectureRoomReservation7 = new DummyLectureRoomReservationState("성107", "1 0 0 0 2 2");
                    DummyLectureRoomReservationState dummyLectureRoomReservation8 = new DummyLectureRoomReservationState("성108", "0 0 0 0 0 0");
                    DummyLectureRoomReservationState dummyLectureRoomReservation9 = new DummyLectureRoomReservationState("성109", "R R R R R R");
                    DummyLectureRoomReservationState dummyLectureRoomReservation10 = new DummyLectureRoomReservationState("성110", "A A A L L L");
                    DummyLectureRoomReservationState dummyLectureRoomReservation11 = new DummyLectureRoomReservationState("성111", "0 1 2 3 2 1");
                    DummyLectureRoomReservationState dummyLectureRoomReservation12 = new DummyLectureRoomReservationState("성112", "A A A A R L");

                    dummyLectureRoomReservationList = new ArrayList<DummyLectureRoomReservationState>();
                    dummyLectureRoomReservationList.add(dummyLectureRoomReservation1);
                    dummyLectureRoomReservationList.add(dummyLectureRoomReservation2);
                    dummyLectureRoomReservationList.add(dummyLectureRoomReservation3);
                    dummyLectureRoomReservationList.add(dummyLectureRoomReservation4);
                    dummyLectureRoomReservationList.add(dummyLectureRoomReservation5);
                    dummyLectureRoomReservationList.add(dummyLectureRoomReservation6);
                    dummyLectureRoomReservationList.add(dummyLectureRoomReservation7);
                    dummyLectureRoomReservationList.add(dummyLectureRoomReservation8);
                    dummyLectureRoomReservationList.add(dummyLectureRoomReservation9);
                    dummyLectureRoomReservationList.add(dummyLectureRoomReservation10);
                    dummyLectureRoomReservationList.add(dummyLectureRoomReservation11);
                    dummyLectureRoomReservationList.add(dummyLectureRoomReservation12);
                }

                //우선순위 sorting을 위해 list초기화
                lectureRoomReservationStateArrayList = new ArrayList<LectureRoomReservationState>();
                for(DummyLectureRoomReservationState d: dummyLectureRoomReservationList){
                    lectureRoomReservationStateArrayList.add(new LectureRoomReservationState(d));
                }

                //선착순 sorting
                if(isFCFS){
//                    선착순인 경우 빈 시간이 많고, 연결되어있는 강의실 우선
                    int i = 0;
                    String previousState = "NULL";
                    for(DummyLectureRoomReservationState data : dummyLectureRoomReservationList){
                        String eachStateList =  data.getStateList();
                        String[] splitState = eachStateList.split("\\s+");
                        for(String eachState : splitState){
                            if(eachState.equals("A")){
                                lectureRoomReservationStateArrayList.get(i).setPriority(lectureRoomReservationStateArrayList.get(i).getPriority());
                            }else{//아니면 최대 가중치인 20을 더한다.
                                lectureRoomReservationStateArrayList.get(i).setPriority(lectureRoomReservationStateArrayList.get(i).getPriority() + 20);
                            }
                            //연속으로 비어있는 강의실이면 -20을 해준다.
                            if(eachState.equals("A") && previousState.equals("A")){
                                lectureRoomReservationStateArrayList.get(i).setPriority(lectureRoomReservationStateArrayList.get(i).getPriority() - 20);
                            }
                            previousState = eachState;
                        }
                        i++;
                    }

                    Collections.sort(lectureRoomReservationStateArrayList, new Comparator<LectureRoomReservationState>() {
                        @Override
                        public int compare(LectureRoomReservationState t1, LectureRoomReservationState t2) {
                            if(t1.getPriority() > t2.getPriority()){
                                return 1;
                            }else if(t1.getPriority() < t2.getPriority()){
                                return -1;
                            }
                            return 0;
                        }
                    });
                }else{//선지망 후추첨 sorting
                    //sorting은 사용가능한 강의실 중
                    //선지망 후추첨인 경우 시간대에 예약 팀수의 합이 가장 적은 강의실 우선
                    String num = "0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19";

                    int i = 0;
                    for(DummyLectureRoomReservationState data : dummyLectureRoomReservationList){
                        String eachStateList =  data.getStateList();
                        String[] splitState = eachStateList.split("\\s+");
                        for(String eachState : splitState){
                            if(num.contains(eachState)){
                                lectureRoomReservationStateArrayList.get(i).setPriority(lectureRoomReservationStateArrayList.get(i).getPriority() + Integer.parseInt(eachState));
                            }else{//아니면 최대 가중치인 20을 더한다.
                                lectureRoomReservationStateArrayList.get(i).setPriority(lectureRoomReservationStateArrayList.get(i).getPriority() + 20);
                            }
                        }
                        i++;
                    }
                    Collections.sort(lectureRoomReservationStateArrayList, new Comparator<LectureRoomReservationState>() {
                        @Override
                        public int compare(LectureRoomReservationState t1, LectureRoomReservationState t2) {
                            if(t1.getPriority() > t2.getPriority()){
                                return 1;
                            }else if(t1.getPriority() < t2.getPriority()){
                                return -1;
                            }
                            return 0;
                        }
                    });
                }

                //시간 배열도 마찬가지로 동적으로 생성
                String timeString = "";
                for(int i = startTimePosition; i <= lastTimePosition; i++){
                    timeString = timeString.concat(DefinedMethod.getTimeByPosition(i) +" ");
                }
                Log.d("timeString", "" + timeString);

                DummyLectureRoomReservationState dummyLectureRoomReservation0 = new DummyLectureRoomReservationState("강의실", timeString);
                lectureRoomReservationStateArrayList.add(0, new LectureRoomReservationState(dummyLectureRoomReservation0));
                //다시 호출된 경우를 대비해서 변수들 초기화
                firstTag = -1;
                secondTag = -1;
                currentPosition = -1;
                currentPositionView = null;
                firstClick = false;
                secondClick = false;

                inflateReservationUI();
            }
        });

        reserveDetermineButton = findViewById(R.id.reserveDetermineButton);

        //강의실 최종 예약 버튼
        reserveDetermineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //강의실 예약을 확정한다. 서버에 데이터를 넣는다.
                //강의실 시간대를 적절히 잘 선택헀는지 확인
                if(firstTag > 0 && secondTag > 0){
                    //시작시간 - 종료시간순으로 정렬
                    if(firstTag > secondTag){
                        int tmp = firstTag;
                        firstTag = secondTag;
                        secondTag = tmp;
                    }

                    //실제 시간 position으로 맞춰준다.
                    firstTag = startTimePosition + firstTag;
                    secondTag = startTimePosition + secondTag;

                    int year = DefinedMethod.getYear(reserveDate);
                    int month = DefinedMethod.getMonth(reserveDate) + 1;
                    int day = DefinedMethod.getDay(reserveDate);
                    String date = "" + year + "-" + month + "-" + day;
                    String lectureroom = lectureRoomReservationStateArrayList.get(currentPosition).getLectureroom();//강의실

                    AlertDialog.Builder builder = new AlertDialog.Builder(LectureroomReservationActivity.this);
                    builder.setTitle("강의실 예약을 확정하겠습니까?").setMessage("" + lectureroom+
                            "\n"+ month + "월" + day + "일" + "\n" + "시작시간: " + DefinedMethod.getTimeByPosition(firstTag) +
                            " 종료시간: " + DefinedMethod.getTimeByPosition(secondTag+1));
                    builder.setPositiveButton("예", new DialogInterface.OnClickListener(){
                        boolean success = false;
                        @Override
                        public void onClick(DialogInterface dialog, int id)
                        {
                            //입력: 날짜(하나), 강의실(하나), 시작시간(하나), 종료시간(하나), 본인id(하나)
                            //입력: {date: "YYYY-M-D" lectureRoom: "성101" startTime: "9:00" lastTime: "10:00", userid: akdsnmkq}
                            //출력: {예약내역id: qninia} - 나중에 추가정보를 입력할 때 이 예약내역 id를 이용한다.

                            //userid는 sharedpreferece로 가져온다.
                            String userid = "leehyunju";

                            GetService service = retrofit.create(GetService.class);
                            //retrofit service에 정의된 method를 사용하여
                            Call<DummyReservationId> call = service.postReservation(date, lectureroom, firstTag, secondTag, userid);

                            //동기 호출, network를 사용한 thread는 main thread에서 처리를 할 수 없기 때문에
                            Thread thread = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        reservationid = call.execute().body();
                                        success = true;
                                        Log.d("run: ", "run: ");
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        Log.d("IOException: ", "IOException: ");
                                    }
                                }
                            });
                            thread.start();
                            try {
                                thread.join();
                                success = true;
                            } catch (Exception e) {
                                success = false;
                                // TODO: handle exception
                            }

                            if(success){
                                //강의실 예약 목적 입력 intent로 이동, 예약내역 id도 이동
                                Toast.makeText(getApplicationContext(), "강의실 예약 목적 intent로 이동할 것임.", Toast.LENGTH_LONG).show();
                            }else{
                                Toast.makeText(getApplicationContext(), "강의실 예약에 실패하였습니다.", Toast.LENGTH_LONG).show();
                            }

                            //리턴
                            //강의실 정보
                        }
                    });
                    builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                }
            }
        });

        lectureRoomLayout = findViewById(R.id.lectureRoomPPDLayout);
        recyclerViewReservation = findViewById(R.id.recyclerPPDrecyclerView);
    }

    private void inflateReservationUI(){
        lectureRoomLayout.setVisibility(View.VISIBLE);
        reserveDetermineButton.setVisibility(View.VISIBLE);
        recyclerViewReservation.setLayoutManager(new LinearLayoutManager(this));

//        selectMultipleTimeButton = findViewById(R.id.selectMultipleTimeButton);
//        selectMultipleTimeButton.setVisibility(View.VISIBLE);
//
//        selectMultipleTimeButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(selectMultipleTimeButtonClicked == false){
//                    selectMultipleTimeButtonClicked = true;
//                    //횡 스크롤 고정
//                    lectureRoomScroll.setOnTouchListener(new View.OnTouchListener() {
//                        @Override
//                        public boolean onTouch(View view, MotionEvent motionEvent) {
//                            return true;
//                        }
//                    });
//                    Toast.makeText(getApplicationContext(), "영역을 선택하세요", Toast.LENGTH_LONG).show();
//                }else{
//                    selectMultipleTimeButtonClicked = false;
//                    //횡 스크롤 고정 풀기
//                    lectureRoomScroll.setOnTouchListener(new View.OnTouchListener() {
//                        @Override
//                        public boolean onTouch(View view, MotionEvent motionEvent) {
//                            return false;
//                        }
//                    });
//                }
//            }
//        });

        lectureRoomScroll = findViewById(R.id.lectureRoomScroll);

        ReservationAdapter adapter = new ReservationAdapter(lectureRoomReservationStateArrayList) ;
        recyclerViewReservation.setAdapter(adapter);

        //adapter의 click event를 listen할 수 있도록 액티비티에서 listener 객체를 생성, 등록하고
        //인터페이스를 통해 adapter에서 위임하여 처리하도록 하며
        //액티비티에서 adapter의 파라미터를 사용할 수 있도록 한다.
        adapter.setOnItemClickListener(new ReservationAdapter.OnItemClickListener() {
            String capacity;
            @Override
            public void onItemClick(View v, int position) {
                if(v.getId() == R.id.lectureRoomNameButton){
                    //강의실 정보 가져오기
                    //입력: {lectureroom: 성101}
                    //출력: {capacity: 50}

                    GetService service = retrofit.create(GetService.class);
                    //retrofit service에 정의된 method를 사용하여
                    Call<DummyLectureroomInfo> call = service.getLectureroomInfo(lectureRoomReservationStateArrayList.get(position).getLectureroom());

                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                DummyLectureroomInfo dummy = call.execute().body();
                                capacity = dummy.getLectureroom();
                                capacity = capacity + "명";
                                Log.d("run: ", "run: ");
                            } catch (IOException e) {
                                e.printStackTrace();
                                capacity = "50명";
                                Log.d("IOException: ", "IOException: ");
                            }
                        }
                    });
                    thread.start();
                    try {
                        thread.join();
                    } catch (Exception e) {
                        // TODO: handle exception
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(LectureroomReservationActivity.this);
                    builder.setTitle("강의실 정보").setMessage("" + lectureRoomReservationStateArrayList.get(position).getLectureroom() + "\n" + capacity);
                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int id)
                        {

                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }

            @Override
            public void onTimeClick(View v, int position, View pv) {
                int tag = Integer.parseInt((String) v.getTag());
                if(currentPosition == -1){//강의실 시간대를 처음 선택할 때
                    currentPosition = position;
                    currentPositionView = pv;
                }else if(currentPosition != position){//다른 강의실의 시간대를 선택할 때
                    firstClick = false;
                    firstTag = -1;
                    secondClick = false;
                    secondTag = -1;
                    //배경을 모두 지움
                    for(int i = 0; i < 6; i++){
                        currentPositionView.findViewWithTag(""+i).setBackgroundColor(Color.argb(0, 0x8B,0xC3,0x4A));
                    }
                    currentPosition = position;
                    currentPositionView = pv;
                }
                //----------------------------
                if(firstClick == false){
                    firstClick = true;
                    firstTag = tag;
                    //배경을 first view만 표시
                    v.setBackgroundColor(Color.argb(51,17,17,17));
                }else if(firstClick == true){
                    //first에서 똑같은 것을 선택하였을 때
                    if(firstTag == tag){
                        firstClick = false;
                        firstTag = -1;
                        secondClick = false;
                        secondTag = -1;
                        //배경을 모두 지움
                        for(int i = 0; i <= lastTimePosition-startTimePosition; i++){
                            pv.findViewWithTag(""+i).setBackgroundColor(Color.argb(0, 0x8B,0xC3,0x4A));
                        }
                    }else{
                        if(secondTag == tag){
                            secondClick = false;
                            secondTag = -1;
                            //배경을 first view빼고 모두 지움
                            for(int i = 0; i <= lastTimePosition-startTimePosition; i++){
                                pv.findViewWithTag(""+i).setBackgroundColor(Color.argb(0, 0x8B,0xC3,0x4A));
                            }
                            pv.findViewWithTag(""+firstTag).setBackgroundColor(Color.argb(51,17,17,17));
                        }else{
                            secondClick = true;
                            secondTag = tag;
                            for(int i = 0; i <= lastTimePosition-startTimePosition; i++){
                                pv.findViewWithTag(""+i).setBackgroundColor(Color.argb(0, 0x8B,0xC3,0x4A));
                            }
                            //배경을 first view에서 second view까지 모두 표시
                            if(secondTag > firstTag){
                                for(int i = firstTag; i <= secondTag; i++){
                                    TextView textView = pv.findViewWithTag(""+i);
                                    pv.findViewWithTag(""+i).setBackgroundColor(Color.argb(51,17,17,17));
                                    //만약 예약이 불가한 날짜가 껴있다면 예약 불가
                                    if(textView.getText().equals("R") || textView.getText().equals("L")){
                                        secondClick = false;
                                        secondTag = -1;
                                        //배경을 first view빼고 모두 지움
                                        for(int j = 0; j <= lastTimePosition-startTimePosition; j++){
                                            pv.findViewWithTag(""+j).setBackgroundColor(Color.argb(0, 0x8B,0xC3,0x4A));
                                        }
                                        pv.findViewWithTag(""+firstTag).setBackgroundColor(Color.argb(51,17,17,17));
                                        Toast.makeText(getApplicationContext(), "예약 불가능한 날짜가 있습니다.", Toast.LENGTH_LONG).show();
                                        break;
                                    }
                                    //------------------
                                }
                            }else{
                                for(int i = secondTag; i <= firstTag; i++){
                                    TextView textView = pv.findViewWithTag(""+i);
                                    pv.findViewWithTag(""+i).setBackgroundColor(Color.argb(51,17,17,17));
                                    //만약 예약이 불가한 날짜가 껴있다면 예약 불가
                                    if(textView.getText().equals("R") || textView.getText().equals("L")){
                                        secondClick = false;
                                        secondTag = -1;
                                        //배경을 first view빼고 모두 지움
                                        for(int j = 0; j < 6; j++){
                                            pv.findViewWithTag(""+j).setBackgroundColor(Color.argb(0, 0x8B,0xC3,0x4A));
                                        }
                                        pv.findViewWithTag(""+firstTag).setBackgroundColor(Color.argb(51,17,17,17));
                                        Toast.makeText(getApplicationContext(), "예약 불가능한 날짜가 있습니다.", Toast.LENGTH_LONG).show();
                                        break;
                                    }
                                    //------------------
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    // lectureroomReservationCanlendarFragment와 주고 받는 부분
    public void removeLectureroomReservationCanlendarFragment(){
        getSupportFragmentManager().beginTransaction().remove(lectureroomReservationCanlendarFragment).commit();
    }
    public void getReservationDate(Date reserveDate){
        dataSelected = true;
        this.reserveDate = reserveDate;
        reserveTimeTextView.setText(""+DefinedMethod.getYear(reserveDate)+"-"+Math.addExact(DefinedMethod.getMonth(reserveDate), 1)+"-"+DefinedMethod.getDay(reserveDate));
    }
    public void getReservationType(boolean isFCFS){
        this.isFCFS = isFCFS;
        if(isFCFS){
            reserveTypeTextView.setText("예약 타입: 선착순");
        }
        else{
            reserveTypeTextView.setText("예약 타입: 선지망 후추첨");
        }
    }
    //------------------------------------------------------
    //retrofit callback

    Callback dummyLectureRoomReservationState = new Callback<List<DummyLectureRoomReservationState>>() {
        @Override
        public void onResponse(Call<List<DummyLectureRoomReservationState>> call, Response<List<DummyLectureRoomReservationState>> response) {
            if (response.isSuccessful()) {
                List<DummyLectureRoomReservationState> dummies = response.body();
                dummyLectureRoomReservationList = new ArrayList<DummyLectureRoomReservationState>(dummies);
                dummyLectureRoomReservationState_state = true;
            } else {
                Log.d("onResponse:", "Fail, " + String.valueOf(response.code()));
            }
        }

        @Override
        public void onFailure(Call<List<DummyLectureRoomReservationState>> call, Throwable t) {
            Log.d("response fail", "onFailure: ");
            dummyLectureRoomReservationState_state = false;
        }
    }; //dummies

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item)
        {
            switch (item.getItemId()){
                case R.id.action_group :
                    Intent intent1 = new Intent(LectureroomReservationActivity.this, StudyBulletinBoardActivity.class);
                    startActivity(intent1);
                    break;
                case R.id.action_reservation :

                    break;
                case R.id.action_check :
                    Intent intent3 = new Intent(LectureroomReservationActivity.this, LectureroomCheckActivity.class);
                    startActivity(intent3);
                    break;
                case R.id.action_cafe :
                    Intent intent4 = new Intent(LectureroomReservationActivity.this, CafeMapActivity.class);
                    startActivity(intent4);
                    break;
                case R.id.action_profile :
                    Intent intent5 = new Intent(LectureroomReservationActivity.this, ProfileActivity.class);
                    startActivity(intent5);
                    break;

            }
            return false;
        }
    };

}
