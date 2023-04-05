package com.example.buzzup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class FeedActivity extends AppCompatActivity {

    Button addPostButton;
    RecyclerView recyclerView;
    SearchView postsSearchView;
    FirebaseFirestore db;
    List<Post> posts;
    List<Post> originalPosts;
    PostAdapter postAdapter;

    final String TAG = "FeedActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        db =  FirebaseFirestore.getInstance();

        addPostButton = findViewById(R.id.add_post_action);
        recyclerView = findViewById(R.id.postsRecyclerView);
        postsSearchView = findViewById(R.id.postsSearchBar);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        // shows newest post first, for this load from last
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);

        recyclerView.setLayoutManager(layoutManager);

        posts = new ArrayList<>();
        originalPosts = new ArrayList<>();
        postAdapter = new PostAdapter(this, posts);
        recyclerView.setAdapter(postAdapter);
        loadPosts();
        addPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),AddPostActivity.class);
                startActivity(intent);
            }
        });

        // Listen for changes to the post collection
        db.collection("posts").addSnapshotListener((querySnapshot, e) -> {
            if (e != null) {
                Log.w(TAG, "Listen failed.", e);
                return;
            }
            posts.clear();
            for (QueryDocumentSnapshot document : querySnapshot) {
                Post post = document.toObject(Post.class);
                posts.add(post);
                postAdapter.notifyDataSetChanged();
            }
        });

        postsSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
                    for(QueryDocumentSnapshot document: queryDocumentSnapshots){
                        Post post = document.toObject(Post.class);
                        posts.add(post);
                        postIDS.add(document.getId());
                    }
                    postAdapter = new PostAdapter(this, posts);
                    recyclerView.setAdapter(postAdapter);
                });

    }

    private void searchPosts(String searchQuery) {
        db.collection("posts")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    posts.clear();
                    ArrayList<String> postIDS = new ArrayList<>();
                    for(QueryDocumentSnapshot document: queryDocumentSnapshots){
                        Post post = document.toObject(Post.class);
                        if(post.getTitle().toLowerCase().contains(searchQuery.toLowerCase()) ||
                                post.getDescription().toLowerCase().contains(searchQuery.toLowerCase())){
                            posts.add(post);
                        }
                    }
                    postAdapter = new PostAdapter(this,posts);
                    recyclerView.setAdapter(postAdapter);
                });
    }
}