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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;

public class MapFragment extends Fragment {
    MapView map;
    LinearLayout eventsNearMeList;

    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore db;

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


        auth = FirebaseAuth.getInstance();
        db =  FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();

        eventsNearMeList = (LinearLayout) getActivity().findViewById(R.id.events_near_me_list);

        // add al events
//        TODO filter events based on time (And interests?)
        db.collection("events")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for(QueryDocumentSnapshot document: queryDocumentSnapshots){
                        Event event = document.toObject(Event.class);
                        addMarkerToMap(event);
                        eventsNearMeList.addView(insertEventNearMeCard(event));
                    }
                });
    }

    private View insertEventNearMeCard(final Event event) {
        LinearLayout layout = new LinearLayout(getContext());

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(500, 300);
        layoutParams.setMargins( 8 , 8 , 8 , 8) ;
        layout.setLayoutParams(layoutParams);

        layout.setGravity(Gravity.CENTER);
        layout.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_rounded));

        TextView tv = new TextView(getActivity());
        tv.setText(event.getName());

        layout.addView(tv);
        return layout;
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
