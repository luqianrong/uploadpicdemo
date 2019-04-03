package com.example.administrator.myapplication.app;


import com.example.administrator.myapplication.api.APIManager;
import com.example.administrator.myapplication.utils.SharedPrefsUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ShareKeyData {
    private static  ShareKeyData  instance = null;
    private String token;
    private String refreshToken;
    private String uid;
    private String token_type;
    private String serverip;
    private String picip;

    private String materialDataVersion;


    private ShareKeyData()
    {
        init();
    }

    private void init()
    {
        token =SharedPrefsUtils.getString(Constants.PREF_KEY_TOKEN);
        refreshToken = SharedPrefsUtils.getString(Constants.PREF_KEY_REFESH_TOKEN);
        uid = SharedPrefsUtils.getString(Constants.PREF_KEY_UID);
        token_type = SharedPrefsUtils.getString(Constants.PREF_KEY_TOKEN_TYPE);

    }

    public static ShareKeyData getInstance()
    {
        if(instance == null)
            instance = new ShareKeyData();
        return instance;
    }

    public void clearUserData()
    {
        SharedPrefsUtils.putString(Constants.PREF_KEY_TOKEN,"");
        SharedPrefsUtils.putString(Constants.PREF_KEY_REFESH_TOKEN,"");
        SharedPrefsUtils.putString(Constants.PREF_KEY_UID,"");
        SharedPrefsUtils.putString(Constants.PREF_KEY_TOKEN_TYPE,"");
        SharedPrefsUtils.putString(Constants.PREF_KEY_SERVER_IP,"");
        SharedPrefsUtils.putString(Constants.PREF_KEY_PIC_IP,"");
        token ="";
        refreshToken ="";
        uid = "";
        token_type = "";
        serverip="";
        picip="";
    }

    public void setServerIp(String serverip){
        this.serverip=serverip;
        SharedPrefsUtils.putString(Constants.PREF_KEY_SERVER_IP,serverip);
    }

    public String getServerip(){
        return serverip;
    }

    public void setPicip(String picip){
        this.picip=picip;
        SharedPrefsUtils.putString(Constants.PREF_KEY_PIC_IP,picip);
    }

    public String getPicip(){
        return picip;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
        SharedPrefsUtils.putString(Constants.PREF_KEY_TOKEN,token);
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;

        SharedPrefsUtils.putString(Constants.PREF_KEY_REFESH_TOKEN,refreshToken);

    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
        SharedPrefsUtils.putString(Constants.PREF_KEY_UID,uid);
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
        SharedPrefsUtils.putString(Constants.PREF_KEY_TOKEN_TYPE,token_type);
    }

    public String getAuthorization()
    {
        if(token_type == null )
            init();
        return token_type + " " + token;
    }




}
