package com.samstudio.isbnsynopsis.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.samstudio.isbnsynopsis.DataInputActivity;
import com.samstudio.isbnsynopsis.HomeActivity;
import com.samstudio.isbnsynopsis.ISBNSynopsisApplication;
import com.samstudio.isbnsynopsis.R;
import com.samstudio.isbnsynopsis.utils.APIAgent;
import com.samstudio.isbnsynopsis.utils.CommonConstants;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by satryaway on 11/15/2015.
 */
public class LoginFragment extends Fragment {

    private View view;
    private Button scanBarcodeBtn;
    private EditText emailET, passwordET;
    private Button loginBtn;
    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";

    public static Fragment newInstance(Context context) {
        LoginFragment scanBarcodeFragment = new LoginFragment();
        return scanBarcodeFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = (ViewGroup) inflater.inflate(R.layout.login_layout, container, false);
        initUI();
        setCallBack();

        return view;
    }

    private void initUI() {
        emailET = (EditText) view.findViewById(R.id.email_et);
        passwordET = (EditText) view.findViewById(R.id.password_et);
        loginBtn = (Button) view.findViewById(R.id.login_btn);
    }

    private void setCallBack() {
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateForm()) {
                    submitForm();
                }
            }
        });
    }

    private void submitForm() {
        String url = CommonConstants.SERVICE_LOGIN;
        RequestParams requestParams = new RequestParams();
        requestParams.put(CommonConstants.EMAIL, emailET.getText().toString());
        requestParams.put(CommonConstants.PASSWORD, passwordET.getText().toString());

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Silahkan tunggu");
        progressDialog.setCancelable(true);

        APIAgent.post(url, requestParams, new JsonHttpResponseHandler(){
            @Override
            public void onStart() {
                progressDialog.show();
            }

            @Override
            public void onFinish() {
                progressDialog.hide();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    int status = response.getInt(CommonConstants.STATUS);
                    if (status == CommonConstants.RESULT_OK) {
                        JSONObject object = response.getJSONObject(CommonConstants.RETURN_DATA);
                        SharedPreferences.Editor editor = ISBNSynopsisApplication.getInstance().getSharedPreferences().edit();
                        editor.putBoolean(CommonConstants.IS_LOGGED_IN, true);
                        editor.putString(CommonConstants.ID, object.getString(CommonConstants.ID));
                        editor.putString(CommonConstants.EMAIL, object.getString(CommonConstants.EMAIL));
                        editor.putString(CommonConstants.NAME, object.getString(CommonConstants.NAME));
                        editor.apply();

                        Intent intent = new Intent (getActivity(), HomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra(CommonConstants.MESSAGE, "Berhasil log in");
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(getActivity(), R.string.RTO, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(getActivity(), R.string.SERVER_ERROR_MSG, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateForm() {
        int tot = 0;

        if (emailET.getText().toString().isEmpty()) {
            emailET.setError("Masukkan email!");
        } else {
            tot++;
        }

        if (passwordET.getText().toString().isEmpty()) {
            passwordET.setText("Masukkan password!");
        } else {
            tot ++;
        }
        return tot == 2;
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
