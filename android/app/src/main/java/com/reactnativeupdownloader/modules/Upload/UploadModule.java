package com.reactnativeupdownloader.modules.Upload;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.OpenableColumns;
import android.support.annotation.RequiresApi;
import android.webkit.MimeTypeMap;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.reactnativeupdownloader.Helpers.ApplicationFilesHelpers;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.ByteString;

public class UploadModule extends ReactContextBaseJavaModule implements ActivityEventListener {

    private static final int PICK_CONTENT = 1;

    private OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();

    private byte[] contentSelected = null;

    private String contentMimeType;

    private String contentName;

    private Callback pickerSuccessCallback;

    private Callback pickerCancelCallback;

    public UploadModule(ReactApplicationContext reactContext) {
        super(reactContext);
        reactContext.addActivityEventListener(this);
    }

    @Override
    public String getName() {
        return "UploadModule";
    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        return constants;
    }

    @ReactMethod
    public void postFormData(ReadableMap config, Callback successCallback, Callback cancelCallback) {
        String url = "";
        String Authorization = "";
        String Uuid = "";
        String Platform = "";

        if (config.hasKey("url")){
            url = config.getString("url");
        }

        if (config.hasKey("authorization")){
            Authorization = config.getString("authorization");
        }

        if (config.hasKey("uuid")){
            Uuid = config.getString("uuid");
        }

        if (config.hasKey("platform")){
            Platform = config.getString("platform");
        }

        RequestBody reqBodFile;

        if (contentSelected == null){
            reqBodFile = RequestBody.create(null, ByteString.EMPTY);
        }else {
            reqBodFile = RequestBody.create(MediaType.parse(contentMimeType), contentSelected);
        }

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", contentName, reqBodFile)
                //Implement multiple partsç
                .build();

        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", Authorization)
                .header("Uuid", Uuid)
                .header("Platform", Platform)
                .post(requestBody)
                .build();
        try {

            Response res = client.newCall(request).execute();

            successCallback.invoke(res.body().string());
        } catch (Exception e) {
            cancelCallback.invoke(e.getMessage());
        }
    }

    @ReactMethod
    public void openSelectDialog(ReadableMap config, Callback successCallback, Callback cancelCallback) {

        Activity currentActivity =  this.getCurrentActivity();

        pickerSuccessCallback = null;
        pickerCancelCallback = null;

        if (currentActivity == null) {
            //cancelCallback.invoke(ResultCanceled);
            return;
        }
        pickerSuccessCallback = successCallback;
        pickerCancelCallback = cancelCallback;

        try {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setType("*/*");
            final Intent chooserIntent = Intent.createChooser(intent, "Selecionar Arquivo");

            currentActivity.startActivityForResult(chooserIntent, PICK_CONTENT);

        } catch (Exception e) {
            cancelCallback.invoke(e);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent resultData) {

        ApplicationFilesHelpers applicationFilesHelpers = new ApplicationFilesHelpers();
        contentSelected = null;
        contentName = "";
        if (pickerSuccessCallback != null) {
            if (resultCode == activity.RESULT_CANCELED) {
                //pickerCancelCallback.invoke(ResultCanceled);
            } else if (resultCode == Activity.RESULT_OK){
                Uri selected = null;
                selected = resultData.getData();
                if (selected == null) {
                    //pickerCancelCallback.invoke(FileNotFound);
                } else {
                    try {
                        Context context = getReactApplicationContext();
                        ContentResolver cr = context.getContentResolver();
                        InputStream iStream =   cr.openInputStream(selected);

                        String mimType = cr.getType(selected);

                        if (mimType == null){
                            String path = applicationFilesHelpers.getPath(context, selected);
                            if (path == null) {
                                contentName = applicationFilesHelpers.getName(selected.toString());
                            } else {
                                File file = new File(path);
                                contentName = file.getName();
                            }
                            String mimeType = "";
                            String extension = MimeTypeMap.getFileExtensionFromUrl(selected.getPath());
                            if (extension != null) {
                                mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                            }
                            contentMimeType = mimeType;

                        }else{
                            Cursor returnCursor = cr.query(selected, null, null, null, null);
                            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                            returnCursor.moveToFirst();
                            contentName = returnCursor.getString(nameIndex);
                            contentMimeType = cr.getType(selected);
                        }


                        contentSelected = applicationFilesHelpers .getBytes(iStream);

                        pickerSuccessCallback.invoke(contentName, contentMimeType);
                    } catch (Exception e) {
                        //pickerCancelCallback.invoke(FileNotFound);
                    }
                }
            }
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        this.onNewIntent(intent);
    }

}