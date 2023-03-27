package com.example.buzzup;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.GeoPoint;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Event {
    String Name;
    String Description;
    long Likes;
    Date Time;
    String Venue;
    GeoPoint VenueCoordinates;
    ArrayList<DocumentReference> Participants;

    public Event() {
    }

    public Event(String name, String description, long likes, Date time, String venue, GeoPoint venueCoordinates, ArrayList<DocumentReference> participants) {
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

    public ArrayList<DocumentReference> getParticipants() {
        return Participants;
    }

    public void setParticipants(ArrayList<DocumentReference> participants) {
        Participants = participants;
    }
}
