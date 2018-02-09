import { Platform } from 'react-native';

var UpdownloadManager = require('./component').component;

const postFormData = (config, successCallback, cancelCallback) => {
    if (Platform.OS == 'android') {
        UpdownloadManager.postFormData(config, successCallback, cancelCallback);
    }
}

const openSelectDialog = (config, successCallback, cancelCallback) => {
    if (Platform.OS == 'android') {
        UpdownloadManager.openSelectDialog(config, successCallback, cancelCallback);
    }
}

const showFile = (URLtoDownload) => {
    if (Platform.OS == 'android') {
        UpdownloadManager.showFile(URLtoDownload);
    }
}

const downloadFile = (URLtoDownload) => {
    if (Platform.OS == 'android') {
        UpdownloadManager.downloadFile(URLtoDownload);
    }
}

const teste = (message) => {
    if (Platform.OS == 'android') {
        UpdownloadManager.teste(message);  
    }  
}

export {
    postFormData,
    openSelectDialog,
    showFile,
    downloadFile,
    teste
}