package com.example.buzzup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class HomeRvAdapter extends RecyclerView.Adapter<HomeRvViewHolder>
{
    Context context;
    List<HomeRvModel> list;
    RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    ClickListener listener;

    public HomeRvAdapter(Context context1, List<HomeRvModel> list1, ClickListener listener1)
    {
        this.context = context1;
        this.list = list1;
        this.listener = listener1;
    }

    @Override
    public HomeRvViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return new HomeRvViewHolder(LayoutInflater.from(context).inflate(R.layout.fragment_home_inner, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HomeRvViewHolder holder, int position)
    {
        final int index = holder.getAdapterPosition();
        HomeRvModel parentItem = list.get(position);
        HomeRvViewHolder.tagName.setText(parentItem.getTagName());
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        layoutManager.setInitialPrefetchItemCount(parentItem.getTagEventLists().size());
        HomeRvAdapterInner childItemAdapter = new HomeRvAdapterInner(context, parentItem.getTagEventLists(), listener);
        HomeRvViewHolder.ChildRecyclerView.setLayoutManager(layoutManager);
        HomeRvViewHolder.ChildRecyclerView.setAdapter(childItemAdapter);
        HomeRvViewHolder.ChildRecyclerView.setRecycledViewPool(viewPool);
    }

    @Override
    public int getItemCount()
    {
        return list.size();
    }
}
