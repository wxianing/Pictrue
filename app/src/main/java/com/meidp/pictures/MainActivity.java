package com.meidp.pictures;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.meidp.pictures.bean.PhotoKey;
import com.meidp.pictures.bean.Photos;
import com.meidp.pictures.bean.PhotosData;
import com.meidp.pictures.utils.ImageUtils;
import com.meidp.pictures.utils.NullUtils;
import com.meidp.pictures.utils.SPUtils;
import com.meidp.pictures.utils.ToastUtils;
import com.soundcloud.android.crop.Crop;
import com.yalantis.ucrop.UCrop;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener, View.OnTouchListener {

    private static final int PHOTO_REQUEST_CAMERA = 1;// 拍照
    public static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    private static final int PHOTO_REQUEST_CUT = 3;// 结果
    private static final String PHOTO_FILE_NAME = "temp_photo.jpg";
    private static final String path = Environment.getExternalStorageDirectory() + File.separator + "picture" + File.separator;
    private Button select_btn;
    private Button crop_btn;
    private File tempFile;
    private Bitmap bitmap;
    private ImageView imageView;
    private RelativeLayout layout;
    private AbsoluteLayout absoluteLayout;
    private LinearLayout content_ll;
    private Button add_note;
    private Photos photos;
    private Photos p;
    private Uri outputUri;

    private ImageView previous_img;

    private ImageView next_img;

    private Button save_btn;
    private String name;
    private final static int PHOTO_REQUEST_IMGNOTE = 4;//添加批注


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        initView();
        initEvent();

    }

    private List<PhotosData> photosDataLists = new ArrayList<>();

    private void initView() {

        select_btn = (Button) findViewById(R.id.select_btn);
        imageView = (ImageView) findViewById(R.id.imageView);
        crop_btn = (Button) findViewById(R.id.crop_btn);
        layout = (RelativeLayout) findViewById(R.id.layout);
        absoluteLayout = (AbsoluteLayout) findViewById(R.id.absoluteLayout);
        content_ll = (LinearLayout) findViewById(R.id.content_ll);

        previous_img = (ImageView) findViewById(R.id.previous_img);
        next_img = (ImageView) findViewById(R.id.next_img);
        add_note = (Button) findViewById(R.id.add_note);
        save_btn = (Button) findViewById(R.id.save_btn);


//        SPUtils.clear(this);

        photos = new Photos();
        p = new Photos();
        String keyString = (String) SPUtils.get(this, "PhotoKey", "");
        Log.e("keyString", ">>>>>>>>>>" + keyString);
        if (NullUtils.isNotNull(keyString)) {
            PhotoKey photoKey = JSONObject.parseObject(keyString, new TypeReference<PhotoKey>() {
            });
            if (photoKey != null) {
                keyLists = photoKey.getKeys();
                if (keyLists != null && keyLists.size() > 0) {

                    String key = keyLists.get(keyLists.size() - 1);//默认打开最后一张图

                    initImage(key);
                }
            }
        }
    }

    List<String> keyLists = new ArrayList<>();//保存图片的数据集合

    private void initImage(final String imageString) {
        String result = (String) SPUtils.get(this, imageString, "");
        Log.e("result", "数据集合：" + result);
        if (NullUtils.isNotNull(result)) {
            try {
                p = JSONObject.parseObject(result, new TypeReference<Photos>() {
                });
                if (p != null) {
                    String url = p.getPaths();
                    imgPaths = url;
                    Log.e("imgPaths", imgPaths);
                    Bitmap bitmap = ImageUtils.getBitmapFromUri(Uri.parse(url), MainActivity.this);
                    imageView.setImageBitmap(bitmap);
                    photosDataLists.addAll(p.getPhotosDatas());

                    if (photosDataLists != null && photosDataLists.size() > 0) {
                        for (int i = 0; i < photosDataLists.size(); i++) {
                            final PhotosData photosData = photosDataLists.get(i);
                            TextView tv = new TextView(this);


                            tv.setX(photosData.getXcoordinate());
                            tv.setY(photosData.getYcoordinate());
                            tv.setBackgroundResource(R.drawable.round_shape);
                            tv.setGravity(Gravity.CENTER);
                            tv.setText(photosData.getNumber() + "");
                            tv.setTag("B");
                            absoluteLayout.addView(tv);

                            TextView notetv = new TextView(MainActivity.this);
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 50);
                            params.rightMargin = 5;
                            params.gravity = Gravity.CENTER;
                            notetv.setLayoutParams(params);
                            notetv.setGravity(Gravity.CENTER);
                            notetv.setTextSize(12);

                            notetv.setText("批注" + photosData.getNumber());
                            notetv.setBackgroundResource(R.drawable.orange_btn_shape);
                            notetv.setTextColor(Color.rgb(235, 147, 73));
                            notetv.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ToastUtils.showl(MainActivity.this, photosData.getContent());
                                }
                            });

                            final int finalI = i;
                            notetv.setOnLongClickListener(new View.OnLongClickListener() {
                                @Override
                                public boolean onLongClick(View v) {
                                    showDeleteDialog(finalI, imageString);
                                    return false;
                                }
                            });

                            content_ll.addView(notetv);
                        }
//                        currindex = photosDataLists.get(0).getNumber() + 1;
                        Log.e("currindex", ">>>>>>>>" + currindex);
                        currindex = photosDataLists.size() + 1;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void initEvent() {
        select_btn.setOnClickListener(this);
        crop_btn.setOnClickListener(this);
        save_btn.setOnClickListener(this);
        imageView.setOnTouchListener(this);
        add_note.setOnClickListener(this);
        previous_img.setOnClickListener(this);
        next_img.setOnClickListener(this);
    }

    int index = 0;

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
                if (outputUri == null) {
                    outputUri = uri;
                }
//                Bitmap b =imageCrop(bmp);
//                imageView.setImageBitmap(b);
//                Crop.of(uri, outputUri).asSquare().start(this);
                startCrop(outputUri);
                break;
            case R.id.add_note:
                View view = absoluteLayout.getChildAt(absoluteLayout.getChildCount() - 1);
                if (view.getTag() != null && !view.getTag().toString().equals("A")) {
                    ToastUtils.showl(MainActivity.this, "请先选择批注点在添加批注");
                    return;
                }
                showaddnoteDialog();
                break;
            case R.id.previous_img:
//                keyLists
                if (keyLists != null && keyLists.size() > 0) {
                    clearData();
                    index--;
                    if (index < 0) {
                        index = keyLists.size() - 1;
                    }
                    String key = keyLists.get(index);
                    initImage(key);
                }


                break;
            case R.id.next_img:
                if (keyLists != null && keyLists.size() > 0) {
                    clearData();
                    index++;
                    if (index >= keyLists.size()) {
                        index = 0;
                    }
                    String key = keyLists.get(index);
                    initImage(key);
                }

                break;
            case R.id.save_btn:
                ToastUtils.shows(this, "正在保存");
                keyLists.add(name);
                PhotoKey photoKey = new PhotoKey();
                photoKey.setKeys(keyLists);
                String photoKeyString = JSONObject.toJSONString(photoKey);
                SPUtils.save(this, "PhotoKey", photoKeyString);
                String photoString = JSONObject.toJSONString(p);
                SPUtils.save(MainActivity.this, name, photoString);
                break;
        }
    }

    private void startCrop(Uri sourceUri) {
//        Uri sourceUri = Uri.parse("http://star.xiziwang.net/uploads/allimg/140512/19_140512150412_1.jpg");
        //裁剪后保存到文件中
        clearData();
        Uri destinationUri = Uri.fromFile(new File(getCacheDir(), System.currentTimeMillis() + ".png"));

        UCrop.of(sourceUri, destinationUri).start(this);
//        UCrop.of(sourceUri, destinationUri).withAspectRatio(1, 1).withMaxResultSize(800, 800).start(this);

    }

    private void showaddnoteDialog() {
        final Dialog dialog = new Dialog(this, R.style.Dialog);
        View contentView = LayoutInflater.from(this).inflate(R.layout.edittext_dialog, null);
        TextView titleName = (TextView) contentView.findViewById(R.id.title);
        final EditText editText = (EditText) contentView.findViewById(R.id.message);
        titleName.setText("批注");
        dialog.setContentView(contentView);
        dialog.setCanceledOnTouchOutside(true);
        ImageView add_img_note = (ImageView) contentView.findViewById(R.id.add_img_note);

        Button negativeButton = (Button) contentView.findViewById(R.id.negativeButton);
        negativeButton.setClickable(true);
        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        Button positiveButton = (Button) contentView.findViewById(R.id.positiveButton);
        positiveButton.setClickable(true);

        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String content = editText.getText().toString().trim();
                PhotosData data = accountSetData();
                data.setContent(content);
                photosDataLists.add(data);
                p.setPaths(imgPaths);
                int dot = imgPaths.lastIndexOf("/");
                name = imgPaths.substring(dot + 1);
                p.setImageName(name);
                p.setPhotosDatas(photosDataLists);
                RelativeLayout.LayoutParams ll = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                ll.setMargins(10, 10, 10, 10);
                TextView tv = new TextView(MainActivity.this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 50);
                params.rightMargin = 5;
                params.gravity = Gravity.CENTER;
                tv.setLayoutParams(params);
                tv.setGravity(Gravity.CENTER);
                tv.setTextSize(12);
                tv.setLayoutParams(ll);
                tv.setGravity(Gravity.CENTER);
                tv.setText("批注" + (currindex++));
                tv.setBackgroundResource(R.drawable.orange_btn_shape);
                tv.setTextColor(Color.rgb(235, 147, 73));
                content_ll.addView(tv);

                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ToastUtils.showl(MainActivity.this, content);
                    }
                });

                dialog.dismiss();
            }
        });

        add_img_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, PHOTO_REQUEST_IMGNOTE);
            }
        });

        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();

        WindowManager wm = getWindowManager();
        Display d = wm.getDefaultDisplay(); // 获取屏幕宽、高用
//        p.height = (int) (d.getHeight() * 0.6); // 高度设置为屏幕的0.6
        lp.width = (int) (d.getWidth() * 0.4); // 宽度设置为屏幕的0.65
        dialogWindow.setAttributes(lp);
        dialog.show();
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
        lp.width = (int) (d.getWidth() * 0.4); // 宽度设置为屏幕的0.35

        dialogWindow.setAttributes(lp);
        dialog.show();
    }

    /**
     * textview对话框
     */
    private void showDeleteDialog(final int index, final String keys) {
        final Dialog dialog = new Dialog(MainActivity.this, R.style.Dialog);
        View contentView = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_textview_layout, null);
        TextView titleName = (TextView) contentView.findViewById(R.id.title);
        TextView content = (TextView) contentView.findViewById(R.id.hint_content);
        titleName.setText("温馨提示");//标题
        content.setText("是否删除此批注？");//提示你内容

        dialog.setContentView(contentView);
        dialog.setCanceledOnTouchOutside(true);
        Button negativeButton = (Button) contentView.findViewById(R.id.negativeButton);
        negativeButton.setClickable(true);
        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        Button positiveButton = (Button) contentView.findViewById(R.id.positiveButton);
        positiveButton.setClickable(true);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photosDataLists.remove(index);
                p.setPhotosDatas(photosDataLists);
                String photoString = JSONObject.toJSONString(p);

                Log.e("photoString", photoString);

                SPUtils.save(MainActivity.this, keys, photoString);
                clearData();
                initImage(keys);

                dialog.dismiss();
            }
        });
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();

        WindowManager wm = getWindowManager();
        Display d = wm.getDefaultDisplay(); // 获取屏幕宽、高用
//        p.height = (int) (d.getHeight() * 0.6); // 高度设置为屏幕的0.6
        lp.width = (int) (d.getWidth() * 0.4); // 宽度设置为屏幕的0.65

        dialogWindow.setAttributes(lp);
        dialog.show();
    }

    /**
     * 从相册获取
     */
    public void gallery() {
        // 激活系统图库，选择一张图片
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
//        intent.setType(path);
        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);


//        ComponentName component = new ComponentName("com.meidp.pictures", "com.meidp.pictures.MainActivity");
//
//        Intent intent = new Intent();
//
//        intent.setComponent(component);
//        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
//
//        GalleryFinal.openGallerySingle(PHOTO_REQUEST_GALLERY, new GalleryFinal.OnHanlderResultCallback() {
//            @Override
//            public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
//                if (reqeustCode == PHOTO_REQUEST_GALLERY && resultList != null && resultList.size() > 0) {
//                    String path = resultList.get(0).getPhotoPath();
//                    Uri uri = Uri.parse(path);
//                    Bitmap b = ImageUtils.getBitmapFromUri(uri, MainActivity.this);
//                    imageView.setImageBitmap(b);
//                }
//            }
//            @Override
//            public void onHanlderFailure(int requestCode, String errorMsg) {
//                Log.e("errorMsg",errorMsg +">>>>");
//            }
//        });

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
        int w = 4 * b.getWidth(); // 得到图片的宽，高
        int h = 4 * b.getHeight();
        intent.putExtra("outputX", 800);
        intent.putExtra("outputY", 800);
        // 图片格式
        intent.putExtra("outputFormat", "JPEG");
        intent.putExtra("noFaceDetection", true);// 取消人脸识别
        intent.putExtra("return-data", true);// true:不返回bitmap，false：返回bitmap

        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    String imgPaths = "";

    private void clearData() {
//        SPUtils.remove(this, "Image");
        content_ll.removeAllViews();
        photosDataLists.clear();
        for (int i = absoluteLayout.getChildCount() - 1; i > 0; i--) {
            View view = absoluteLayout.getChildAt(i);
            absoluteLayout.removeView(view);
        }
        currindex = 1;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PHOTO_REQUEST_GALLERY) {//从相册选择
            if (data != null) {
                clearData();
                // 得到图片的全路径
                Uri uri = data.getData();
                photoUri = uri;
                outputUri = uri;
                Log.e("uriPath", uri.getPath());
                imgPaths = uri.toString();
                Bitmap bm = ImageUtils.getBitmapFromUri(uri, this);
                this.imageView.setImageBitmap(bm);
                saveBitmapFile(bm);
                p.setPaths(imgPaths);
                String pString = JSONObject.toJSONString(p);
                SPUtils.save(MainActivity.this, "Image", pString);
            }

        } else if (requestCode == PHOTO_REQUEST_CAMERA) {// 拍照
            if (hasSdcard()) {
                clearData();
                tempFile = new File(Environment.getExternalStorageDirectory(), PHOTO_FILE_NAME);
                Bitmap bm = ImageUtils.getBitmapFromUri(Uri.fromFile(tempFile), this);
//                crop(Uri.fromFile(tempFile), bm);
                outputUri = Uri.fromFile(tempFile);
                saveBitmapFile(bm);
                this.imageView.setImageBitmap(bm);
            } else {
                ToastUtils.shows(this, "未找到存储卡，无法存储照片！");
            }

        } else if (requestCode == PHOTO_REQUEST_CUT && data != null) {
            try {
                clearData();
                bitmap = data.getParcelableExtra("data");
                this.imageView.setImageBitmap(bitmap);
                saveBitmapFile(bitmap);
                if (tempFile != null) {
                    boolean delete = tempFile.delete();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == Crop.REQUEST_CROP && resultCode == RESULT_OK) {
            clearData();
            Bitmap bm = ImageUtils.getBitmapFromUri(outputUri, this);
            imgPaths = outputUri.toString();
            imageView.setImageBitmap(bm);
        } else if (requestCode == UCrop.REQUEST_CROP && data != null) {
            Uri croppedFileUri = UCrop.getOutput(data);
            outputUri = croppedFileUri;
            Bitmap bm = ImageUtils.getBitmapFromUri(croppedFileUri, this);
            saveBitmapFile(bm);
            imageView.setImageBitmap(bm);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void saveBitmapFile(Bitmap bitmap) {
        try {
            //把bitmap转成文件
            File dirFile = new File(path);//文件夹路径
            if (!dirFile.exists()) {
                dirFile.mkdir();
            }

            File file = new File(path + System.currentTimeMillis() + ".png");//图片完整名称

            FileOutputStream fos = new FileOutputStream(file);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();//关闭流
            //打开图片
            imgPaths = Uri.fromFile(file).toString();
            p.setPaths(Uri.fromFile(file).toString());
            String pString = JSONObject.toJSONString(p);
            SPUtils.save(MainActivity.this, "Image", pString);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    int currindex = 1;
    private Uri photoUri;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        accountClearData();
//        ToastUtils.showl(this, "x=" + event.getX() + "  ,y=" + event.getY());
        TextView tv = new TextView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //params.setMarginStart((int) event.getX());
        //tv.setLayoutParams(params);
        tv.setX(event.getX());
        tv.setBackgroundResource(R.drawable.round_shape);
        tv.setY(event.getY());
        tv.setGravity(Gravity.CENTER);
        tv.setText(currindex + "");
        tv.setTag("A");
        absoluteLayout.addView(tv);
        return false;
    }

    private void accountClearData() {
        View view = absoluteLayout.getChildAt(absoluteLayout.getChildCount() - 1);
        if (view.getTag() != null && view.getTag().toString().equals("A")) {
            absoluteLayout.removeView(view);
        }
    }

    private PhotosData accountSetData() {
        View view = absoluteLayout.getChildAt(absoluteLayout.getChildCount() - 1);
        if (view.getTag() != null && view.getTag().toString().equals("A")) {
            view.setTag("B");
        }
        return new PhotosData(currindex, view.getX(), view.getY(), "");
    }
}
