package com.example.administrator.myapplication.api;


import com.example.administrator.myapplication.data.model.HttpResult;
import com.example.administrator.myapplication.pic.ImageUploadBean;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;


public interface APIService {
    //上传图片
    @Multipart
    @POST("/app/file-upload/sysFileUpload")
    Observable<HttpResult<ImageUploadBean>> uploadServerFile(@Part MultipartBody.Part file);

    //上传图片
    @Multipart
    @POST("/app/file-upload/sysFileUpload")
    Flowable<HttpResult<ImageUploadBean>> uploadFile(@Part MultipartBody.Part file);

    //删除图片
    @FormUrlEncoded
    @POST("/app/file-upload/sysFileDelete")
    Observable<HttpResult> deleteServerFile(@Field("id") String id);



}