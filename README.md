# uploadpicdemo
主要是通过Rx2.0做上传图片的操作（上传图片的操作看MainActivity,UploadPhotoHelper类就可以了）

这个项目的主要功能是做多张图片的上传，包括了用Rx2.0做批量图片的处理，Retrofit做图片上传的操作，主要是看MainActivity中uploadImages方法

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
            
 
compositeDisposable.add（Disposable d）这个方法中的Disposable d是管理一个订阅，使用.add来管理多个订阅，防止由于没有及时取消，导致Activity/Fragment
无法销毁而引起内存泄露

UploadPhotoHelper.requestUpload(List<String> paths)就是用于图片上传的方法
public static Flowable<HttpResult<ImageUploadBean>> requestUpload(List<String> paths) throws Exception {

        //just（）：将一个或多个对象转换成发射这个或这些对象的一个Observable
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
                //String是传递值  File是返回值
                .map(new Function<String, File>() {
                    @Override
                    public File apply(@NonNull String file) throws Exception {
                        //做图片压缩处理
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
                        //图片上传的操作
                        return APIManager.getAPIService()
                                .uploadFile(body);

                    }
                });

