package com.example.buzzup;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HomeFragment extends Fragment
{
    FirebaseFirestore db;
    HashMap<String, ArrayList<Event>> dict;
    RecyclerView recyclerView;
    ArrayList<HomeRvModel> eventData;
    LinearLayoutManager linearLayoutManager;
    HomeRvAdapter homeRvAdapter;
    ClickListener listener;
    public HomeFragment()
    {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        //1. set stuff
        db = FirebaseFirestore.getInstance();
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerHome);
        linearLayoutManager = new LinearLayoutManager(getActivity(), linearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        eventData = new ArrayList<HomeRvModel>();
        /*eventData.add("Hello");
        eventData.add("yo");
        eventData.add("brett");
        eventData.add("hiya");
        eventData.add("eddie");*/

        //1. api call to get list of all events associated with each tag
        String[] tags = new String[]{"literature", "music", "dance", "art"};
        dict = new HashMap<String, ArrayList<Event>>();

        db.collection("events")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for(QueryDocumentSnapshot document: queryDocumentSnapshots)
                    {
                        Event event = document.toObject(Event.class);
                        try
                        {
                            if (dict.containsKey(event.getTags().get(0))) {
                                ArrayList<Event> temparr = dict.get(event.getTags().get(0));
                                temparr.add(event);
                                dict.put(event.getTags().get(0), temparr);
                            } else {
                                ArrayList<Event> temparr = new ArrayList<>();
                                temparr.add(event);
                                dict.put(event.getTags().get(0), temparr);
                                System.out.println(event.getTags().get(0));
                                Log.d("HOME1", dict.size() + "");
                            }
                        }
                        catch (Exception e)
                        {
                            System.out.println(e);
                            break;
                        }
                    }
                    //continue here
                    for (String tag : dict.keySet())
                    {
                        List<HomeRvModelInner> eventInner = new ArrayList<>();
                        ArrayList<Event> tagEventDetails = dict.get(tag);
                        for (Event e : tagEventDetails)
                        {
                            eventInner.add(new HomeRvModelInner(e.getImageUrls().get(0), e.getName()));
                        }
                        eventData.add(new HomeRvModel(eventInner, tag));
                    }
                    /*List<HomeRvModelInner> eventInner = new ArrayList<>();
                    ArrayList<Event> tagEventDetails = dict.get("Literature");
                    for (Event e : tagEventDetails)
                    {
                        eventInner.add(new HomeRvModelInner(e.getImageUrls().get(0), e.getName()));
                    }
                    eventData.add(new HomeRvModel(eventInner, "Literature"));
                    eventInner = new ArrayList<>();
                    tagEventDetails = dict.get("Music");
                    for (Event e : tagEventDetails)
                    {
                        eventInner.add(new HomeRvModelInner(e.getImageUrls().get(0), e.getName()));
                    }
                    eventData.add(new HomeRvModel(eventInner, "Music"));*/
                    listener = new ClickListener() {
                        @Override
                        public void click(int index, String eventName)
                        {
                            Log.d("CLICK EVENT", "CLICKED AT INDEX "+index+eventName);
                            //transition to new activity with details of myModelList[index]
                            Intent i = new Intent(getActivity(),ViewEventActivity.class);
                            i.putExtra("index", "-1");
                            i.putExtra("name", eventName);
                            getActivity().startActivity(i);

                        }
                    };
                    homeRvAdapter = new HomeRvAdapter(getActivity(), eventData, listener);
                    recyclerView.setAdapter(homeRvAdapter);
                });
        /*Log.d("HOME", dict.size()+"");
        for(String key : dict.keySet())
        {
            ArrayList<Event> tempev = dict.get(key);
            Log.d("HOME", key);
            for (Event e : tempev)
            {
                Log.d("HOME", e.getDescription());
            }
        }*/
        return v;
    }
}
