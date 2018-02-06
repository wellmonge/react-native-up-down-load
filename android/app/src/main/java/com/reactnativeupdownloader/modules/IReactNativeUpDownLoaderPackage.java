package com.reactnativeupdownloader.modules;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.JavaScriptModule;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;

import java.util.List;

/**
 * Created by welli on 06/02/2018.
 */

public interface IReactNativeUpDownLoaderPackage extends ReactPackage {
    @Override
    List<NativeModule> createNativeModules(ReactApplicationContext reactContext);

    List<Class<? extends JavaScriptModule>> createJSModules();

    @Override
    List<ViewManager> createViewManagers(ReactApplicationContext reactContext);
}
