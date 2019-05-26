package com.inuker.bluetooth.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.inuker.bluetooth.R;

public class WidgetsAdapter extends RecyclerView.Adapter<WidgetsAdapter.WidgetHolder> {

    private Context context;

    public WidgetsAdapter(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override
    public WidgetHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item_debug_widget,
                parent, false);
        return new WidgetHolder(view);
    }

    @Override
    public void onBindViewHolder(WidgetHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 6;
    }

    class WidgetHolder extends RecyclerView.ViewHolder {

        public WidgetHolder(View itemView) {
            super(itemView);
        }
    }
}
