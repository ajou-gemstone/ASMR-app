package com.example.capstonedesignandroid.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import com.example.capstonedesignandroid.DTO.Group;
import com.example.capstonedesignandroid.DTO.User;
import com.example.capstonedesignandroid.GroupService;
import com.example.capstonedesignandroid.R;
import com.example.capstonedesignandroid.ReadGroupActivity;
import com.example.capstonedesignandroid.StaticMethodAndOthers.MyConstants;
import com.example.capstonedesignandroid.StaticMethodAndOthers.SharedPreference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GroupFragment1 extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    Intent intent1,intent2;
    ArrayAdapter adapter;
    Button search;
    String userId;
    TextView text;
    SwipeRefreshLayout mSwipeRefreshLayout;
    Context context;
    ArrayList<String> list, titleArray, tagArray, categoryArray, tempArray;
    ArrayList<Integer> idArray, currentNumArray, totalNumArray, idListviewArray;
    EditText editSearch;
    GroupListAdapter grouplistAdapter = new GroupListAdapter();
    ListView listview;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.group_fragment_1, container, false);
        mSwipeRefreshLayout = view.findViewById(R.id.refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        context = container.getContext();
        editSearch = view.findViewById(R.id.editSearch);
        search = view.findViewById(R.id.search);
        titleArray = new ArrayList<>();
        tagArray = new ArrayList<>();
        categoryArray = new ArrayList<>();
        idArray = new ArrayList<>();
        currentNumArray = new ArrayList<>();
        totalNumArray = new ArrayList<>();
        tempArray = new ArrayList<>();
        idListviewArray = new ArrayList<>();

        text = (TextView) view.findViewById(R.id.text);
        list = new ArrayList<>();
        adapter = new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1,list);

        userId = SharedPreference.getAttribute(getActivity().getApplicationContext(), "userId");

        Retrofit retrofit2 = new Retrofit.Builder()
                .baseUrl(MyConstants.BASE)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GroupService groupservice = retrofit2.create(GroupService.class);
        Call<List<Group>> call = groupservice.getStudyList();
        CallThread(call);


        listview = (ListView)view.findViewById(R.id.listview1);
        listview.setAdapter(grouplistAdapter);
        //모든 목록 추가하기
        for (int i = 0; i <= titleArray.size() - 1; i++) {
            if(categoryArray.get(i).equals("all"))
                grouplistAdapter.add(idArray.get(i), tagArray.get(i), titleArray.get(i), categoryArray.get(i), currentNumArray.get(i), totalNumArray.get(i));
        }

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

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                intent2 = new Intent(getActivity(), ReadGroupActivity.class);
                intent2.putExtra("groupId", idListviewArray.get(position).toString());
                startActivity(intent2);
            }
        });

        return view;
    } //onCreateView

    private void CallThread(Call<List<Group>> call) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<Group> dummies = call.execute().body();
                    tagArray.clear(); idArray.clear(); titleArray.clear(); categoryArray.clear(); currentNumArray.clear(); totalNumArray.clear(); idListviewArray.clear();
                    for(int i = 0; i< dummies.size(); i++){
                        if(dummies.get(dummies.size()-1-i).getCategory().equals("all")) {
                            String tag = "";
                            if (dummies.get(dummies.size()-1-i).getTagName().size() != 0) {
                                for (int t = 0; t < dummies.get(dummies.size()-1-i).getTagName().size(); t++) {
                                    tag = tag + "#" + dummies.get(dummies.size()-1-i).getTagName().get(t).getTagName() + " ";
                                }
                            }
                            tagArray.add(tag);

                            idArray.add(dummies.get(dummies.size()-1-i).getId());
                            idListviewArray.add(dummies.get(dummies.size()-1-i).getId());
                            titleArray.add(dummies.get(dummies.size()-1-i).getTitle());
                            categoryArray.add(dummies.get(dummies.size()-1-i).getCategory());
                            currentNumArray.add(dummies.get(dummies.size()-1-i).getStudyGroupNumCurrent());
                            totalNumArray.add(dummies.get(dummies.size()-1-i).getStudyGroupNumTotal());
                        }
                    }
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

    //모임 제목, 태그 검색
    public void search(String charText) {

        grouplistAdapter.clear();
        idListviewArray.clear();

        if (charText.length() == 0) {
            for (int i = 0; i <= titleArray.size() - 1; i++) {
                if(categoryArray.get(i).equals("all")) {
                    grouplistAdapter.add(idArray.get(i), tagArray.get(i), titleArray.get(i), categoryArray.get(i), currentNumArray.get(i), totalNumArray.get(i));
                    idListviewArray.add(idArray.get(i));
                }
            }
        }
        else{
            for (int i = 0; i <= titleArray.size() - 1; i++) {
                if(categoryArray.get(i).equals("all")) {
                    if (titleArray.get(i).contains(charText)) {
                        grouplistAdapter.add(idArray.get(i), tagArray.get(i), titleArray.get(i), categoryArray.get(i), currentNumArray.get(i), totalNumArray.get(i));
                        idListviewArray.add(idArray.get(i));
                    } else if (tagArray.get(i).contains(charText)) {
                        grouplistAdapter.add(idArray.get(i), tagArray.get(i), titleArray.get(i), categoryArray.get(i), currentNumArray.get(i), totalNumArray.get(i));
                        idListviewArray.add(idArray.get(i));
                    }
                }
            }
        }
        grouplistAdapter.notifyDataSetChanged();
    }

    //새로고침 코드
    @Override
    public void onRefresh(){
        mSwipeRefreshLayout.setRefreshing(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() { // 여기에 코드 추가
                if (editSearch.getText().toString().equals("")) {
                    Retrofit retrofit1 = new Retrofit.Builder()
                            .baseUrl(MyConstants.BASE)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    GroupService groupService = retrofit1.create(GroupService.class);
                    Call<List<Group>> call1 = groupService.getStudyList();
                    CallThread(call1);
                    listview.setAdapter(grouplistAdapter);
                    grouplistAdapter.clear();
                    //모든 목록 추가하기
                    for (int i = 0; i <= titleArray.size() - 1; i++) {
                        if (categoryArray.get(i).equals("all")) {
                            grouplistAdapter.add(idArray.get(i), tagArray.get(i), titleArray.get(i), categoryArray.get(i), currentNumArray.get(i), totalNumArray.get(i));
                        }
                    }
                    grouplistAdapter.notifyDataSetChanged();

                }
            }
        },100); 
        // 새로고침 완료
        mSwipeRefreshLayout.setRefreshing(false);
    }//onRefresh

}