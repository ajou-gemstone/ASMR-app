package com.example.capstonedesignandroid;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.capstonedesignandroid.Adapter.GroupListAdapter;

import org.json.*;
import org.json.simple.parser.*;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

//public class TabFragment1 extends Fragment implements SwipeRefreshLayout.OnRefreshListener   새로고침 사용하면 이걸로 바꿔야 함
public class TabFragment1 extends Fragment {

    Intent intent1,intent2;
    ArrayAdapter adapter;
    Button search;
    String userId,userPassword, maintext, name, trust,emotion, selecttitle, like;
    String chattingroom_id = "0";
    TextView text, text_sorted;
    SwipeRefreshLayout mSwipeRefreshLayout;
    Context context;
    ArrayAdapter<String> adapter1;
    ArrayList<String> list;
    String[] userInfo, titleArray, categoryArray, profileArray, likeArray,tempArray;
    int[] indexArray;
    Boolean ischecked2;

    EditText editSearch;
    boolean likesorting;
    String category = "";

    GroupListAdapter groupAdapter = new GroupListAdapter();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ListView listview;
        View view = inflater.inflate(R.layout.tab_fragment_1, container, false);
        //mSwipeRefreshLayout = view.findViewById(R.id.refresh);
        //mSwipeRefreshLayout.setOnRefreshListener(this);
        //mSwipeRefreshLayout.setColorSchemeResources(R.color.blue);
        context = container.getContext();
        editSearch = view.findViewById(R.id.editSearch);
        search = view.findViewById(R.id.search);

        //        Intent intent1 = getActivity().getIntent();
//        userInfo = intent1.getStringArrayExtra("strings");
////        userId = userInfo[0];
////        userPassword = userInfo[1];
////        name = userInfo[3];
////        trust = userInfo[4];
////        emotion = userInfo[5];
//        like = "1";
//        text = (TextView) view.findViewById(R.id.text);
//        list = new ArrayList<>();
//        adapter = new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1,list);

        //db에서 가져오기
        listview = (ListView)view.findViewById(R.id.listview1);
        listview.setAdapter(groupAdapter);
        groupAdapter.add(0, "삼성코테같이준비해요", "모든", "타이틀", "하반기무조건합격보장해드림", "없음", 1, 4);
        groupAdapter.add(1, "오픽AL5번연속배출모임", "모든", "타이틀2", "야 너두 할 수 있어", "없음", 2, 10);
        groupAdapter.add(2, "test1", "모든", "title3", "야 너두 할 수 있어", "없음", 2, 10);
        groupAdapter.add(3, "test2", "모든", "title4", "야 너두 할 수 있어", "없음", 2, 10);


//        titleArray=userInfo[2].split(",");
//        likeArray=userInfo[6].split(",");
//        categoryArray=userInfo[7].split(",");
//        profileArray=userInfo[8].split(",");

        //groupAdapter.clear();

        editSearch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && i == KeyEvent.KEYCODE_ENTER) {
                    listview.requestFocus();
                    return true;
                }
                return false;
            }
        });
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // input창에 문자를 입력할때마다 호출된다.
                // search 메소드를 호출한다.
                String text = editSearch.getText().toString();
                search(text);
            }
        });
/*
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                intent2 = new Intent(getActivity(), ReadGroupActivity.class);
                intent2.putExtra("str", "넘기기테스트");
                startActivity(intent2);

                final String BASE = SharedPreference.getAttribute(context.getApplicationContext(), "IP");
                selecttitle = groupAdapter.getPosition(position);

                Retrofit retrofit2 = new Retrofit.Builder()
                        .baseUrl(BASE)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                GetService readPostInterface = retrofit2.create(GetService.class);
                Call<List<Dummy3>> call3 = readPostInterface.listDummies();
                call3.enqueue(dummies3);
            }

        });
*/
        return view;
    } //onCreateView

    //모임 제목, 태그 검색
    public void search(String charText) {

        // 문자 입력시마다 리스트를 지우고 새로 뿌려준다.
        groupAdapter.clear();

        // 문자 입력이 없을때는 모든 데이터를 보여준다.
        if (charText.length() == 0) {
//            if(userInfo[2].equals("")){
//            }
//            else{
                groupAdapter.add(0, "삼성코테같이준비해요", "모든", "타이틀", "하반기무조건합격보장해드림", "없음", 1, 4);
                groupAdapter.add(1, "오픽AL5번연속배출모임", "모든", "타이틀2", "야 너두 할 수 있어", "없음", 2, 10);
                groupAdapter.add(2, "test1", "모든", "title3", "야 너두 할 수 있어", "없음", 2, 10);
                groupAdapter.add(3, "test2", "모든", "title4", "야 너두 할 수 있어", "없음", 2, 10);
//            }
        }
        // 문자 입력을 할때..
        else{ titleArray = new String[10];
            titleArray= "삼성코테같이준비해요,오픽AL5번연속배출모임,test1,test2".split(",");
            // 리스트의 모든 데이터를 검색한다.
            for(int i = titleArray.length-1;i >=0; i--)
            {
                // arraylist의 모든 데이터에 입력받은 단어(charText)가 포함되어 있으면 true를 반환한다.
                if (titleArray[i].contains(charText))
                {
                    // 검색된 데이터를 리스트에 추가한다.
                    groupAdapter.add(0, "삼성코테같이준비해요", "모든", "타이틀", "하반기무조건합격보장해드림", "없음", 1, 4);
                }
            }
        }
        // 리스트 데이터가 변경되었으므로 아답터를 갱신하여 검색된 데이터를 화면에 보여준다.
        groupAdapter.notifyDataSetChanged();
    }

    //새로고침 코드
//    @Override
//    public void onRefresh(){
//        mSwipeRefreshLayout.setRefreshing(true);
//        final String BASE = SharedPreference.getAttribute(context.getApplicationContext(), "IP");
//
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() { // 여기에 코드 추가
//                Retrofit retrofit1 = new Retrofit.Builder()
//                        .baseUrl(BASE)
//                        .addConverterFactory(GsonConverterFactory.create())
//                        .build();
//
//                ChattingInformationInterface chattingInformationInterface = retrofit1.create(ChattingInformationInterface.class);
//                Call<List<Dummy3>> call1 = chattingInformationInterface.listDummies(userId);
//                call1.enqueue(dummies1);
//
//            }
//        },1000); // 1초후에 새로고침 끝
//
//    }
    Callback dummies3 = new Callback<List<Dummy3>>() {

        @Override
        public void onResponse(Call<List<Dummy3>> call, Response<List<Dummy3>> response) {
            if (response.isSuccessful()) {
                List<Dummy3> dummies = response.body();
                for(int i=0;i<dummies.size();i++){
                    Log.d("tag"+i,dummies.get(i).getId().toString());
                }

                String[] result;
            }
        }

        @Override
        public void onFailure(Call<List<Dummy3>> call1, Throwable t) {

        }
    };




}