package com.samstudio.isbnsynopsis.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.samstudio.isbnsynopsis.DataInputActivity;
import com.samstudio.isbnsynopsis.R;
import com.samstudio.isbnsynopsis.utils.CommonConstants;

/**
 * Created by satryaway on 11/15/2015.
 */
public class DataInputFragment extends Fragment {

    private View view;
    private Button scanBarcodeBtn;
    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";

    public static Fragment newInstance(Context context) {
        DataInputFragment scanBarcodeFragment = new DataInputFragment();
        return scanBarcodeFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = (ViewGroup) inflater.inflate(R.layout.search_book_layout, null);
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == getActivity().RESULT_OK) {
            String contents = data.getStringExtra("SCAN_RESULT");
            String format = data.getStringExtra("SCAN_RESULT_FORMAT");

            Intent intent = new Intent(getActivity(), DataInputActivity.class);
            intent.putExtra(CommonConstants.CONTENT_CODE, contents);
            startActivity(intent);
        }
    }
}
