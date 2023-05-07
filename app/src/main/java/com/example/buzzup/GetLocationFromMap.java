package com.example.buzzup;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;

public class GetLocationFromMap extends AppCompatActivity implements MapEventsReceiver {

    private MapView mapView;
    private IMapController mapController;
    private Marker marker;
    MapEventsOverlay mapEventsOverlay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        setContentView(R.layout.activity_get_location_from_map);

        mapView = findViewById(R.id.map_getcoords_view);
        mapView.setTileSource(TileSourceFactory.MAPNIK);

        mapView.setBuiltInZoomControls(false);
        mapView.setMultiTouchControls(true);

        mapEventsOverlay = new MapEventsOverlay(this, this);
        mapView.getOverlays().add(0, mapEventsOverlay);

        mapController = mapView.getController();
        mapController.setZoom(19.50);

        GeoPoint startPoint = new GeoPoint(28.5465, 77.2730);
        mapController.animateTo(startPoint);

        marker = new Marker(mapView);
        marker.setPosition(startPoint);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        mapView.getOverlays().add(marker);
    }

    @Override
    public boolean singleTapConfirmedHelper(GeoPoint p) {
        marker.setPosition(p);
        mapView.invalidate();

        Intent resultIntent = new Intent();
        resultIntent.putExtra("latitude", p.getLatitude());
        resultIntent.putExtra("longitude", p.getLongitude());
        setResult(Activity.RESULT_OK, resultIntent);
        finish();

        return true;
    }

    @Override
    public boolean longPressHelper(GeoPoint p) {
        return false;
    }
}