# Premium OEM Showroom Customizer Suite

Welcome to the ultimate Android Automotive OS (AAOS) Customization demonstration suite. This project is a hybrid theming engine designed to showcase to OEMs how quickly and deeply the AAOS UI can be re-skinned without modifying core framework code.

This suite is divided into three major architectural components:

1. **CarMediaCustomizerApp**: A Privileged system app that dynamically changes UI primitives in real-time.
2. **Media_RRO**: A Static Runtime Resource Overlay (RRO) for structural layout injections.
3. **SystemFont_RRO**: A global typography engine for system-wide font replacement.

---

## 🚀 1. Deployment Steps (Hardware Setup)

To prepare a target head-unit or emulator for the OEM demonstration, push the compiled APKs to their respective system partitions.

1. **Connect via ADB** to your target vehicle or emulator:
   ```bash
   adb root
   adb remount
   ```

2. **Push the Customizer App** (Dynamic Engine):
   ```bash
   adb push CarMediaCustomizerApp/APK/CarMediaCustomizer.apk /system/priv-app/CarMediaCustomizer/
   ```

3. **Push the Static Layout Engine** (Media_RRO):
   ```bash
   adb push Media_RRO/APK/CarMediaAppRRO.apk /product/overlay/
   ```

4. **Push the Global Typography Engine** (SystemFont_RRO):
   ```bash
   adb push SystemFont_RRO/APK/SystemFont_RRO.apk /product/overlay/
   ```

5. **Reboot the device**:
   ```bash
   adb reboot
   ```

---

## 🎬 2. The Showroom Demonstration Flow

When you sit down with the OEM technical leads, follow this script to demonstrate total UI mastery:

### Step A: The Real-Time Primitive Customizer (Dynamic)
1. Open the **OEM Showroom Configurator** app on the screen.
2. Hit the **"SPORT"**, **"LUXURY"**, or **"ECO"** macro buttons. Watch the colors, spacing, and boolean flags of the Media App change instantly.
3. Explain: *"Using `FabricatedOverlay`, we can change any primitive value (Colors, Paddings, Integers, Booleans) in real-time, instantly reflecting your brand guidelines without a reboot."*

### Step B: The Master Controller (God-Mode Layout Swap)
1. Tell the OEM: *"Dynamic colors are great, but what if you want to physically move the Play button from the bottom to the top? For structural layout changes, we use pre-compiled Static RROs."*
2. Press the **"ENABLE LAYOUT RRO"** button in the Customizer app.
3. Explain: *"Our app just executed an `OverlayManagerTransaction`. It commanded the Android OS to instantly drop the default layout and inject a totally different XML structure (our static RRO), all managed at runtime."*

### Step C: Global Typography
1. Explain: *"Finally, true branding requires custom fonts like 'PorscheSans'. Our SystemFont_RRO targets the core `android` framework (`config_headlineFontFamily`), instantly replacing the typography across the entire vehicle—from the HVAC to the Home Screen."*

---

## 🛠️ 3. Exhaustive Customization Options

This suite exposes over 240+ variables. Here is what you can customize:

### A. Primitive Dynamic Attributes (via CarMediaCustomizerApp)
* **Colors & Tints (Hex Codes)**: `car_ui_toolbar_background_color`, `progress_bar_highlight`, `browse_icon_tint`, `secondary_text_color`, etc.
* **Dimensions (DP/SP)**: `car_ui_padding_1`, `playback_control_margin`, `queue_button_background_size`, `album_art_size`.
* **Booleans (Toggles)**: `show_persistent_tabs`, `show_mini_playback_controls`, `use_custom_branding`.
* **Strings**: `fragment_playback_title`, `queue_empty_message`.

### B. Structural Attributes (via Media_RRO)
* **Layout XMLs**: Complete structural re-arrangement of the view hierarchy (`res/layout/fragment_playback.xml`, `res/layout/browse_grid.xml`).
* **View Types**: Swapping a `LinearLayout` for a `ConstraintLayout`.
* **Anchor Points**: Changing where items are anchored or gravitated on the screen.
* **Binary Assets**: Replacing the actual `.png` or `.svg` icons (e.g., `ic_play`, `ic_pause`).

### C. Global Framework Attributes (via SystemFont_RRO)
* **config_headlineFontFamily**: Changes the default bold/header font across all apps.
* **config_bodyFontFamily**: Changes the default regular/reading font across all apps.
