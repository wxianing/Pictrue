package com.meidp.pictures.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.meidp.pictures.R;
import com.meidp.pictures.utils.ImageUtils;

public class ScanImageActivity extends BaseActivity {

    Bitmap bitmap = null;
    ImageView imageview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_image);
        imageview = (ImageView) findViewById(R.id.imageView);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("Bitmap")) {
            bitmap = intent.getParcelableExtra("Bitmap");
            Matrix matrix = new Matrix(); //接收图片之后放大 1.5倍
            matrix.postScale(1.5f, 1.5f);
            Bitmap bit = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                    bitmap.getHeight(), matrix, true);
            imageview.setImageBitmap(bit);
        }

        imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                if (num == true) {
//                    Matrix matrix = new Matrix();
//                    matrix.postScale(scaleWidth, scaleHeight);
//
//                    Bitmap newBitmap = Bitmap.createBitmap(bp, 0, 0, bp.getWidth(),
//                            bp.getHeight(), matrix, true);
//                    imageview.setImageBitmap(newBitmap);
//                    num = false;
//                } else {
//                    Matrix matrix = new Matrix();
//                    matrix.postScale(1.0f, 1.0f);
//                    Bitmap newBitmap = Bitmap.createBitmap(bp, 0, 0, bp.getWidth(),
//                            bp.getHeight(), matrix, true);
//                    imageview.setImageBitmap(newBitmap);
//                    num = true;
//                }
//                break;
//        }
//        return super.onTouchEvent(event);
//    }
}
