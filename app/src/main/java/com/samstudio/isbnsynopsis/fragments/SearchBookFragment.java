package com.samstudio.isbnsynopsis.fragments;

import android.content.Context;
import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mrengineer13.snackbar.SnackBar;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.samstudio.isbnsynopsis.BookDetailActivity;
import com.samstudio.isbnsynopsis.R;
import com.samstudio.isbnsynopsis.adapters.BookListAdapter;
import com.samstudio.isbnsynopsis.models.Book;
import com.samstudio.isbnsynopsis.utils.APIAgent;
import com.samstudio.isbnsynopsis.utils.CommonConstants;
import com.samstudio.isbnsynopsis.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by satryaway on 11/15/2015.
 */
public class SearchBookFragment extends Fragment {
    private View view;
    private Button scanBarcodeBtn;
    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
    private EditText searchET;
    private Button searchBtn;
    private ListView bookLV;
    private BookListAdapter mAdapter;
    private ProgressBar loadingPB;
    private List<Book> bookList = new ArrayList<>();
    private ImageView clearIV;

    public static Fragment newInstance(Context context) {
        SearchBookFragment searchBookFragment = new SearchBookFragment();
        return searchBookFragment;
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
        view = (ViewGroup) inflater.inflate(R.layout.search_book_layout, null);

        initUI();
        setCallBack();

        return view;
    }

    private void initUI() {
        searchET = (EditText) view.findViewById(R.id.search_et);
        searchBtn = (Button) view.findViewById(R.id.search_btn);
        bookLV = (ListView) view.findViewById(R.id.book_lv);
        loadingPB = (ProgressBar) view.findViewById(R.id.loading_pb);
        clearIV = (ImageView) view.findViewById(R.id.clear_iv);

        mAdapter = new BookListAdapter(getActivity());
        bookLV.setAdapter(mAdapter);
    }

    private void setCallBack() {
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchET.getText().toString().isEmpty()) {
                    searchET.setError("Masukkan kata kunci");
                } else {
                    makeRequest();
                }
            }
        });

        bookLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), BookDetailActivity.class);
                intent.putExtra(CommonConstants.CONTENT_CODE, bookList.get(position).getId());
                intent.putExtra(CommonConstants.IS_PASSING_ID, true);
                startActivity(intent);
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

        String url = CommonConstants.SERVICE_GET_BOOK_BY_JUDUL + searchET.getText().toString();

        APIAgent.get(url, null, new JsonHttpResponseHandler(){
            @Override
            public void onStart() {
                loadingPB.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinish() {
                loadingPB.setVisibility(View.GONE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String message = "Buku tidak ditemukan";
                    int status = response.getInt(CommonConstants.STATUS);
                    if (status == 1) {
                        bookList.clear();
                        message = "Menampilkan data pencarian";
                        JSONArray array = response.getJSONArray(CommonConstants.RETURN_DATA);
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            Book book = Utility.parseBook(object);
                            bookList.add(book);
                        }

                        mAdapter.updateContent(bookList);
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
}
