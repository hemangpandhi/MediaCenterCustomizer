package com.android.car.media.customizer;

import android.content.SharedPreferences;
import android.content.om.FabricatedOverlay;
import android.content.om.OverlayManager;
import android.content.om.OverlayManagerTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import androidx.preference.PreferenceFragmentCompat;
import java.util.Map;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TARGET_PACKAGE = "com.android.car.media";
    private static final String OVERLAY_NAME = "dynamic_media_theme";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(getResources().getIdentifier("preferences", "xml", requireContext().getPackageName()), rootKey);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        applyOverlay(sharedPreferences);
    }

    private void applyOverlay(SharedPreferences prefs) {
        FabricatedOverlay overlay = new FabricatedOverlay(OVERLAY_NAME, TARGET_PACKAGE);
        
        Map<String, ?> allEntries = prefs.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value == null) continue;
            
            try {
                if (key.startsWith("bool/")) {
                    boolean b = (Boolean) value;
                    overlay.setResourceValue(key, TypedValue.TYPE_INT_BOOLEAN, b ? 1 : 0, null);
                } else if (key.startsWith("color/")) {
                    String colorStr = (String) value;
                    if (colorStr != null && !colorStr.trim().isEmpty() && colorStr.startsWith("#")) {
                        overlay.setResourceValue(key, TypedValue.TYPE_INT_COLOR_ARGB8, Color.parseColor(colorStr.trim()), null);
                    }
                } else if (key.startsWith("dimen/")) {
                    String dimenStr = (String) value;
                    if (dimenStr != null && !dimenStr.trim().isEmpty()) {
                        float f = Float.parseFloat(dimenStr.trim());
                        overlay.setResourceValue(key, f, TypedValue.COMPLEX_UNIT_DIP, null);
                    }
                } else if (key.startsWith("integer/")) {
                    String intStr = (String) value;
                    if (intStr != null && !intStr.trim().isEmpty()) {
                        int i = Integer.parseInt(intStr.trim());
                        overlay.setResourceValue(key, TypedValue.TYPE_INT_DEC, i, null);
                    }
                } else if (key.startsWith("string/")) {
                    String str = (String) value;
                    if (str != null && !str.trim().isEmpty()) {
                        overlay.setResourceValue(key, TypedValue.TYPE_STRING, str, null);
                    }
                }
            } catch (Exception e) {
                Log.e("CarMediaCustomizer", "Failed to parse preference " + key, e);
            }
        }

        OverlayManager overlayManager = requireContext().getSystemService(OverlayManager.class);
        if (overlayManager != null) {
            OverlayManagerTransaction transaction = new OverlayManagerTransaction.Builder()
                    .registerFabricatedOverlay(overlay)
                    .build();
            overlayManager.commit(transaction);
        }
    }
}
