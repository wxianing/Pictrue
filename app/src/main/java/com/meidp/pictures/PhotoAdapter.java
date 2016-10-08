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
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

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

    private int index = 1;

    public PhotoAdapter(List<String> keyLists, Context mContext) {
        this.keyLists = keyLists;
        this.mContext = mContext;
        mInflater = LayoutInflater.from(mContext);
        Log.e("keyLists + mContext", ">>>>>>>>>>" + keyLists.size());
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_photo_layout, parent, false);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        String key = keyLists.get(position);

        Log.e("keys", key);

//        if (NullUtils.isNotNull(key)) {
//
//            String result = (String) SPUtils.get(mContext, key, "");
//            Log.e("index", ">>>>>>>>>" + index + ">>>:" + key);
//            Log.e("key", "key：" + keyLists.get(finalPostion));
//            Log.e("result", "数据集合：" + result);
//
//            if (NullUtils.isNotNull(result)) {
//                try {
//                    Photos photo = JSONObject.parseObject(result, new TypeReference<Photos>() {
//                    });
//                    if (photo != null) {
//                        String url = photo.getPaths();
//                        Bitmap bitmap = ImageUtils.getBitmapFromUri(Uri.parse(url), mContext);
//                        vh.img.setImageBitmap(bitmap);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        index++;

//        vh.delete_img.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                keyLists.remove(position);
//                PhotoKey photoKey = new PhotoKey();
//                photoKey.setKeys(keyLists);
//                String photoKeyString = JSONObject.toJSONString(photoKey);
//                SPUtils.save(mContext, "PhotoKey", photoKeyString);
//                finalVh.delete_img.setVisibility(View.GONE);
//                notifyDataSetChanged();
//            }
//        });

        return convertView;
    }

    public class ViewHolder {
        public ImageView delete_img;
        public ImageView img;

        public ViewHolder(View view) {
            delete_img = (ImageView) view.findViewById(R.id.delete_img);
            img = (ImageView) view.findViewById(R.id.img);
        }
    }

//    private void initImage(final String imageString, ImageView img) {
//
//        String result = (String) SPUtils.get(mContext, imageString, "");
//        Log.e("index", ">>>>>>>>>" + index);
//        Log.e("key", "key：" + imageString);
//        Log.e("result", "数据集合：" + result);
//
//        if (NullUtils.isNotNull(result)) {
//            try {
//                Photos photo = JSONObject.parseObject(result, new TypeReference<Photos>() {
//                });
//                if (photo != null) {
//                    String url = photo.getPaths();
//                    Bitmap bitmap = ImageUtils.getBitmapFromUri(Uri.parse(url), mContext);
//                    img.setImageBitmap(bitmap);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        index++;
//    }
}
