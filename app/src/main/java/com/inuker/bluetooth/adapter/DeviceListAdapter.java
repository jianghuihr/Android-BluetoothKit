package com.inuker.bluetooth.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.inuker.bluetooth.R;
import com.inuker.bluetooth.library.beacon.Beacon;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.utils.BluetoothLog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by dingjikerbo on 2016/9/1
 */
public class DeviceListAdapter extends BaseAdapter implements Comparator<SearchResult> {

    private Context mContext;

    private List<SearchResult> mDataList;
    private OnClickItemListener listener;

    public DeviceListAdapter(Context context) {
        mContext = context;
        mDataList = new ArrayList<>();
    }

    public void refresh(List<SearchResult> data) {
        mDataList.clear();
        mDataList.addAll(data);
        Collections.sort(mDataList, this);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int compare(SearchResult lhs, SearchResult rhs) {
        return rhs.rssi - lhs.rssi;
    }

    private static class ViewHolder {
        TextView name;
        TextView mac;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.device_list_item, null, false);

            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.mac = (TextView) convertView.findViewById(R.id.mac);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final SearchResult result = (SearchResult) getItem(position);

        holder.name.setText(result.getName());
        holder.mac.setText(result.getAddress());

        Beacon beacon = new Beacon(result.scanRecord);
        convertView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.setClass(mContext, DeviceDetailActivity.class);
//                intent.putExtra("mac", result.getAddress());
//                mContext.startActivity(intent);
                if (null != listener) {
                    listener.onClickItem(result);
                }
            }
        });

        return convertView;
    }

    public interface OnClickItemListener {
        void onClickItem(SearchResult result);
    }

    public void setOnClickItemListener(OnClickItemListener listener) {
        this.listener = listener;
    }
}
