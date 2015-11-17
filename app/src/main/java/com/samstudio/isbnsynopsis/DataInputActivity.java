package com.samstudio.isbnsynopsis;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.mrengineer13.snackbar.SnackBar;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.samstudio.isbnsynopsis.utils.APIAgent;
import com.samstudio.isbnsynopsis.utils.CommonConstants;
import com.soundcloud.android.crop.Crop;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import cz.msebera.android.httpclient.Header;

/**
 * Created by satryaway on 11/15/2015.
 */
public class DataInputActivity extends AppCompatActivity {
    private String contentCode;
    private EditText contentCodeET, judulET, penulisET, isbnET, sinopsisET;
    private ImageView coverIV;
    private File coverImageFile = null;
    private Button saveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_input_layout);

        handleIntent();
        initUI();
        setCallBack();
        putData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
            beginCrop(data.getData());
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, data);
        }
    }

    private void setCallBack() {
        coverIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePopupDialog();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateForm()) {
                    try {
                        sendData();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void sendData() throws FileNotFoundException {
        String URL = CommonConstants.SERVICE_POST_BOOK;

        RequestParams requestParams = new RequestParams();
        requestParams.put(CommonConstants.KODE, contentCodeET.getText().toString());
        requestParams.put(CommonConstants.JUDUL, judulET.getText().toString());
        requestParams.put(CommonConstants.PENULIS, penulisET.getText().toString());
        requestParams.put(CommonConstants.ISBN, isbnET.getText().toString());
        requestParams.put(CommonConstants.SINOPSIS, sinopsisET.getText().toString());
        requestParams.put(CommonConstants.COVER, coverImageFile, "image/jpeg");

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.please_wait));

        APIAgent.post(URL, requestParams, new JsonHttpResponseHandler() {
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
                        Intent intent = new Intent(DataInputActivity.this, HomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra(CommonConstants.MESSAGE, "Berhasil input buku!");
                        startActivity(intent);
                    } else {
                        new SnackBar.Builder(DataInputActivity.this)
                                .withMessage("Gagal input buku!") // OR
                                .withTextColorId(R.color.colorAccent)
                                .withBackgroundColorId(R.color.colorPrimary)
                                .withDuration((short) 5000)
                                .show();
                    }
                    //Toast.makeText(DataInputActivity.this, response.getString(CommonConstants.MESSAGE), Toast.LENGTH_SHORT).show();
                    /*new SnackBar.Builder(DataInputActivity.this)
                            .withMessage("This library is awesome!") // OR
                            .withActionMessage("Action") // OR
                            .withTextColorId(R.color.colorAccent)
                            .withBackgroundColorId(R.color.colorPrimary)
                            .withDuration((short) 1000)
                            .show();*/

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(DataInputActivity.this, R.string.RTO, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(DataInputActivity.this, R.string.SERVER_ERROR_MSG, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateForm() {
        int total = 0;

        if (contentCodeET.getText().toString().isEmpty()) {
            contentCodeET.setError(getString(R.string.should_not_empty));
        } else {
            total++;
        }

        if (judulET.getText().toString().isEmpty()) {
            judulET.setError(getString(R.string.should_not_empty));
        } else {
            total++;
        }

        if (penulisET.getText().toString().isEmpty()) {
            penulisET.setError(getString(R.string.should_not_empty));
        } else {
            total++;
        }

        if (isbnET.getText().toString().isEmpty()) {
            isbnET.setError(getString(R.string.should_not_empty));
        } else {
            total++;
        }

        if (sinopsisET.getText().toString().isEmpty()) {
            sinopsisET.setError(getString(R.string.should_not_empty));
        } else {
            total++;
        }

        if (coverImageFile == null) {
            Toast.makeText(DataInputActivity.this, R.string.insert_cover_image, Toast.LENGTH_SHORT).show();
        } else {
            total++;
        }

        return total == 6;
    }

    private void makePopupDialog() {
        final String[] option = getResources().getStringArray(R.array.set_picture_item);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(DataInputActivity.this, android.R.layout.select_dialog_item, option);
        AlertDialog.Builder builder = new AlertDialog.Builder(DataInputActivity.this);

        builder.setTitle(R.string.set_cover_image);
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                switch (which) {
                    case 0:
                        Crop.pickImage(DataInputActivity.this);
                        break;
                    default:
                        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                        startActivityForResult(intent, Crop.REQUEST_PICK);
                        break;
                }
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void handleIntent() {
        Intent intent = getIntent();
        contentCode = intent.getStringExtra(CommonConstants.CONTENT_CODE);
    }

    private void initUI() {
        contentCodeET = (EditText) findViewById(R.id.content_code_et);
        judulET = (EditText) findViewById(R.id.judul_et);
        penulisET = (EditText) findViewById(R.id.penulis_et);
        isbnET = (EditText) findViewById(R.id.isbn_et);
        sinopsisET = (EditText) findViewById(R.id.sinopsis_et);
        coverIV = (ImageView) findViewById(R.id.cover_iv);
        saveBtn = (Button) findViewById(R.id.save_btn);
    }

    private void putData() {
        contentCodeET.setText(contentCode);
    }

    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            coverIV.setImageBitmap(null);
            coverIV.setImageBitmap(setPic(Crop.getOutput(result)));
            Bitmap imageBitmap = setPic(Crop.getOutput(result));
            coverImageFile = storeImage(imageBitmap);
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private File storeImage(Bitmap image) {
        File f = getOutputMediaFile();

        String TAG = CommonConstants.COVER_IMAGE;
        if (f == null) {
            Log.d(TAG, "Error creating media file, check storage permissions: ");// e.getMessage());
            return null;
        }
        try {
            FileOutputStream fos = new FileOutputStream(f);
            image.compress(Bitmap.CompressFormat.JPEG, 60, fos);
            fos.close();
            return f;
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }

        return null;
    }

    private File getOutputMediaFile() {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + getApplicationContext().getPackageName()
                + "/Images");

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        // Create a media file name
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "cover" + ".jpg");
        return mediaFile;
    }

    private Bitmap setPic(Uri uri) {
        int targetW;
        int targetH;

        // Get the dimensions of the View
        targetW = coverIV.getWidth();
        targetH = coverIV.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(uri.getPath(), bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(uri.getPath(), bmOptions);

        //Checking image orientation
        try {
            int orientation = getExifOrientation(uri.getPath());

            if (orientation == 1) {
                return bitmap;
            }
            boolean isRotated = false;
            Matrix matrix = new Matrix();
            switch (orientation) {
                case 2:
                    matrix.setScale(-1, 1);
                    break;
                case 3:
                    matrix.setRotate(180);
                    break;
                case 4:
                    matrix.setRotate(180);
                    matrix.postScale(-1, 1);
                    break;
                case 5:
                    matrix.setRotate(90);
                    matrix.postScale(-1, 1);
                    isRotated = true;
                    break;
                case 6:
                    matrix.setRotate(90);
                    isRotated = true;
                    break;
                case 7:
                    matrix.setRotate(-90);
                    matrix.postScale(-1, 1);
                    isRotated = true;
                    break;
                case 8:
                    matrix.setRotate(-90);
                    isRotated = true;
                    break;
                default:
                    return bitmap;
            }

            try {
                if (isRotated) {
                    Bitmap oriented = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                    Bitmap result = Bitmap.createScaledBitmap(oriented, oriented.getHeight(), oriented.getWidth(), true);
                    bitmap.recycle();
                    return result;
                } else {
                    Bitmap oriented = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                    bitmap.recycle();
                    return oriented;
                }
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
                return bitmap;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    private static int getExifOrientation(String src) throws IOException {
        int orientation = 1;

        try {
            if (Build.VERSION.SDK_INT >= 5) {
                Class<?> exifClass = Class.forName("android.media.ExifInterface");
                Constructor<?> exifConstructor = exifClass.getConstructor(new Class[]{String.class});
                Object exifInstance = exifConstructor.newInstance(new Object[]{src});
                Method getAttributeInt = exifClass.getMethod("getAttributeInt", new Class[]{String.class, int.class});
                Field tagOrientationField = exifClass.getField("TAG_ORIENTATION");
                String tagOrientation = (String) tagOrientationField.get(null);
                orientation = (Integer) getAttributeInt.invoke(exifInstance, new Object[]{tagOrientation, 1});
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        return orientation;
    }
}
