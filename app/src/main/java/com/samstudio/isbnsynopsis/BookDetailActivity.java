package com.samstudio.isbnsynopsis;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mrengineer13.snackbar.SnackBar;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.samstudio.isbnsynopsis.models.Book;
import com.samstudio.isbnsynopsis.utils.APIAgent;
import com.samstudio.isbnsynopsis.utils.CommonConstants;
import com.samstudio.isbnsynopsis.utils.UniversalImageLoader;
import com.samstudio.isbnsynopsis.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by satryaway on 11/15/2015.
 */
public class BookDetailActivity extends AppCompatActivity {
    private TextView judulTV, isbnTV, penulisTV, sinopsisTV;
    private Button backBtn;
    private String code;
    private Book book = new Book();
    private UniversalImageLoader imageLoader;
    private ImageView coverIV;
    private boolean isPassingID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageLoader = new UniversalImageLoader(this);
        imageLoader.initImageLoader();
        setContentView(R.layout.book_detail_layout);
        overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);

        handleIntent();
        getData();
        initUI();
        setCallBack();
    }

    private void handleIntent() {
        Intent intent = getIntent();
        code = intent.getStringExtra(CommonConstants.CONTENT_CODE);
        isPassingID = intent.getBooleanExtra(CommonConstants.IS_PASSING_ID, false);
    }

    private void getData() {
        String url = isPassingID ? CommonConstants.SERVICE_GET_BOOK_BY_ID : CommonConstants.SERVICE_GET_BOOK_BY_CODE;
        url = url + code;

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.please_wait));

        APIAgent.get(url, null, new JsonHttpResponseHandler(){
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
                        book = Utility.parseBook(response.getJSONArray(CommonConstants.RETURN_DATA).getJSONObject(0));
                        putData();
                    }

                    //Toast.makeText(DataInputActivity.this, response.getString(CommonConstants.MESSAGE), Toast.LENGTH_SHORT).show();


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(BookDetailActivity.this, R.string.RTO, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(BookDetailActivity.this, R.string.SERVER_ERROR_MSG, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initUI() {
        judulTV = (TextView) findViewById(R.id.judul_tv);
        penulisTV = (TextView) findViewById(R.id.penulis_tv);
        isbnTV = (TextView) findViewById(R.id.isbn_tv);
        sinopsisTV = (TextView) findViewById(R.id.sinopsis_tv);
        backBtn = (Button) findViewById(R.id.back_btn);
        coverIV = (ImageView) findViewById(R.id.cover_iv);
    }

    private void setCallBack() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
            }
        });
    }

    private void putData() {
        imageLoader.display(coverIV, CommonConstants.SERVICE_GET_COVER_IMAGE + book.getCover());

        judulTV.setText(book.getJudul());
        penulisTV.setText(book.getPenulis());
        isbnTV.setText(book.getIsbn());
        sinopsisTV.setText(book.getSinopsis());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
    }
}
