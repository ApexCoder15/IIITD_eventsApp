package com.example.buzzup;

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

public class HomeFragment extends Fragment
{
    FirebaseFirestore db;
    HashMap<String, ArrayList<Event>> dict;
    RecyclerView recyclerView;
    ArrayList<HomeRvModel> eventData;
    LinearLayoutManager linearLayoutManager;
    HomeRvAdapter homeRvAdapter;
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
        linearLayoutManager = new LinearLayoutManager(getActivity(), linearLayoutManager.HORIZONTAL, false);
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
                    ArrayList<Event> tagEventDetails = dict.get("Literature");
                    for (Event e : tagEventDetails)
                    {
                        eventData.add(new HomeRvModel(e.getImageUrls().get(0), e.getName()));
                    }
                    homeRvAdapter = new HomeRvAdapter(getActivity(), eventData);
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
