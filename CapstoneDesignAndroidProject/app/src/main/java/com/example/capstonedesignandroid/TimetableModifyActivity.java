package com.example.capstonedesignandroid;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.capstonedesignandroid.DTO.DummyTile;
import com.example.capstonedesignandroid.Fragment.TimeTableFragment;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class TimetableModifyActivity extends AppCompatActivity {

    private TextView copyView;
    private TextView selectView;
    public String mode = "read";
    private RelativeLayout container;
    public EditText contentsEditText;
    public EditText locationEditText;
    private RelativeLayout timetablemodify;
    private TimeTableFragment timeTableFragment;
    private TextView selectCancelView;
    private ArrayList<DummyTile> dummiesDummyTile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable_modify);
        selectView = findViewById(R.id.selectView);
//        copyView = findViewById(R.id.copyView);
        selectCancelView = findViewById(R.id.selectCancelView);

        timeTableFragment = new TimeTableFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.timeTableFrame, timeTableFragment).commit();

        mode = "select";

        selectView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if(mode.equals("read")){
//                    selectView.setText("추가/수정/삭제하기");
//                    mode = "select";
//                    selectCancelView.setVisibility(View.VISIBLE);
//                }else{
                    timetablemodify.setVisibility(View.VISIBLE);
                //}
            }
        });

//        copyView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(mode.equals("read")){
//                    copyView.setText("붙여넣기");
//                    mode = "copy";
//                }else{
//                    copyView.setText("시간표 복사 (붙여넣기)");
////                    timeTableFragment.copyAndPaste();
//                    mode = "read";
//                }
//            }
//        });
        selectCancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //selectView.setText("시간표 다수 선택 (추가/수정)");
                timetablemodify.setVisibility(View.INVISIBLE);
                //mode = "read";
                timeTableFragment.selectCancel();
                //selectCancelView.setVisibility(View.INVISIBLE);
            }
        });

        //-------수정 기능 부분-----
        container = findViewById(R.id.container);
        LayoutInflater inflater = getLayoutInflater();
        timetablemodify = (RelativeLayout) inflater.inflate(R.layout.timetablemodify, container, false);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        timetablemodify.setLayoutParams(layoutParams);
        container.addView(timetablemodify);

        contentsEditText = timetablemodify.findViewById(R.id.contentsEditText);
        locationEditText = timetablemodify.findViewById(R.id.locationEditText);

        //수정 취소
        Button cancelButton = timetablemodify.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timetablemodify.setVisibility(View.INVISIBLE);
                mode = "select";
            }
        });

        //수정 완료
        Button confirmButton = timetablemodify.findViewById(R.id.confirmButton);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //아무것도 작성하지 않았을 때는 확인이 안된다.
                if(contentsEditText.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "일정을 작성해주세요.", Toast.LENGTH_LONG).show();
                    return;
                }
                //selectView.setText("시간표 다수 선택 (추가/수정)");
                timetablemodify.setVisibility(View.INVISIBLE);
                //mode = "read";
                timeTableFragment.selectAndModify();
                //selectCancelView.setVisibility(View.INVISIBLE);
            }
        });

        Button deleteTileButton = timetablemodify.findViewById(R.id.deleteTileButton);
        deleteTileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(TimetableModifyActivity.this);
                builder.setTitle("정말로 선택한 정보를 삭제하시겠습니까?");
                builder.setPositiveButton("예", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //selectView.setText("시간표 다수 선택 (추가/수정)");
                        timetablemodify.setVisibility(View.INVISIBLE);
                        //mode = "read";
                        timeTableFragment.selectAndDelete();
                        //selectCancelView.setVisibility(View.INVISIBLE);
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
        });

        timetablemodify.setVisibility(View.INVISIBLE);

        com.otaliastudios.zoom.ZoomLayout zoomLayout = findViewById(R.id.zoomLayout);

        Handler mHandler = new Handler();
        TimerTask mTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            zoomLayout.moveTo(2.0f, 0, 1, true);
                        }
                    });
                } catch (Exception e) {
                    Log.d("ExceptionTimer", ": " + e);
                }
            }
        };

        Timer mTimer = new Timer();
        mTimer.schedule(mTask, 100);
    }
}
