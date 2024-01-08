package com.sunricher.zigbeenfcdemo.view;

import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sunricher.zigbeenfcdemo.R;
import com.sunricher.zigbeenfcdemo.model.AttributeItem;

public class DeviceViewHolder extends RecyclerView.ViewHolder {
    private TextView titleTv;
    private TextView detailTv;

    private OnItemClickListener itemClickListener;

    public DeviceViewHolder(View itemView, OnItemClickListener listener) {
        super(itemView);
        titleTv = itemView.findViewById(R.id.title_tv);
        detailTv = itemView.findViewById(R.id.detail_tv);
        itemClickListener = listener;
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(getAdapterPosition());
                }
            }
        });
    }

    public void bind(AttributeItem item) {
        titleTv.setText(item.getTitle());
        detailTv.setText(item.getDetail());
    }
}
