package com.android.car.media.customizer;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import java.io.File;
import java.io.FileWriter;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getResources().getIdentifier("activity_main", "layout", getPackageName()));

        SettingsFragment settingsFragment = new SettingsFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(getResources().getIdentifier("settings_container", "id", getPackageName()), settingsFragment)
                .commit();

        android.widget.EditText searchBar = findViewById(getResources().getIdentifier("search_bar", "id", getPackageName()));
        if (searchBar != null) {
            searchBar.addTextChangedListener(new android.text.TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    settingsFragment.filterPreferences(s.toString());
                }

                @Override
                public void afterTextChanged(android.text.Editable s) {}
            });
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        findViewById(getResources().getIdentifier("btn_sport", "id", getPackageName())).setOnClickListener(v -> {
            prefs.edit()
                .putBoolean("bool/show_persistent_tabs", false)
                .putBoolean("bool/show_mini_playback_controls", false)
                .putString("color/progress_bar_highlight", "#FFFF0000")
                .putString("color/car_ui_toolbar_background_color", "#FF220000")
                .putString("dimen/browse_fragment_top_padding", "0")
                .putString("string/fragment_playback_title", "SPORT MEDIA")
                .apply();
            Toast.makeText(this, "Sport Profile Applied", Toast.LENGTH_SHORT).show();
        });

        findViewById(getResources().getIdentifier("btn_luxury", "id", getPackageName())).setOnClickListener(v -> {
            prefs.edit()
                .putBoolean("bool/show_persistent_tabs", true)
                .putBoolean("bool/show_mini_playback_controls", true)
                .putString("color/progress_bar_highlight", "#FFD700")
                .putString("color/car_ui_toolbar_background_color", "#FF111111")
                .putString("dimen/browse_fragment_top_padding", "32")
                .putString("string/fragment_playback_title", "PREMIUM AUDIO")
                .apply();
            Toast.makeText(this, "Luxury Profile Applied", Toast.LENGTH_SHORT).show();
        });

        findViewById(getResources().getIdentifier("btn_eco", "id", getPackageName())).setOnClickListener(v -> {
            prefs.edit()
                .putBoolean("bool/show_persistent_tabs", true)
                .putBoolean("bool/show_mini_playback_controls", false)
                .putString("color/progress_bar_highlight", "#FF4CAF50")
                .putString("color/car_ui_toolbar_background_color", "#FF002200")
                .putString("dimen/browse_fragment_top_padding", "16")
                .putString("string/fragment_playback_title", "ECO MEDIA")
                .apply();
            Toast.makeText(this, "Eco Profile Applied", Toast.LENGTH_SHORT).show();
        });

        findViewById(getResources().getIdentifier("btn_export", "id", getPackageName())).setOnClickListener(v -> {
            try {
                File dir = new File("/sdcard/Download");
                if (!dir.exists()) dir.mkdirs();
                File file = new File(dir, "Media_RRO_Export.xml");
                FileWriter writer = new FileWriter(file);
                writer.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<resources>\n");
                
                Map<String, ?> entries = prefs.getAll();
                for (Map.Entry<String, ?> entry : entries.entrySet()) {
                    String k = entry.getKey();
                    Object val = entry.getValue();
                    if (val == null) continue;
                    
                    if (k.startsWith("bool/")) {
                        writer.write("    <bool name=\"" + k.replace("bool/", "") + "\">" + val + "</bool>\n");
                    } else if (k.startsWith("color/")) {
                        writer.write("    <color name=\"" + k.replace("color/", "") + "\">" + val + "</color>\n");
                    } else if (k.startsWith("dimen/")) {
                        writer.write("    <dimen name=\"" + k.replace("dimen/", "") + "\">" + val + "dp</dimen>\n");
                    } else if (k.startsWith("integer/")) {
                        writer.write("    <integer name=\"" + k.replace("integer/", "") + "\">" + val + "</integer>\n");
                    } else if (k.startsWith("string/")) {
                        writer.write("    <string name=\"" + k.replace("string/", "") + "\">" + val + "</string>\n");
                    }
                }
                writer.write("</resources>\n");
                writer.close();
                Toast.makeText(this, "Exported to /sdcard/Download/Media_RRO_Export.xml", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(this, "Export failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        findViewById(getResources().getIdentifier("btn_enable_rro", "id", getPackageName())).setOnClickListener(v -> {
            try {
                android.content.om.OverlayManager om = getSystemService(android.content.om.OverlayManager.class);
                if (om != null) {
                    android.content.om.OverlayManagerTransaction transaction = new android.content.om.OverlayManagerTransaction.Builder()
                            .setEnabled(new android.content.om.OverlayIdentifier("com.android.car.media.rro"), true, android.os.UserHandle.myUserId())
                            .build();
                    om.commit(transaction);
                    Toast.makeText(this, "Static Layout RRO Enabled!", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, "Failed to enable RRO: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        findViewById(getResources().getIdentifier("btn_disable_rro", "id", getPackageName())).setOnClickListener(v -> {
            try {
                android.content.om.OverlayManager om = getSystemService(android.content.om.OverlayManager.class);
                if (om != null) {
                    android.content.om.OverlayManagerTransaction transaction = new android.content.om.OverlayManagerTransaction.Builder()
                            .setEnabled(new android.content.om.OverlayIdentifier("com.android.car.media.rro"), false, android.os.UserHandle.myUserId())
                            .build();
                    om.commit(transaction);
                    Toast.makeText(this, "Static Layout RRO Disabled!", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, "Failed to disable RRO: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
