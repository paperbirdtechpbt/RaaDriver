package com.pbt.raadrivers.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import com.android.volley.*;
import com.android.volley.toolbox.Volley;

import java.io.*;
import java.util.*;

import static android.content.Context.MODE_PRIVATE;

public class CustomExceptionHandler implements Thread.UncaughtExceptionHandler,AppConstant{

    private Thread.UncaughtExceptionHandler defaultUEH;
    private String localPath;
    private String url;
    Context context;

    public CustomExceptionHandler(String localPath, String url, Context context) {
        this.localPath = localPath;
        this.context = context;
        this.url = url;
        this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
    }

    public void uncaughtException(Thread t, Throwable e) {
        android.text.format.DateFormat df = new android.text.format.DateFormat();
        String timestamp = (String) df.format("yyyy-MM-dd_kk:mm:ss", new Date());
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        e.printStackTrace(printWriter);
        String stacktrace = result.toString();
        printWriter.close();
        String filename = "";
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e12) {
        }
        String version_name = packageInfo.versionName;

        SharedPreferences prefs = context.getSharedPreferences("Login", MODE_PRIVATE);
        if (prefs != null && !prefs.equals("")) {
            String role = prefs.getString("role", "");
            String userId = prefs.getString("userid", "");
            String fName = prefs.getString("first_name", "");
            String lName = prefs.getString("last_name", "");
            String phone = prefs.getString("phone", "");
            if (!userId.isEmpty() && !role.isEmpty() && !phone.isEmpty())
                filename = role + "_" + userId + "_" + fName + "_" + lName + "_" + phone + "_" + "Version_" + version_name + "_" + Build.MODEL + "_" + "Android_" + Build.VERSION.RELEASE + "_" + timestamp + ".txt";
            else
                filename = "Version_" + version_name + "_" + Build.MODEL + timestamp + ".txt";
        } else
            filename = "Version_" + version_name + "_" + Build.MODEL + timestamp + ".txt";

        if (localPath != null) {
            writeToFile(stacktrace, filename);
        }
        defaultUEH.uncaughtException(t, e);
    }

    private void writeToFile(String stacktrace, String filename) {
        try {
            BufferedWriter bos = new BufferedWriter(new FileWriter(
                    localPath + "/" + filename));
            bos.write(stacktrace);
            bos.flush();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendToServer(Context context) {
        upload_file(context);
    }

    public static void upload_file(Context context) {

        String path = Environment.getExternalStorageDirectory().toString() + "/RaadarbarCrash";
        Log.e("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();

        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                Log.e("Files", "FileName:" + files[i].getName());
                uploadPDF(context, files[i].getName(), Uri.fromFile(files[i]), files[i]);
            }
            List<File> fileList = new ArrayList<>();
            Collections.addAll(fileList, files);
        } else {
            Log.e("Files", "File no file created  " + path);
        }
    }

    public static byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();

    }

    private static void uploadPDF(final Context context, final String pdfname, Uri pdffile, final File file) {

        InputStream iStream = null;

        try {

            iStream = context.getContentResolver().openInputStream(pdffile);
            final byte[] inputData = getBytes(iStream);

            VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, errorUpload,
                    new Response.Listener<NetworkResponse>() {
                        @Override
                        public void onResponse(NetworkResponse response) {
                            boolean deleted = file.delete();
                            if (!deleted) {
                                boolean deleted2 = false;
                                try {
                                    deleted2 = file.getCanonicalFile().delete();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                if (!deleted2) {
                                    boolean deleted3 = context.deleteFile(file.getName());
                                }
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                        }
                    }) {

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> paramms = new HashMap<>();
                    return paramms;
                }

                @Override
                protected Map<String, DataPart> getByteData() {
                    Map<String, DataPart> params = new HashMap<>();
                    params.put("file", new DataPart(pdfname, inputData));
                    return params;
                }
            };

            volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            RequestQueue rQueue = Volley.newRequestQueue(context);
            rQueue.add(volleyMultipartRequest);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


