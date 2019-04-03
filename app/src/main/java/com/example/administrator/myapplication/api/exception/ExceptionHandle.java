package com.example.administrator.myapplication.api.exception;

import android.content.Context;
import android.util.Log;

import com.example.administrator.myapplication.data.model.HttpResultBase;
import com.example.administrator.myapplication.utils.ToastUtils;
import com.google.gson.JsonParseException;

import java.net.ConnectException;

import retrofit2.HttpException;

//import com.cars.ict.equipmentkeeper.ui.main.LoginActivity;

public class ExceptionHandle {

    /**
     * 未传token;client的id和secret错误;访问无权限的web接口
     */
    public static final int UNAUTHORIZED_CODE = 401;

    /**
     * 服务器内部错误
     */
    public static final int INTERNAL_ERROR_CODE = 500;

    /**
     * 未知异常
     */
    public static final int UNKNOW_ERROR_CODE = 600;

    /**
     * 访问登陆接口, 未写用户名密码或者用户名密码不匹配
     */
    public static final int USERNAME_OR_PASSWORD_ERROR_CODE = 601;

    /**
     * 缺少grant_type
     */
    public static final int MISSING_GRANT_TYPE_CODE = 602;

    /**
     * token过期
     */
    public static final int TOKEN_EXPIRED_CODE = 603;

    /**
     * token解析错误
     */
    public static final int BAD_TOKEN_CODE = 604;

    /**
     * refresh_token过期
     */
    public static final int REFRESH_TOKEN_EXPIRED_CODE = 605;

    /**
     * 缺少参数
     */
    public static final int MISSING_PARAMETER_CODE = 606;

    /**
     * 参数类型不匹配
     */
    public static final int ARG_TYPE_MISMATCH_CODE = 607;

    /**
     * 404异常
     */
    public static final int CANNOT_FOUND_ERROR_CODE = 404;

    /**
     * 方法不被支持(如请求方式get, post)
     */
    public static final int METHOD_NOT_SUPPORTED_CODE = 405;


    public static HttpResultBase handleException(Throwable e, Context ctx) {
        ResponeThrowable ex;
        Log.i("ExceptionHandle", "handleException e.toString = " + e.toString());
        if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            ex = new ResponeThrowable(e, ERROR.HTTP_ERROR);
            switch (httpException.code()) {
                case UNAUTHORIZED_CODE:
                    ex.code = httpException.code();
                    ex.message = "用户认证失败，请重新登录";
                    startLoginActivity(ctx);
                    break;
                case TOKEN_EXPIRED_CODE:
                    ex.code = httpException.code();
                    ex.message = "用户认证过期，请重新登录";
                    startLoginActivity(ctx);
                    break;
                case INTERNAL_ERROR_CODE:
                    ex.code = httpException.code();
                    ex.message = "服务器内部错误";
                    break;
                case BAD_TOKEN_CODE:
                    ex.code = httpException.code();
                    ex.message = "token解析错误";
                    break;
                case MISSING_PARAMETER_CODE:
                    ex.code = httpException.code();
                    ex.message = "缺少参数";
                    break;
                case ARG_TYPE_MISMATCH_CODE:
                    ex.code = httpException.code();
                    ex.message = "参数类型不匹配";
                    break;
                default:
                    ex.code = httpException.code();
                    ex.message = "未知错误";
                    break;
            }
        }  else if (e instanceof JsonParseException
            /*|| e instanceof ParseException*/) {
            ex = new ResponeThrowable(e, ERROR.PARSE_ERROR);
            ex.message = "数据解析错误";

        } else if (e instanceof ConnectException) {
            ex = new ResponeThrowable(e, ERROR.NETWORD_ERROR);
            ex.message = "连接失败";
        } else if (e instanceof javax.net.ssl.SSLHandshakeException) {
            ex = new ResponeThrowable(e, ERROR.SSL_ERROR);
            ex.message = "证书验证失败";
        } else if(e instanceof  java.net.SocketTimeoutException)
        {
            ex = new ResponeThrowable(e, ERROR.NETWORD_ERROR);
            ex.message = "网络连接失败";
        }
        else {
            ex = new ResponeThrowable(e, ERROR.UNKNOWN);
            ex.message = "未知错误";
        }



        ToastUtils.showLong(ex.message,ctx);
        HttpResultBase result = new HttpResultBase(ex.code,ex.message);
        return result;
    }

    public static void handleRespResultError(HttpResultBase result, Context ctx) {
        Log.i("ExceptionHandle", "handleRespResultError result = " + result.toString());
        switch (result.getRespStatus()) {
            case UNAUTHORIZED_CODE:
                startLoginActivity(ctx);
                break;
            case TOKEN_EXPIRED_CODE:
                startLoginActivity(ctx);
                break;

            default:
                break;
        }
        ToastUtils.showLong(result.getMessage(),ctx);
    }

    /**
     * 约定异常
     */
    class ERROR {
        /**
         * 未知错误
         */
        public static final int UNKNOWN = 1000;
        /**
         * 解析错误
         */
        public static final int PARSE_ERROR = 1001;
        /**
         * 网络错误
         */
        public static final int NETWORD_ERROR = 1002;
        /**
         * 协议出错
         */
        public static final int HTTP_ERROR = 1003;

        /**
         * 证书出错
         */
        public static final int SSL_ERROR = 1005;
    }

    private static void startLoginActivity(Context ctx)
    {
        /*
        Intent i = new Intent(ctx,LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(i);
        */
    }

    public static class ResponeThrowable extends Exception {
        public int code;
        public String message;

        public ResponeThrowable(Throwable throwable, int code) {
            super(throwable);
            this.code = code;
        }
    }

}
