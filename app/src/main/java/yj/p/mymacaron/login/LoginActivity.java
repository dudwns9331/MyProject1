package yj.p.mymacaron.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import yj.p.mymacaron.MainActivity;
import yj.p.mymacaron.R;
import yj.p.mymacaron.databinding.ActivityLoginBinding;
import yj.p.mymacaron.env.Env;
import yj.p.mymacaron.models.User;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "SignInActivity";

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        Env.checker = true;

        // Views
        setProgressBar(R.id.loginprogressBar);

        // Click listeners
        binding.loginbuttonSignIn.setOnClickListener(this);
        binding.loginbuttonSignUp.setOnClickListener(this);

        if(mAuth.getCurrentUser() != null){
            onAuthSuccess(mAuth.getCurrentUser(), false);
        }
    }

    private void onAuthSuccess(FirebaseUser user, boolean firstvisitor) {
        String username = usernameFromEmail(user.getEmail());
        Intent intent;

        if(firstvisitor) {
            writeNewUser(user.getUid(), username, user.getEmail());
            intent = new Intent(LoginActivity.this, MemberInfoActivity.class);
        } else {
            intent = new Intent(LoginActivity.this, MainActivity.class);
        }
        intent.putExtra("from",Env.SIGNIN);
        startActivity(intent);
//        finish();
    }

    private void writeNewUser(String uid, String username, String email) {
        User user = new User(username, email);

        mDatabase.child("users").child(uid).setValue(user);
    }

    private String usernameFromEmail(String email) {
        if(email.contains("@")){
            return email.split("@")[0];
        } else {
            return email;
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if(i == R.id.loginbuttonSignIn) {
            signIn();
        } else if (i == R.id.loginbuttonSignUp){
            signUp();
        }
    }

    private void signIn() {
        Log.d(TAG, "signIn");
        if(!validateForm()){
            return;
        }

        showProgressBar();
        String email = binding.loginfieldEmail.getText().toString();
        String password = binding.loginfieldPassword.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "signIn:onComplete:" + task.isSuccessful());
                hideProgressBar();

                if(task.isSuccessful()){
                    onAuthSuccess(task.getResult().getUser(), false);
                } else {
                    Toast.makeText(LoginActivity.this, "Sign In Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void signUp(){
        Log.d(TAG, "signUp");
        if (!validateForm()) {
            return;
        }

        showProgressBar();
        String email = binding.loginfieldEmail.getText().toString();
        String password = binding.loginfieldPassword.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUser:onComplete:" + task.isSuccessful());
                        hideProgressBar();

                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser(), true);
                        } else {
                            Toast.makeText(LoginActivity.this, "Sign Up Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(binding.loginfieldEmail.getText().toString())) {
            binding.loginfieldEmail.setError("Required");
            result = false;
        } else {
            binding.loginfieldEmail.setError(null);
        }

        if (TextUtils.isEmpty(binding.loginfieldPassword.getText().toString())) {
            binding.loginfieldPassword.setError("Required");
            result = false;
        } else {
            binding.loginfieldPassword.setError(null);
        }

        return result;
    }

    private long backKeyPressedTime = 0;

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if (System.currentTimeMillis() - backKeyPressedTime >= 800) {
            backKeyPressedTime = System.currentTimeMillis();
            Toast.makeText(this, "한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
        } else if (System.currentTimeMillis() - backKeyPressedTime < 800) {
            ActivityCompat.finishAffinity(this);
        }
    }
}