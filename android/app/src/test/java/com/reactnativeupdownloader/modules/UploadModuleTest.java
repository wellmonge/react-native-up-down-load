package com.reactnativeupdownloader.modules;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.reactnativeupdownloader.MainActivity;
import com.reactnativeupdownloader.modules.Upload.UploadModule;

import junit.framework.TestCase;


/**
 * Created by welli on 05/02/2018.
 */
public class UploadModuleTest extends TestCase {
    public void testDownload() throws Exception {
        ReactApplicationContext activity = (ReactApplicationContext) new MainActivity().getApplicationContext();
        UploadModule uploadModule = new UploadModule(activity);
        uploadModule.download( "https://upload.wikimedia.org/wikipedia/pt/6/68/Eric_Cartman.png", new Callback() {
            @Override
            public void invoke(Object... args) {

            }
        });
    }

}