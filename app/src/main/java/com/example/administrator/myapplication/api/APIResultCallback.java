package com.example.administrator.myapplication.api;

import android.content.Context;
import android.support.annotation.NonNull;

import com.example.administrator.myapplication.api.exception.ExceptionHandle;
import com.example.administrator.myapplication.data.model.HttpResultBase;



public abstract class APIResultCallback<T> implements IAPICallback<T> {

    private Context mCtx;
    public APIResultCallback(Context ctx){
        mCtx = ctx;
    }

    public void onNext(@NonNull T t){
        onSuccess(t);
    }

    public void onError(@NonNull HttpResultBase result){
        ExceptionHandle.handleRespResultError(result,mCtx);
        onError(result.getRespStatus(),result.getMessage());
    }

    public void onError(@NonNull Throwable e){
        HttpResultBase respEx = ExceptionHandle.handleException(e,mCtx);
        onError(respEx.getRespStatus(),respEx.getMessage());
    }
}
