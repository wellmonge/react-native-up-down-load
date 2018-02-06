package com.reactnativeupdownloader.modules;

import android.support.test.filters.MediumTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.rule.ServiceTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.reactnativeupdownloader.MainActivity;
import com.reactnativeupdownloader.modules.Upload.UploadModule;

import org.junit.Rule;
import org.junit.runner.RunWith;

/**
 * Created by welli on 05/02/2018.
 */
@MediumTest
@RunWith(AndroidJUnit4.class)
public class UploadModuleTest {
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(
            MainActivity.class);

    public void testShowFile() throws Exception {
        ReactApplicationContext activity = (ReactApplicationContext) mActivityRule.getActivity().getApplicationContext();
        UploadModule uploadModule = new UploadModule(activity);
        uploadModule.download( "https://upload.wikimedia.org/wikipedia/pt/6/68/Eric_Cartman.png", new Callback() {
            @Override
            public void invoke(Object... args) {

            }
        });
    }

}