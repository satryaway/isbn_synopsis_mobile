package com.samstudio.isbnsynopsis.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.samstudio.isbnsynopsis.R;

/**
 * Created by satryaway on 11/15/2015.
 */
public class HomeFragment extends Fragment {

    private View view;

    public static Fragment newInstance(Context context) {
        HomeFragment homeFragment = new HomeFragment();
        return homeFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = (ViewGroup) inflater.inflate(R.layout.content_home, container, false);
        initUI();
        setCallBack();

        return view;
    }

    private void initUI() {
    }

    private void setCallBack() {
    }

}
