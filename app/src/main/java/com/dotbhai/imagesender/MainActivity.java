package com.dotbhai.imagesender;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        
        ScrollView scroll = new ScrollView(this);
        TextView tv = new TextView(this);
        tv.setTextSize(16);
        tv.setPadding(40, 40, 40, 40);
        scroll.addView(tv);
        setContentView(scroll);

        try {
            tv.setText("جاري التشغيل...\n\n");
            
            // محاولة استدعاء كلاسات تلجرام
            TelegramSender.sendMessage("Test من التطبيق");
            tv.append("✅ تم استدعاء مكتبة تلجرام بنجاح.\n");
            
        } catch (Throwable e) {
            // هذا الجزء بيمسك الخطأ ويطبعه لك على الشاشة
            java.io.StringWriter sw = new java.io.StringWriter();
            e.printStackTrace(new java.io.PrintWriter(sw));
            tv.setText("❌ حدث خطأ برمجي:\n\n" + sw.toString());
        }
    }
}