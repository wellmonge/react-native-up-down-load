'use strict';

var {
} = require('react-native');
import {
    NativeModules,
    DeviceEventEmitter,
} from 'react-native';

var nativeModule = NativeModules.ReactNativeUpDownLoad;

var RNUpdownloadManager = () => { }

RNUpdownloadManager.prototype.postFormData = (config, successCallback, cancelCallback) => {
    nativeModule.postFormData(config, successCallback, cancelCallback);
}

RNUpdownloadManager.prototype.openSelectDialog = (config, successCallback, cancelCallback) => {
    nativeModule.openSelectDialog(config, successCallback, cancelCallback);
}

RNUpdownloadManager.prototype.showFile = (URLtoDownload) => {
    nativeModule.showFile(URLtoDownload);
}

RNUpdownloadManager.prototype.downloadFile = (URLtoDownload, ) => {
    nativeModules.openSelectDialog(config, successCallback, cancelCallback);
}

RNUpdownloadManager.prototype.teste = (message) => {
    nativeModule.teste(message);    
}

module.exports = {
	component: new RNUpdownloadManager()
};