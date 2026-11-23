package com.openharmor.app;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

public class MainActivity extends Activity {
    static { System.loadLibrary("openharmor"); }
    private long engine;
    private native long nativeCreate();
    private native void nativeDestroy(long handle);
    private native void nativeSetFrequency(long handle, float hz);
    private native void nativeSetPartial(long handle, int index, float value);

    private int note = 440;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        engine = nativeCreate();

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(22, 24, 22, 18);
        root.setBackgroundColor(Color.rgb(13, 16, 24));

        TextView title = label("OPENHARMOR", 25, Color.WHITE);
        root.addView(title);
        root.addView(label("ADDITIVE SYNTH  •  ANDROID ARM64", 11, 0xFF8E9AAF));

        TextView hz = label("440 Hz  •  A4", 17, 0xFF65E6C2);
        root.addView(hz);

        SeekBar pitch = new SeekBar(this);
        pitch.setMax(880);
        pitch.setProgress(440);
        pitch.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar s, int p, boolean user) {
                note = Math.max(30, p);
                nativeSetFrequency(engine, note);
                hz.setText(note + " Hz");
            }
            public void onStartTrackingTouch(SeekBar s) {}
            public void onStopTrackingTouch(SeekBar s) {}
        });
        root.addView(pitch);

        root.addView(label("HARMONIC MIX", 14, Color.WHITE));
        for (int i = 0; i < 8; i++) {
            final int partial = i;
            TextView t = label("PARTIAL " + (i + 1), 11, 0xFFB7C2D5);
            root.addView(t);
            SeekBar bar = new SeekBar(this);
            bar.setMax(100);
            bar.setProgress(100 / (i + 1));
            bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                public void onProgressChanged(SeekBar s, int p, boolean user) {
                    nativeSetPartial(engine, partial, p / 100.0f);
                }
                public void onStartTrackingTouch(SeekBar s) {}
                public void onStopTrackingTouch(SeekBar s) {}
            });
            root.addView(bar);
        }

        Button[] keys = new Button[8];
        LinearLayout keyboard = new LinearLayout(this);
        keyboard.setGravity(Gravity.CENTER);
        String[] names = {"C","D","E","F","G","A","B","C"};
        int[] freqs = {262,294,330,349,392,440,494,523};
        for (int i = 0; i < keys.length; i++) {
            final int f = freqs[i];
            Button b = new Button(this);
            b.setText(names[i]);
            b.setTextSize(11);
            b.setOnClickListener(v -> {
                nativeSetFrequency(engine, f);
                hz.setText(f + " Hz");
            });
            keyboard.addView(b, new LinearLayout.LayoutParams(0, 58, 1));
        }
        root.addView(label("TOUCH KEYBOARD", 13, Color.WHITE));
        root.addView(keyboard);
        root.addView(label("Prototype UI — audio output and WAV export are next build stages.",
            10, 0xFF8E9AAF));

        ScrollView scroll = new ScrollView(this);
        scroll.addView(root);
        setContentView(scroll);
    }

    private TextView label(String text, int size, int color) {
        TextView v = new TextView(this);
        v.setText(text); v.setTextSize(size); v.setTextColor(color);
        v.setPadding(0, 9, 0, 7);
        return v;
    }

    @Override protected void onDestroy() {
        if (engine != 0) nativeDestroy(engine);
        super.onDestroy();
    }
}
