package com.example.administrator.myapplication;

import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.administrator.myapplication.api.APIManager;
import com.example.administrator.myapplication.api.APIResultCallback;
import com.example.administrator.myapplication.api.exception.ExceptionHandle;
import com.example.administrator.myapplication.common.CustomDisplayer;
import com.example.administrator.myapplication.common.UploadPhotoHelper;
import com.example.administrator.myapplication.common.imagepicker.ImageDetailActivity;
import com.example.administrator.myapplication.common.imagepicker.SelectDialog;
import com.example.administrator.myapplication.data.model.HttpResult;
import com.example.administrator.myapplication.pic.GridAdapterCallback;
import com.example.administrator.myapplication.pic.GridViewAddImgesAdpter;
import com.example.administrator.myapplication.pic.ImageUploadBean;
import com.example.administrator.myapplication.widgets.LoadingDialog;
import com.lwkandroid.imagepicker.ImagePicker;
import com.lwkandroid.imagepicker.data.ImageBean;
import com.lwkandroid.imagepicker.data.ImagePickType;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.example.administrator.myapplication.common.imagepicker.ImageDetailActivity.IMAGE_URL;

public class MainActivity extends AppCompatActivity {
    private List<ImageUploadBean> medicalList = new ArrayList<>();
    private List<String> picList=new ArrayList<>();
    private GridViewAddImgesAdpter gridViewAddImgesAdpter;
    private GridView gw;
    private int currentPosition;
    private String cachePath;
    private CustomDisplayer glideImagePickerDisplayer;
    private final int REQUEST_CODE = 111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gw = (GridView) findViewById(R.id.gw);

        glideImagePickerDisplayer=new CustomDisplayer();

        cachePath = getExternalFilesDir(null) + "/mypics/photos/";

        gridViewAddImgesAdpter = new GridViewAddImgesAdpter(medicalList, this);
        gridViewAddImgesAdpter.setAdapterCallback(new GridAdapterCallback() {
            @Override
            public void onItemDelete(int position) {
                currentPosition = position;
                LoadingDialog.showLoading(MainActivity.this).setText("加载中");
                APIManager.deleteServerFile(medicalList.get(position).getId(), new APIResultCallback<String>(MainActivity.this) {
                    @Override
                    public void onSuccess(@NonNull String t) {
                        LoadingDialog.dismissLoading();
                        medicalList.remove(currentPosition);
                        picList.remove(currentPosition);

                        gridViewAddImgesAdpter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(int code, @NonNull String msg) {

                    }
                });

            }

            @Override
            public void onItemZoom(View v, String url) {
                Intent intent = new Intent(MainActivity.this, ImageDetailActivity.class);
                intent.putExtra(IMAGE_URL, url);
                gotoImageDetail(intent, v);
            }

            @Override
            public void camera() {
                caname();
            }

        });
        gw.setAdapter(gridViewAddImgesAdpter);
        gw.setSelector(new ColorDrawable(Color.TRANSPARENT));
    }

    public void caname(){
        List<String> names = new ArrayList<>();
        names.add("拍照");
        names.add("相册");
        showDialog(new SelectDialog.SelectDialogListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: // 直接调起相机
                        createPicker(0);
                        break;
                    case 1://获取图片信息
                        createPicker(1);
                        break;
                    default:
                        break;
                }
            }
        }, names);
    }


    protected void gotoImageDetail(Intent intent, View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, view, "shareNames").toBundle());
        } else {
            startActivity(intent);
        }
    }

    private SelectDialog showDialog(SelectDialog.SelectDialogListener listener, List<String> names) {
        SelectDialog dialog = new SelectDialog(this, R.style.transparentFrameWindowStyle,
                listener, names);
        if (!this.isFinishing()) {
            dialog.show();
        }
        return dialog;
    }

    //0为调用照相机 1为从手机本地选择图片
    private ImagePickType getPickType(int id)
    {
        if (id == 0){
            return ImagePickType.ONLY_CAMERA;
        }else{
            return ImagePickType.MULTI;
        }
    }

    private void createPicker(int id){
        new ImagePicker()
                .pickType(getPickType(id))//设置选取类型(拍照、单选、多选)
                .maxNum(9)//设置最大选择数量(拍照和单选都是1，修改后也无效)
                .needCamera(true)//是否需要在界面中显示相机入口(类似微信)
                .cachePath(cachePath)//自定义缓存路径
                .displayer(glideImagePickerDisplayer)//自定义图片加载器，默认是Glide实现的,可自定义图片加载器
                .start(this, REQUEST_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null)
        {
            List<ImageBean> resultList = data.getExtras().getParcelableArrayList(ImagePicker.INTENT_RESULT_DATA);
            Log.i("ImagePickerDemo", "选择的图片：" + resultList.toString());
            Log.e("ii","选择的图片："+resultList.toString());
            uploadImages(resultList);

        }
    }

    private void uploadImages(List<ImageBean> resultList)
    {
        if(resultList != null && !resultList.isEmpty()){

            List<String>  files = new ArrayList<String>(resultList.size());
            for(ImageBean image : resultList)
            {
                files.add(image.getImagePath());
            }

            final CompositeDisposable compositeDisposable = new CompositeDisposable();
            Dialog dlg =LoadingDialog.showLoading(MainActivity.this)
                    .setText("上传中")
                    .canceledOnTouchOutside(false);
            dlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    //主动解除订阅
                    compositeDisposable.dispose();
                }
            });
            dlg.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    compositeDisposable.dispose();
                }
            });

            try {

                compositeDisposable.add(
                        UploadPhotoHelper.requestUpload(files)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Consumer<HttpResult<ImageUploadBean>>() {
                                               @Override
                                               public void accept(HttpResult<ImageUploadBean> imageBean) throws Exception {
                                                   if(imageBean.isSuccessful())
                                                   {
                                                       medicalList.add(imageBean.getData());
                                                       picList.add(imageBean.getData().getId());

                                                       gridViewAddImgesAdpter.notifyDataSetChanged();
                                                   }
                                                   else
                                                       ExceptionHandle.handleRespResultError(imageBean, MainActivity.this);


                                               }
                                           }, new Consumer<Throwable>() {
                                               @Override
                                               public void accept(Throwable e) throws Exception {
                                                   ExceptionHandle.handleException(e,MainActivity.this);
                                                   LoadingDialog.dismissLoading();
                                               }
                                           }, new Action() {
                                               @Override
                                               public void run() throws Exception {
                                                   LoadingDialog.dismissLoading();
                                               }
                                           }
                                )
                );


            }
            catch (Exception e)
            {
                e.printStackTrace();
                LoadingDialog.dismissLoading();
            }
        }
    }
}
