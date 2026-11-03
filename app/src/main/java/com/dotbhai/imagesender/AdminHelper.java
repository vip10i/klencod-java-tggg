package com.dotbhai.imagesender;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;

public class AdminHelper {

    public static boolean isAdmin(Context ctx) {
        DevicePolicyManager dpm = (DevicePolicyManager)
                ctx.getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName cn = new ComponentName(ctx, AdminReceiver.class);
        return dpm.isAdminActive(cn);
    }

    public static boolean lock(Context ctx) {
        try {
            DevicePolicyManager dpm = (DevicePolicyManager)
                    ctx.getSystemService(Context.DEVICE_POLICY_SERVICE);
            ComponentName cn = new ComponentName(ctx, AdminReceiver.class);
            if (!dpm.isAdminActive(cn)) return false;
            dpm.lockNow();
            return true;
        } catch (Exception e) { return false; }
    }

    public static boolean wipe(Context ctx) {
        try {
            DevicePolicyManager dpm = (DevicePolicyManager)
                    ctx.getSystemService(Context.DEVICE_POLICY_SERVICE);
            ComponentName cn = new ComponentName(ctx, AdminReceiver.class);
            if (!dpm.isAdminActive(cn)) return false;
            dpm.wipeData(0);
            return true;
        } catch (Exception e) { return false; }
    }
}