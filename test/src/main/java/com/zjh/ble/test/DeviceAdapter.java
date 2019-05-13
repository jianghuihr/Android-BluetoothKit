package com.zjh.ble.test;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceHolder> {

    private Context context;
    private List<BleDevice> deviceList = new ArrayList<>();

    public DeviceAdapter(Context context) {
        this.context = context.getApplicationContext();
    }

    @NonNull
    @Override
    public DeviceHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceHolder deviceHolder, int i) {
        BleDevice device = deviceList.get(i);
        if (null == device) {
            return;
        }

    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }

    class DeviceHolder extends RecyclerView.ViewHolder {

        public DeviceHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public void refresh(List<BleDevice> devices) {
        deviceList.clear();
        deviceList.addAll(devices);
        notifyDataSetChanged();
    }
}
