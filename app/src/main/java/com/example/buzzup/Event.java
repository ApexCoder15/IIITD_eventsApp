package com.example.buzzup;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.GeoPoint;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Event {
    String Name, Description, Venue;
    List<String> ImageUrls;
    long Likes;
    Date Time;
    GeoPoint VenueCoordinates;
    List<DocumentReference> Participants;
    List<String> Tags;

    public Event() {
    }

    public Event(String name, String description, long likes, Timestamp time, String venue, GeoPoint venueCoordinates, List<DocumentReference> participants, List<String> imageUrls, List<String> tags) {
        this.Name = name;
        this.Description = description;
        this.Likes = likes;
        this.Time = time.toDate();
        this.Venue = venue;
        this.VenueCoordinates = venueCoordinates;
        this.Participants = participants;
        this.ImageUrls = imageUrls;
        this.Tags = tags;
    }

    public String getName() {
        return this.Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public String getDescription() {
        return this.Description;
    }

    public void setDescription(String description) {
        this.Description = description;
    }

    public long getLikes() {
        return this.Likes;
    }

    public void setLikes(long likes) {
        this.Likes = likes;
    }

    public Date getTime() {
        return this.Time;
    }

    public String getTimeSimple(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss");
        String date = simpleDateFormat.format(this.Time);
        return date.substring(0, date.length()-3);
    }

    public void setTime(Date time) {
        this.Time = time;
    }

    public String getVenue() {
        return this.Venue;
    }

    public void setVenue(String venue) {
        this.Venue = venue;
    }

    public GeoPoint getVenueCoordinates() {
        return this.VenueCoordinates;
    }

    public void setVenueCoordinates(GeoPoint venueCoordinates) {
        this.VenueCoordinates = venueCoordinates;
    }

    public List<DocumentReference> getParticipants() {
        return this.Participants;
    }

    public void setParticipants(ArrayList<DocumentReference> participants) {
        this.Participants = participants;
    }

    public List<String> getImageUrls() {
        return this.ImageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.ImageUrls = imageUrls;
    }

    public List<String> getTags() {
        return this.Tags;
    }

    public void setTag(List<String> tags) {
        this.Tags = tags;
    }
}
