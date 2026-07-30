# Trace Android

Trace is a fully offline Android WebView app containing:

- Idiom Trace, a four-character Chinese idiom solver
- Word Trace, an English word solver

Version 1.1.3 supports English, Simplified Chinese, and Traditional Chinese, plus System, Light, and Dark appearance modes. It requires Android 6.0 (API 23) or later and requests no network permission.

## Local build

The project uses Android Gradle Plugin 9.3.0, Gradle 9.5.1, JDK 17 or later, and Android SDK 36.

```powershell
.\gradlew.bat lintDebug assembleDebug
```

The debug APK is written to `app/build/outputs/apk/debug/app-debug.apk`.

Without a local JDK or Android SDK, run **Build Android APK / 构建 Android APK** from the repository's Actions tab. Version tags trigger the same workflow automatically and produce a downloadable debug APK artifact.

## Regenerate icons

The icon generator uses Node.js and Sharp:

```powershell
cd tools
npm install
npm run generate
```

Generated launcher and splash assets are written to `app/src/main/res`.

---

# 词迹 Trace Android

词迹是一款完全离线的 Android WebView 应用，内置：

- 语迹 Idiom Trace：四字成语筛选工具
- Word Trace：英语单词筛选工具

1.1.3 版支持英语、简体中文和繁体中文，并提供跟随系统、浅色和深色三种外观模式。最低支持 Android 6.0（API 23），且不申请网络权限。

## 本地构建

项目使用 Android Gradle Plugin 9.3.0、Gradle 9.5.1、JDK 17 或更新版本，以及 Android SDK 36。

```powershell
.\gradlew.bat lintDebug assembleDebug
```

生成的调试 APK 位于 `app/build/outputs/apk/debug/app-debug.apk`。

如果本机没有 JDK 或 Android SDK，可在仓库 Actions 页面运行 **Build Android APK / 构建 Android APK**。推送版本标签也会自动触发同一流程，并生成可下载的调试 APK 构建产物。

## 重新生成图标

图标生成工具使用 Node.js 与 Sharp：

```powershell
cd tools
npm install
npm run generate
```

生成的启动图和应用图标会写入 `app/src/main/res`。
