package yj.p.mymacaron.add;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import yj.p.mymacaron.R;

import java.util.Objects;

/**
 * inputActivity 에서 스와이프 하고 수정 버튼을 누르면 수행되는 클래스 (다이얼로그)
 */
public class CustomDialog extends Dialog {

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    private OnDialogListener listener;
    private Context context;
    private EditText mod_name;
    private TextView start_time;
    private TextView end_time;

    public String work_time;

    private String name;
    private int s_Hour, s_Minute;
    private int e_Hour, e_Minute;
    private String uid;
    String s_time;
    String e_time;

    @SuppressLint("SetTextI18n")
    public CustomDialog(final Context context, final int position, final Work_date date) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        (Objects.requireNonNull(getWindow())).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.customdialog);


        mod_name = findViewById(R.id.mod_name);
        mod_name.setText(null);

        // 근무자 추가 버튼
        Button mod_button = findViewById(R.id.mod_button);
        mod_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    name = mod_name.getText().toString();


                    if (name.length() >= 2 && s_time != null && e_time != null) {
                        date.setWorker(name);
//                        date.addWorker(name);
                        work_time = s_time + " ~ " + e_time;

                        date.setStart_time(s_time);      // 시작시간
                        date.setEnd_time(e_time);        // 종료시간

                        date.setWork_time(work_time);
//                        date.addwork_time(work_time);

                        mDatabase.child("users").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    String str = dataSnapshot.child("username").getValue(String.class);
                                    if (str != null && str.equals(name)) {
                                        uid = dataSnapshot.getKey();
//                                        Toast.makeText(context, uid, Toast.LENGTH_SHORT).show();
                                        break;
                                    }
                                }
                                if (uid == null) {
                                    Toast.makeText(context, "get uid Failed", Toast.LENGTH_SHORT).show();
                                } else {
                                    mDatabase.child("schedule").child(date.getDateData()).child(uid).setValue(date.toMap());
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    } else {
                        Toast.makeText(context, "이름을 입력하세요.", Toast.LENGTH_SHORT).show();
                        Toast.makeText(context, "시작 시간과 종료 시간을 설정 해 주세요.", Toast.LENGTH_SHORT).show();
                    }
//                  Toast.makeText(context, date.getWorkerall(), Toast.LENGTH_SHORT).show();
                    listener.onFinish(position, date);
                    dismiss();
                }

            }
        });

        // 시작 시간 추가 버튼
        start_time = findViewById(R.id.start_time);

        start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                        s_Hour = hourOfDay;
                        s_Minute = minute;
                        s_time = s_Hour + "시" + s_Minute + "분";
                        start_time.setText(s_time);
                    }

                }, s_Hour, s_Minute, false);
                timePickerDialog.show();
            }
        });

        // 종료 시간 버튼
        end_time = findViewById(R.id.end_time);
        end_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                        e_Hour = hourOfDay;
                        e_Minute = minute;
                        e_time = e_Hour + "시" + e_Minute + "분";
                        end_time.setText(e_time);
                    }

                }, e_Hour, e_Minute, false);
                timePickerDialog.show();
            }
        });
    }

    public void setDialogListener(OnDialogListener listener) {
        this.listener = listener;
    }
}
