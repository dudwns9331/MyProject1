package com.kangwon.macaronproject.add;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.kangwon.macaronproject.MainActivity;
import com.kangwon.macaronproject.R;
import com.kangwon.macaronproject.env.Env;
import com.kangwon.macaronproject.swipefunction.ItemTouchHelperCallback;

import java.util.ArrayList;

public class inputActivity extends AppCompatActivity {


    ItemTouchHelper itemTouchHelper;
    RecyclerView recyclerView;

    Button select_all_button;       // 모두 선택 버튼
    Button delete_button;           // 지우기 버튼
    Button save_button;             // 저장 버튼
    Work_date work_date;


    private DatabaseReference mDatabase;

    public ArrayList<String> data;
    public ArrayList<String> data2 = new ArrayList<String>();
    public ArrayList<String> delete_data = new ArrayList<String>();
    @SuppressLint("StaticFieldLeak")
    public static Work_date_adapter date_adapter;


    @Override
    protected void onResume() {
        super.onResume();
        setUpRecyclerView();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        select_all_button = findViewById(R.id.select_all);      // 선택 버튼
        delete_button = findViewById(R.id.delete);              // 지우기 버튼
        save_button = findViewById(R.id.save);                  // 저장 버튼


        if(!Env.checker) {
            select_all_button.setVisibility(View.INVISIBLE);
            delete_button.setVisibility(View.INVISIBLE);
            save_button.setText("뒤로가기");
        } else {
            save_button.setText("일정 저장");
            select_all_button.setVisibility(View.VISIBLE);
            delete_button.setVisibility(View.VISIBLE);
        }

        // 리싸이클러뷰 -> 달력에서 넣은 날짜 배열 리스트로 표현해서 보여줌
        recyclerView = findViewById(R.id.recyclerView);

        // 레이아웃 지정
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);


        // 어댑터 추가
        date_adapter = new Work_date_adapter(this);

        // 어댑터 보여주기
        recyclerView.setAdapter(date_adapter);

        // 스와이프, 터치 관련 인터페이스, 클래스 활용

        if(Env.checker) {
            itemTouchHelper = new ItemTouchHelper(new ItemTouchHelperCallback(date_adapter));
            itemTouchHelper.attachToRecyclerView(recyclerView);
        }

        // 메인 엑티비티(달력) 에서 intent로 값 불러옴
        Intent intent = getIntent();
        data = (ArrayList<String>) intent.getSerializableExtra("work_data"); // 배열안에 날짜 들어가 있음.
        assert data != null;
        for (int i = 0; i < data.size(); i++) {
            // , 으로 구분된 날짜 하나씩 찢기
            String[] result = data.get(i).split("-");


            int year = Integer.parseInt(result[0]);
            int month = Integer.parseInt(result[1]);
            int day = Integer.parseInt(result[2]);
            String worker = null;
            work_date = new Work_date(year, month, day, worker);
            work_date.setWork_time(null);

            date_adapter.addItem(work_date);    // work_date 객체에  year, month, day, worker 의 값이 들어간다.
        }

        // 삭제 버튼 눌렸을때, 삭제
        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = date_adapter.getItemCount() - 1; i >= 0; i--) {
                    if (date_adapter.isItemSelected(i)) {    // 리스트에 있는 날짜가 선택되었을 때,
                        date_adapter.deleteItem(date_adapter.getItem(i));   // 리스트에 아이템을 지운다.
                        date_adapter.notifyItemRemoved(i);                  // 지운 날짜 업데이트
                        date_adapter.notifyItemRangeChanged(i, date_adapter.getItemCount()); // 지운 날짜에 대해서 전체 업데이트
                    }
                }
                date_adapter.clearSelectedItem();   // 지우기 되면 선택된 모든 효과는 사라진다.
            }
        });

        // 전체 삭제 눌렸을때
        select_all_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < date_adapter.getItemCount(); i++)
                    date_adapter.toggleItemSelected(i); // 선택된 리스트 모두 삭제
            }
        });


        /**
         * 저장 버튼이 눌렸을 때, list_fragment 프래그먼트에 어댑터에 추가했던 리스트를 그대로 전달한다.
         */
        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        });
        setUpRecyclerView();
    }

    //리싸이클려 뷰 다시 그려줌
    private void setUpRecyclerView() {
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.onDraw(c, parent, state);
            }
        });
    }


    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }


}