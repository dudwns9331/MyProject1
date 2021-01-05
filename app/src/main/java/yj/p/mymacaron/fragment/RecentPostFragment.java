package yj.p.mymacaron.fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class RecentPostFragment extends PostListFragment {

    public RecentPostFragment() {}

    @Override
    public Query getQuery(DatabaseReference mDatabase) {
        // [START recent_posts_query]
        // Last 100 posts, these are automatically the 100 most recent
        // due to sorting by push() keys
        Query recentPostQuery = mDatabase.child("posts").limitToFirst(100);
        // [END recent_posts_query]

        return recentPostQuery;
    }
}