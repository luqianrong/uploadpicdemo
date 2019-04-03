package com.example.administrator.myapplication.widgets;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.administrator.myapplication.R;


public class LoadingDialog extends Dialog {
    private TextView msg, btn;
    private static  LoadingDialog  loading;

    public LoadingDialog(@NonNull Context context) {
        super(context);
        initView(context);
    }

    //初始化
    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null);
        msg = (TextView) view.findViewById(R.id.dialog_load_msg);
        btn = (TextView) view.findViewById(R.id.dialog_load_btn);

        //Android5.0以下去掉标题线(必须setContentView之前)
        requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
        setContentView(view);
        //默认点击空白处不消失
        setCanceledOnTouchOutside(false);
        //背景透明
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        show();
    }

    public static LoadingDialog showLoading(Context context)
    {
        dismissLoading();
        loading = new LoadingDialog(context);
        return loading;
    }


    public LoadingDialog getLoadingView()
    {
        return loading;
    }

    public static void dismissLoading()
    {
        if(loading == null)
            return;
        loading.dismiss();
        loading = null;
    }
    /**
     * 设置加载信息
     *
     * @param text
     * @return
     */
    public LoadingDialog setText(String text) {
        msg.setText(text);
        return this;
    }

    /**
     * 设置加载信息
     *
     * @param text 右下角按钮
     * @return
     */
    /*
    public LoadingDialog setButton(String text, final OnClickBtnListener onClickListener) {
        btn.setVisibility(View.VISIBLE);
        btn.setText(text);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onClick(LoadingDialog.this, v);
                dismiss();
            }
        });
        return this;
    }
    */


    /**
     * 设置空白处是否可取消
     *
     * @param cancel
     * @return
     */
    public LoadingDialog canceledOnTouchOutside(boolean cancel) {
        setCanceledOnTouchOutside(cancel);
        return this;
    }

    /**
     * 设置是否可取消
     *
     * @param cancel
     * @return
     */
    public LoadingDialog cancelable(boolean cancel) {
        setCancelable(cancel);
        return this;
    }

}
