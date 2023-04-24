package com.example.buzzup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class PostDetailActivity extends AppCompatActivity {

    String myEmail, myName, postId;
    ImageView pImageIv;
    TextView uNameTv, pTimeTv, pTitleTv, pDescriptionTv, pLikesTv;
    Button likeBtn;

    EditText commentEt;
    ImageButton sendBtn;
    ActionBar actionBar;
    FirebaseFirestore db;
    ProgressDialog pd;
    FirebaseAuth auth;
    RecyclerView commentsRecyclerView;
    List<Comment> commentList;
    CommentAdapter commentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        db =  FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        actionBar = getSupportActionBar();
        actionBar.setTitle("Post Detail");
        // enable back button in action bar
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        postId = intent.getStringExtra("postId");

        pImageIv = findViewById(R.id.pImageIv);
        uNameTv = findViewById(R.id.uNameTv);
        pDescriptionTv = findViewById(R.id.pDescriptionTv);
        pTimeTv = findViewById(R.id.pTimeTv);
        pTitleTv = findViewById(R.id.pTitleTv);
        pLikesTv = findViewById(R.id.pLikesTv);
        likeBtn = findViewById(R.id.likeBtn);

        commentEt = findViewById(R.id.commentEt);
        sendBtn = findViewById(R.id.sendBtn);

        commentsRecyclerView = findViewById(R.id.commentsRecyclerView);

        loadPostInfo();
        loadComments();
        

        myEmail = auth.getCurrentUser().getEmail();
        db.collection("user").document(auth.getCurrentUser().getEmail()).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                myName = task.getResult().getData().get("name").toString();
            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postComment();
            }
        });

    }

    private void loadComments() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        // set Layout to recyclerview
        commentsRecyclerView.setLayoutManager(layoutManager);
        commentList = new ArrayList<Comment>();
        commentAdapter = new CommentAdapter(this, commentList);
        commentsRecyclerView.setAdapter(commentAdapter);
        db.collection("posts").document(postId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        commentList.clear();
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            ArrayList<HashMap<String,Object>> comments = (ArrayList<HashMap<String,Object>>) document.getData().get("comments");
                            for (HashMap<String, Object> hashMap : comments) {

                                String cId = (String) hashMap.get("cId");
                                String comment = (String) hashMap.get("comment");
                                String timestamp = (String) hashMap.get("timestamp");
                                String uEmail = (String) hashMap.get("uEmail");
                                String uName = (String) hashMap.get("uName");

                                Comment comment1 = new Comment(cId,comment,timestamp,uName,uEmail);
                                commentList.add(comment1);
                            }
                            commentAdapter = new CommentAdapter(this, commentList);
                            commentsRecyclerView.setAdapter(commentAdapter);
                        }
                    }
                });

        // Listen for changes in comments section
        db.collection("posts").document(postId).addSnapshotListener((snapshot, error) -> {
            if (error != null) {
                Log.w("PostDetail", "Listen failed.", error);
                return;
            }
            if (snapshot != null && snapshot.exists()) {
                commentList.clear();
                ArrayList<HashMap<String,Object>> updatedList = (ArrayList<HashMap<String,Object>>)snapshot.get("comments");
                for (HashMap<String, Object> hashMap : updatedList) {
                    String cId = (String) hashMap.get("cId");
                    String comment = (String) hashMap.get("comment");
                    String timestamp = (String) hashMap.get("timestamp");
                    String uEmail = (String) hashMap.get("uEmail");
                    String uName = (String) hashMap.get("uName");

                    Comment comment1 = new Comment(cId,comment,timestamp,uName,uEmail);
                    commentList.add(comment1);
                }
                commentAdapter.notifyDataSetChanged();


            }
        });
    }

    private void postComment() {
        pd = new ProgressDialog(this);
        pd.setMessage("Adding comment..");

        String comment = commentEt.getText().toString().trim();
        if(TextUtils.isEmpty(comment)){
            Toast.makeText(this,"comment is empty",Toast.LENGTH_SHORT).show();
            return;
        }
        String timestamp = String.valueOf(System.currentTimeMillis());
        db.collection("posts").document(postId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            HashMap<String, Object> hashMap = new HashMap<>();

                            hashMap.put("cId",timestamp);
                            hashMap.put("comment",comment);
                            hashMap.put("timestamp",timestamp);
                            hashMap.put("uEmail",myEmail);
                            hashMap.put("uName",myName);

                            ArrayList<HashMap<String,Object>> comments = (ArrayList<HashMap<String,Object>>) document.getData().get("comments");
                            comments.add(hashMap);

                            db.collection("posts").document(postId).update("comments", comments)
                                    .addOnSuccessListener(unused -> {
                                        pd.dismiss();
                                        // Toast.makeText(this,"Comment added!",Toast.LENGTH_SHORT).show();
                                        commentEt.setText("");
                                    });

                        }
                    }
                });
    }

    private void loadPostInfo() {
        db.collection("posts").document(postId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Post post = document.toObject(Post.class);
                            // Convert timestamp to dd/mm/yyyy hh:mm am/pm
                            Calendar calendar = Calendar.getInstance(Locale.getDefault());
                            calendar.setTimeInMillis(Long.parseLong(post.getTime()));
                            String pTime = DateFormat.format("dd/MM/yyyy hh:mm aa",calendar).toString();
                            String imageUrl = post.getImageUrl();
                            pTitleTv.setText(post.getTitle());
                            pDescriptionTv.setText(post.getDescription());
                            uNameTv.setText(post.getuName());
                            pTimeTv.setText(pTime);
                            pLikesTv.setText(Long.toString(post.getLikes()));

                            if(imageUrl.equals("noImage")){
                                // hide image view
                                pImageIv.setVisibility(View.GONE);
                            }
                            else {
                                // set post image
                                try {
                                    Picasso.get().load(imageUrl).into(pImageIv);
                                } catch (Exception e) {

                                }
                            }
                        }
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // go to previous activity
        return super.onSupportNavigateUp();
    }
}