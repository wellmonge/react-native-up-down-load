package com.reactnativeupdownloader.modules.Download;

import android.content.Context;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadModule extends ReactContextBaseJavaModule {

    public static File currentFile;

    public DownloadModule(ReactApplicationContext reactContext) {
        super( reactContext );
    }

    @Override
    public String getName() {
        return "DownloadModule";
    }

    @ReactMethod
    public void download(String URLtoDownload, Callback callback) throws JSONException, IOException {
        try {
            currentFile = this.DownloadFile(URLtoDownload);
            //callback.invoke(SuccessMessage);
        } catch (android.content.ActivityNotFoundException e) {
            //callback.invoke(InternalError);
        }
    }

    public File DownloadFile (String URLtoDownload) throws IOException {
        try {
            Context context = getCurrentActivity();

            String fileName = URLtoDownload.split("/")[3].toString();

            File f = new File(context.getCacheDir(), fileName);

            if (!f.exists()){
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(URLtoDownload)
                        .addHeader("Content-Type", "application/json")
                        .build();
                Response res  = client.newCall(request).execute();
                InputStream reader = res.body().byteStream();

                f.setReadable(true, false);

                FileOutputStream outStream = new FileOutputStream(f);
                byte[] buffer = new byte[1024];
                int readBytes = reader.read(buffer);
                while (readBytes > 0) {
                    outStream.write(buffer, 0, readBytes);
                    readBytes = reader.read(buffer);
                }
                reader.close();
                outStream.close();
            }

            return f;

        } catch (android.content.ActivityNotFoundException e) {
            throw new IOException(e.getMessage());
        }
    }

}
