package com.example.buzzup;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HomeRvViewHolder extends RecyclerView.ViewHolder
{
    static RecyclerView ChildRecyclerView;
    static TextView tagName;
    public HomeRvViewHolder(@NonNull View itemView)
    {
        super(itemView);
        ChildRecyclerView = itemView.findViewById(R.id.recyclerHome_Inner);
        tagName = itemView.findViewById(R.id.fragment_home);
    }
}
