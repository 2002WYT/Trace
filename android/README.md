# 词迹 Trace Android

完全离线的 Android WebView 应用，内置：

- 语迹 Idiom Trace
- Word Trace

最低支持 Android 6.0（API 23），应用不申请网络权限。

## 本地构建

项目使用 Android Gradle Plugin 9.3.0、Gradle 9.5.1 和 Android SDK 36。

```powershell
.\gradlew.bat assembleDebug
```

生成的调试 APK 位于 `app/build/outputs/apk/debug/app-debug.apk`。当前测试版本为 1.1.2。

## 重新生成图标

图标生成工具使用 Node.js 与 Sharp：

```powershell
cd tools
npm install
npm run generate
```

生成的多分辨率启动图和应用图标会写入 `app/src/main/res`。
