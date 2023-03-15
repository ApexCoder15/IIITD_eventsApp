package com.example.buzzup;

import android.content.Context;
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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class EventAdapter extends ArrayAdapter<Event> implements Filterable {

    Context context;
    int resource;
    ArrayList<Event>originalEvents;
    ArrayList<Event>filteredEvents;

//    private EventFilter eventFilter = new EventFilter();

    public EventAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Event> events) {
        super(context, resource, events);

        this.context = context;
        this.resource = resource;
        this.originalEvents = events;
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

        eventName.setText(getItem(position).getName());
        eventDescription.setText(getItem(position).getDescription());
        eventTime.setText(getItem(position).getTimeSimple());
        eventVenue.setText(getItem(position).getVenue());
        eventLikes.setText(Long.toString(getItem(position).getLikes()));

        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long likes = getItem(position).getLikes();
                getItem(position).setLikes(likes + 1);
                eventLikes.setText(Long.toString(getItem(position).getLikes()));
            }
        });

        View finalConvertView = convertView;
        rsvpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(finalConvertView.getContext(), "Implement RSVP", Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;
    }

//    public Filter getFilter() {
//        return eventFilter;
//    }
//
//    public void addAllAgain(ArrayList<Event> events) {
//        this.originalEvents = events;
//        this.filteredEvents = events;
//    }

//    private class EventFilter extends Filter {
//        @Override
//        protected FilterResults performFiltering(CharSequence constraint) {
//
//            String filterString = constraint.toString().toLowerCase();
//
//            FilterResults results = new FilterResults();
//
//            final ArrayList<Event> list = originalEvents;
//
//            int count = list.size();
//            final ArrayList<Event> nlist = new ArrayList<Event>(count);
//
//            String filterableString ;
//
//            for (int i = 0; i < count; i++) {
//                filterableString = list.get(i).getName();
//                if (filterableString.toLowerCase().contains(filterString)) {
//                    nlist.add(list.get(i));
//                }
//            }
//
//            results.values = nlist;
//            results.count = nlist.size();
//
//            return results;
//        }
//
//        @SuppressWarnings("unchecked")
//        @Override
//        protected void publishResults(CharSequence constraint, FilterResults results) {
//            filteredEvents = (ArrayList<Event>) results.values;
//            notifyDataSetChanged();
//        }
//    }
}
