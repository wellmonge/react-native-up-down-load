package com.reactnativeupdownloader.modules.Viewer;
import com.reactnativeupdownloader.modules.Download.DownloadModule;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import java.io.File;
import java.io.IOException;

import static android.support.v4.content.FileProvider.getUriForFile;

public class ViewerModule extends ReactContextBaseJavaModule {

    private File currentFile;

    public ViewerModule(ReactApplicationContext reactContext) {
        super( reactContext );
    }

    @Override
    public String getName() {
        return "ViewerModule";
    }

    @ReactMethod
    public void showFile(String URLtoDownload)throws IOException {
        try{
            Context context = getCurrentActivity();

            ReactApplicationContext reactApplicationContext = (ReactApplicationContext) context.getApplicationContext();

            DownloadModule downloadModule = new DownloadModule(reactApplicationContext);

            currentFile = downloadModule.DownloadFile(URLtoDownload);

            Uri contentUri = getUriForFile(reactApplicationContext, "reactnativeupdownloader.FileProvider", currentFile);

            currentFile.delete();

            ContentResolver cr = reactApplicationContext.getContentResolver();
            String type = cr.getType(contentUri);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(contentUri,type);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            if (intent.resolveActivity(reactApplicationContext.getPackageManager()) != null) {
                reactApplicationContext.startActivity(intent);
            }

            //callback.invoke(SuccessMessage);

        } catch (android.content.ActivityNotFoundException e) {
            //callback.invoke(InternalError);
        }

    }
}
