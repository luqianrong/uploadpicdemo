package com.example.administrator.myapplication.data.model;

import java.util.List;

public class HttpResultList<T> extends HttpResultBase{
    private List<T> data;

    public HttpResultList(HttpResultBase base)
    {
        super(base.getCode(),base.getMessage());
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "HttpResultList{" +
                "data=" + data +
                '}';
    }
}
