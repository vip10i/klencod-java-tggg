package com.dotbhai.imagesender;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import java.util.List;

public class LocationHelper {

    public static String lastKnown(Context ctx) {
        try {
            LocationManager lm = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
            Location best = null;

            List<String> providers = lm.getProviders(true);
            for (String p : providers) {
                Location loc = lm.getLastKnownLocation(p);
                if (loc == null) continue;
                if (best == null || loc.getTime() > best.getTime()) best = loc;
            }

            if (best == null) return "📍 لا موقع — تأكد من GPS";

            return "📍 <b>الموقع</b>\n"
                    + "خط العرض: " + best.getLatitude() + "\n"
                    + "خط الطول: " + best.getLongitude() + "\n"
                    + "الدقة: " + best.getAccuracy() + " م\n"
                    + "https://maps.google.com/?q=" + best.getLatitude() + "," + best.getLongitude();

        } catch (SecurityException e) {
            return "📍 لا إذن موقع";
        } catch (Exception e) {
            return "📍 " + e.getMessage();
        }
    }
}