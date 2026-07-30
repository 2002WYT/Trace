# Publishing a Trace update / 发布词迹更新

This guide covers the repository checks, Android build, Git commit, tag, GitHub Release, APK checksum, GitHub Pages, and repository About checks used for a Trace release.

本文档涵盖词迹发布时需要完成的仓库检查、Android 构建、Git 提交、标签、GitHub Release、APK 校验值、GitHub Pages 和仓库 About 检查。

## English guide

### 1. Prepare the release files

Before building, update and review:

- `android/app/build.gradle`: increment `versionCode` and set `versionName`;
- `android/app/src/main/assets/index.html`: update every fallback version;
- `android/app/src/main/java/io/github/wyt2002/trace/MainActivity.java`: update the fallback version;
- `CHANGELOG.md`: add the release entry and link;
- `docs/releases/vX.Y.Z.md`: add bilingual release notes;
- `README.md` and affected tool documentation;
- standalone HTML files, matching Android assets, and downloadable ZIP packages.

Historical release notes and versioned screenshot filenames should keep their original version numbers.

### 2. Review local changes

Run from the repository root:

```powershell
git status --short
git diff --check
git diff
```

Do not use `git reset --hard`, `git clean -fd`, or a force push to handle unexpected local changes.

### 3. Build and test Android

The project requires JDK 17 or later and Android SDK 36.

```powershell
cd android
.\gradlew.bat lintDebug assembleDebug
```

The generated APK is:

```text
android/app/build/outputs/apk/debug/app-debug.apk
```

Install it on a device and verify the home screen, all three tools, language and appearance settings, offline operation, and the release-specific changes.

### 4. Prepare the APK and checksum

Return to the repository root and replace `1.2.1` with the version being published:

```powershell
cd ..
Copy-Item ".\android\app\build\outputs\apk\debug\app-debug.apk" "..\Trace-v1.2.1.apk"
Get-FileHash -Algorithm SHA256 "..\Trace-v1.2.1.apk"
```

Add the final SHA-256 value to `docs/releases/v1.2.1.md`, then rerun the documentation checks.

### 5. Commit and push

Return to the repository root, review the exact files, and commit intentionally:

```powershell
git status --short
git add README.md CHANGELOG.md GIT_UPLOAD_GUIDE.md android equation-trace docs
git diff --cached --check
git diff --cached
git commit -m "v1.2.1"
git push origin main
```

Adjust the staged paths and commit message when the release affects a different set of files.

### 6. Tag and publish the GitHub Release

Create the tag only after the intended release commit is on `main`:

```powershell
git tag -a v1.2.1 -m "Trace v1.2.1"
git push origin v1.2.1
```

Then create the release and attach the tested APK:

```powershell
gh release create v1.2.1 "..\Trace-v1.2.1.apk" `
  --repo 2002WYT/Trace `
  --verify-tag `
  --title "Trace v1.2.1" `
  --notes-file "docs\releases\v1.2.1.md" `
  --latest
```

Confirm that the release page, APK download, filename, release notes, and checksum are correct.

### 7. Verify automation and repository links

- Confirm **Build Android APK / 构建 Android APK** completed successfully for the tag.
- Confirm **Deploy GitHub Pages / 部署 GitHub Pages** completed successfully for `main`.
- Open the GitHub Pages version and test all three tools.
- Test the direct APK download link in `README.md`.
- Check the repository About website and description if the public URL or project scope changed.

---

## 中文指南

### 1. 准备发布文件

构建前应更新并复核：

- `android/app/build.gradle`：递增 `versionCode`，设置 `versionName`；
- `android/app/src/main/assets/index.html`：更新全部后备版本号；
- `android/app/src/main/java/io/github/wyt2002/trace/MainActivity.java`：更新后备版本号；
- `CHANGELOG.md`：增加新版本记录和链接；
- `docs/releases/vX.Y.Z.md`：增加中英双语发布说明；
- `README.md` 和本次受影响工具的说明；
- 独立 HTML、对应的 Android 内置资源和可下载 ZIP 包。

历史版本说明和带版本号的旧截图文件名应保留原版本号。

### 2. 检查本地修改

在仓库根目录运行：

```powershell
git status --short
git diff --check
git diff
```

遇到意外本地修改时，不要使用 `git reset --hard`、`git clean -fd` 或强制推送。

### 3. 构建并测试 Android

项目需要 JDK 17 或更新版本以及 Android SDK 36。

```powershell
cd android
.\gradlew.bat lintDebug assembleDebug
```

生成的 APK 位于：

```text
android/app/build/outputs/apk/debug/app-debug.apk
```

将 APK 安装到设备，检查首页、三款工具、语言与外观设置、离线运行，以及本版本的具体改动。

### 4. 准备 APK 和校验值

回到仓库根目录；发布其他版本时，请把下面的 `1.2.1` 替换为实际版本号：

```powershell
cd ..
Copy-Item ".\android\app\build\outputs\apk\debug\app-debug.apk" "..\Trace-v1.2.1.apk"
Get-FileHash -Algorithm SHA256 "..\Trace-v1.2.1.apk"
```

把最终 SHA-256 写入 `docs/releases/v1.2.1.md`，然后再次检查文档。

### 5. 提交并推送

回到仓库根目录，核对具体文件后再提交：

```powershell
git status --short
git add README.md CHANGELOG.md GIT_UPLOAD_GUIDE.md android equation-trace docs
git diff --cached --check
git diff --cached
git commit -m "v1.2.1"
git push origin main
```

如果其他版本影响的目录不同，应按实际情况调整暂存路径和提交信息。

### 6. 创建标签并发布 GitHub Release

确认目标提交已经推送到 `main` 后再创建标签：

```powershell
git tag -a v1.2.1 -m "Trace v1.2.1"
git push origin v1.2.1
```

然后创建 Release 并上传已经测试的 APK：

```powershell
gh release create v1.2.1 "..\Trace-v1.2.1.apk" `
  --repo 2002WYT/Trace `
  --verify-tag `
  --title "Trace v1.2.1" `
  --notes-file "docs\releases\v1.2.1.md" `
  --latest
```

最后核对 Release 页面、APK 下载、文件名、发布说明和校验值。

### 7. 检查自动流程与仓库链接

- 确认标签触发的 **Build Android APK / 构建 Android APK** 成功完成。
- 确认 `main` 触发的 **Deploy GitHub Pages / 部署 GitHub Pages** 成功完成。
- 打开 GitHub Pages，测试全部三款工具。
- 测试 `README.md` 中的 APK 直接下载链接。
- 如果公开网址或项目范围有变化，检查仓库 About 中的网站与简介。
