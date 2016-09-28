package com.meidp.pictures;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.meidp.pictures.bean.PhotoKey;
import com.meidp.pictures.bean.Photos;
import com.meidp.pictures.utils.ImageUtils;
import com.meidp.pictures.utils.NullUtils;
import com.meidp.pictures.utils.SPUtils;

import java.util.List;

/**
 * Package： com.meidp.pictures
 * Author： wxianing
 * 作  用：
 * 时  间： 2016/9/28
 */
public class PhotoAdapter extends BaseAdapter {
    private List<String> keyLists;
    private Context mContext;
    private LayoutInflater mInflater;

    public PhotoAdapter(List<String> keyLists, Context mContext) {
        this.keyLists = keyLists;
        this.mContext = mContext;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return keyLists.size();
    }

    @Override
    public Object getItem(int position) {
        return keyLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder vh = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_photo_layout, parent, false);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        initImage(keyLists.get(position), vh);

//        new MyAsyncTask(keyLists.get(position), vh.img).execute();

        final ViewHolder finalVh = vh;
        vh.delete_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyLists.remove(position);
                PhotoKey photoKey = new PhotoKey();
                photoKey.setKeys(keyLists);
                String photoKeyString = JSONObject.toJSONString(photoKey);
                SPUtils.save(mContext, "PhotoKey", photoKeyString);
                finalVh.delete_img.setVisibility(View.GONE);
                notifyDataSetChanged();
            }
        });

        return convertView;
    }

    public static class ViewHolder {
        private ImageView img;
        public ImageView delete_img;

        public ViewHolder(View view) {
            this.img = (ImageView) view.findViewById(R.id.img);
            delete_img = (ImageView) view.findViewById(R.id.delete_img);
        }
    }

    private void initImage(final String imageString, ViewHolder vh) {
        String result = (String) SPUtils.get(mContext, imageString, "");

        Log.e("key", "key：" + imageString);
        Log.e("result", "数据集合：" + result);

        if (NullUtils.isNotNull(result)) {
            try {
                Photos photo = JSONObject.parseObject(result, new TypeReference<Photos>() {
                });
                if (photo != null) {
                    String url = photo.getPaths();
                    Bitmap bitmap = ImageUtils.getBitmapFromUri(Uri.parse(url), mContext);
                    vh.img.setImageBitmap(bitmap);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void loadImage(String keys, ImageView img) {
        String result = (String) SPUtils.get(mContext, keys, "");

        Log.e("key", "key：" + keys);
        Log.e("result", "数据集合：" + result);

        if (NullUtils.isNotNull(result)) {
            try {
                Photos photo = JSONObject.parseObject(result, new TypeReference<Photos>() {
                });
                if (photo != null) {
                    String url = photo.getPaths();
                    Bitmap bitmap = ImageUtils.getBitmapFromUri(Uri.parse(url), mContext);
                    img.setImageBitmap(bitmap);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class MyAsyncTask extends AsyncTask<String, Void, Bitmap> {
        private String key;
        private ImageView img;

        public MyAsyncTask(String key, ImageView img) {
            this.key = key;
            this.img = img;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            loadImage(key, img);
            return null;
        }
    }
}
