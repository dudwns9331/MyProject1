package com.kangwon.macaronproject.fragment;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class MyPostsFragment extends PostListFragment {

    public MyPostsFragment() {}
    @Override
    public Query getQuery(DatabaseReference mDatabase) {
        // All my posts
        return mDatabase.child("user-posts").child(getUid());
    }
}
