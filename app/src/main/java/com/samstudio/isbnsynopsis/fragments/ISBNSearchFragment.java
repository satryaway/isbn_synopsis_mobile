package com.samstudio.isbnsynopsis.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mrengineer13.snackbar.SnackBar;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.samstudio.isbnsynopsis.R;
import com.samstudio.isbnsynopsis.models.Book;
import com.samstudio.isbnsynopsis.utils.APIAgent;
import com.samstudio.isbnsynopsis.utils.CommonConstants;
import com.samstudio.isbnsynopsis.utils.UniversalImageLoader;
import com.samstudio.isbnsynopsis.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by satryaway on 11/15/2015.
 */
public class ISBNSearchFragment extends Fragment {

    private View view;
    private TextView judulTV, penulisTV;
    private Button searchBtn, backBtn;
    private EditText searchET;
    private ImageView coverIV;
    private ScrollView detailWrapper;
    private ImageView clearIV;
    private Book book;
    private UniversalImageLoader imageLoader;

    public static Fragment newInstance(Context context) {
        ISBNSearchFragment isbnSearchFragment = new ISBNSearchFragment();
        return isbnSearchFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = (ViewGroup) inflater.inflate(R.layout.isbn_search_layout, container, false);
        imageLoader = new UniversalImageLoader(getActivity());
        imageLoader.initImageLoader();

        initUI();
        setCallBack();

        return view;
    }

    private void initUI() {
        judulTV = (TextView) view.findViewById(R.id.judul_tv);
        penulisTV = (TextView) view.findViewById(R.id.penulis_tv);
        searchBtn = (Button) view.findViewById(R.id.search_btn);
        backBtn = (Button) view.findViewById(R.id.back_btn);
        coverIV = (ImageView) view.findViewById(R.id.cover_iv);
        searchET = (EditText) view.findViewById(R.id.search_et);
        detailWrapper = (ScrollView) view.findViewById(R.id.detail_wrapper);
        clearIV = (ImageView) view.findViewById(R.id.clear_iv);
    }

    private void setCallBack() {
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchET.getText().toString().isEmpty()) {
                    searchET.setError("Masukkan no ISBN");
                } else {
                    makeRequest();
                }
            }
        });

        searchET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (searchET.getText().toString().isEmpty()) {
                        searchET.setError("Masukkan kata kunci");
                    } else {
                        makeRequest();
                    }
                    return true;
                }
                return false;
            }
        });

        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                clearIV.setVisibility(s.length() == 0 ? View.GONE : View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        clearIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchET.setText("");
            }
        });
    }

    private void makeRequest() {
        InputMethodManager inputManager = (InputMethodManager)
                getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);

        String url = CommonConstants.SERVICE_GET_BOOK_BY_ISBN + searchET.getText().toString();

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please Wait");

        APIAgent.get(url, null, new JsonHttpResponseHandler() {
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
                    String message = "Buku tidak ditemukan";
                    int status = response.getInt(CommonConstants.STATUS);
                    if (status == 1) {
                        detailWrapper.setVisibility(View.VISIBLE);
                        message = "Menampilkan data pencarian";
                        JSONArray array = response.getJSONArray(CommonConstants.RETURN_DATA);
                        JSONObject object = array.getJSONObject(0);
                        book = Utility.parseBook(object);
                        putData();
                    } else {
                        detailWrapper.setVisibility(View.INVISIBLE);
                    }

                    new SnackBar.Builder(getActivity())
                            .withMessage(message) // OR
                            .withTextColorId(R.color.colorAccent)
                            .withBackgroundColorId(R.color.colorPrimary)
                            .withDuration((short) 3000)
                            .show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(getActivity(), R.string.SERVER_ERROR_MSG, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(getActivity(), R.string.SERVER_ERROR_MSG, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void putData() {
        imageLoader.display(coverIV, CommonConstants.SERVICE_GET_COVER_IMAGE + book.getCover());
        judulTV.setText(book.getJudul());
        penulisTV.setText(book.getPenulis());
    }

}
