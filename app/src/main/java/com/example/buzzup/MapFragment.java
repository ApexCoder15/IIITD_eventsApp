package com.example.buzzup;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
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
import com.squareup.picasso.Picasso;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapFragment extends Fragment implements MapEventsReceiver {
    MapView map;

    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore db;

    RecyclerView recyclerView; // recycler view for events near me
    List<Event> eventsNearMe = new ArrayList<>();

    Map<Event, Marker> mp = new HashMap<>();
    MapEventsOverlay mapEventsOverlay;

    @Override public boolean singleTapConfirmedHelper(GeoPoint p) {
        InfoWindow.closeAllInfoWindowsOn(map);
        return true;
    }

    @Override public boolean longPressHelper(GeoPoint p) {
        //DO NOTHING FOR NOW:
        return false;
    }

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

        mapEventsOverlay = new MapEventsOverlay(getContext(), this);
        map.getOverlays().add(0, mapEventsOverlay);


        IMapController mapController = map.getController();
        mapController.setZoom(19.50);
        map.setMapOrientation(-18.5f);
        GeoPoint startPoint = new GeoPoint(28.5465, 77.2730);
//        mapController.setCenter(startPoint);
        mapController.animateTo(startPoint);

        auth = FirebaseAuth.getInstance();
        db =  FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();

//        making the recycler view for EventsNearMe horizontal using a linear layout manager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView = viewItem.findViewById(R.id.recycler_view_events_near_me);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        EventsNearMeAdapter eventsNearMeAdapter = new EventsNearMeAdapter(eventsNearMe, mapController, mp, getActivity());
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

        new AsyncTask<String, Integer, Drawable>() {
            @Override
            protected Drawable doInBackground(String... strings) {
                Bitmap bmp = null;
                try {
                    HttpURLConnection connection = (HttpURLConnection) new URL(event.getImageUrls().get(0)).openConnection();
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    bmp = BitmapFactory.decodeStream(input);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return new BitmapDrawable(bmp);
            }

            protected void onPostExecute(Drawable result) {
                //Add image to ImageView
                mr.setImage(result);
            }
        }.execute();

        mp.put(event, mr);
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
