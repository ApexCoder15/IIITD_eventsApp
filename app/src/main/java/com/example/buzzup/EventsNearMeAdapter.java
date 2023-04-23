package com.example.buzzup;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class EventsNearMeAdapter extends RecyclerView.Adapter<EventsNearMeAdapter.ViewHolder> {
    private List<Event> eventsNearMeList;
    private Activity activity;

    public EventsNearMeAdapter(List<Event> eventsNearMeList, Activity activity) {
        this.eventsNearMeList = eventsNearMeList;
        this.activity = activity;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_event_near_me, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Event event = eventsNearMeList.get(position);
        holder.textView.setText(event.getName());
        Picasso.get().load(event.imageUrls.get(0)).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return eventsNearMeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView imageView;
        private TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_event_near_me_img);
            textView = itemView.findViewById(R.id.tv_event_near_me_name);
        }
    }

}
