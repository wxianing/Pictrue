package com.meidp.pictures.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

/**
 * Package com.meidp.pictures.utils
 * 作  用:
 * Author: wxianing
 * 时  间: 16/9/16
 */
public class ImageUtils {
    public static Bitmap getBitmapFromUri(Uri uri, Context context) {
        try {
            // 读取uri所在的图片
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Uri getUriFromBitmap(Bitmap bitmap, Context context) {
        try {
            Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, null, null));
            return uri;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
