package com.meidp.pictures.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;


import com.meidp.pictures.MainActivity;

import java.io.File;

/**
 * Package com.meidp.pictures.utils
 * 作  用:
 * Author: wxianing
 * 时  间: 16/9/16
 */
public class PictureScanner implements MediaScannerConnection.MediaScannerConnectionClient {

    private MediaScannerConnection mMs;
    private File mFile;
    private Context context;
    private Activity activity;

    private File[] allFiles;

    public PictureScanner(Context context, String pictureFolderPath, Activity activity) {
        File folder = new File(pictureFolderPath);
        allFiles = folder.listFiles();
        swap(allFiles);
        this.activity = activity;
        this.context = context;
        mFile = allFiles[0];
        mMs = new MediaScannerConnection(context, this);
        mMs.connect();
    }

    public void onMediaScannerConnected() {
        mMs.scanFile(mFile.getAbsolutePath(), null);
    }

    public void onScanCompleted(String path, Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        activity.startActivityForResult(intent, MainActivity.PHOTO_REQUEST_GALLERY);
        mMs.disconnect();
    }

    private void swap(File a[]) {
        int len = a.length;
        for (int i = 0; i < len / 2; i++) {
            File tmp = a[i];
            a[i] = a[len - 1 - i];
            a[len - 1 - i] = tmp;
        }
    }
    
}

