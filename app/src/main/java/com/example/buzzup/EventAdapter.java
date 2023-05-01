package com.example.buzzup;

import static java.lang.Math.max;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;

public class EventAdapter extends ArrayAdapter<Event> implements Filterable {

    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore db;
    Context context;
    int resource;
    ArrayList<Event>originalEvents;
    ArrayList<Event>filteredEvents;
    ArrayList<DocumentReference> userLikedEvents;
    ArrayList<DocumentReference> userRSVPedEvents;
    ArrayList<String> eventIDS;

    public EventAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Event> events, FirebaseAuth auth, FirebaseUser user, FirebaseFirestore db) {
        super(context, resource, events);

        this.context = context;
        this.resource = resource;
        this.originalEvents = new ArrayList<>(events);
        this.filteredEvents = events;
        this.auth = auth;
        this.db = db;
        this.user = user;
    }

    // has the user RSVPed to given event
    public int hasUserRSVPedToEvent(String EventID){
        for(int i = 0; i < userRSVPedEvents.size(); i++){
            if (userRSVPedEvents.get(i).getId().equals(EventID)){
                return i;
            }
        }
        return -1;
    }

    // has the user Liked given event
    public int hasUserLikedEvent(String EventID){
        for(int i = 0; i < userLikedEvents.size();i++){
            if (userLikedEvents.get(i).getId().equals(EventID)){
                return i;
            }
        }
        return -1;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(this.context);

        convertView = layoutInflater.inflate(this.resource, parent, false);

        TextView eventName = convertView.findViewById(R.id.eventName);
        TextView eventDescription = convertView.findViewById(R.id.eventDescription);
        TextView eventTime = convertView.findViewById(R.id.eventTime);
        TextView eventVenue = convertView.findViewById(R.id.eventVenue);
        TextView eventLikes = convertView.findViewById(R.id.eventLikes);

        Button likeButton= convertView.findViewById(R.id.eventLikeButton);
        Button rsvpButton= convertView.findViewById(R.id.eventRSVPButton);
        Button viewButton = convertView.findViewById(R.id.eventViewButton);

        eventName.setText(filteredEvents.get(position).getName());
        eventDescription.setText(filteredEvents.get(position).getDescription());
        eventTime.setText(filteredEvents.get(position).getTimeSimple());
        eventVenue.setText(filteredEvents.get(position).getVenue());

        long likes = max(0,filteredEvents.get(position).getLikes());
        eventLikes.setText(Long.toString(likes));

        db.collection("user").document(user.getEmail())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            userRSVPedEvents = (ArrayList<DocumentReference>) document.getData().get("rsvpEvents");

                            if(hasUserRSVPedToEvent(eventIDS.get(position)) >= 0){
                                rsvpButton.setText("unRSVP");
                            } else{
                                rsvpButton.setText("RSVP");
                            }

                        }
                    }
                });

        View finalConvertView = convertView;

        // like the event on clicking like button
        // on clicking one more time, unlike the event
        likeButton.setOnClickListener(view -> {
            Toast.makeText(context, "Like Button Clicked", Toast.LENGTH_SHORT).show();
            userLikedEvents = new ArrayList<>();

            db.collection("user").document(user.getEmail())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // get all events like by current user
                                userLikedEvents = (ArrayList<DocumentReference>) document.getData().get("likedEvents");

                                if(hasUserLikedEvent(eventIDS.get(position)) >=0){
                                    int index = hasUserLikedEvent(eventIDS.get(position));
                                    Toast.makeText(finalConvertView.getContext(), "Unliking event", Toast.LENGTH_SHORT).show();

                                    long likes1 = max(1,filteredEvents.get(position).getLikes());
                                    likes1 -= 1;
                                    eventLikes.setText(Long.toString(likes1));
                                    filteredEvents.get(position).setLikes(likes1);
                                    userLikedEvents.remove(index);

                                    long finalLikes = likes1;
//                                    update on backend
                                    db.runTransaction((Transaction.Function<Void>) transaction -> {
                                        DocumentReference userDocRef = db.collection("user").document(auth.getCurrentUser().getEmail());
                                        transaction.update(userDocRef, "likedEvents", userLikedEvents);

                                        DocumentReference eventDocRef = db.collection("events").document(eventIDS.get(position));
                                        transaction.update(eventDocRef, "Likes", finalLikes);
                                        return null;
                                    }).addOnSuccessListener(unused -> Log.d("ADDED", "Liked Event added successfully."))
                                            .addOnFailureListener(e -> Log.d("ADDED", "Liked Event not added successfully."));
                                    Log.i("ADDED", "Liked Event removed " + db.collection("events").document(eventIDS.get(position)).getId() + " " + userLikedEvents.size());
                                }
                                else{
                                    // event not liked by user before, liking first time.
                                    Toast.makeText(finalConvertView.getContext(), "Liking event", Toast.LENGTH_SHORT).show();
                                    long likes1 = max(0,filteredEvents.get(position).getLikes());
                                    likes1 += 1;
                                    eventLikes.setText(Long.toString(likes1));
                                    filteredEvents.get(position).setLikes(likes1);
                                    userLikedEvents.add(db.collection("events").document(eventIDS.get(position)));

                                    long finalLikes = likes1;
//                                    update backend
                                    db.runTransaction((Transaction.Function<Void>) transaction -> {
                                        DocumentReference userDocRef = db.collection("user").document(auth.getCurrentUser().getEmail());
                                        transaction.update(userDocRef, "likedEvents", userLikedEvents);

                                        DocumentReference eventDocRef = db.collection("events").document(eventIDS.get(position));
                                        transaction.update(eventDocRef, "Likes", finalLikes);
                                        return null;
                                    }).addOnSuccessListener(unused -> Log.d("ADDED", "Liked Event removed successfully."))
                                            .addOnFailureListener(e -> Log.d("ADDED", "Liked Event not removed successfully."));

                                    Log.i("ADDED", "Liked Event added " + db.collection("events").document(eventIDS.get(position)).getId() + " " + userLikedEvents.size());
                                }
                            }
                        }
                    });
        });

        rsvpButton.setOnClickListener(view -> {
            Toast.makeText(context, "RSVP button clicked", Toast.LENGTH_SHORT).show();
            userRSVPedEvents = new ArrayList<>();
            db.collection("user").document(user.getEmail())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                userRSVPedEvents = (ArrayList<DocumentReference>) document.getData().get("rsvpEvents");

                                if(hasUserRSVPedToEvent(eventIDS.get(position))>=0){
                                    Toast.makeText(finalConvertView.getContext(), "Undoing RSVP to event", Toast.LENGTH_SHORT).show();
                                    userRSVPedEvents.remove(db.collection("events").document(eventIDS.get(position)));
                                    rsvpButton.setText("RSVP");

                                    db.runTransaction((Transaction.Function<Void>) transaction -> {
                                        DocumentReference userDocRef = db.collection("user").document(auth.getCurrentUser().getEmail());
                                        transaction.update(userDocRef, "rsvpEvents", userRSVPedEvents);

                                        DocumentReference eventDocRef = db.collection("events").document(eventIDS.get(position));
                                        Log.i("Doc Ref", eventDocRef.getId());

                                        eventDocRef.get()
                                                .addOnSuccessListener(documentSnapshot -> {
                                                    if (documentSnapshot.exists()){
                                                        ArrayList<DocumentReference> Participants = (ArrayList<DocumentReference>) documentSnapshot.get("Participants");
                                                        Participants.remove(db.collection("user").document(auth.getCurrentUser().getEmail()));

                                                        eventDocRef.update("Participants", Participants)
                                                                .addOnSuccessListener(unused -> Log.d("UPDATE", "Document updated."));
                                                    }
                                                });
                                        return null;
                                    }).addOnSuccessListener(unused -> Log.d("ADDED", "RSVP Event removed successfully."))
                                            .addOnFailureListener(e -> Log.d("ADDED", "RSVP Event not removed successfully."));

                                }
                                else{
                                    Toast.makeText(finalConvertView.getContext(), "RSVPing to event", Toast.LENGTH_SHORT).show();
                                    userRSVPedEvents.add(db.collection("events").document(eventIDS.get(position)));
                                    rsvpButton.setText("unRSVP");

                                    db.runTransaction((Transaction.Function<Void>) transaction -> {
                                        DocumentReference userDocRef = db.collection("user").document(auth.getCurrentUser().getEmail());
                                        transaction.update(userDocRef, "rsvpEvents", userRSVPedEvents);

                                        DocumentReference eventDocRef = db.collection("events").document(eventIDS.get(position));
                                        Log.i("Doc Ref", eventDocRef.getId());

                                        eventDocRef.get()
                                                .addOnSuccessListener(documentSnapshot -> {
                                                    if (documentSnapshot.exists()){
                                                        ArrayList<DocumentReference> Participants = (ArrayList<DocumentReference>) documentSnapshot.get("Participants");
                                                        Participants.add(db.collection("user").document(auth.getCurrentUser().getEmail()));

                                                        eventDocRef.update("Participants", Participants)
                                                                .addOnSuccessListener(unused -> Log.d("UPDATE", "Document updated."));
                                                    }
                                                });
                                        return null;
                                    }).addOnSuccessListener(unused -> Log.d("ADDED", "RSVP Event added successfully."))
                                            .addOnFailureListener(e -> Log.d("ADDED", "RSVP Event not added successfully."));
                                }
                            }
                        }
                    });
        });

        viewButton.setOnClickListener(view -> {
            Intent i = new Intent(context,ViewEventActivity.class);
            i.putExtra("index", Integer.toString(position));
            context.startActivity(i);
        });

        return convertView;
    }

    public void setOriginalEvents(ArrayList<Event> originalEvents){
        this.originalEvents = originalEvents;
    }

    public void setEventIDS(ArrayList<String> eventids){eventIDS = eventids;}

    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if(constraint == null || constraint.length() == 0){
                    results.values = originalEvents;
                    results.count = originalEvents.size();
                    return results;
                }
                String filterString = constraint.toString().toLowerCase();

                final ArrayList<Event> list = originalEvents;

                int count = list.size();
                final ArrayList<Event> nlist = new ArrayList<Event>(count);

                String filterableString ;

                for (int i = 0; i < count; i++) {
                    filterableString = list.get(i).getName();
                    if (filterableString.toLowerCase().contains(filterString)) {
                        nlist.add(list.get(i));
                    }
                }
                results.values = nlist;
                results.count = nlist.size();

                return results;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredEvents.clear();
                filteredEvents.addAll((ArrayList<Event>) results.values);
                notifyDataSetChanged();
            }
        };
    }
}
