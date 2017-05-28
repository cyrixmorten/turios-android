package com.turios.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class UpdateApp extends AsyncTask<File,Void,Void> {

    private Context context;

    public static void fromFile(Context context, File file) {
        new UpdateApp(context).execute(file);
    }

    UpdateApp(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(File... arg0) {
        try {
            File fromFile = arg0[0];

            String PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
            String FILENAME = "update.apk";
            File file = new File(PATH);
            file.mkdirs();
            File outputFile = new File(file, FILENAME);

            copyFile(fromFile, outputFile);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(outputFile), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // without this flag android returned a intent error!
            context.startActivity(intent);


        } catch (Exception e) {
            Log.e("UpdateAPP", "Update error! " + e.getMessage());
        }
        return null;
    }

    private void copyFile(File src, File dst) throws IOException {
        FileInputStream var2 = new FileInputStream(src);
        FileOutputStream var3 = new FileOutputStream(dst);
        byte[] var4 = new byte[1024];

        int var5;
        while((var5 = var2.read(var4)) > 0) {
            var3.write(var4, 0, var5);
        }

        var2.close();
        var3.close();
    }


}
