# Changelog / 更新日志

Important changes to the Trace Android app and its bundled web tools are documented here. English appears first in every release, followed by the Chinese translation.

本文件记录词迹 Android 应用及其内置网页工具的重要变更。每个版本均为英文在前、中文翻译在后。

## [1.1.3] - 2026-07-30

### Added

- Added English, Simplified Chinese, and Traditional Chinese interfaces to the home screen, settings, About page, Idiom Trace, and Word Trace.
- Added System, Light, and Dark appearance modes in Settings.
- Added native Android status-bar, navigation-bar, and WebView background synchronization for the selected appearance.
- Added a bilingual publishing guide covering Git commits, pushes, tags, GitHub Releases, APK uploads, checksums, Pages, and repository About settings.
- Added an official GitHub Actions workflow that lints the Android project and produces an installable debug APK for tags or manual runs.

### Changed

- Reworked the interface into a compact productivity-tool layout with neutral surfaces, clear borders, 6–8 px radii, system fonts, and one low-saturation green accent.
- Removed decorative gradients, oversized headings, excessive cards, large corner radii, and heavy shadows.
- Applied the same language and appearance behavior to the Android assets and downloadable standalone pages.
- Rewrote the repository README, Android README, tool READMEs, changelog, release notes, and GitHub Actions labels with English first and Chinese second.
- Updated the Android version to 1.1.3 (`versionCode 7`).

### Fixed

- Removed the light flash when switching between the home screen, Idiom Trace, and Word Trace in Dark mode, both with transitions enabled and disabled.

### 新增

- 首页、设置、关于、语迹和 Word Trace 新增英语、简体中文和繁体中文界面。
- 设置中新增跟随系统、浅色和深色三种外观模式。
- Android 状态栏、导航栏和 WebView 背景会跟随所选外观模式。
- 新增中英双语发布指南，覆盖 Git 提交、推送、标签、GitHub Release、APK 上传、校验值、Pages 和仓库 About 设置。
- 新增官方 GitHub Actions 构建流程，可在推送标签或手动运行时检查 Android 工程并生成可安装的调试 APK。

### 改进

- 界面改为紧凑的效率工具布局，使用中性色背景、清晰边界、6～8 像素圆角、系统字体和单一低饱和绿色强调色。
- 移除装饰性渐变、超大标题、过多卡片、大圆角和厚重阴影。
- Android 内置页面与可下载的独立网页统一使用相同的语言和外观逻辑。
- 根目录 README、Android README、工具 README、更新日志、发布说明和 GitHub Actions 标签全部调整为英文在前、中文在后。
- Android 版本更新为 1.1.3（`versionCode 7`）。

### 修复

- 修复深色模式下在首页、语迹和 Word Trace 之间切换时短暂闪过浅色背景的问题，开启或关闭过渡动画时均生效。

## [1.1.2] - 2026-07-29

### Added

- Added toggles for transitions and haptic feedback.
- Added an option to resume the last tool on launch.
- Added top-left back controls to the home, settings, About, Idiom Trace, and Word Trace screens.

### Changed

- Rebuilt the home screen as a compact app-style tool picker that fits both tools in a typical phone viewport.
- Removed repeated offline, account, APK, and entry explanations from the home screen.
- Reorganized headings and tool cards and added lightweight page, menu, and dialog transitions.
- Returned to a native non-fullscreen Android window so system bars reserve their own space.
- Resized the usable area with the soft keyboard and removed custom system-bar handling that could fail on some devices.

### 新增

- 增加过渡动画和触感反馈开关。
- 增加“启动时继续上次工具”选项。
- 首页、设置、关于、猜成语和猜单词界面增加左上角返回按钮。

### 改进

- 首页重构为紧凑的应用式工具选择页，普通手机竖屏首屏内即可看到两个工具。
- 删除首页重复的离线、账号、APK 和入口说明。
- 重新整理标题与工具卡片，并增加轻量的页面、菜单和弹窗过渡动画。
- 改用 Android 原生非全屏窗口，让系统栏保留独立空间。
- 软键盘弹出时同步缩小可操作区域，并移除可能在部分设备启动失败的自定义系统栏逻辑。

## [1.0.2] - 2026-07-29

### Added

- Added a top-right home menu with Settings and About.
- Added Standard, Large, and Largest text-size options.
- Added a keep-screen-on option while solving.
- Saved settings locally and restored them on the next launch.
- Added version, author `2002WYT`, and project links to About.

### Changed

- Added the author credit to the home footer.
- Opened GitHub links in the external browser while keeping the app free of network permission.

### 新增

- 首页右上角增加“设置”和“关于”菜单。
- 增加标准、较大、特大三档文字大小。
- 增加“解题时保持屏幕常亮”设置。
- 设置保存在设备本地，并在下次启动时恢复。
- 关于页面显示版本、作者 `2002WYT` 和项目链接。

### 改进

- 首页底部增加作者署名。
- GitHub 链接使用外部浏览器打开，应用本体仍不申请网络权限。

## [1.0.1] - 2026-07-29

### Fixed

- Fixed clipped third and fourth idiom positions on phones in portrait orientation.
- Fixed horizontal overflow in Word Trace for words with nine or more letters.
- Wrapped 10–18 letter words with at most nine letters per row.
- Regenerated high-resolution launcher assets to improve icon sharpness.

### 修复

- 修复猜成语页面在手机竖屏下第三、第四个字位显示不完整的问题。
- 修复 Word Trace 在单词长度达到 9 个及以上时横向溢出的问题。
- 10～18 个字母会自动换行，每行最多显示 9 个字母。
- 重新生成多档高分辨率启动图与应用图标，改善清晰度。

## [1.0.0] - 2026-07-29

### Added

- Released the first unified Trace Android app.
- Bundled Idiom Trace, Word Trace, their dictionaries, and filtering logic.
- Added a shared home screen and fully offline operation without accounts or network permission.

### 新增

- 发布首个统一的词迹 Android 应用。
- 内置语迹、Word Trace、完整题库与筛选逻辑。
- 增加统一首页，并支持无需账号和网络权限的完全离线运行。

[1.1.3]: https://github.com/2002WYT/Trace/releases/tag/v1.1.3
[1.1.2]: https://github.com/2002WYT/Trace/releases/tag/v1.1.2
[1.0.2]: https://github.com/2002WYT/Trace/releases/tag/v1.0.2
[1.0.1]: https://github.com/2002WYT/Trace/blob/main/CHANGELOG.md#101---2026-07-29
[1.0.0]: https://github.com/2002WYT/Trace/blob/main/CHANGELOG.md#100---2026-07-29
