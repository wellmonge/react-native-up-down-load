package uploadcomponent;

import com.facebook.react.bridge.JavaScriptModule;

import java.util.List;

/**
 * Created by wellinton on 25/01/2018.
 */

interface IUploadPackage {
    List<Class<? extends JavaScriptModule>> createJSModules();
}
