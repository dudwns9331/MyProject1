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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kangwon.macaronproject.R;
import com.kangwon.macaronproject.swipefunction.ItemTouchHelperListener;

import java.util.ArrayList;
import java.util.Objects;

public class Firebase_Work_date_adapter extends FirebaseRecyclerAdapter<Work_date, Firebase_Work_date_adapter.ViewHolder> implements OnWorkDateClickListener, ItemTouchHelperListener, OnDialogListener {

    private OnWorkDateClickListener listener;
    private Context context;
    private ArrayList<Work_date> items = new ArrayList<>();
    private int count = 0;
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    private SparseBooleanArray mSelectedItems = new SparseBooleanArray(0);


    public Firebase_Work_date_adapter(@NonNull FirebaseRecyclerOptions<Work_date> options) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull Firebase_Work_date_adapter.ViewHolder holder, int position, @NonNull Work_date model) {
        holder.textView.setText(model.getYear());
        holder.textView2.setText(model.getMonth());
        holder.textView3.setText(model.getDate());
        holder.textView4.setText(model.getWorker());
        holder.textView5.setText(model.getWork_time());

        // 아이템이 선택되었다면
        if (isItemSelected(position)) {
            holder.itemView.setBackgroundColor(Color.CYAN); // 이 색으로 선택된거 배경 표시
        } else {
            holder.itemView.setBackgroundColor(Color.parseColor("#6688ff")); // 리스트의 배경 색 설정
        }
    }

    private boolean isItemSelected(int position) {
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

    @NonNull
    @Override
    public Firebase_Work_date_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.date_item, parent, false);


        return new Firebase_Work_date_adapter.ViewHolder(view, this);
    }

    @Override
    public void onItemClick(Work_date_adapter.ViewHolder holder, View view, int position) {
        if (listener != null) {
            listener.onItemClick(holder, view, position);
        }
    }

    public void toggleItemSelected(int position) {
        if (mSelectedItems.get(position, false)) {
            mSelectedItems.delete(position);
        } else {
            mSelectedItems.put(position, true);
        }
        notifyItemChanged(position);
    }

    // 아이템 설정이 끝나면 업데이트
    @Override
    public void onFinish(int position, Work_date work_date) {
        items.set(position, work_date);
        notifyItemChanged(position);
    }

    // 아이템이 이동했을때 업데이트 시켜주는 함수 삭제, 추가 등등
    @Override
    public boolean onItemMove(int from_position, int to_position) {
        Work_date work_date = items.get(from_position);
        items.remove(from_position);
        items.add(to_position, work_date);
        notifyItemMoved(from_position, to_position);
        return true;
    }

    // 아이템을 왼쪽이나 오른쪽으로 스와이프 한 경우
    @Override
    public void onItemSwipe(int position) {
        items.remove(position);
        notifyItemRemoved(position);
    }

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


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        TextView textView2;
        TextView textView3;
        TextView textView4;
        TextView textView5;

        public ViewHolder(@NonNull View item, final OnWorkDateClickListener listener) {
            super(item);

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
//                    Log.d("test", "position = " + position);
//                    Toast.makeText(context, mSelectedItems.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

}
