package com.kangwon.macaronproject.salary;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kangwon.macaronproject.R;
import com.kangwon.macaronproject.add.Work_date;
import com.kangwon.macaronproject.add.Work_date_adapter;
import com.kangwon.macaronproject.models.User;

// 2020/12/21 수정
public class Salary extends AppCompatActivity {

    Button search_btn;
    TextView worker_info;
    TextView salary;

    Work_date work_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salary);

        DatabaseReference mDatabase;

        search_btn = findViewById(R.id.search_button);
        worker_info = findViewById(R.id.worker_info);
        salary = findViewById(R.id.salary);


        mDatabase = FirebaseDatabase.getInstance().getReference();

        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

//        Work_date_adapter work_date_adapter = new Work_date_adapter();

        mDatabase.child("schedule").child("2020-12-15").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String str = dataSnapshot.child("worker").getValue(String.class);
                    worker_info.setText(str);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}