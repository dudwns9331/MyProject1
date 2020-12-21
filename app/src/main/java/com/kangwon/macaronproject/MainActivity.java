package com.kangwon.macaronproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kangwon.macaronproject.add.inputActivity;
import com.kangwon.macaronproject.databinding.ActivityMainBinding;
import com.kangwon.macaronproject.decorators.EventDecorator;
import com.kangwon.macaronproject.decorators.OneDayDecorator;
import com.kangwon.macaronproject.decorators.SaturdayDecorator;
import com.kangwon.macaronproject.decorators.SundayDecorator;
import com.kangwon.macaronproject.env.Env;
import com.kangwon.macaronproject.login.BaseActivity;
import com.kangwon.macaronproject.login.LoginActivity;
import com.kangwon.macaronproject.login.MemberInfoActivity;
import com.kangwon.macaronproject.notice_board.NoticeActivity;
import com.kangwon.macaronproject.salary.Salary;
import com.kangwon.macaronproject.view_cal.list_fragment;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnRangeSelectedListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executors;

// import android.util.Log;
// import android.widget.Toast;

public class MainActivity extends BaseActivity {

    int Year, Month, Day;       // 선택된 날짜 연도, 달, 일 기록하는 변수
    private final OneDayDecorator oneDayDecorator = new OneDayDecorator(); // 현재 날짜를 커스텀하기 위한 데코레이터
    MaterialCalendarView materialCalendarView;
    ArrayList<String> selected_list;            // 선택된 날짜 String 값으로 저장하는 리스트
    ArrayList<String> selected_list2;            // 선택된 날짜 String 값으로 저장하는 리스트
    static ArrayList<String> selected_list3 = new ArrayList<String>();            // 선택된 날짜 String 값으로 저장하는 리스트
    static ArrayList<String> delete_list;            // 선택된 날짜 String 값으로 저장하는 리스트
    ArrayList<Integer> day_list;
    private int selected_date;
    private int position;
    public boolean mode;            // 날짜를 선택하는 모드가 무엇인지 -> 다중 선택모드(여러개 따로 선택), 범위선택(처음과 끝까지)
    private int count = 0;      // 날짜 범위 선택시 첫번째 선택인지, 두번째 선택인지 지정.
    list_fragment list_fragment;
    Button add_button;
    Button select_all_range;
    Button clear_button;
    Button modify_button;
    private long time = 0;
    boolean is_modify;


    static boolean is_hidden = false;

    ArrayList<String> temp = new ArrayList<>();
    ;

    ActionBar actionBar;

    private static final String TAG = "MainActivity";

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private ActivityMainBinding binding;

    @Override
    protected void onResume() {
        super.onResume();
        try {
            selected_list2 = (ArrayList<String>) getIntent().getSerializableExtra("work_data_new");
            delete_list = (ArrayList<String>) getIntent().getSerializableExtra("work_data_delete");

//            if (!selected_list2.isEmpty()) {
//                for (int i = 0; i < selected_list2.size(); i++) {
//                    if (selected_list2.get(i) != null) {
//                        if (!selected_list3.contains(selected_list2.get(i)))
//                            selected_list3.add(selected_list2.get(i));
//                    }
//                }
//            }
//            int index;
//            if (!delete_list.isEmpty()) {
//                for (int i = 0; i < delete_list.size(); i++) {
//                    if (delete_list.get(i) != null) {
//                        index = selected_list3.indexOf(delete_list.get(i));
////                        Toast.makeText(this, delete_list.get(i), Toast.LENGTH_SHORT).show();
//                        selected_list3.remove(index);
//                    }
//                }
//            }
            new ApiSimulator(temp).executeOnExecutor(Executors.newSingleThreadExecutor());
            day_list = new ArrayList<>();

            if (selected_list2.size() != 0) {
                for (int i = 0; i < selected_list2.size(); i++) {
                    String[] result = selected_list2.get(i).split("-");
                    int dayy = Integer.parseInt(result[2]);
                    day_list.add(dayy);
                }
            }

            select_all_range.setVisibility(View.GONE);
            add_button.setVisibility(View.GONE);
            clear_button.setVisibility(View.GONE);
            modify_button.setVisibility(View.VISIBLE);


            materialCalendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_SINGLE);
            is_modify = true;


        } catch (Exception e) {

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_calendar);

        actionBar = getSupportActionBar();

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();


        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
//        if(mDatabase.child("users").child(mUser.getUid()).child("isOwner").getValue(Boolean.class)){
//            Toast.makeText(this, "test", Toast.LENGTH_SHORT).show();
//        }

        mDatabase.child("schedule").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String str = dataSnapshot.getKey();
                    temp.add(str);
                }
                new ApiSimulator(temp).executeOnExecutor(Executors.newSingleThreadExecutor());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Log.d("get value from mDatabase: ", temp.toString());

        materialCalendarView = findViewById(R.id.calendarView);     // 캘린더 아이디 지정
        add_button = findViewById(R.id.add_button);          // 추가 버튼
        clear_button = findViewById(R.id.clear_button);      // 선택 해제 버튼
        select_all_range = findViewById(R.id.select_range); // 범위선택 버튼
        modify_button = findViewById(R.id.modify_button);

        mode = true;

        final Intent intent = getIntent();
        selected_list = (ArrayList<String>) intent.getSerializableExtra("work_data");

        list_fragment = (list_fragment) getSupportFragmentManager().findFragmentById(R.id.list_fragment);

        // 달력 초기 설정
        materialCalendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setMinimumDate(CalendarDay.from(2020, 0, 1))     // 달력 표시 최소범위
                .setMaximumDate(CalendarDay.from(2030, 11, 31))   // 달려 표시 최대 범위
                .setCalendarDisplayMode(CalendarMode.MONTHS)                    // 달로 보여주기 WEEK 도 가능
                .commit();

        //달력 선택 모드, 표현 범위
        materialCalendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_MULTIPLE);

        //달력에 토요일, 일요일, 현재 날짜 표시하기 위해 참조
        materialCalendarView.addDecorators(
                new SaturdayDecorator(), //토요일 표시기
                new SundayDecorator(),  // 일요일 표시기
                oneDayDecorator);       // 하루 표시기

        selected_list = new ArrayList<>();        // 선택된 날짜 리스트 -> 클릭했을 때 색으로 변하는 날짜들은 여기에 들어감

        // input 에서 수정한 리스트를 들고온다.

        // 만약 선택되었다면
        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {

                if (is_modify) {
                    if (selected_list2 != null) {
                        if (selected) {
                            selected_date = date.getDay();
                            for (int i = 0; i < selected_list2.size(); i++) {
                                if (selected_date == day_list.get(i))
                                    position = i;
                            }
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    list_fragment.recyclerView.smoothScrollToPosition(position);
                                }
                            }, 700);
                        } else {
                            position = 0;
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    list_fragment.recyclerView.smoothScrollToPosition(position);
                                }
                            }, 700);
                        }
                    }
                } else {
                    // 날짜가 선택되었다면,
                    if (mode) { // mode = true 시 다중선택모드
                        Year = date.getYear();
                        Month = date.getMonth() + 1;        // 달의 값은 하나 적어서 +1
                        Day = date.getDay();

                        String shot_Day = Year + "-" + Month + "-" + Day; // 선택한 날짜 2020,00,00 형식으로 들어감.

                        if (selected) {
                            if (!temp.contains(shot_Day))
                                selected_list.add(shot_Day); // 만약에 선택되었다면 -> 리스트에 추가 "2020,00,00"
                        } else selected_list.remove(shot_Day);       // 선택 해제 시 리스트에서 제거
                    }
                }
            }
        });

        // 범위선택 시
        materialCalendarView.setOnRangeSelectedListener(new OnRangeSelectedListener() {
            @Override
            public void onRangeSelected(@NonNull MaterialCalendarView widget, @NonNull List<CalendarDay> dates) {

                if (count == 1) { // 두번째 선택될 때
                    selected_list.clear();  // 이전에 선택됐던 값 제거
                    count = 0;  // 처음 선택으로 되돌림
                }

                if (!mode) {    // 범위 선택일때,
                    for (int i = 0; i < dates.size(); i++) {
                        // 선택된 날짜들 리스트 dates 불러와서 연도, 달, 일 저장
                        Year = dates.get(i).getYear();
                        Month = dates.get(i).getMonth() + 1;
                        Day = dates.get(i).getDay();

                        String shot_Day = Year + "-" + Month + "-" + Day; // 선택한 날짜 2020,00,00 형식으로 들어감.
                        selected_list.add(shot_Day);
                    }
                    count++; // 범위로 선택 한번 하면 카운트 증가
                }
            }
        });

        final String str = null;
        // 추가 버튼 누르면 수행 -> 선택된 날짜 리스트 "2020,00,00" intent 로 보내고 inputActivity 시작함.
        // inputActivity 는 선택한 날짜를 recyclerView 로 목록을 만들어 보여주는 엑티비티입니다.
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!selected_list.isEmpty()) {
                    for (int i = 0; i < selected_list.size(); i++) {
                        if (selected_list.get(i) != null)
                            if (!temp.contains(selected_list.get(i)))
                                temp.add(selected_list.get(i));
                    }
                }
                Intent input_activity = new Intent(getApplicationContext(), inputActivity.class);   // 인텐트 생성
                order_date(temp);      // 선택된 날짜 이른 날짜부터 정렬
                input_activity.putExtra("work_data", temp);        // "work_data" 로 선택된 리스트 넘김

//                input_activity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(input_activity);
            }
        });

        // 삭제 버튼일때,
        clear_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selected_list.clear();      // 리스트 초기화
                materialCalendarView.clearSelection();  // 선택으로 칠해진 날짜들 초기화

            }
        });

        // 범위 선택 버튼이 눌렸을때,
        select_all_range.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mode) {
                    materialCalendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_RANGE); // 달력 선택 모드 바꿈 -> 범위
                    select_all_range.setText("다중 선택");
                    mode = false;
                    count = 0;
                } else {
                    materialCalendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_MULTIPLE); // 달력 선택 모드 바꿈 -> 다중
                    select_all_range.setText("범위 선택");
                    mode = true;
                }
            }
        });

        // 수정 버튼
        modify_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                select_all_range.setVisibility(View.VISIBLE);
                add_button.setVisibility(View.VISIBLE);
                clear_button.setVisibility(View.VISIBLE);
                modify_button.setVisibility(View.GONE);
                materialCalendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_MULTIPLE);
                is_modify = false;
                selected_list.clear();      // 리스트 초기화
                materialCalendarView.clearSelection();  // 선택으로 칠해진 날짜들 초기화
            }
        });
    }

    private class ApiSimulator extends AsyncTask<Void, Void, List<CalendarDay>> {

        ArrayList<String> Time_Result;

        private ApiSimulator(ArrayList<String> Time_Result) {
            this.Time_Result = Time_Result;
        }

        @Override
        protected List<CalendarDay> doInBackground(Void... voids) {

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Calendar calendar = Calendar.getInstance();
            ArrayList<CalendarDay> dates = new ArrayList<>();
            calendar.add(Calendar.MONTH, 0);

            /**
             * 특정날짜 달력에 점 표시 해주는 곳
             * 월을 0이 1월 년, 일은 그대로 한다.
             * String 문자열인 Time_Result 를 받아와서 ,를 기준으로 자르고 String을 int로 변환
             * 아직 안씀.
             */

            for (int i = 0; i < Time_Result.size(); i++) {
                String[] time = Time_Result.get(i).split("-");
                int year = Integer.parseInt(time[0]);
                int month = Integer.parseInt(time[1]);
                int dayy = Integer.parseInt(time[2]);
                CalendarDay day = CalendarDay.from(year, month - 1, dayy);
                dates.add(day);
                calendar.set(year, month - 1, dayy);
            }
            return dates;
        }

        @Override
        protected void onPostExecute(List<CalendarDay> calendarDays) {
            super.onPostExecute(calendarDays);

            if (isFinishing()) {
                return;
            }
            materialCalendarView.addDecorator(new EventDecorator(Color.MAGENTA, calendarDays, MainActivity.this));
        }
    }


    /**
     * 날짜순으로 정렬한다.
     *
     * @param list 선택된 날짜 리스트
     */
    public void order_date(ArrayList<String> list) {
        Collections.sort(list, new Comparator<String>() {
            @Override
            public int compare(String t1, String t2) {
                String[] result1 = t1.split("-");
                String[] result2 = t2.split("-");

                StringBuilder time1 = new StringBuilder();
                StringBuilder time2 = new StringBuilder();

                for (int i = 0; i < 3; i++) {
                    time1.append(result1[i]);
                    time2.append(result2[i]);
                }
                return Integer.compare(Integer.parseInt(time1.toString()), Integer.parseInt(time2.toString()));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu); // 인플레이터로 객체화 시키기
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) { // 메뉴 아이템의 아이디 가져옴.
        int curId = item.getItemId();
        switch (curId) {

            case R.id.board:
                startActivity(new Intent(MainActivity.this, NoticeActivity.class));
                break;

            case R.id.mainupdate:
                Intent intent = new Intent(MainActivity.this, MemberInfoActivity.class);
                intent.putExtra("from", Env.MAIN);
                startActivity(intent);
                finish();
                break;
            case R.id.action_logout:
                Intent intent1 = new Intent(this, LoginActivity.class);
                mAuth.signOut();
                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent1);
//                finish();
                break;

            case R.id.salary_menu :
                Intent intent2 = new Intent(this, Salary.class);
                intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent2);
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private long backKeyPressedTime = 0;
    @Override
    public void onBackPressed() {

//        super.onBackPressed();

        if (System.currentTimeMillis() - backKeyPressedTime >= 800) {
            backKeyPressedTime = System.currentTimeMillis();
            Toast.makeText(this, "한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
        } else if (System.currentTimeMillis() - backKeyPressedTime < 800) {
            ActivityCompat.finishAffinity(this);
        }

    }
    //    private void signout() {
//    }
}