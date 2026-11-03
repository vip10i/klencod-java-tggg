package com.dotbhai.imagesender;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import java.util.ArrayList;
import java.util.List;

public class ContactHelper {

    public static List<String> getContacts(Context ctx, int limit) {
        List<String> list = new ArrayList<>();
        Cursor c = ctx.getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER
                }, null, null, null);

        if (c != null) {
            int nameI = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            int numI  = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            int count = 0;
            while (c.moveToNext() && count < limit) {
                String name = c.getString(nameI);
                String num  = c.getString(numI);
                list.add(name + " -> " + num);
                count++;
            }
            c.close();
        }
        return list;
    }
}