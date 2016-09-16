package com.meidp.pictures;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.meidp.pictures.utils.ImageUtils;
import com.meidp.pictures.utils.PictureScanner;
import com.meidp.pictures.utils.ToastUtils;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.util.List;

import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;


public class MainActivity extends BaseActivity implements View.OnClickListener, View.OnTouchListener {

    private static final int PHOTO_REQUEST_CAMERA = 1;// 拍照
    public static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    private static final int PHOTO_REQUEST_CUT = 3;// 结果
    private static final String PHOTO_FILE_NAME = "temp_photo.jpg";
    private static final String path = Environment.getExternalStorageDirectory() + File.separator;

    private Button select_btn;
    private Button crop_btn;

    private File tempFile;
    private Bitmap bitmap;

    private ImageView imageView;

    private RelativeLayout layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initEvent();
    }

    private void initView() {
        select_btn = (Button) findViewById(R.id.select_btn);
        imageView = (ImageView) findViewById(R.id.imageView);
        crop_btn = (Button) findViewById(R.id.crop_btn);
        layout = (RelativeLayout) findViewById(R.id.layout);
    }

    private void initEvent() {
        select_btn.setOnClickListener(this);
        crop_btn.setOnClickListener(this);
        imageView.setOnTouchListener(this);
    }

    private Uri outputUri;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.select_btn:
                showDialog();
                break;
            case R.id.crop_btn:
                imageView.buildDrawingCache();
                Bitmap bmp = imageView.getDrawingCache();
                Uri uri = ImageUtils.getUriFromBitmap(bmp, this);
//                crop(uri, bmp);
                outputUri = uri;
//                Bitmap b =imageCrop(bmp);
//                imageView.setImageBitmap(b);
                Crop.of(uri, outputUri).asSquare().start(this);
                break;
        }
    }

    private void showDialog() {
        final Dialog dialog = new Dialog(MainActivity.this, R.style.Dialog);
        View contentView = LayoutInflater.from(MainActivity.this).inflate(R.layout.customer_dialog_layout, null);
        dialog.setContentView(contentView);
        dialog.setCanceledOnTouchOutside(true);
        //取消
        TextView cancal = (TextView) contentView.findViewById(R.id.cancel);
        cancal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        //打开相册
        TextView open_album = (TextView) contentView.findViewById(R.id.open_album);
        open_album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                gallery();
                dialog.dismiss();
            }
        });
        //拍照
        TextView take_photos = (TextView) contentView.findViewById(R.id.take_photos);
        take_photos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera();
                dialog.dismiss();
            }
        });

        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();

        WindowManager wm = getWindowManager();
        Display d = wm.getDefaultDisplay(); // 获取屏幕宽、高用
//        p.height = (int) (d.getHeight() * 0.6); // 高度设置为屏幕的0.6
        lp.width = (int) (d.getWidth() * 0.35); // 宽度设置为屏幕的0.35

        dialogWindow.setAttributes(lp);
        dialog.show();
    }

    /**
     * 从相册获取
     */
    public void gallery() {
        // 激活系统图库，选择一张图片
//        Intent intent = new Intent(Intent.ACTION_PICK);
//        intent.setType("image/*");
////        intent.setType(path);
//        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);


//        ComponentName component = new ComponentName("com.meidp.pictures", "com.meidp.pictures.MainActivity");
//
//        Intent intent = new Intent();
//
//        intent.setComponent(component);
//        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);

        GalleryFinal.openGallerySingle(PHOTO_REQUEST_GALLERY, new GalleryFinal.OnHanlderResultCallback() {
            @Override
            public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
                if (reqeustCode == PHOTO_REQUEST_GALLERY && resultList != null && resultList.size() > 0) {
                    String path = resultList.get(0).getPhotoPath();
                    Uri uri = Uri.parse(path);
                    Bitmap b = ImageUtils.getBitmapFromUri(uri, MainActivity.this);
                    imageView.setImageBitmap(b);
                }
            }
            @Override
            public void onHanlderFailure(int requestCode, String errorMsg) {
                Log.e("errorMsg",errorMsg);
            }
        });

//带配置
//        GalleryFinal.openGallerySingle(REQUEST_CODE_GALLERY, functionConfig, mOnHanlderResultCallback);
    }

    /**
     * 从相机获取
     */
    public void camera() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        // 判断存储卡是否可以用，可用进行存储
        if (hasSdcard()) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(new File(Environment
                            .getExternalStorageDirectory(), PHOTO_FILE_NAME)));
        }
        startActivityForResult(intent, PHOTO_REQUEST_CAMERA);
    }

    /**
     * 检查SD卡
     *
     * @return
     */
    private boolean hasSdcard() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }


    private void crop(Uri uri, Bitmap b) {
        // 裁剪图片意图
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // 裁剪框的比例，1：1
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // 裁剪后输出图片的尺寸大小
        int w = b.getWidth(); // 得到图片的宽，高
        int h = b.getHeight();
        intent.putExtra("outputX", w);
        intent.putExtra("outputY", h);
        // 图片格式
        intent.putExtra("outputFormat", "JPEG");
        intent.putExtra("noFaceDetection", true);// 取消人脸识别
        intent.putExtra("return-data", true);// true:不返回uri，false：返回uri
        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PHOTO_REQUEST_GALLERY) {//从相册选择
            if (data != null) {
                // 得到图片的全路径
                Uri uri = data.getData();
                Bitmap bm = ImageUtils.getBitmapFromUri(uri, this);
                this.imageView.setImageBitmap(bm);
            }

        } else if (requestCode == PHOTO_REQUEST_CAMERA) {// 拍照
            if (hasSdcard()) {
                tempFile = new File(Environment.getExternalStorageDirectory(), PHOTO_FILE_NAME);
                Bitmap bm = ImageUtils.getBitmapFromUri(Uri.fromFile(tempFile), this);
                this.imageView.setImageBitmap(bm);
            } else {
                ToastUtils.shows(this, "未找到存储卡，无法存储照片！");
            }

        } else if (requestCode == PHOTO_REQUEST_CUT) {
            try {
                bitmap = data.getParcelableExtra("data");
                this.imageView.setImageBitmap(bitmap);
                if (tempFile != null) {
                    boolean delete = tempFile.delete();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == Crop.REQUEST_CROP && resultCode == RESULT_OK) {
            Bitmap bm = ImageUtils.getBitmapFromUri(outputUri, this);
            imageView.setImageBitmap(bm);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        ToastUtils.showl(this, "x=" + event.getX() + "  ,y=" + event.getY());
        TextView tv = new TextView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMarginStart((int) event.getX());
        tv.setLayoutParams(params);
//        tv.setTextColor(Color.WHITE);
        tv.setText("1");
        layout.addView(tv, 100, 100);
        return false;
    }
}
