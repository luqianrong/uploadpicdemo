package com.example.administrator.myapplication.common;




import com.example.administrator.myapplication.api.APIManager;
import com.example.administrator.myapplication.app.PicApplication;
import com.example.administrator.myapplication.data.model.HttpResult;
import com.example.administrator.myapplication.pic.ImageUploadBean;

import org.reactivestreams.Publisher;

import java.io.File;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import top.zibin.luban.Luban;

public class UploadPhotoHelper {

    public static Flowable<HttpResult<ImageUploadBean>> requestUpload(List<String> paths) throws Exception {


        return Flowable.just(paths)
                //concatmap作用，将数据集合转换成Observables集合
                .concatMap(new Function<List<String>, Publisher<String>>() {
                    @Override
                    public Publisher<String> apply(List<String> strings) throws Exception {
                        //将这些Observale发射的数据平坦化的放进一个单独的Observale中
                        return Flowable.fromIterable(strings);
                    }
                })
                .subscribeOn(Schedulers.io())
                .map(new Function<String, File>() {
                    @Override
                    public File apply(@NonNull String file) throws Exception {

                        return Luban.with(PicApplication.getAppContext())
                                .ignoreBy(300)
                                .setTargetDir(PicApplication.getAppContext().getExternalCacheDir().getAbsolutePath())
                                //.load(file)
                                .get(file);
                    }
                })
                .concatMap(new Function<File, Publisher<HttpResult<ImageUploadBean>>>() {
                    @Override
                    public Publisher<HttpResult<ImageUploadBean>> apply(File file) throws Exception {
                        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
                        return APIManager.getAPIService()
                                .uploadFile(body);

                    }
                });


    }

    public interface onUploadPhotoListener {
        void onStart();
        void onSuccess(ImageUploadBean imageBean);
        void onComplete();
        void onError(String msg);
    }

}
