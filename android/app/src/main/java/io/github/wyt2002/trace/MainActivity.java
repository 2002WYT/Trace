package io.github.wyt2002.trace;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
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
    private static final String PREFERENCES_NAME = "trace_settings";
    private static final String KEY_TEXT_ZOOM = "text_zoom";
    private static final String KEY_KEEP_SCREEN_ON = "keep_screen_on";
    private static final int DEFAULT_TEXT_ZOOM = 100;
    private WebView webView;
    private WebSettings webSettings;
    private SharedPreferences preferences;
    private OnBackInvokedCallback backCallback;

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
        applySystemBarInsets(webView);
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
        });
        registerPredictiveBack();

        if (savedInstanceState == null) {
            webView.loadUrl(HOME_URL);
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

    @SuppressWarnings("deprecation")
    private String getVersionName() {
        try {
            return getPackageManager()
                    .getPackageInfo(getPackageName(), 0)
                    .versionName;
        } catch (android.content.pm.PackageManager.NameNotFoundException exception) {
            return "1.0.2";
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
        public String getVersionName() {
            return MainActivity.this.getVersionName();
        }
    }

    private void configureSystemBars() {
        Window window = getWindow();
        window.setStatusBarColor(Color.rgb(16, 45, 37));
        window.setNavigationBarColor(Color.rgb(243, 239, 228));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            );
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.setNavigationBarDividerColor(Color.rgb(214, 208, 193));
        }
    }

    private void applySystemBarInsets(View view) {
        view.setOnApplyWindowInsetsListener((target, insets) -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                android.graphics.Insets bars = insets.getInsets(
                        WindowInsets.Type.systemBars() | WindowInsets.Type.displayCutout()
                );
                target.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            } else {
                target.setPadding(
                        insets.getSystemWindowInsetLeft(),
                        insets.getSystemWindowInsetTop(),
                        insets.getSystemWindowInsetRight(),
                        insets.getSystemWindowInsetBottom()
                );
            }
            return insets;
        });
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
