package com.kangwon.macaronproject.login;

import androidx.annotation.NonNull;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kangwon.macaronproject.MainActivity;
import com.kangwon.macaronproject.R;
import com.kangwon.macaronproject.databinding.ActivityMemberInfoBinding;
import com.kangwon.macaronproject.env.Env;
import com.kangwon.macaronproject.models.User;

import java.util.HashMap;
import java.util.Map;

public class MemberInfoActivity extends BaseActivity implements View.OnClickListener {

    public static Activity MemberInfoActivity;
    private static final String TAG = "MemberInfoActivity";
    private static final String REQUIRED = "Required";
    private static int CODE;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private ActivityMemberInfoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMemberInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        CODE = intent.getExtras().getInt("from");
        Log.d(TAG, Integer.toString(CODE)+":MAIN:CODENUM");

        // Views
        setProgressBar(R.id.meminfoprogressBar);

        if(!Env.checker) {
            binding.meminfoIsowner.setVisibility(View.INVISIBLE);
        } else {
            binding.meminfoIsowner.setVisibility(View.VISIBLE);
        }

        binding.meminfoupdateBtn.setOnClickListener(this);
        binding.meminforevokeBtn.setOnClickListener(this);
        binding.meminfocancelBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if(i == R.id.meminfoupdateBtn){
            update();
        } else if(i == R.id.meminfocancelBtn) {
            cancel();
        } else if(i == R.id.meminforevokeBtn){
            revoke();
        }
    }



    private void update() {
        String username = binding.meminfousername.getText().toString();
        String phone = binding.meminfophoneNum.getText().toString();
        boolean isowner = binding.meminfoIsowner.isChecked();

        if(TextUtils.isEmpty(username)){
            binding.meminfousername.setError(REQUIRED);
            return;
        }

        final String userId = getUid();
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                if(user == null){
                    Log.d(TAG, "User " + userId + "is unexpectedly null");
                    Toast.makeText(MemberInfoActivity.this, "ERR: Could not fetch user", Toast.LENGTH_SHORT).show();
                } else {
                    writeNewPost(username, phone, isowner);
                    startActivity(new Intent(MemberInfoActivity.this, MainActivity.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "getUser:onCancelled", error.toException());
            }
        });

    }

    private String usernameFromEmail(String email) {
        if(email.contains("@")){
            return email.split("@")[0];
        } else {
            return email;
        }
    }

    // [START write_user_info_out]
    private void writeNewPost(String username, String phone, boolean isowner) {

        FirebaseUser currentUser = mAuth.getCurrentUser();
        String id = usernameFromEmail(currentUser.getEmail());
//        String key = mDatabase.child("users").push().getKey();///
//        String key = "user-info";
//        Info info = new Info(username, phone, isowner);
        User user = new User(id, currentUser.getEmail(), username, phone, isowner);
        Map<String, Object> infoValues = user.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/users/"+ getUid() + "/", infoValues);///

        mDatabase.updateChildren(childUpdates);
    }
    // [END write_user_info_out]

    private void cancel(){
        FirebaseUser user = mAuth.getCurrentUser();
        switch(CODE){
            case Env.MAIN:
                startActivity(new Intent(MemberInfoActivity.this, MainActivity.class));
                finish();
                break;
            case Env.SIGNIN:
                revoke();
                break;
        }
        finish();
    }


    private void revoke() {
        String user = mAuth.getCurrentUser().getUid();
        for(String table: Env.DBTABLES){
            mDatabase.child(Env.DBTABLES[0]).child(user).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "mDatabase:user:"+user+":delete completely");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "mDatabase:user:"+user+":delete Failed");
                }
            });

//            mDatabase.child("users").equalTo(user).addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
//                        appleSnapshot.getRef().removeValue();
//                    }
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//                    Log.e(TAG, "onCancelled", databaseError.toException());
//                }
//            });
        }
        mAuth.getCurrentUser().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "mAuth:user:"+user+":delete completely");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "mAuth:user:"+user+":delete Failed");
            }
        });

        finish();
    }
    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
    //    private void signout() {
//    }
}