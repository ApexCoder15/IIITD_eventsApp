package com.example.buzzup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class HomeRvAdapterInner extends RecyclerView.Adapter<HomeRvViewHolderInner>
{
    Context context;
    List<HomeRvModelInner> list;
    ClickListener listener;

    public HomeRvAdapterInner(Context context1, List<HomeRvModelInner> list1, ClickListener listener1)
    {
        this.context = context1;
        this.list = list1;
        this.listener = listener1;
    }

    @Override
    public HomeRvViewHolderInner onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return new HomeRvViewHolderInner(LayoutInflater.from(context).inflate(R.layout.row_layout_home, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HomeRvViewHolderInner holder, int position)
    {
        final int index = holder.getAdapterPosition();
        holder.eventName.setText(list.get(position).getEventName());
        Picasso.get().load(list.get(position).eventImageUrl).into(holder.eventImage);
        holder.eventImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.click(index, list.get(position).getEventName());
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return list.size();
    }
}
