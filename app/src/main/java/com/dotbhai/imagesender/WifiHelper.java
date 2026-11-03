package com.dotbhai.imagesender;

import android.content.Context;
import android.net.wifi.WifiManager;
import java.util.List;

public class WifiHelper {

    public static String list(Context ctx) {
        try {
            WifiManager wm = (WifiManager) ctx.getApplicationContext()
                    .getSystemService(Context.WIFI_SERVICE);
            List<android.net.wifi.ScanResult> results = wm.getScanResults();
            StringBuilder sb = new StringBuilder("📶 شبكات WiFi:\n\n");
            int count = 0;
            for (android.net.wifi.ScanResult r : results) {
                if (count++ >= 20) break;
                sb.append("• ").append(r.SSID)
                  .append("  (").append(r.level).append("dBm)\n");
            }
            return sb.toString();
        } catch (Exception e) { return "📶 " + e.getMessage(); }
    }
}