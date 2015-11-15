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

import com.samstudio.isbnsynopsis.BookDetailActivity;
import com.samstudio.isbnsynopsis.DataInputActivity;
import com.samstudio.isbnsynopsis.R;
import com.samstudio.isbnsynopsis.utils.CommonConstants;

/**
 * Created by satryaway on 11/15/2015.
 */
public class ScanBarcodeFragment extends Fragment {
    private View view;
    private Button scanBarcodeBtn;
    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";

    public static Fragment newInstance(Context context) {
        ScanBarcodeFragment scanBarcodeFragment = new ScanBarcodeFragment();
        return scanBarcodeFragment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == getActivity().RESULT_OK) {
            String contents = data.getStringExtra("SCAN_RESULT");
            String format = data.getStringExtra("SCAN_RESULT_FORMAT");

            Intent intent = new Intent(getActivity(), BookDetailActivity.class);
            intent.putExtra(CommonConstants.CONTENT_CODE, contents);
            startActivity(intent);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = (ViewGroup) inflater.inflate(R.layout.scan_barcode_layout, null);
        scanBarcodeBtn = (Button) view.findViewById(R.id.scan_barcode_btn);
        scanBarcodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(ACTION_SCAN);
                    intent.putExtra("SCAN_MODE", "PRODUCT_MODE");
                    startActivityForResult(intent, 0);
                } catch (ActivityNotFoundException anfe) {
                    showDialog(getActivity(), "No Scanner Found", "Download a scanner code activity?", "Yes", "No").show();
                }
            }
        });
        return view;
    }

    private static AlertDialog showDialog(final Activity act, CharSequence title, CharSequence message, CharSequence buttonYes, CharSequence buttonNo) {
        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(act);
        downloadDialog.setTitle(title);
        downloadDialog.setMessage(message);
        downloadDialog.setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Uri uri = Uri.parse("market://search?q=pname:" + "com.google.zxing.client.android");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    act.startActivity(intent);
                } catch (ActivityNotFoundException anfe) {

                }
            }
        });
        downloadDialog.setNegativeButton(buttonNo, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        return downloadDialog.show();
    }
}
