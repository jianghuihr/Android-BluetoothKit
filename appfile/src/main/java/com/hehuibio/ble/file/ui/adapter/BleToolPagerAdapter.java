package com.hehuibio.ble.file.ui.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.hehuibio.ble.file.ui.view.PageView;

import java.util.ArrayList;
import java.util.List;

public class BleToolPagerAdapter extends PagerAdapter {

    private List<PageView> pageViewList = new ArrayList<>();

    public BleToolPagerAdapter() {
    }

    @Override
    public CharSequence getPageTitle(int position) {
        PageView pageView = pageViewList.get(position);
        return null == pageView ? "" : pageView.getTitle();
    }

    @Override
    public int getCount() {
        return pageViewList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        PageView pageView = pageViewList.get(position);
        View view = null;
        if (null != pageView) {
            view = pageView.getView();
            container.addView(view);
        }
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//        PageView pageView = pageViewList.get(position);
//        if (null != pageView) {
//            View view = pageView.getView();
//            container.removeView(view);
//        } else {
//            container.removeViewAt(position);
//        }
        container.removeView((View) object);
    }

    public void refresh(List<PageView> pageViews) {
        pageViewList.clear();
        pageViewList.addAll(pageViews);
        notifyDataSetChanged();
    }
}
