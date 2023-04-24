package com.example.buzzup;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class FeedFragment extends Fragment {

    Button mAddPostBtn;
    RecyclerView mRecyclerView;
    SearchView mPostsSearchView;
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore db;

    List<Post> posts;
    List<Post> originalPosts;
    PostAdapter postAdapter;

    public FeedFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feed, container, false);
    }

    @Override
    public void onViewCreated(View itemView, Bundle savedInstanceState) {
        auth = FirebaseAuth.getInstance();
        db =  FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();

        mAddPostBtn = getActivity().findViewById(R.id.add_post_to_feed_btn);
        mRecyclerView = getActivity().findViewById(R.id.feed_rv);
        mPostsSearchView = getActivity().findViewById(R.id.feed_search_bar);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        // shows newest post first, for this load from last
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);

        mRecyclerView.setLayoutManager(layoutManager);

        posts = new ArrayList<>();
        originalPosts = new ArrayList<>();
        postAdapter = new PostAdapter(getActivity(), posts, auth, user, db);
        mRecyclerView.setAdapter(postAdapter);
        loadPosts();

        mAddPostBtn.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), AddPostActivity.class);
            startActivity(intent);
        });

        db.collection("posts").addSnapshotListener((querySnapshot, e) -> {
            if (e != null) {
                Log.w("FeedFragment", "Listen failed.", e);
                return;
            }
            posts.clear();
            for (QueryDocumentSnapshot document : querySnapshot) {
                Post post = document.toObject(Post.class);
                posts.add(post);
                postAdapter.notifyDataSetChanged();
            }
        });

        mPostsSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchPosts(newText);
                return true;
            }
        });

    }

    private void loadPosts() {
        db.collection("posts")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    posts.clear();
                    ArrayList<String> postIDS = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Post post = document.toObject(Post.class);
                        posts.add(post);
                        postIDS.add(document.getId());
                    }
                    postAdapter = new PostAdapter(getActivity(), posts, auth, user, db);
                    mRecyclerView.setAdapter(postAdapter);
                });

    }

    private void searchPosts(String searchQuery) {
        db.collection("posts")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    posts.clear();
                    ArrayList<String> postIDS = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Post post = document.toObject(Post.class);
                        if (post.getTitle().toLowerCase().contains(searchQuery.toLowerCase()) ||
                                post.getDescription().toLowerCase().contains(searchQuery.toLowerCase())) {
                            posts.add(post);
                        }
                    }
                    postAdapter = new PostAdapter(getActivity(), posts, auth, user, db);
                    mRecyclerView.setAdapter(postAdapter);
                });
    }
}
