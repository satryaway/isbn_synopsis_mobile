package com.samstudio.isbnsynopsis.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.samstudio.isbnsynopsis.R;

/**
 * Created by M Agung Satrio on 6/26/2015.
 * a class to handle UIL
 */
public class UniversalImageLoader {
    private Context mContext;
    private ImageLoader imageLoader = ImageLoader.getInstance();

    public UniversalImageLoader(Context mContext){
        this.mContext = mContext;
    }
    public void initImageLoader() {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                mContext).threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs().build();
        if (!imageLoader.isInited())
            imageLoader.init(config);
        imageLoader.handleSlowNetwork(true);
    }

    public static DisplayImageOptions getUILOption() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.book)
                .showImageForEmptyUri(R.drawable.book)
                .showImageOnFail(R.drawable.book)
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
        return options;
    }

    public void display(ImageView img, String url)
    {
        imageLoader.displayImage(url, img, getUILOption(), new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        }, new ImageLoadingProgressListener() {
            @Override
            public void onProgressUpdate(String imageUri, View view, int current, int total) {

            }
        });
    }
}
