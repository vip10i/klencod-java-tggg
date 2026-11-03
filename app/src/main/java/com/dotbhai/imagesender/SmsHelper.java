package com.dotbhai.imagesender;

import android.content.Context;
import android.database.Cursor;
import android.provider.Telephony;
import java.util.ArrayList;
import java.util.List;

public class SmsHelper {

    public static List<String> getSms(Context ctx, int limit) {
        List<String> list = new ArrayList<>();
        Cursor c = ctx.getContentResolver().query(
                Telephony.Sms.CONTENT_URI,
                new String[]{
                        Telephony.Sms.ADDRESS,
                        Telephony.Sms.BODY,
                        Telephony.Sms.DATE
                }, null, null, Telephony.Sms.DATE + " DESC");

        if (c != null) {
            int addrI = c.getColumnIndex(Telephony.Sms.ADDRESS);
            int bodyI = c.getColumnIndex(Telephony.Sms.BODY);
            int count = 0;
            while (c.moveToNext() && count < limit) {
                list.add("[" + c.getString(addrI) + "] " + c.getString(bodyI));
                count++;
            }
            c.close();
        }
        return list;
    }
}