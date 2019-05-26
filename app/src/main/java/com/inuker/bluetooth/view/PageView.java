package com.inuker.bluetooth.view;

import android.view.View;

public class PageView {

    private View view;
    private String title;

    public PageView(View view, String title) {
        this.view = view;
        this.title = title;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
