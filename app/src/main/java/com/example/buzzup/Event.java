package com.example.buzzup;

import android.provider.Telephony;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.GeoPoint;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Event {
//    String id;
    String Name;
    String Description;
    long Likes;
    Date Time;
    String Venue;
    GeoPoint VenueCoordinates;
    ArrayList<String> Participants;

    public Event() {
    }

    public Event(String name, String description, long likes, Date time, String venue, GeoPoint venueCoordinates, ArrayList<String> participants) {
        Name = name;
        Description = description;
        Likes = likes;
        Time = time;
        Venue = venue;
        VenueCoordinates = venueCoordinates;
        Participants = participants;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public long getLikes() {
        return Likes;
    }

    public void setLikes(long likes) {
        Likes = likes;
    }

    public Date getTime() {
        return Time;
    }

    public String getTimeSimple(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss");
        String date = simpleDateFormat.format(Time);
        return date.substring(0, date.length()-3);
    }

    public void setTime(Date time) {
        Time = time;
    }

    public String getVenue() {
        return Venue;
    }

    public void setVenue(String venue) {
        Venue = venue;
    }

    public GeoPoint getVenueCoordinates() {
        return VenueCoordinates;
    }

    public void setVenueCoordinates(GeoPoint venueCoordinates) {
        VenueCoordinates = venueCoordinates;
    }

    public ArrayList<String> getParticipants() {
        return Participants;
    }

    public void setParticipants(ArrayList<String> participants) {
        Participants = participants;
    }
}
