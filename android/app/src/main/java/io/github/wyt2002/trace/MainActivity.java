package io.github.wyt2002.trace;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.window.OnBackInvokedCallback;
import android.window.OnBackInvokedDispatcher;

public final class MainActivity extends Activity {
    private static final String HOME_URL = "file:///android_asset/index.html";
    private static final String IDIOM_URL =
            "file:///android_asset/idiom-trace-offline.html";
    private static final String WORD_URL =
            "file:///android_asset/word-trace-offline-en.html";
    private static final String PREFERENCES_NAME = "trace_settings";
    private static final String KEY_TEXT_ZOOM = "text_zoom";
    private static final String KEY_KEEP_SCREEN_ON = "keep_screen_on";
    private static final String KEY_ANIMATIONS_ENABLED = "animations_enabled";
    private static final String KEY_HAPTICS_ENABLED = "haptics_enabled";
    private static final String KEY_RESUME_LAST_TOOL = "resume_last_tool";
    private static final String KEY_LAST_TOOL = "last_tool";
    private static final int DEFAULT_TEXT_ZOOM = 100;
    private static final long PAGE_ANIMATION_DURATION_MS = 220L;
    private WebView webView;
    private WebSettings webSettings;
    private SharedPreferences preferences;
    private OnBackInvokedCallback backCallback;
    private boolean clearHistoryWhenHomeLoads;

    @Override
    @SuppressLint("SetJavaScriptEnabled")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configureSystemBars();

        preferences = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        applyKeepScreenOn(preferences.getBoolean(KEY_KEEP_SCREEN_ON, false));

        webView = new WebView(this);
        webView.setBackgroundColor(Color.rgb(243, 239, 228));
        webView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        webView.setHapticFeedbackEnabled(true);
        setContentView(webView);

        webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(false);
        webSettings.setAllowFileAccessFromFileURLs(false);
        webSettings.setAllowUniversalAccessFromFileURLs(false);
        webSettings.setBlockNetworkLoads(true);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setDisplayZoomControls(false);
        webSettings.setSupportZoom(false);
        webSettings.setMediaPlaybackRequiresUserGesture(true);
        webSettings.setTextZoom(normalizeTextZoom(
                preferences.getInt(KEY_TEXT_ZOOM, DEFAULT_TEXT_ZOOM)
        ));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            webSettings.setSafeBrowsingEnabled(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            webSettings.setForceDark(WebSettings.FORCE_DARK_OFF);
        }

        webView.addJavascriptInterface(new AppBridge(), "TraceApp");
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(
                    WebView view,
                    WebResourceRequest request
            ) {
                return handleNavigation(request.getUrl());
            }

            @Override
            @SuppressWarnings("deprecation")
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return handleNavigation(Uri.parse(url));
            }

            @Override
            public void onPageStarted(
                    WebView view,
                    String url,
                    android.graphics.Bitmap favicon
            ) {
                if (areAnimationsEnabled()) {
                    view.animate().cancel();
                    view.setAlpha(0f);
                } else {
                    view.setAlpha(1f);
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                applyMotionPreference();
                if (clearHistoryWhenHomeLoads && HOME_URL.equals(url)) {
                    view.clearHistory();
                    clearHistoryWhenHomeLoads = false;
                }
                if (areAnimationsEnabled()) {
                    view.animate()
                            .alpha(1f)
                            .setDuration(PAGE_ANIMATION_DURATION_MS)
                            .start();
                } else {
                    view.setAlpha(1f);
                }
            }
        });
        registerPredictiveBack();

        if (savedInstanceState == null) {
            webView.loadUrl(getStartUrl());
        } else {
            webView.restoreState(savedInstanceState);
        }
    }

    private boolean handleNavigation(Uri uri) {
        String scheme = uri.getScheme();
        if ("file".equalsIgnoreCase(scheme) || "about".equalsIgnoreCase(scheme)) {
            return false;
        }
        if ("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme)) {
            startActivity(new Intent(Intent.ACTION_VIEW, uri));
        }
        return true;
    }

    private String getStartUrl() {
        if (!preferences.getBoolean(KEY_RESUME_LAST_TOOL, false)) {
            return HOME_URL;
        }
        String lastTool = preferences.getString(KEY_LAST_TOOL, "");
        if ("idiom".equals(lastTool)) {
            return IDIOM_URL;
        }
        if ("word".equals(lastTool)) {
            return WORD_URL;
        }
        return HOME_URL;
    }

    private boolean isToolUrl(String url) {
        return IDIOM_URL.equals(url) || WORD_URL.equals(url);
    }

    private void navigateHome() {
        if (webView == null || HOME_URL.equals(webView.getUrl())) {
            return;
        }
        clearHistoryWhenHomeLoads = true;
        webView.loadUrl(HOME_URL);
    }

    private boolean areAnimationsEnabled() {
        return preferences.getBoolean(KEY_ANIMATIONS_ENABLED, true);
    }

    private void applyMotionPreference() {
        if (webView == null) {
            return;
        }
        boolean reduceMotion = !areAnimationsEnabled();
        webView.evaluateJavascript(
                "document.documentElement.classList.toggle("
                        + "'trace-reduce-motion',"
                        + reduceMotion
                        + ")",
                null
        );
    }

    private int normalizeTextZoom(int value) {
        if (value <= 107) {
            return 100;
        }
        if (value <= 122) {
            return 115;
        }
        return 130;
    }

    private void applyKeepScreenOn(boolean enabled) {
        if (enabled) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    private boolean isKnownTool(String tool) {
        return "idiom".equals(tool) || "word".equals(tool);
    }

    @SuppressWarnings("deprecation")
    private String getVersionName() {
        try {
            return getPackageManager()
                    .getPackageInfo(getPackageName(), 0)
                    .versionName;
        } catch (android.content.pm.PackageManager.NameNotFoundException exception) {
            return "1.1.2";
        }
    }

    private final class AppBridge {
        @JavascriptInterface
        public int getTextZoom() {
            return normalizeTextZoom(
                    preferences.getInt(KEY_TEXT_ZOOM, DEFAULT_TEXT_ZOOM)
            );
        }

        @JavascriptInterface
        public void setTextZoom(int value) {
            int normalizedValue = normalizeTextZoom(value);
            preferences.edit().putInt(KEY_TEXT_ZOOM, normalizedValue).apply();
            runOnUiThread(() -> {
                if (webSettings != null) {
                    webSettings.setTextZoom(normalizedValue);
                }
            });
        }

        @JavascriptInterface
        public boolean getKeepScreenOn() {
            return preferences.getBoolean(KEY_KEEP_SCREEN_ON, false);
        }

        @JavascriptInterface
        public void setKeepScreenOn(boolean enabled) {
            preferences.edit().putBoolean(KEY_KEEP_SCREEN_ON, enabled).apply();
            runOnUiThread(() -> applyKeepScreenOn(enabled));
        }

        @JavascriptInterface
        public boolean getAnimationsEnabled() {
            return areAnimationsEnabled();
        }

        @JavascriptInterface
        public void setAnimationsEnabled(boolean enabled) {
            preferences.edit().putBoolean(KEY_ANIMATIONS_ENABLED, enabled).apply();
            runOnUiThread(() -> {
                if (webView != null) {
                    webView.animate().cancel();
                    webView.setAlpha(1f);
                    applyMotionPreference();
                }
            });
        }

        @JavascriptInterface
        public boolean getHapticsEnabled() {
            return preferences.getBoolean(KEY_HAPTICS_ENABLED, true);
        }

        @JavascriptInterface
        public void setHapticsEnabled(boolean enabled) {
            preferences.edit().putBoolean(KEY_HAPTICS_ENABLED, enabled).apply();
        }

        @JavascriptInterface
        public void performHaptic() {
            if (!preferences.getBoolean(KEY_HAPTICS_ENABLED, true)) {
                return;
            }
            runOnUiThread(() -> {
                if (webView != null) {
                    webView.performHapticFeedback(
                            HapticFeedbackConstants.CONTEXT_CLICK
                    );
                }
            });
        }

        @JavascriptInterface
        public boolean getResumeLastTool() {
            return preferences.getBoolean(KEY_RESUME_LAST_TOOL, false);
        }

        @JavascriptInterface
        public void setResumeLastTool(boolean enabled) {
            preferences.edit().putBoolean(KEY_RESUME_LAST_TOOL, enabled).apply();
        }

        @JavascriptInterface
        public void setLastTool(String tool) {
            if (isKnownTool(tool)) {
                preferences.edit().putString(KEY_LAST_TOOL, tool).apply();
            }
        }

        @JavascriptInterface
        public void goHome() {
            runOnUiThread(MainActivity.this::navigateHome);
        }

        @JavascriptInterface
        public void exitApp() {
            runOnUiThread(MainActivity.this::finishAfterTransition);
        }

        @JavascriptInterface
        public String getVersionName() {
            return MainActivity.this.getVersionName();
        }
    }

    private void configureSystemBars() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.rgb(16, 45, 37));
        window.setNavigationBarColor(Color.rgb(243, 239, 228));

        int systemUiFlags = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            systemUiFlags |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(true);
        }
        window.getDecorView().setSystemUiVisibility(systemUiFlags);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.setNavigationBarDividerColor(Color.rgb(214, 208, 193));
        }
    }

    private void registerPredictiveBack() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            backCallback = this::navigateBackOrFinish;
            getOnBackInvokedDispatcher().registerOnBackInvokedCallback(
                    OnBackInvokedDispatcher.PRIORITY_DEFAULT,
                    backCallback
            );
        }
    }

    private void navigateBackOrFinish() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else if (webView != null && isToolUrl(webView.getUrl())) {
            navigateHome();
        } else {
            finishAfterTransition();
        }
    }

    @Override
    @SuppressLint("GestureBackNavigation")
    public void onBackPressed() {
        navigateBackOrFinish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (webView != null) {
            webView.saveState(outState);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && backCallback != null) {
            getOnBackInvokedDispatcher().unregisterOnBackInvokedCallback(backCallback);
            backCallback = null;
        }
        if (webView != null) {
            webView.animate().cancel();
            webView.stopLoading();
            webView.removeJavascriptInterface("TraceApp");
            webView.setWebChromeClient(null);
            webView.setWebViewClient(null);
            webView.destroy();
            webView = null;
        }
        webSettings = null;
        super.onDestroy();
    }
}
