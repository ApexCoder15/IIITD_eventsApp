package com.example.buzzup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class HomeRvAdapter extends RecyclerView.Adapter<HomeRvViewHolder>
{
    Context context;
    List<HomeRvModel> list;

    public HomeRvAdapter(Context context1, List<HomeRvModel> list1)
    {
        this.context = context1;
        this.list = list1;
    }

    @Override
    public HomeRvViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return new HomeRvViewHolder(LayoutInflater.from(context).inflate(R.layout.row_layout_home, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HomeRvViewHolder holder, int position)
    {
        final int index = holder.getAdapterPosition();
        holder.eventName.setText(list.get(position).getEventName());
        Picasso.get().load(list.get(position).eventImageUrl).into(holder.eventImage);
    }

    @Override
    public int getItemCount()
    {
        return list.size();
    }
}
