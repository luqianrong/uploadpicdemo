package com.example.administrator.myapplication.data.model;

public class HttpResultBase {
    public static final int SUCCESS = 0;

    private int respStatus;
    private String message;
    private String code;



    public HttpResultBase()
    {

    }
    public HttpResultBase(String code, String msg)
    {
        this.code = code;
        message= msg;
        this.respStatus = Integer.parseInt(code);
    }

    public HttpResultBase(int code, String msg)
    {
        respStatus = code;
        this.code = "" + code;
        message= msg;
    }
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
        this.respStatus = Integer.parseInt(code);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String errorMsg) {
        this.message = errorMsg;
    }



    public boolean isSuccessful() {
        return respStatus == SUCCESS || respStatus == 2000 || respStatus == 201;
    }

    public int getRespStatus()
    {
        return respStatus;
    }


}
