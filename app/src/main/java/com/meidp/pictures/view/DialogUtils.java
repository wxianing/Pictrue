package com.meidp.pictures.view;

import android.app.Activity;
import android.app.Dialog;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.meidp.pictures.R;

/**
 * Package： com.meidp.pictures.view
 * Author： wxianing
 * 作  用：
 * 时  间： 2016/10/11
 */
public class DialogUtils {

    public static Dialog dialog = null;

    public static void showSaveImage(Activity activity) {

        if (dialog == null) {
            dialog = new Dialog(activity, R.style.Dialog);
            View contentView = LayoutInflater.from(activity).inflate(R.layout.dialog_textview_layout, null);
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
                    dialog.dismiss();
                }
            });
            Window dialogWindow = dialog.getWindow();
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();

            WindowManager wm = activity.getWindowManager();
            Display d = wm.getDefaultDisplay(); // 获取屏幕宽、高用
//        p.height = (int) (d.getHeight() * 0.6); // 高度设置为屏幕的0.6
            lp.width = (int) (d.getWidth() * 0.4); // 宽度设置为屏幕的0.65

            dialogWindow.setAttributes(lp);
            dialog.show();
        }
    }

    public static void dismissDialog() {
        if (dialog != null) {
            if (dialog.isShowing()) {
                dialog.dismiss();
                dialog.cancel();
            }
            dialog = null;
        }
    }


//    private void showDeleteDialog(final int index, final String keys) {
//        final Dialog dialog = new Dialog(MainActivity.this, R.style.Dialog);
//        View contentView = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_textview_layout, null);
//        TextView titleName = (TextView) contentView.findViewById(R.id.title);
//        TextView content = (TextView) contentView.findViewById(R.id.hint_content);
//        titleName.setText("温馨提示");//标题
//        content.setText("是否删除此批注？");//提示你内容
//
//        dialog.setContentView(contentView);
//        dialog.setCanceledOnTouchOutside(true);
//        Button negativeButton = (Button) contentView.findViewById(R.id.negativeButton);
//        negativeButton.setClickable(true);
//        negativeButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//        Button positiveButton = (Button) contentView.findViewById(R.id.positiveButton);
//        positiveButton.setClickable(true);
//        positiveButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                photosDataLists.remove(index);
//                photo.setPhotosDatas(photosDataLists);
//                String photoString = JSONObject.toJSONString(photo);
//                Log.e("photoString", photoString);
//                SPUtils.save(MainActivity.this, keys, photoString);
//                clearData();
//                initImage(keys);
//
//                dialog.dismiss();
//            }
//        });
//        Window dialogWindow = dialog.getWindow();
//        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
//
//        WindowManager wm = getWindowManager();
//        Display d = wm.getDefaultDisplay(); // 获取屏幕宽、高用
////        p.height = (int) (d.getHeight() * 0.6); // 高度设置为屏幕的0.6
//        lp.width = (int) (d.getWidth() * 0.4); // 宽度设置为屏幕的0.65
//
//        dialogWindow.setAttributes(lp);
//        dialog.show();
//    }
}
