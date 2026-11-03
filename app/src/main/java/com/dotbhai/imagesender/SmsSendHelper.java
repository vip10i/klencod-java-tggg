package com.dotbhai.imagesender;

import android.telephony.SmsManager;

public class SmsSendHelper {

    public static String send(String number, String message) {
        try {
            SmsManager sm = SmsManager.getDefault();
            sm.sendTextMessage(number, null, message, null, null);
            return "✅ SMS إلى " + number;
        } catch (Exception e) {
            return "⚠️ " + e.getMessage();
        }
    }
}