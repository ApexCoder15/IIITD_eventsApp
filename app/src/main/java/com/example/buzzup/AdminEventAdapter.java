package com.example.buzzup;

import android.content.Context;
import android.content.Intent;
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

public class AdminEventAdapter extends ArrayAdapter<Event> implements Filterable {
    Context context;
    int resource;
    ArrayList<Event> originalEvents;
    ArrayList<Event>filteredEvents;

    public AdminEventAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Event> events) {
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

        TextView eventName = convertView.findViewById(R.id.eventNameAdmin);
        TextView eventDescription = convertView.findViewById(R.id.eventDescriptionAdmin);
        TextView eventTime = convertView.findViewById(R.id.eventTimeAdmin);
        TextView eventVenue = convertView.findViewById(R.id.eventVenueAdmin);
        Button viewButton = convertView.findViewById(R.id.eventViewButtonAdmin);

        eventName.setText(filteredEvents.get(position).getName());
        eventDescription.setText(filteredEvents.get(position).getDescription());
        eventTime.setText(filteredEvents.get(position).getTimeSimple());
        eventVenue.setText(filteredEvents.get(position).getVenue());

        viewButton.setOnClickListener(view -> {
            Intent i = new Intent(context, ViewEventActivity.class);
            i.putExtra("index", Integer.toString(position));
            context.startActivity(i);
        });

        return convertView;
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
                final ArrayList<Event> nList = new ArrayList<>(count);

                String filterableString ;

                for (int i = 0; i < count; i++) {
                    filterableString = list.get(i).getName();
                    if (filterableString.toLowerCase().contains(filterString)) {
                        nList.add(list.get(i));
                    }
                }
                results.values = nList;
                results.count = nList.size();

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

    public void setOriginalEvents(ArrayList<Event> originalEvents) {
        this.originalEvents = originalEvents;
    }
}
