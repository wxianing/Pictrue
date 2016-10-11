package com.meidp.pictures;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.DisplayMetrics;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.meidp.pictures.activity.BaseActivity;
import com.meidp.pictures.adapter.ImageAdapter;
import com.meidp.pictures.bean.NotePhoto;
import com.meidp.pictures.bean.PhotoKey;
import com.meidp.pictures.bean.Photos;
import com.meidp.pictures.bean.PhotosData;
import com.meidp.pictures.utils.FileUtils;
import com.meidp.pictures.utils.ImageUtils;
import com.meidp.pictures.utils.NullUtils;
import com.meidp.pictures.utils.PermissionUtils;
import com.meidp.pictures.utils.SDCardUtils;
import com.meidp.pictures.utils.SPUtils;
import com.meidp.pictures.utils.ToastUtils;
import com.xinlan.imageeditlibrary.editimage.EditImageActivity;
import com.xinlan.imageeditlibrary.editimage.utils.BitmapUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener, View.OnTouchListener {

    private String[] PERMISSIONS_CONTACT = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS};

    private int REQUEST_CONTACTS = 100;

    private static final int PHOTO_REQUEST_CAMERA = 1;// 拍照
    public static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    public static final int ACTION_REQUEST_EDITIMAGE = 3;//裁剪
    private final static int PHOTO_REQUEST_IMGNOTE = 4;//添加批注
    public static final int ACTION_REQUEST_NOTE_EDITIMAGE = 5;//添加图片批注
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
    private Photos photo;
    private Uri outputUri;
    private ImageView previous_img;
    private ImageView next_img;
    private Button save_btn;
    private String name = "";

    private Button open;
    private ImageView add_img_note;
    private NotePhoto notePhoto;
    private int imageWidth, imageHeight;

    private List<String> keyLists = new ArrayList<>();//保存图片的数据集合
    private List<PhotosData> photosDataLists = new ArrayList<>();
    private String imgPaths = "";
    private ImageAdapter mAdapter;

    private List<Bitmap> bitmapLists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

//        if (Build.VERSION.SDK_INT >= 23) {
            dynamicPermission();
//        } else {
//            initView();
//        }
        initEvent();
    }

    public void dynamicPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            requestContactsPermissions();
        } else {
            initView();
        }
    }

    private void requestContactsPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                || ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_PHONE_STATE)
                ) {
        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS_CONTACT, REQUEST_CONTACTS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CONTACTS) {
            if (PermissionUtils.verifyPermissions(grantResults)) {
                initView();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void initView() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        imageWidth = metrics.widthPixels;
        imageHeight = metrics.heightPixels;

        bitmapLists = new ArrayList<>();

//        SPUtils.clear(this);

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
        open = (Button) findViewById(R.id.open);


        photo = new Photos();
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

    private void initImage(final String imageString) {
        String result = (String) SPUtils.get(this, imageString, "");
        Log.e("result", "数据集合：" + result);
        if (NullUtils.isNotNull(result)) {
            try {
                photo = JSONObject.parseObject(result, new TypeReference<Photos>() {
                });
                if (photo != null) {
                    clearData();
                    String url = photo.getPaths();
                    imgPaths = url;
                    outputUri = Uri.parse(url);
                    Log.e("imgPaths", imgPaths);
                    Bitmap bitmap = ImageUtils.getBitmapFromUri(Uri.parse(url), MainActivity.this);
                    imageView.setImageBitmap(bitmap);
                    photosDataLists.addAll(photo.getPhotosDatas());
                    if (photosDataLists != null && photosDataLists.size() > 0) {
                        for (int i = 0; i < photosDataLists.size(); i++) {
                            int index = i + 1;
                            final PhotosData photosData = photosDataLists.get(i);
                            TextView tv = new TextView(this);
                            tv.setX(photosData.getXcoordinate());
                            tv.setY(photosData.getYcoordinate());
                            tv.setBackgroundResource(R.drawable.round_shape);
                            tv.setGravity(Gravity.CENTER);
                            tv.setText(index + "");
                            tv.setTag("B");
                            absoluteLayout.addView(tv);
                            TextView notetv = new TextView(MainActivity.this);
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 50);
                            params.rightMargin = 5;
                            params.gravity = Gravity.CENTER;
                            notetv.setLayoutParams(params);
                            notetv.setGravity(Gravity.CENTER);
                            notetv.setTextSize(12);
                            notetv.setText("批注" + index);
                            notetv.setBackgroundResource(R.drawable.orange_btn_shape);
                            notetv.setTextColor(Color.rgb(235, 147, 73));
                            notetv.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
//                                    ToastUtils.showl(MainActivity.this, photosData.getContent());
                                    showNoteMsgDialog(photosData.getContent(), photosData);
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
        open.setOnClickListener(this);
    }

    int index = 0;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.select_btn:
                showDialog();
                break;
            case R.id.crop_btn:
//                Bitmap b = imageCrop(bmp);
//                imageView.setImageBitmap(b);
//                Crop.of(uri, outputUri).asSquare().start(this);
//                startCrop(outputUri);
                Uri uri = Uri.parse(imgPaths);
                Intent intent = new Intent(MainActivity.this, EditImageActivity.class);
                intent.putExtra(EditImageActivity.FILE_PATH, uri.getPath());
                File outputFile = FileUtils.getEmptyFile("picture"
                        + System.currentTimeMillis() + ".png");
                intent.putExtra(EditImageActivity.EXTRA_OUTPUT,
                        outputFile.getAbsolutePath());
                MainActivity.this.startActivityForResult(intent,
                        ACTION_REQUEST_EDITIMAGE);
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
                if (keyLists != null && keyLists.size() > 0) {
                    for (int i = 0; i < keyLists.size(); i++) {
                        if (keyLists.get(i).equals(name)) {
                            keyLists.remove(i);
                        }
                    }
                }
                keyLists.add(name);
                PhotoKey photoKey = new PhotoKey();
                photoKey.setKeys(keyLists);
                String photoKeyString = JSONObject.toJSONString(photoKey);
                SPUtils.save(this, "PhotoKey", photoKeyString);
                String photoString = JSONObject.toJSONString(photo);
                SPUtils.save(MainActivity.this, name, photoString);
                Log.e("PhotoString", photoString);
                ToastUtils.shows(this, "保存成功");
                photo.setPhotosDatas(null);

                break;
            case R.id.open:
                showOpenDialog();
                break;
        }
    }

    class MyAsyncTask extends AsyncTask<String, String, Bitmap> {

        private Bitmap bitmap;

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = null;
            if (NullUtils.isNotNull(params[0])) {
                String result = (String) SPUtils.get(MainActivity.this, params[0], "");
                if (NullUtils.isNotNull(result)) {
                    try {
                        Photos photo = JSONObject.parseObject(result, new TypeReference<Photos>() {
                        });
                        if (photo != null) {
                            String url = photo.getPaths();
                            Uri uri = Uri.parse(url);
                            bitmap = BitmapUtils.getSampledBitmap(uri.getPath(), imageWidth / 4, imageHeight / 4);
                            return bitmap;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
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
            bitmapLists.add(bitmap);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void showaddnoteDialog() {

        final Dialog dialog = new Dialog(this, R.style.Dialog);
        View contentView = LayoutInflater.from(this).inflate(R.layout.edittext_dialog, null);
        TextView titleName = (TextView) contentView.findViewById(R.id.title);
        final EditText editText = (EditText) contentView.findViewById(R.id.message);
        titleName.setText("批注");
        dialog.setContentView(contentView);
        dialog.setCanceledOnTouchOutside(true);
        add_img_note = (ImageView) contentView.findViewById(R.id.add_img_note);

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
                final PhotosData data = accountSetData();
                if (notePhoto != null) {
                    data.setPhoto(notePhoto);
                }
                data.setContent(content);
                photosDataLists.add(data);
                photo.setPaths(imgPaths);
                int dot = imgPaths.lastIndexOf("/");
                name = imgPaths.substring(dot + 1);
                photo.setImageName(name);
                photo.setPhotosDatas(photosDataLists);

                RelativeLayout.LayoutParams ll = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                ll.setMargins(10, 10, 10, 10);
                TextView tv = new TextView(MainActivity.this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 50);
                params.rightMargin = 5;
                params.leftMargin = 5;
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
//                        ToastUtils.showl(MainActivity.this, content);
                        showNoteMsgDialog(content, data);
                    }
                });

                notePhoto = null;
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

    private void showOpenDialog() {
        final Dialog dialog = new Dialog(MainActivity.this, R.style.Dialog);
        View contentView = LayoutInflater.from(MainActivity.this).inflate(R.layout.photo_dialog_layout, null);
        dialog.setContentView(contentView);
        dialog.setCanceledOnTouchOutside(true);
        GridView gridview = (GridView) contentView.findViewById(R.id.gridview);

        Log.e("keyLists", ">>>>>>>>>" + keyLists.size());

//        if (keyLists.size() > bitmapLists.size()) {

        for (int i = 0; i < keyLists.size(); i++) {
            String key = keyLists.get(i);
            Log.e("keyLists:", key);
            new MyAsyncTask().execute(key);
        }
//        }

        mAdapter = new ImageAdapter(bitmapLists, MainActivity.this, new ImageAdapter.ClickCallBack() {
            @Override
            public void click(int position) {
                bitmapLists.remove(position);
                keyLists.remove(position);
                mAdapter.notifyDataSetChanged();
            }
        });

        gridview.setAdapter(mAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String key = keyLists.get(position);
                initImage(key);
                dialog.dismiss();
            }
        });
        bitmapLists.clear();
        gridview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ImageAdapter.ViewHolder vh = (ImageAdapter.ViewHolder) view.getTag();
                vh.delete_img.setVisibility(View.VISIBLE);
                return true;
            }
        });

        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();

        WindowManager wm = getWindowManager();
        Display d = wm.getDefaultDisplay(); // 获取屏幕宽、高用
        lp.height = (int) (d.getHeight() * 0.8); // 高度设置为屏幕的0.6
        lp.width = (int) (d.getWidth() * 0.7); // 宽度设置为屏幕的0.35

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

    private void showNoteMsgDialog(String msg, PhotosData data) {
        final Dialog dialog = new Dialog(MainActivity.this, R.style.Dialog);
        View contentView = LayoutInflater.from(MainActivity.this).inflate(R.layout.show_msg_dialog_layout, null);
        dialog.setContentView(contentView);
        dialog.setCanceledOnTouchOutside(true);

        TextView content = (TextView) contentView.findViewById(R.id.note_content);
        content.setText(msg);
        ImageView noteImg = (ImageView) contentView.findViewById(R.id.note_img);
        if (data.getPhoto() != null) {
            String url = data.getPhoto().getPaths();
            Uri uri = Uri.parse(url);
            Bitmap bm = ImageUtils.getBitmapFromUri(uri, this);
            noteImg.setImageBitmap(bm);
        }

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
                photo.setPhotosDatas(photosDataLists);
                String photoString = JSONObject.toJSONString(photo);
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
    }

    /**
     * 从相机获取
     */
    public void camera() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        // 判断存储卡是否可以用，可用进行存储
        if (SDCardUtils.isSDCardEnable()) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(new File(Environment
                            .getExternalStorageDirectory(), PHOTO_FILE_NAME)));
        }
        startActivityForResult(intent, PHOTO_REQUEST_CAMERA);
    }

    private void clearData() {
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
                outputUri = uri;
                Log.e("uriPath", uri.getPath());
                imgPaths = uri.toString();
                Log.e("ImgPaths", "onActivityResult: " + imgPaths);
                Bitmap bm = ImageUtils.getBitmapFromUri(uri, this);
                this.imageView.setImageBitmap(bm);
                saveBitmapFile(bm);
                photo.setPaths(imgPaths);
                String pString = JSONObject.toJSONString(photo);
                SPUtils.save(MainActivity.this, "Image", pString);
            }
        } else if (requestCode == PHOTO_REQUEST_IMGNOTE) {//批注添加图片
            if (data != null) {
                Bitmap bm = null;
                try {
                    Uri uri = data.getData();

                    bm = ImageUtils.getBitmapFromUri(uri, this);
                    notePhoto = new NotePhoto();

                    //把bitmap转成文件
                    File dirFile = new File(path);//文件夹路径
                    if (!dirFile.exists()) {
                        dirFile.mkdir();
                    }
                    File file = new File(path + System.currentTimeMillis() + ".png");//图片完整名称
                    FileOutputStream fos = new FileOutputStream(file);
                    BufferedOutputStream bos = new BufferedOutputStream(fos);
                    bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                    bos.flush();
                    bos.close();//关闭流

                    Intent intent = new Intent(MainActivity.this, EditImageActivity.class);
                    intent.putExtra(EditImageActivity.FILE_PATH, Uri.fromFile(file).getPath());
                    File outputFile = FileUtils.getEmptyFile("picture"
                            + System.currentTimeMillis() + ".png");
                    intent.putExtra(EditImageActivity.EXTRA_OUTPUT,
                            outputFile.getAbsolutePath());
                    MainActivity.this.startActivityForResult(intent,
                            ACTION_REQUEST_NOTE_EDITIMAGE);

                    String imgPaths = Uri.fromFile(outputFile).toString();
                    int dot = imgPaths.lastIndexOf("/");
                    String imageName = imgPaths.substring(dot + 1);
                    notePhoto.setPaths(imgPaths);
                    notePhoto.setImageName(imageName);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == PHOTO_REQUEST_CAMERA) {// 拍照
            if (SDCardUtils.isSDCardEnable()) {
                clearData();
                tempFile = new File(Environment.getExternalStorageDirectory(), PHOTO_FILE_NAME);
                Bitmap bm = ImageUtils.getBitmapFromUri(Uri.fromFile(tempFile), this);
                outputUri = Uri.fromFile(tempFile);
                saveBitmapFile(bm);
                this.imageView.setImageBitmap(bm);
            } else {
                ToastUtils.shows(this, "未找到存储卡，无法存储照片！");
            }
        } else if (requestCode == ACTION_REQUEST_EDITIMAGE && data != null) {//裁剪结果
            clearData();//清除之前的批注
            handleEditorImage(data, this.imageView);
        } else if (requestCode == ACTION_REQUEST_NOTE_EDITIMAGE && data != null) {
            handleNoteCropImage(data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void handleNoteCropImage(Intent data) {
        String newFilePath = data.getStringExtra("save_file_path");
        new NoteCropLoadImageTask(this.add_img_note).execute(newFilePath);
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
            String namestr = Uri.fromFile(file).toString();
            int dot = namestr.lastIndexOf("/");
            name = namestr.substring(dot + 1);
            imgPaths = Uri.fromFile(file).toString();
            photo.setPaths(Uri.fromFile(file).toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    int currindex = 1;

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

    private void handleEditorImage(Intent data, ImageView imageView) {
        String newFilePath = data.getStringExtra("save_file_path");
        outputUri = Uri.parse(newFilePath);

        new LoadImageTask(imageView).execute(newFilePath);
    }

    private final class LoadImageTask extends AsyncTask<String, Void, Bitmap> {

        private ImageView img;

        public LoadImageTask(ImageView imageView) {
            this.img = imageView;
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
            saveBitmapFile(bitmap);
        }
    }

    private final class NoteCropLoadImageTask extends AsyncTask<String, Void, Bitmap> {

        private ImageView img;

        public NoteCropLoadImageTask(ImageView imageView) {
            this.img = imageView;
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
}
