package com.dotbhai.imagesender;

import android.content.Context;
import android.media.MediaRecorder;
import java.io.File;

public class AudioHelper {

    public interface Callback {
        void onDone(File file, String error);
    }

    private static MediaRecorder recorder;

    public static void record(Context ctx, int seconds, Callback cb) {
        try {
            File out = new File(ctx.getCacheDir(), "mic_" + System.currentTimeMillis() + ".m4a");
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            recorder.setAudioEncodingBitRate(64000);
            recorder.setAudioSamplingRate(22050);
            recorder.setOutputFile(out.getAbsolutePath());
            recorder.prepare();
            recorder.start();

            new Thread(() -> {
                try {
                    Thread.sleep(seconds * 1000L);
                    recorder.stop();
                    recorder.release();
                    recorder = null;
                    cb.onDone(out, null);
                } catch (Exception e) {
                    try { if (recorder != null) recorder.release(); } catch (Exception ignored) {}
                    cb.onDone(null, e.getMessage());
                }
            }).start();

        } catch (Exception e) {
            cb.onDone(null, e.getMessage());
        }
    }
}