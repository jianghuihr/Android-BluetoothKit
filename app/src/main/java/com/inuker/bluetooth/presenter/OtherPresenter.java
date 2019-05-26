package com.inuker.bluetooth.presenter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;

import com.inuker.bluetooth.R;

public class OtherPresenter extends BasePresenter {

    private Activity activity;
    private final String TITLE = "其他";

    public OtherPresenter(Activity activity) {
        this.activity = activity;
    }

    @Override
    public View getView() {
        View view = LayoutInflater.from(activity).inflate(R.layout.view_presenter_other, null);
        return view;
    }

    @Override
    public String getTitle() {
        return TITLE;
    }
}
