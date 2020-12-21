package com.kangwon.macaronproject.salary;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kangwon.macaronproject.R;
import com.kangwon.macaronproject.add.Work_date;
import com.kangwon.macaronproject.add.Work_date_adapter;
import com.kangwon.macaronproject.env.Env;
import com.kangwon.macaronproject.models.User;

import java.util.Objects;

// 2020/12/21 수정
public class Salary extends AppCompatActivity {

    Button search_btn;
    TextView worker_info;
    TextView salary;
    String worker_name;

    EditText search_id;
    EditText salary_per_hour;

    Work_date work_date;
    long full_time = 0;
    long salary_per_hour_value = 0;
    String employee_id;

    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salary);


        search_btn = findViewById(R.id.search_button);
        salary = findViewById(R.id.salary);
        search_id = findViewById(R.id.worker_id);
        salary_per_hour = findViewById(R.id.salary_per_hour);

        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                worker_name = search_id.getText().toString();
                salary_per_hour_value = Integer.parseInt(salary_per_hour.getText().toString());
                full_time = 0;
                mDatabase.child("schedule").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot datasnapshot : snapshot.getChildren()) {
                            for (DataSnapshot daydata : datasnapshot.getChildren()) {
                                if (Objects.equals(daydata.child("worker").getValue(String.class), worker_name)) {
                                    if(full_time == 0)full_time = Env.split_time(daydata.child("end_time")
                                            .getValue(String.class), daydata.child("start_time").getValue(String.class));
                                    else full_time += Env.split_time(daydata.child("end_time")
                                            .getValue(String.class), daydata.child("start_time").getValue(String.class));
                                    Log.d("Salary: ",daydata.child("worker").getValue()+" "+worker_name+" "+String.valueOf(full_time));

                                }
                            }
                        }
                        salary.setText(worker_name +"님의 급여는 \n" + String.valueOf(full_time * salary_per_hour_value/60) + "원 입니다.");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

//        full_time = 0;
    }

    private void getUserId(String worker_name) {
        mDatabase.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    if(Objects.equals(dataSnapshot.child("username").getValue(String.class), worker_name)){
                        employee_id = dataSnapshot.getKey();
                        Log.d("Salary: ",dataSnapshot.getKey()+"getUserId:SCREEWWWWWEEEDDD");
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
//        FirebaseAuth mAuth = FirebaseAuth.getInstance();
//
        Log.d("Salary: ",employee_id+" getUserId:SCREEWWWWWEEEDDD");

//        return "Yd88TigJsceziPdShJoG9KB1wtf1";
    }
}