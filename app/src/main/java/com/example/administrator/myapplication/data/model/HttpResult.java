package com.example.administrator.myapplication.data.model;



public class HttpResult<T> extends HttpResultBase{

    private T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public HttpResult()
    {

    }

    public HttpResult(HttpResultBase base)
    {
        super(base.getCode(),base.getMessage());
    }
}
