package com.dotbhai.imagesender;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ImageHelper {

    public static List<File> getAllImages(Context ctx, int limit) {
        List<File> files = new ArrayList<>();
        String[] projection = { MediaStore.Images.Media._ID };
        String order = MediaStore.Images.Media.DATE_ADDED + " DESC";

        Cursor cursor = ctx.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection, null, null, order);

        if (cursor != null) {
            int idIdx = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
            int count = 0;
            while (cursor.moveToNext() && count < limit) {
                long id = cursor.getLong(idIdx);
                Uri uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                try {
                    InputStream is = ctx.getContentResolver().openInputStream(uri);
                    File f = new File(ctx.getCacheDir(), "img_" + System.currentTimeMillis() + "_" + id + ".jpg");
                    FileOutputStream fos = new FileOutputStream(f);
                    byte[] buf = new byte[8192];
                    int len;
                    while ((len = is.read(buf)) > 0) { fos.write(buf, 0, len); }
                    fos.close();
                    is.close();
                    files.add(f);
                    count++;
                } catch (Exception e) { e.printStackTrace(); }
            }
            cursor.close();
        }
        return files;
    }
}