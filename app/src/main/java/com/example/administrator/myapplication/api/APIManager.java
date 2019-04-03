package com.example.administrator.myapplication.api;


import android.support.annotation.NonNull;
import android.util.Log;


import com.example.administrator.myapplication.app.Constants;
import com.example.administrator.myapplication.app.PicApplication;
import com.example.administrator.myapplication.app.ShareKeyData;
import com.example.administrator.myapplication.data.model.HttpResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public class APIManager {
    private static long CONNECT_TIMEOUT = 60L;
    private static long READ_TIMEOUT = 10L;
    private static long WRITE_TIMEOUT = 10L;
    //设缓存有效期为10分钟
    //private static final long CACHE_STALE_SEC = 60 * 10;
    //查询缓存的Cache-Control设置，为if-only-cache时只查询缓存而不会请求服务器，max-stale可以配合设置缓存失效时间
    //public static final String CACHE_CONTROL_CACHE = "only-if-cached, max-stale=" + CACHE_STALE_SEC;
    //查询网络的Cache-Control设置
    //(假如请求了服务器并在a时刻返回响应结果，则在max-age规定的秒数内，浏览器将不会发送对应的请求到服务器，数据由缓存直接返回)
    public static final String CACHE_CONTROL_NETWORK = "Cache-Control: public, max-age=10";
    // 避免出现 HTTP 403 Forbidden，参考：http://stackoverflow.com/questions/13670692/403-forbidden-with-java-but-not-web-browser
    private static final String AVOID_HTTP403_FORBIDDEN = "User-Agent: Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11";
    private static volatile OkHttpClient mOkHttpClient;
    private static volatile APIService mAPIService;

    /**
     * 云端响应头拦截器，用来配置缓存策略
     * Dangerous interceptor that rewrites the server's cache-control header.
     */
    /*
    private static final Interceptor mRewriteCacheControlInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            if (!NetworkUtils.isConnected()) {
                request = request.newBuilder()
                        .cacheControl(CacheControl.FORCE_CACHE)
                        .build();
            }
            Response originalResponse = chain.proceed(request);
            if (NetworkUtils.isConnected()) {
                //有网的时候读接口上的@Headers里的配置，可以在这里进行统一的设置
                String cacheControl = request.cacheControl().toString();
                return originalResponse.newBuilder()
                        .header("Cache-Control", cacheControl)
                        .removeHeader("Pragma")
                        .build();
            } else {
                return originalResponse.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + CACHE_CONTROL_CACHE)
                        .removeHeader("Pragma")
                        .build();
            }
        }
    };
    */


    /**
     * 请求添加额外tokenheader
     */
    private static final Interceptor mAuthInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            String auth= ShareKeyData.getInstance().getAuthorization();
            Request request = chain.request()
                    .newBuilder()
                    .addHeader("Authorization", auth)
                    .build();
            Response response = chain.proceed(request);
            Log.e("HTTP_LOG","authorization:"+ auth);
            return response;
        }
    };

    private static final   HttpLoggingInterceptor.Logger mLoger = new HttpLoggingInterceptor.Logger(){
        @Override
        public void log(String message) {
            Log.d("HTTP_LOG", message);
        }
    };

    private static final  HttpLoggingInterceptor  mLogInterceptor = new HttpLoggingInterceptor(mLoger);

    /**
     * 获取OkHttpClient实例
     *
     * @return
     */
    private static OkHttpClient getOkHttpClient() {
        if (mOkHttpClient == null) {
            synchronized (APIManager.class) {
                Cache cache = new Cache(new File(PicApplication.getAppContext().getCacheDir(), "HttpCache"), 1024 * 1024 * 100);
                if (mOkHttpClient == null) {

                    mLogInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

                    mOkHttpClient = new OkHttpClient.Builder().cache(cache)
                            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                            //.addInterceptor(mRewriteCacheControlInterceptor)
                            .addInterceptor(mLogInterceptor)
                            .addInterceptor(mAuthInterceptor)
                            //.cookieJar(new CookiesManager())
                            .build();
                }
            }
        }
        return mOkHttpClient;
    }

    private static OkHttpClient getOkHttpClientLogin() {

        mLogInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                            //.addInterceptor(mRewriteCacheControlInterceptor)
                            .addInterceptor(mLogInterceptor)
                            .build();

        return okHttpClient;
    }


    public static Gson buildGson() {

        Gson gson = new GsonBuilder().serializeNulls().registerTypeAdapterFactory(new NullStringToEmptyAdapterFactory()).create();
        return gson;
    }

    /**
     * 获取Service
     *
     */
    public static APIService  getAPIService() {
        if (mAPIService == null) {
            synchronized (APIManager.class) {
                Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.NET_API_BASE_URL)
                        .client(getOkHttpClient())
                        .addConverterFactory(GsonConverterFactory.create(buildGson()))
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .build();
                mAPIService = retrofit.create(APIService.class);
            }
        }
        return mAPIService;
    }


    //删除图片
    public static void deleteServerFile(@NonNull String id, @NonNull final APIResultCallback<String> callback){

        APIManager.getAPIService()
                .deleteServerFile(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HttpResult>() {

                               @Override
                               public void onSubscribe(Disposable d) {

                               }

                               @Override
                               public void onNext(HttpResult result) {
                                   if(result.isSuccessful())
                                       callback.onNext("");
                                   else
                                       callback.onError(result);
                               }




                               @Override
                               public void onError(Throwable e) {
                                   callback.onError(e);
                               }

                               @Override
                               public void onComplete() {

                               }
                           }
                );
    }





}
