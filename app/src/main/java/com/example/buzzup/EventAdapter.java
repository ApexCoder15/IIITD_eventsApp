package com.example.buzzup;

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

import java.util.ArrayList;

public class EventAdapter extends ArrayAdapter<Event> implements Filterable {

    Context context;
    int resource;
    ArrayList<Event>originalEvents;
    ArrayList<Event>filteredEvents;

    public EventAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Event> events) {
        super(context, resource, events);

        this.context = context;
        this.resource = resource;
        this.originalEvents = new ArrayList<>(events);
        this.filteredEvents = events;
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
        eventLikes.setText(Long.toString(filteredEvents.get(position).getLikes()));

        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long likes = filteredEvents.get(position).getLikes();
                filteredEvents.get(position).setLikes(likes + 1);
                eventLikes.setText(Long.toString(filteredEvents.get(position).getLikes()));
            }
        });

        View finalConvertView = convertView;
        rsvpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(finalConvertView.getContext(), "Implement RSVP", Toast.LENGTH_SHORT).show();
            }
        });

        viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(finalConvertView.getContext(), "Implement View", Toast.LENGTH_SHORT).show();
                //Transition to new activity with event details
                Intent i = new Intent(context,ViewEventActivity.class);
                i.putExtra("index", position+"");
                context.startActivity(i);
            }
        });

        return convertView;
    }

    public void setOriginalEvents(ArrayList<Event> originalEvents){
        this.originalEvents = originalEvents;
    }

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
