package com.example.buzzup;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.GeoPoint;
import com.squareup.picasso.Picasso;

import org.osmdroid.api.IMapController;
import org.osmdroid.views.overlay.Marker;

import java.util.List;
import java.util.Map;

public class EventsNearMeAdapter extends RecyclerView.Adapter<EventsNearMeAdapter.ViewHolder> {
    private List<Event> eventsNearMeList;
    private Activity activity;
    private IMapController mapController;
    private Map<Event, Marker> mp;

    public EventsNearMeAdapter(List<Event> eventsNearMeList, final IMapController mapController, final Map<Event, Marker> mp, Activity activity) {
        this.eventsNearMeList = eventsNearMeList;
        this.activity = activity;
        this.mapController = mapController;
        this.mp = mp;
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

        // on each holder, add an event listener
        holder.imageView.setOnClickListener(view->{
            // navigate to the map location
            Toast.makeText(view.getContext(), "Short Press", Toast.LENGTH_SHORT);
            GeoPoint point = event.getVenueCoordinates();
            org.osmdroid.util.GeoPoint mapLoc = new org.osmdroid.util.GeoPoint(point.getLatitude(), point.getLongitude());
            mapController.animateTo(mapLoc, 20.0, 1000L);
            Marker mr = mp.get(event);
            mr.showInfoWindow();
        });

//        TODO long press listener not working
//        holder.imageView.setOnLongClickListener(view->{
//            // open the Event Activity
//            Toast.makeText(view.getContext(), "Long Press", Toast.LENGTH_SHORT);
//            return true;
//        });
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
