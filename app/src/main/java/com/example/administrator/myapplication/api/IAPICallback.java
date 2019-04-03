package com.example.administrator.myapplication.api;


import android.support.annotation.NonNull;

/**
 * Created by lijibin on 2019/3/14.
 */

public interface IAPICallback<T> {
    void onSuccess(@NonNull T t);

    void onError(int code, @NonNull String msg);
}
