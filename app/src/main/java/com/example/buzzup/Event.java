package com.example.buzzup;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.GeoPoint;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Event {
    String name, description, venue;
    List<String> imageUrls;
    long likes;
    Date time;
    GeoPoint venueCoordinates;
    List<DocumentReference> participants;
    List<String> tags;

    public Event() {
    }

    public Event(String name, String description, long likes, Date time, String venue, GeoPoint venueCoordinates, List<DocumentReference> participants, List<String> imageUrls, List<String> tags) {
        this.name = name;
        this.description = description;
        this.likes = likes;
        this.time = time;
        this.venue = venue;
        this.venueCoordinates = venueCoordinates;
        this.participants = participants;
        this.imageUrls = imageUrls;
        this.tags = tags;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getLikes() {
        return this.likes;
    }

    public void setLikes(long likes) {
        this.likes = likes;
    }

    public Date getTime() {
        return this.time;
    }

    public String getTimeSimple(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss");
        String date = simpleDateFormat.format(this.time);
        return date.substring(0, date.length()-3);
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getVenue() {
        return this.venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public GeoPoint getVenueCoordinates() {
        return this.venueCoordinates;
    }

    public void setVenueCoordinates(GeoPoint venueCoordinates) {
        this.venueCoordinates = venueCoordinates;
    }

    public List<DocumentReference> getParticipants() {
        return this.participants;
    }

    public void setParticipants(ArrayList<DocumentReference> participants) {
        this.participants = participants;
    }

    public List<String> getImageUrls() {
        return this.imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public List<String> getTags() {
        return this.tags;
    }

    public void setTag(List<String> tags) {
        this.tags = tags;
    }
}
