package com.meidp.pictures.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
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
import com.xinlan.imageeditlibrary.editimage.utils.BitmapUtils;

import java.util.List;

/**
 * Package： com.meidp.pictures.adapter
 * Author： wxianing
 * 作  用：
 * 时  间： 2016/10/7
 */
public class ImageAdapter extends BaseAdapter {

    private List<Bitmap> mDatas;
    private Context mContext;
    private LayoutInflater mInflater;
    private ClickCallBack mCallBack;

    public interface ClickCallBack {
        public void click(int position);
    }

    public ImageAdapter(List<Bitmap> mDatas, Context mContext, ClickCallBack mCallBack) {
        this.mDatas = mDatas;
        this.mContext = mContext;
        mInflater = LayoutInflater.from(mContext);
        mCallBack = mCallBack;
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
        Bitmap bitmap = mDatas.get(position);
        ViewHolder vh = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_photo_layout, parent, false);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        vh.img.setImageBitmap(bitmap);
        final int finalPosition = position;

        vh.delete_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mCallBack.click(finalPosition);
                notifyDataSetChanged();
            }
        });
        return convertView;
    }

    public static class ViewHolder {
        public ImageView img;
        public ImageView delete_img;

        public ViewHolder(View view) {
            img = (ImageView) view.findViewById(R.id.img);
            delete_img = (ImageView) view.findViewById(R.id.delete_img);
        }
    }
}
