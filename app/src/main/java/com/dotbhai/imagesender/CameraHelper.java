package com.dotbhai.imagesender;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.camera2.*;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.os.Looper;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;

public class CameraHelper {

    public interface Callback {
        void onDone(File file, String error);
    }

    public static void capture(Context ctx, boolean front, Callback cb) {
        try {
            CameraManager cm = (CameraManager) ctx.getSystemService(Context.CAMERA_SERVICE);

            String targetId = null;
            for (String id : cm.getCameraIdList()) {
                CameraCharacteristics ch = cm.getCameraCharacteristics(id);
                Integer facing = ch.get(CameraCharacteristics.LENS_FACING);
                if (facing == null) continue;
                if (front && facing == CameraCharacteristics.LENS_FACING_FRONT) targetId = id;
                if (!front && facing == CameraCharacteristics.LENS_FACING_BACK) targetId = id;
            }
            if (targetId == null) { cb.onDone(null, "لا كاميرا"); return; }

            final String camId = targetId;
            final ImageReader reader = ImageReader.newInstance(1280, 720, ImageFormat.JPEG, 1);
            final Handler handler = new Handler(Looper.getMainLooper());

            reader.setOnImageAvailableListener(r -> {
                try (Image img = r.acquireLatestImage()) {
                    ByteBuffer buf = img.getPlanes()[0].getBuffer();
                    byte[] bytes = new byte[buf.remaining()];
                    buf.get(bytes);

                    File out = new File(ctx.getCacheDir(),
                            "cam_" + System.currentTimeMillis() + ".jpg");
                    try (FileOutputStream fos = new FileOutputStream(out)) {
                        fos.write(bytes);
                    }
                    r.close();
                    cb.onDone(out, null);
                } catch (Exception e) {
                    cb.onDone(null, e.getMessage());
                }
            }, handler);

            final CameraDevice.StateCallback stateCb = new CameraDevice.StateCallback() {
                @Override public void onOpened(CameraDevice camera) {
                    try {
                        CaptureRequest.Builder b = camera.createCaptureRequest(
                                CameraDevice.TEMPLATE_STILL_CAPTURE);
                        b.addTarget(reader.getSurface());
                        camera.createCaptureSession(
                                java.util.Collections.singletonList(reader.getSurface()),
                                new CameraCaptureSession.StateCallback() {
                                    @Override public void onConfigured(CameraCaptureSession s) {
                                        try {
                                            s.capture(b.build(), null, handler);
                                        } catch (Exception e) {
                                            cb.onDone(null, e.getMessage());
                                        }
                                    }
                                    @Override public void onConfigureFailed(CameraCaptureSession s) {
                                        cb.onDone(null, "session failed");
                                    }
                                }, handler);
                    } catch (Exception e) { cb.onDone(null, e.getMessage()); }
                }
                @Override public void onDisconnected(CameraDevice c) { cb.onDone(null, "disconnected"); }
                @Override public void onError(CameraDevice c, int code) { cb.onDone(null, "err " + code); }
            };

            if (androidx.core.content.ContextCompat.checkSelfPermission(ctx,
                    android.Manifest.permission.CAMERA)
                    != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                cb.onDone(null, "لا إذن كاميرا");
                return;
            }

            cm.openCamera(camId, stateCb, handler);

        } catch (Exception e) {
            cb.onDone(null, e.getMessage());
        }
    }
}