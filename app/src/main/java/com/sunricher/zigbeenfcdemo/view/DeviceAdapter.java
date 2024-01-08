package com.sunricher.zigbeenfcdemo.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sunricher.zigbeenfcdemo.R;
import com.sunricher.zigbeenfcdemo.model.AttributeItem;

import java.util.ArrayList;
import java.util.List;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceViewHolder> {
    private List<AttributeItem> items = new ArrayList<>();
    private OnItemClickListener itemClickListener;

    public DeviceAdapter(List<AttributeItem> items, OnItemClickListener listener) {
        this.items = items;
        this.itemClickListener = listener;
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_attr_item, parent, false);
        return new DeviceViewHolder(view, itemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(List<AttributeItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public List<AttributeItem> getItems() {
        return items;
    }
}
