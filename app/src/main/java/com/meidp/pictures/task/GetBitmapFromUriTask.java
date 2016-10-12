package com.meidp.pictures.task;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.widget.ImageView;

import com.xinlan.imageeditlibrary.editimage.utils.BitmapUtils;

/**
 * Package： com.meidp.pictures.task
 * Author： wxianing
 * 作  用：
 * 时  间： 2016/10/11
 */
public class GetBitmapFromUriTask extends AsyncTask<String, Void, Bitmap> {

    private ImageView img;
    private Bitmap bitmap;
    private int imageWidth, imageHeight;

    public GetBitmapFromUriTask(ImageView img, Context mContext) {
        this.img = img;
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        imageWidth = metrics.widthPixels;
        imageHeight = metrics.heightPixels;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        return BitmapUtils.getSampledBitmap(params[0], imageWidth / 4, imageHeight / 4);
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);
        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
            System.gc();
        }
        bitmap = result;
        img.setImageBitmap(bitmap);
    }
}
