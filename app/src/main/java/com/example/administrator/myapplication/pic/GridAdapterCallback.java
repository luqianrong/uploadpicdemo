package com.example.administrator.myapplication.pic;

import android.view.View;

/**
 * Created by 乃军 on 2018/1/8.
 */

public interface GridAdapterCallback {
    void onItemDelete(int position);
    void onItemZoom(View v, String url);
    void camera();

}
