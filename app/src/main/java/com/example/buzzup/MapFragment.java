package com.example.buzzup;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment {
    MapView map;

    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore db;

    RecyclerView recyclerView; // recycler view for events near me
    List<Event> eventsNearMe = new ArrayList<>();

    public MapFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Context ctx = getContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(View viewItem, Bundle savedInstanceState) {

        auth = FirebaseAuth.getInstance();
        db =  FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();

//        making the recycler view for EventsNearMe horizontal using a linear layout manager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView = viewItem.findViewById(R.id.recycler_view_events_near_me);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        EventsNearMeAdapter eventsNearMeAdapter = new EventsNearMeAdapter(eventsNearMe, getActivity());
        recyclerView.setAdapter(eventsNearMeAdapter);

        // load events and put in RV
//        TODO filter events based on time (And interests?)
        db.collection("events")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    eventsNearMe.clear();
                    for(QueryDocumentSnapshot document: queryDocumentSnapshots){
                        //                        Event event = document.toObject(Event.class);

                        Event event = new Event();
                        // set each field manually
                        event.setName((String)document.get("Name"));
                        event.setDescription((String)document.get("Description"));
                        event.setLikes((Long)document.get("Likes"));
                        event.setParticipants((ArrayList<DocumentReference>)document.get("Participants"));
//                        event.setTime((Date)document.get("Time"));
                        event.setVenue((String)document.get("Venue"));
                        event.setVenueCoordinates((com.google.firebase.firestore.GeoPoint)document.get("VenueCoordinates"));
                        event.setImageUrls((List<String>)document.get("ImageUrls"));

                        addMarkerToMap(event);
                        eventsNearMe.add(event);
                        eventsNearMeAdapter.notifyDataSetChanged();
                    }
                });



        map = (MapView) getActivity().findViewById(R.id.mapview);
        map.setTileSource(TileSourceFactory.MAPNIK);

        map.setBuiltInZoomControls(false);
        map.setMultiTouchControls(true);


        IMapController mapController = map.getController();
        mapController.setZoom(19.50);
        map.setMapOrientation(-18.5f);
        GeoPoint startPoint = new GeoPoint(28.5465, 77.2730);
//        mapController.setCenter(startPoint);
        mapController.animateTo(startPoint);

    }

    private void addMarkerToMap(final Event event) {
        com.google.firebase.firestore.GeoPoint eventCoords = event.getVenueCoordinates();
        double lat = eventCoords.getLatitude();
        double lon = eventCoords.getLongitude();
        GeoPoint markerLoc = new GeoPoint(lat, lon);
        Marker mr = new Marker(map);
        mr.setPosition(markerLoc);
        mr.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        map.getOverlays().add(mr);
        mr.setTitle(event.getName());
        mr.setSnippet(event.getDescription());
        mr.setSubDescription(event.getVenue());
    }

    @Override
    public void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        map.onPause();
    }
}
