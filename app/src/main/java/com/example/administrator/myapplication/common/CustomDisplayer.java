package com.example.administrator.myapplication.common;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.lwkandroid.imagepicker.utils.IImagePickerDisplayer;

/**
 * Created by lijibin on 2019/3/14.
 */

public class CustomDisplayer implements IImagePickerDisplayer {
    @Override
    public void display(Context context, String url, ImageView imageView, int maxWidth, int maxHeight) {
        Glide.with(context).load(url).into(imageView);
    }

    @Override
    public void display(Context context, String url, ImageView imageView, int placeHolder, int errorHolder, int maxWidth, int maxHeight) {
        Glide.with(context).load(url).into(imageView);
    }
}