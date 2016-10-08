package com.meidp.pictures.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.meidp.pictures.R;
import com.meidp.pictures.bean.Photos;
import com.meidp.pictures.utils.ImageUtils;
import com.meidp.pictures.utils.NullUtils;
import com.meidp.pictures.utils.SPUtils;

import java.util.List;

/**
 * Package： com.meidp.pictures.adapter
 * Author： wxianing
 * 作  用：
 * 时  间： 2016/10/7
 */
public class ImageAdapter extends BaseAdapter {

    private List<String> mDatas;
    private Context mContext;
    private LayoutInflater mInflater;

    public ImageAdapter(List<String> mDatas, Context mContext) {
        this.mDatas = mDatas;
        this.mContext = mContext;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_photo_layout, parent, false);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        String key = mDatas.get(position);
//        new MyAsyncTask(vh.img).execute(key);
        display(key, vh.img);

        return convertView;
    }

    public static class ViewHolder {
        public ImageView img;

        public ViewHolder(View view) {
            img = (ImageView) view.findViewById(R.id.img);
        }
    }

    class MyAsyncTask extends AsyncTask<String, String, Bitmap> {

        private ImageView img;

        public MyAsyncTask(ImageView img) {
            this.img = img;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = null;
            if (NullUtils.isNotNull(params[0])) {
                String result = (String) SPUtils.get(mContext, params[0], "");
                if (NullUtils.isNotNull(result)) {
                    try {
                        Photos photo = JSONObject.parseObject(result, new TypeReference<Photos>() {
                        });
                        if (photo != null) {
                            String url = photo.getPaths();
                            bitmap = ImageUtils.getThumbnail(mContext, Uri.parse(url));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap != null) {
                img.setImageBitmap(bitmap);
                ImageAdapter.this.notifyDataSetChanged();
            }
        }
    }

    public void display(String key, ImageView img) {
        if (NullUtils.isNotNull(key)) {
            String result = (String) SPUtils.get(mContext, key, "");
            if (NullUtils.isNotNull(result)) {
                try {
                    Photos photo = JSONObject.parseObject(result, new TypeReference<Photos>() {
                    });
                    if (photo != null) {
                        String url = photo.getPaths();
                        Bitmap bitmap = ImageUtils.getThumbnail(mContext, Uri.parse(url));
                        img.setImageBitmap(bitmap);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
