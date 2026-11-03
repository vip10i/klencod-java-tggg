package com.dotbhai.imagesender;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImageHelper {

    public static List<File> getAllImages(Context ctx, int limit) {
        List<File> files = new ArrayList<>();
        String[] projection = {
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DATE_ADDED
        };
        String order = MediaStore.Images.Media.DATE_ADDED + " DESC";

        Cursor cursor = ctx.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection, null, null, order);

        if (cursor != null) {
            int dataIdx = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            int count = 0;
            while (cursor.moveToNext() && count < limit) {
                String path = cursor.getString(dataIdx);
                File f = new File(path);
                if (f.exists()) {
                    files.add(f);
                    count++;
                }
            }
            cursor.close();
        }
        return files;
    }
}