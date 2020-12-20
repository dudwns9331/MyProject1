package com.kangwon.macaronproject.add;

import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kangwon.macaronproject.R;
import com.kangwon.macaronproject.swipefunction.ItemTouchHelperListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Work_date_adapter extends RecyclerView.Adapter<Work_date_adapter.ViewHolder>
        implements ItemTouchHelperListener, OnDialogListener, OnWorkDateClickListener {

    private int count = 0;
    private String date;

    private Map<String, Object> workers = new HashMap<>();
    private ArrayList<String> start_time;
    private ArrayList<String> end_time;

    private final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    // 선택된 아이템을 위해서 사용
    private SparseBooleanArray mSelectedItems = new SparseBooleanArray(0);
    ArrayList<Work_date> items = new ArrayList<>();     // 리스트에 들어있는 원소들
    Context context;
    OnWorkDateClickListener listener;

    // context 전달
    public Work_date_adapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.date_item, viewGroup, false);

        return new ViewHolder(itemView, this);
        // 뷰 생성해주기
    }

    // 리싸이클러 뷰를 그려주는 함수
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Work_date item = items.get(position);
        viewHolder.setItem(item);
        // 아이템이 선택되었다면
        if (isItemSelected(position)) {
            viewHolder.textView4.setTextColor(Color.MAGENTA); // 이 색으로 선택된거 배경 표시
            viewHolder.textView5.setTextColor(Color.MAGENTA); // 이 색으로 선택된거 배경 표시
        } else {
            viewHolder.textView4.setTextColor(Color.BLACK); // 이 색으로 선택된거 배경 표시
            viewHolder.textView5.setTextColor(Color.BLACK); // 이 색으로 선택된거 배경 표시
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(Work_date item) {

        String date = item.getDateData();
        mDatabase.child("schedule").child(date).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String worker = dataSnapshot.child("worker").getValue(String.class);
                    String end_time = dataSnapshot.child("end_time").getValue(String.class);
                    String start_time = dataSnapshot.child("start_time").getValue(String.class);
                    item.addWorker(worker);
                    item.addwork_time(start_time + "~" + end_time);
                    item.setWorker(worker);
                    item.setWork_time(start_time + "~" + end_time);
                }
                notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "someting wrong", Toast.LENGTH_SHORT).show();
            }
        });
        items.add(item);
    }

    public void deleteItem(Work_date item) {
        items.remove(item);
    }

    public void setItems(ArrayList<Work_date> items) {
        this.items = items;
    }

    public Work_date getItem(int position) {
        return items.get(position);
    }

    public void setItem(int position, Work_date item) {
        items.set(position, item);
    }

    // 리스트가 선택된된건지 아닌지 토글로 설정해주는 함수
    public void toggleItemSelected(int position) {
        if (mSelectedItems.get(position, false)) {
            mSelectedItems.delete(position);
        } else {
            mSelectedItems.put(position, true);
        }
        notifyItemChanged(position);
    }

    // 아이템이 선택되었는지 아닌지를 알려줌
    public boolean isItemSelected(int position) {
        return mSelectedItems.get(position, false);
    }

    // 선택된거 모두 취소함
    public void clearSelectedItem() {
        int position;
        for (int i = 0; i < mSelectedItems.size(); i++) {
            position = mSelectedItems.keyAt(i);
            mSelectedItems.put(position, false);
            notifyItemChanged(position);
        }
        mSelectedItems.clear();
    }

    // 아이템이 이동했을때 업데이트 시켜주는 함수 삭제, 추가 등등
    @Override
    public boolean onItemMove(int from_position, int to_position) {
        Work_date work_date = items.get(from_position);
        items.remove(from_position);
//        items.add(to_position, work_date);
        notifyItemMoved(from_position, to_position);
        return true;
    }

    // 아이템을 왼쪽이나 오른쪽으로 스와이프 한 경우
    @Override
    public void onItemSwipe(int position) {
        items.remove(position);
        notifyItemRemoved(position);
    }

    // 스와이프 후 왼쪽에 수정 버튼을 누르면 수행되는 함수
    // 수정 클릭 시 수정 다이얼로그 띄워줌

    @Override
    public void onLeftClick(int position, RecyclerView.ViewHolder viewHolder) {

        count++;

        CustomDialog dialog = new CustomDialog(context, position, items.get(position));
        DisplayMetrics dm = context.getApplicationContext().getResources().getDisplayMetrics();

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        WindowManager.LayoutParams wm = Objects.requireNonNull(dialog.getWindow()).getAttributes();
        wm.copyFrom(dialog.getWindow().getAttributes());
        wm.width = (int) (width * 0.7);
        wm.height = height / 3;

        dialog.setDialogListener(this);
        dialog.show();

    }

    // 오른쪽에 있는 버튼 클릭시 삭제
    @Override
    public void onRightClick(int position, RecyclerView.ViewHolder viewHolder) {
        mDatabase.child("schedule").child(items.remove(position).getDateData()).removeValue();
        notifyItemRemoved(position);
    }

    // 아이템 설정이 끝나면 업데이트
    @Override
    public void onFinish(int position, Work_date work_date) {
        items.set(position, work_date);
        notifyItemChanged(position);
    }

    // 아이템이 클릭 되었을 때,
    @Override
    public void onItemClick(ViewHolder holder, View view, int position) {
        if (listener != null) {
            listener.onItemClick(holder, view, position);
        }
    }

    public void setOnItemClickListener(OnWorkDateClickListener listener) {
        this.listener = listener;
    }


    /**
     * 뷰 홀더 보이는 뷰들을 조정함
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        TextView textView2;
        TextView textView3;
        TextView textView4;
        TextView textView5;

        public ViewHolder(View item, final OnWorkDateClickListener listener) {
            super(item);
            // 여기있는 텍스트뷰들이 아이템 하나에 저장되는겁니다.
            textView = item.findViewById(R.id.year_data);
            textView2 = item.findViewById(R.id.month_data);
            textView3 = item.findViewById(R.id.day_data);
            textView4 = item.findViewById(R.id.name);
            textView5 = item.findViewById(R.id.time);

            // 눌렀을때 선택되게함.
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    toggleItemSelected(position);
/*
                    Log.d("test", "position = " + position);
                    Toast.makeText(context, mSelectedItems.toString(), Toast.LENGTH_SHORT).show();
*/
                }
            });
        }

        // 리싸이클러 뷰에서 보이는 뷰들 설정
        public void setItem(Work_date item) {
            textView.setText(String.valueOf(item.getYear()));
            textView2.setText(String.valueOf(item.getMonth()));
            textView3.setText(String.valueOf(item.getDate()));

            int i = 0;

            date = String.valueOf(item.getYear()) + String.valueOf(item.getMonth()) + String.valueOf(item.getDate());
//            Toast.makeText(context, date, Toast.LENGTH_SHORT).show();

            textView4.setText(item.getWorkerall());
            textView5.setText(item.getWork_timeall());
        }
    }
}

