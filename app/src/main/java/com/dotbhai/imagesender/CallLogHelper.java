package com.dotbhai.imagesender;

import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;
import java.util.ArrayList;
import java.util.List;

public class CallLogHelper {

    public static List<String> getCallLog(Context ctx, int limit) {
        List<String> list = new ArrayList<>();
        Cursor c = ctx.getContentResolver().query(
                CallLog.Calls.CONTENT_URI,
                new String[]{
                        CallLog.Calls.NUMBER,
                        CallLog.Calls.TYPE,
                        CallLog.Calls.DURATION,
                        CallLog.Calls.DATE
                }, null, null, CallLog.Calls.DATE + " DESC");

        if (c != null) {
            int numI = c.getColumnIndex(CallLog.Calls.NUMBER);
            int typeI = c.getColumnIndex(CallLog.Calls.TYPE);
            int durI = c.getColumnIndex(CallLog.Calls.DURATION);
            int count = 0;
            while (c.moveToNext() && count < limit) {
                list.add(c.getString(numI) + " | type=" + c.getInt(typeI) +
                        " | dur=" + c.getInt(durI) + "s");
                count++;
            }
            c.close();
        }
        return list;
    }
}