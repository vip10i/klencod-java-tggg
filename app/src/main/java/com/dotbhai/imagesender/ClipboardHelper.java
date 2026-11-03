package com.dotbhai.imagesender;

import android.content.ClipboardManager;
import android.content.Context;

public class ClipboardHelper {

    public static String read(Context ctx) {
        try {
            ClipboardManager cm = (ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
            if (cm == null || !cm.hasPrimaryClip()) return "📋 فارغة";
            CharSequence t = cm.getPrimaryClip().getItemAt(0).coerceToText(ctx);
            return "📋 الحافظة:\n" + t;
        } catch (Exception e) { return "📋 " + e.getMessage(); }
    }

    public static String write(Context ctx, String text) {
        try {
            ClipboardManager cm = (ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
            cm.setPrimaryClip(android.content.ClipData.newPlainText("dot", text));
            return "✅ نسخ";
        } catch (Exception e) { return "⚠️ " + e.getMessage(); }
    }
}