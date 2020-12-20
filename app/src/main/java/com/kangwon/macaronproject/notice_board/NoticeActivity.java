package com.kangwon.macaronproject.notice_board;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;

import com.kangwon.macaronproject.R;
import com.kangwon.macaronproject.databinding.ActivityNoticeBinding;
import com.kangwon.macaronproject.fragment.MyPostsFragment;
import com.kangwon.macaronproject.fragment.MyTopPostsFragment;
import com.kangwon.macaronproject.fragment.RecentPostFragment;
import com.kangwon.macaronproject.login.BaseActivity;
import com.kangwon.macaronproject.login.LoginActivity;

public class NoticeActivity extends BaseActivity {

    private static final String TAG = "NoticeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityNoticeBinding binding = ActivityNoticeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // Create the adapter that will return a fregment for each section
        FragmentPagerAdapter mPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager(),
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            private final Fragment[] mFragments = new Fragment[]{
                    new RecentPostFragment(),
                    new MyPostsFragment(),
                    new MyTopPostsFragment(),
            };
            private final String[] mFragmentNames = new String[]{
                    getString(R.string.heading_recent),
                    getString(R.string.heading_my_posts),
                    getString(R.string.heading_my_top_posts)
            };

            @Override
            public int getCount() {
                return mFragments.length;
            }

            @NonNull
            @Override
            public Fragment getItem(int position) {
                return mFragments[position];
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return mFragmentNames[position];
            }
        };
        // Set up the ViewPager with the sections adapter.
        binding.container.setAdapter(mPagerAdapter);
        binding.tabs.setupWithViewPager(binding.container);

        // Button launches NewPostActivity
        binding.fabNewPost.setOnClickListener((v) -> {
            startActivity(new Intent(NoticeActivity.this, NewPostActivity.class));
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int i = item.getItemId();

        /**
         * 메뉴에서 고르면 시작하는거 수정 필요함. 게시판에서 업데이트로 가는거 환경변수 때문에 오류.
         */
        if (i == R.id.action_logout) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            return true;
        } else if (i == R.id.cal) {
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}