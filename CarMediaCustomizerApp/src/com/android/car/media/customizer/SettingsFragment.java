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
        FabricatedOverlay overlayApp = new FabricatedOverlay.Builder(requireContext().getPackageName(), OVERLAY_NAME + "_app", TARGET_PACKAGE).setTargetOverlayable("CarMediaApp").build();
        FabricatedOverlay overlayUi = new FabricatedOverlay.Builder(requireContext().getPackageName(), OVERLAY_NAME + "_ui", TARGET_PACKAGE).setTargetOverlayable("car-ui-lib").build();
        
        Map<String, ?> allEntries = prefs.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value == null) continue;
            
            FabricatedOverlay targetOverlay = key.contains("car_ui_") ? overlayUi : overlayApp;
            try {
                if (key.startsWith("bool/")) {
                    boolean b = (Boolean) value;
                    targetOverlay.setResourceValue(key, TypedValue.TYPE_INT_BOOLEAN, b ? 1 : 0, null);
                } else if (key.startsWith("color/")) {
                    String colorStr = (String) value;
                    if (colorStr != null && !colorStr.trim().isEmpty() && colorStr.startsWith("#")) {
                        targetOverlay.setResourceValue(key, TypedValue.TYPE_INT_COLOR_ARGB8, Color.parseColor(colorStr.trim()), null);
                    }
                } else if (key.startsWith("dimen/")) {
                    String dimenStr = (String) value;
                    if (dimenStr != null && !dimenStr.trim().isEmpty()) {
                        float f = Float.parseFloat(dimenStr.trim());
                        targetOverlay.setResourceValue(key, f, TypedValue.COMPLEX_UNIT_DIP, null);
                    }
                } else if (key.startsWith("integer/")) {
                    String intStr = (String) value;
                    if (intStr != null && !intStr.trim().isEmpty()) {
                        int i = Integer.parseInt(intStr.trim());
                        targetOverlay.setResourceValue(key, TypedValue.TYPE_INT_DEC, i, null);
                    }
                } else if (key.startsWith("string/")) {
                    String str = (String) value;
                    if (str != null && !str.trim().isEmpty()) {
                        targetOverlay.setResourceValue(key, TypedValue.TYPE_STRING, str, null);
                    }
                }
            } catch (Exception e) {
                Log.e("CarMediaCustomizer", "Failed to parse preference " + key, e);
            }
        }

        OverlayManager overlayManager = requireContext().getSystemService(OverlayManager.class);
        if (overlayManager != null) {
            OverlayManagerTransaction transaction = new OverlayManagerTransaction.Builder()
                    .registerFabricatedOverlay(overlayApp)
                    .registerFabricatedOverlay(overlayUi)
                    .setEnabled(overlayApp.getIdentifier(), true)
                    .setEnabled(overlayUi.getIdentifier(), true)
                    .build();
            overlayManager.commit(transaction);
        }
    }

    public void filterPreferences(String query) {
        androidx.preference.PreferenceScreen screen = getPreferenceScreen();
        if (screen == null) return;
        String lowerQuery = query != null ? query.toLowerCase() : "";
        filterRecursively(screen, lowerQuery);
    }

    private boolean filterRecursively(androidx.preference.PreferenceGroup group, String query) {
        boolean groupHasVisibleChild = false;
        for (int i = 0; i < group.getPreferenceCount(); i++) {
            androidx.preference.Preference pref = group.getPreference(i);
            if (pref instanceof androidx.preference.PreferenceGroup) {
                boolean hasVisible = filterRecursively((androidx.preference.PreferenceGroup) pref, query);
                pref.setVisible(hasVisible);
                if (hasVisible) groupHasVisibleChild = true;
            } else {
                boolean match = false;
                if (pref.getTitle() != null && pref.getTitle().toString().toLowerCase().contains(query)) {
                    match = true;
                }
                if (pref.getSummary() != null && pref.getSummary().toString().toLowerCase().contains(query)) {
                    match = true;
                }
                pref.setVisible(match);
                if (match) groupHasVisibleChild = true;
            }
        }
        return groupHasVisibleChild;
    }
}
