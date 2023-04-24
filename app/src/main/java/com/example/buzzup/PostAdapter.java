package com.example.buzzup;

import static java.lang.Math.max;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Transaction;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

public class PostAdapter extends  RecyclerView.Adapter<PostAdapter.MyHolder> {

    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore db;
    Context context;
    List<Post> postList;
    ArrayList<DocumentReference> userLikedPosts;

    public PostAdapter(Context context, List<Post> postList, FirebaseAuth auth, FirebaseUser user, FirebaseFirestore db) {
        this.context = context;
        this.postList = postList;
        this.auth = auth;
        this.db = db;
        this.user = user;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout row_post.xml
        View view = LayoutInflater.from(context).inflate(R.layout.row_posts,parent,false);

        return new MyHolder(view);
    }

    public int hasUserLikedPost(String PostID){
        for(int i = 0; i < userLikedPosts.size();i++){
            if (userLikedPosts.get(i).getId().equals(PostID)){
                return i;
            }
        }
        return -1;
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        // get data
        String uName = postList.get(position).getuName();
        String email = postList.get(position).getEmail();
        String description = postList.get(position).getDescription();
        String title = postList.get(position).getTitle();
        String imageUrl = postList.get(position).getImageUrl();
        String timestamp = postList.get(position).getTime();
        String id = postList.get(position).getId();
        long likes = postList.get(position).getLikes();

        // Convert timestamp to dd/mm/yyyy hh:mm am/pm
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(timestamp));
        String pTime = DateFormat.format("dd/MM/yyyy hh:mm aa",calendar).toString();

        // set data
        holder.pTitleTv.setText(title);
        holder.pDescriptionTv.setText(description);
        holder.uNameTv.setText(uName);
        holder.pTimeTv.setText(pTime);
        String likesText = Long.toString(likes)+" likes";
        holder.pLikesTv.setText(likesText);

        if(imageUrl.equals("noImage")){
            // hide image view
            holder.pImageIv.setVisibility(View.GONE);
        }
        else {
            // set post image
            try {
                Picasso.get().load(imageUrl).into(holder.pImageIv);
            } catch (Exception e) {

            }
        }

        holder.likeBtn.setOnClickListener(view -> {
            Toast.makeText(context, "Like Button Clicked", Toast.LENGTH_SHORT).show();
            userLikedPosts = new ArrayList<>();

            db.collection("user").document(user.getEmail())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // get all events like by current user
                                userLikedPosts = (ArrayList<DocumentReference>) document.getData().get("likedPosts");

                                int index = hasUserLikedPost(postList.get(position).getId());
                                if(index >=0){
                                    // Toast.makeText(context.getApplicationContext(), "Unliking post", Toast.LENGTH_SHORT).show();

                                    long likes1 = max(1,postList.get(position).getLikes());
                                    likes1 -= 1;
                                    holder.pLikesTv.setText(Long.toString(likes1));
                                    postList.get(position).setLikes(likes1);
                                    userLikedPosts.remove(index);

                                    long finalLikes = likes1;
//                                    update on backend
                                    db.runTransaction((Transaction.Function<Void>) transaction -> {
                                                DocumentReference userDocRef = db.collection("user").document(auth.getCurrentUser().getEmail());
                                                transaction.update(userDocRef, "likedPosts", userLikedPosts);

                                                DocumentReference postDocRef = db.collection("posts").document(postList.get(position).getId());
                                                transaction.update(postDocRef, "likes", finalLikes);
                                                return null;
                                            }).addOnSuccessListener(unused -> Log.d("ADDED", "Liked Post removed successfully."))
                                            .addOnFailureListener(e -> Log.d("ADDED", "Liked Post not removed successfully."));
                                    Log.i("ADDED", "Liked Post removed " + db.collection("posts").document(postList.get(position).getId()) + " " + userLikedPosts.size());
                                }
                                else{
                                    // event not liked by user before, liking first time.
                                    // Toast.makeText(context.getApplicationContext(), "Liking post", Toast.LENGTH_SHORT).show();

                                    long likes1 = max(0,postList.get(position).getLikes());
                                    likes1 += 1;

                                    postList.get(position).setLikes(likes1);
                                    holder.pLikesTv.setText(Long.toString(postList.get(position).getLikes()));

                                    userLikedPosts.add(db.collection("posts").document(postList.get(position).getId()));

                                    long finalLikes = likes1;
//                                    update backend
                                    db.runTransaction((Transaction.Function<Void>) transaction -> {
                                                DocumentReference userDocRef = db.collection("user").document(auth.getCurrentUser().getEmail());
                                                transaction.update(userDocRef, "likedPosts", userLikedPosts);

                                                DocumentReference postDocRef = db.collection("posts").document(postList.get(position).getId());
                                                transaction.update(postDocRef, "likes", finalLikes);
                                                return null;
                                            }).addOnSuccessListener(unused -> Log.d("ADDED", "Liked Post added successfully."))
                                            .addOnFailureListener(e -> Log.d("ADDED", "Liked Post not added successfully."));

                                    Log.i("ADDED", "Liked Post added " + db.collection("posts").document(postList.get(position).getId()) + " " + userLikedPosts.size());
                                }
                            }
                        }
                    });
        });

        holder.commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,PostDetailActivity.class);
                intent.putExtra("postId",id);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{
        ImageView pImageIv;
        TextView uNameTv, pTimeTv, pTitleTv, pDescriptionTv, pLikesTv;
        Button likeBtn, commentBtn;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            pImageIv = itemView.findViewById(R.id.pImageIv);
            uNameTv = itemView.findViewById(R.id.uNameTv);
            pDescriptionTv = itemView.findViewById(R.id.pDescriptionTv);
            pTimeTv = itemView.findViewById(R.id.pTimeTv);
            pTitleTv = itemView.findViewById(R.id.pTitleTv);
            pLikesTv = itemView.findViewById(R.id.pLikesTv);
            likeBtn = itemView.findViewById(R.id.likeBtn);
            commentBtn = itemView.findViewById(R.id.commentBtn);
        }
    }
}
