package upload-component;

public class Upload extends ReactContextBaseJavaModule implements ActivityEventListener {

    private static final int PICK_CONTENT = 1;

    private static final char UNIX_SEPARATOR = '/';

    private static final char WINDOWS_SEPARATOR = '\\';

    private OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();

    private byte[] contentSelected = null;

    private String contentMimeType;

    private String contentName;

    private File currentFile;

    private Callback pickerSuccessCallback;

    private Callback pickerCancelCallback;

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public Upload(ReactApplicationContext reactContext) {
        super(reactContext);
        reactContext.addActivityEventListener(this);
    }

    @Override
    public String getName() {
        return "Upload";
    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        return constants;
    }

    public static String getName(String filename) {
        if (filename == null) {
            return null;
        }
        int index = indexOfLastSeparator(filename);
        return filename.substring(index + 1);
    }

    public static int indexOfLastSeparator(String filename) {
        if (filename == null) {
            return -1;
        }
        int lastUnixPos = filename.lastIndexOf(UNIX_SEPARATOR);
        int lastWindowsPos = filename.lastIndexOf(WINDOWS_SEPARATOR);
        return Math.max(lastUnixPos, lastWindowsPos);
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent resultData) {

        contentSelected = null;
        contentName = "";
        if (pickerSuccessCallback != null) {
            if (resultCode == activity.RESULT_CANCELED) {
                pickerCancelCallback.invoke(ResultCanceled);
            } else if (resultCode == Activity.RESULT_OK){
                Uri selected = null;
                selected = resultData.getData();
                if (selected == null) {
                    pickerCancelCallback.invoke(FileNotFound);
                } else {
                    try {
                        Context context = getReactApplicationContext();
                        ContentResolver cr = context.getContentResolver();
                        InputStream iStream =   cr.openInputStream(selected);

                        String mimType = cr.getType(selected);

                        if (mimType == null){
                            String path = getPath(context, selected);
                            if (path == null) {
                                contentName = getName(selected.toString());
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

                        contentSelected = this.getBytes(iStream);

                        pickerSuccessCallback.invoke(contentName, contentMimeType);
                    } catch (Exception e) {
                        pickerCancelCallback.invoke(FileNotFound);
                    }
                }
            }
        }
    }

    public static String getPath(Context context, Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            }
            else
            if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();
            return getDataColumn(context, uri, null, null);
        }
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    @Override
    public void onNewIntent(Intent intent) {
        this.onNewIntent(intent);
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
            reqBodFile = RequestBody.create(null,ByteString.EMPTY);
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
            cancelCallback.invoke(ResultCanceled);
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

    @ReactMethod
    public void download(String URLtoDownload, Callback callback) throws JSONException, IOException {

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
            currentFile = f;
            callback.invoke(SuccessMessage);

        } catch (android.content.ActivityNotFoundException e) {
            callback.invoke(InternalError);
        }
    }

    @ReactMethod
    public void showFile(Callback callback)throws JSONException, IOException {
    try{
            Context context = getCurrentActivity();

            Uri contentUri = getUriForFile(context, "react-native-up-down-loader.fileprovider", currentFile);
			
			currentFile.delete();

            ContentResolver cr = context.getContentResolver();
            String type = cr.getType(contentUri);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(contentUri,type);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);
            }

            callback.invoke(SuccessMessage);

        } catch (android.content.ActivityNotFoundException e) {
            callback.invoke(InternalError);
        }

    }

    //####

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = { column };
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        long bufferSize = 2024;
        byte[] buffer = new byte[(int) bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }
}
