package com.meidp.pictures;

import android.app.Activity;
import android.graphics.drawable.Drawable;

import cn.finalteam.galleryfinal.widget.GFImageView;

/**
 * Package com.meidp.pictures
 * 作  用:
 * Author: wxianing
 * 时  间: 16/9/16
 */
public class GlideImageLoader  {

//    @Override
//    public void displayImage(Activity activity, String path, final GFImageView imageView, Drawable defaultDrawable, int width, int height) {
//        Glide.with(activity)
//                .load("file://" + path)
//                .placeholder(defaultDrawable)
//                .error(defaultDrawable)
//                .override(width, height)
//                .diskCacheStrategy(DiskCacheStrategy.NONE) //不缓存到SD卡
//                .skipMemoryCache(true)
//                //.centerCrop()
//                .into(new ImageViewTarget<GlideDrawable>(imageView) {
//                    @Override
//                    protected void setResource(GlideDrawable resource) {
//                        imageView.setImageDrawable(resource);
//                    }
//
//                    @Override
//                    public void setRequest(Request request) {
//                        imageView.setTag(R.id.adapter_item_tag_key,request);
//                    }
//
//                    @Override
//                    public Request getRequest() {
//                        return (Request) imageView.getTag(R.id.adapter_item_tag_key);
//                    }
//                });
//    }

//    @Override
//    public void clearMemoryCache() {
//    }
}
