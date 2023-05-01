package com.example.buzzup;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HomeRvViewHolderInner extends RecyclerView.ViewHolder
{
    ImageView eventImage;
    TextView eventName;
    public HomeRvViewHolderInner(@NonNull View itemView)
    {
        super(itemView);
        eventName = itemView.findViewById(R.id.eventName);
        eventImage = itemView.findViewById(R.id.eventImage);
    }
}
