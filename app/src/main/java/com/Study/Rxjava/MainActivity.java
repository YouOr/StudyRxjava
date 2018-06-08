package com.Study.Rxjava;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.Study.Rxjava.callback.PermissionListener;
import com.Study.Rxjava.http.Fault;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseActivity {
    String TAG = "MainActivityLog";
    private Button btn_upFile, btn_select_img;
    private UserLoader userLoader;
    private static final int FILE_SELECT_CODE = 0;

    private static final int REQUEST_CODE_CHOOSE = 23;//定义请求码常量

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        userLoader = new UserLoader();
        userLoader.getUser("GetStopListByEmpID", "zhaojingq", "全部", "全部", "全部", 1).subscribe(new Action1<List<UserBean>>() {
            @Override
            public void call(List<UserBean> userBeans) {
                Log.i(TAG, userBeans.get(1).getAddress());
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                Log.e("TAG", "error message:" + throwable.getMessage());
                if (throwable instanceof Fault) {
                    Fault fault = (Fault) throwable;
                    if (fault.getErrorCode() == 404) {
                        //错误处理
                    } else if (fault.getErrorCode() == 500) {
                        //错误处理
                    } else if (fault.getErrorCode() == 501) {
                        //错误处理
                    }
                }
                Log.i(TAG, throwable.toString());
            }
        });


//        try {
//            Retrofit retrofit = new Retrofit.Builder()
//                    .addConverterFactory(GsonConverterFactory.create())
//                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
//                    .baseUrl("http://118.190.157.161/")
//                    .client(getOkHttpClient())
//                    .build();
//            MyService service = retrofit.create(MyService.class);
//            service.login("GetStopListByEmpID", "B2F145B12F23554185991E471C9C0BB44", "zhaojingq", "全部", "全部", "全部", 1)//获取Observable对象
//                    .subscribeOn(Schedulers.newThread())//请求在新的线程中执行
//                    .observeOn(Schedulers.io())         //请求完成后在io线程中执行
//                    .doOnNext(new Action1<List<UserBean>>() {
//                        @Override
//                        public void call(List<UserBean> userBeans) {
//
//                        }
//                    .subscribe(new Subscriber<List<UserBean>>() {
//                        @Override
//                        public void onCompleted() {
//
//                        }
//
//                        @Override
//                        public void onError(Throwable e) {
//                            //请求失败
//
//                        }
//
//                        @Override
//                        public void onNext(List<UserBean> userBeans) {
//                            //请求成功
//                            Log.i(TAG, userBeans.get(0).getStopName());
//                        }
//                    });
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        test();
        intentUrl();
    }

    private void intentUrl() {
        Intent i = getIntent();
        String action = i.getAction();
        //将文件复制到制定目录中
        if (Intent.ACTION_VIEW.equals(action)) {
            String str = i.getDataString();
            Log.e("uri", str);
            if (str != null) {
                final Uri uri = Uri.parse(str);//uri路径
                upFile(uri);
            }
        }
    }

    private void init() {
        btn_upFile = (Button) findViewById(R.id.btn_upFile);
        btn_select_img = (Button) findViewById(R.id.btn_select_img);
        btn_upFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestRuntimePermission(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, new PermissionListener() {
                    @Override
                    public void onGranted() {
                        chooseFile();
                    }

                    @Override
                    public void onDenied(List<String> deniedPermission) {
                        for (String permission : deniedPermission) {
                            Toast.makeText(MainActivity.this, "被拒绝权限：" + permission, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        btn_select_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestRuntimePermission(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE}, new PermissionListener() {
                    @Override
                    public void onGranted() {
                        Matisse
                                .from(MainActivity.this)
                                .choose(MimeType.ofImage())//照片视频全部显示
                                .countable(true)//有序选择图片
                                .maxSelectable(9)//最大选择数量为9
                                .gridExpectedSize(350)//图片显示表格的大小getResources()
//                              .getDimensionPixelSize(R.dimen.grid_expected_size)
                                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)//图像选择和预览活动所需的方向。
                                .thumbnailScale(0.85f)//缩放比例
                                .theme(R.style.Matisse_Zhihu)//主题  暗色主题 R.style.Matisse_Dracula
                                .imageEngine(new GlideEngine())//加载方式
                                .capture(true)
                                .captureStrategy(new CaptureStrategy(true, "com.Study.Rxjava.fileprovider"))
                                .forResult(REQUEST_CODE_CHOOSE);//请求码
                    }

                    @Override
                    public void onDenied(List<String> deniedPermission) {
                        for (String permission : deniedPermission) {
                            Toast.makeText(MainActivity.this, "被拒绝权限：" + permission, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void test() {
        //创建一个观察者
        Observer<String> observer = new Observer<String>() {

            @Override
            public void onCompleted() {
                Log.i(TAG, "Completed");
            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, "Error");
            }

            @Override
            public void onNext(String s) {
                Log.i(TAG, s);
            }
        };
        //使用Observable.create()创建被观察者
        Observable observable1 = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext("Hello");
                subscriber.onNext("Wrold");
                subscriber.onCompleted();
            }
        });
        //订阅
        observable1.subscribe(observer);
    }

    /**
     * ftp上传
     *
     * @param url          ftp地址
     * @param port         ftp连接端口号
     * @param username     登录用户名
     * @param password     登录密码
     * @param fileNamePath 本地文件保存路径
     * @return
     */
    public String ftpUpload(String url, String port, String username, String password, String fileNamePath) {
        FTPClient ftpClient = new FTPClient();
        FileInputStream fis = null;
        String returnMessage = "0";
        try {
            ftpClient.connect(url, Integer.parseInt(port));
            boolean loginResult = ftpClient.login(username, password);
            int returnCode = ftpClient.getReplyCode();
            if (loginResult && FTPReply.isPositiveCompletion(returnCode)) {// 如果登录成功
                ftpClient.setBufferSize(1024);
                ftpClient.setControlEncoding("UTF-8");
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                ftpClient.enterLocalPassiveMode();
                fis = new FileInputStream(fileNamePath);
                ftpClient.storeFile(new String(fileNamePath.substring(fileNamePath.lastIndexOf("/") + 1, fileNamePath.length()).getBytes("GBK"), "iso-8859-1"), fis);
                returnMessage = "1";   //上传成功
            } else {// 如果登录失败
                returnMessage = "0";
            }
        } catch (IOException e) {
            e.printStackTrace();
//		     throw new RuntimeException("FTP客户端出错！", e);
            returnMessage = "-1";
        } finally {
            //IOUtils.closeQuietly(fis);
            try {
                ftpClient.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("关闭FTP连接发生异常！", e);
            }
        }
        return returnMessage;
    }

    private void chooseFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "选择文件"), FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "没有文件管理器", Toast.LENGTH_SHORT).show();
        }
    }

    List<Uri> mSelected;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if (resultCode != Activity.RESULT_OK) {
            Log.e(TAG, "onActivityResult() error, resultCode: " + resultCode);
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }
        if (requestCode == FILE_SELECT_CODE) {
            final Uri uri = data.getData();
            upFile(uri);
        }
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            mSelected = Matisse.obtainResult(data);
            Log.d("Matisse", "mSelected: " + mSelected);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void upFile(final Uri uri) {
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext(ftpUpload("106.15.74.192", "21", "ffdemo", "000000", getFileAbsolutePath(MainActivity.this, uri)));
            }
        }).subscribeOn(Schedulers.io())//在子线程中运行
                .observeOn(AndroidSchedulers.mainThread())//在主线程中运行
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        if (s.equals("1")) {
                            Toast.makeText(MainActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
                        } else if (s.equals("-1")) {
                            Toast.makeText(MainActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "登录FTP失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    /**
     * 根据Uri获取文件的绝对路径，解决Android4.4以上版本Uri转换
     *
     * @param fileUri
     */
    @TargetApi(19)
    public static String getFileAbsolutePath(Activity context, Uri fileUri) {
        try {
            if (context == null || fileUri == null)
                return null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, fileUri)) {
                if (isExternalStorageDocument(fileUri)) {
                    String docId = DocumentsContract.getDocumentId(fileUri);
                    String[] split = docId.split(":");
                    String type = split[0];
                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }
                } else if (isDownloadsDocument(fileUri)) {
                    String id = DocumentsContract.getDocumentId(fileUri);
                    Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                    return getDataColumn(context, contentUri, null, null);
                } else if (isMediaDocument(fileUri)) {
                    String docId = DocumentsContract.getDocumentId(fileUri);
                    String[] split = docId.split(":");
                    String type = split[0];
                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }
                    String selection = MediaStore.Images.Media._ID + "=?";
                    String[] selectionArgs = new String[]{split[1]};
                    return getDataColumn(context, contentUri, selection, selectionArgs);
                }
            } // MediaStore (and general)
            else if ("content".equalsIgnoreCase(fileUri.getScheme())) {
                // Return the remote address
                if (isGooglePhotosUri(fileUri))
                    return fileUri.getLastPathSegment();
                return getDataColumn(context, fileUri, null, null);
            }
            // File
            else if ("file".equalsIgnoreCase(fileUri.getScheme())) {
                return fileUri.getPath();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String[] projection = {MediaStore.Images.Media.DATA};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
}
