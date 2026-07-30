<div align="center">

# Idiom Trace · 语迹

### A private, fully offline Chinese idiom solver

Filter four-character Chinese idioms with character, initial, final, and tone clues.

[![Offline](https://img.shields.io/badge/runtime-100%25_offline-2f6f55?style=flat-square)](./idiom-trace-offline.html)
[![Dictionary](https://img.shields.io/badge/dictionary-29%2C474_idioms-9a6700?style=flat-square)](./idiom-trace-offline.html)
[![Single file](https://img.shields.io/badge/package-single_HTML-24292f?style=flat-square)](./idiom-trace-offline.html)

[**Open the app**](./idiom-trace-offline.html) · [**Download the ZIP**](./idiom-trace-offline.zip)

</div>

## What it does

Idiom Trace is a deduction assistant for four-character Chinese idioms. Describe what is known about each position—the character, pinyin initial, final, or tone—and it narrows a dictionary of 29,474 idioms to the matching candidates.

The interface supports English, Simplified Chinese, and Traditional Chinese. It follows the browser language and system light or dark appearance when opened as a standalone page. In the Trace Android app, both are selectable in Settings.

## Main features

| Feature | Behavior |
| --- | --- |
| Four-tone shortcut | Enter a sequence such as `4212` to fill and lock all four tones |
| Tone-first entry | Use `1`–`4`; use `0` or `5` for neutral tone |
| Pinyin input | Type initials and finals or use the quick panel |
| Global exclusions | Exclude initials or finals from every position |
| Character input | Enter one character per tile or autofill from a complete idiom |
| Color clues | Gray means absent, yellow means wrong position, green means correct position |
| Duplicate handling | Repeated characters and pinyin components are counted correctly |
| Multiple rows | Combine evidence from several guesses |
| Local search | Search and copy without uploading any input |

## Quick start

1. Download and open [`idiom-trace-offline.html`](./idiom-trace-offline.html) in a modern browser.
2. Enter a complete idiom, individual characters, pinyin components, or tones.
3. Click a clue color to cycle through gray, yellow, and green.
4. Use the initial/final exclusion panel for components that cannot appear anywhere.
5. Select **Find remaining idioms**.

Type `v` for `ü` when entering finals; for example, `ve` is normalized to `üe`.

## Privacy and data

- No installation, account, server, cookies, analytics, or network request.
- The complete dictionary and solver are embedded in the HTML file.
- Idiom data is derived from [`pwxcoo/chinese-xinhua`](https://github.com/pwxcoo/chinese-xinhua).

---

# 语迹 · Idiom Trace

### 完全离线、注重隐私的四字成语筛选工具

根据汉字、声母、韵母和声调线索筛选四字成语。

[**打开工具**](./idiom-trace-offline.html) · [**下载 ZIP**](./idiom-trace-offline.zip)

## 功能说明

语迹是一款四字成语推理辅助工具。输入每个位置已知的汉字、拼音声母、韵母或声调，即可从 29,474 条成语中筛出符合条件的候选结果。

界面支持英语、简体中文和繁体中文。独立网页会跟随浏览器语言和系统深浅色外观；在词迹 Android 应用中，可直接在设置里选择语言和外观。

## 主要功能

| 功能 | 说明 |
| --- | --- |
| 四声调快捷输入 | 输入 `4212` 之类的序列，一次填写并锁定四个声调 |
| 声调优先 | 使用 `1`～`4`；`0` 或 `5` 代表轻声 |
| 拼音输入 | 可手动输入声母、韵母，也可使用快捷面板 |
| 全局排除 | 从所有位置统一排除指定声母或韵母 |
| 汉字输入 | 可逐格输入汉字，也可通过完整成语自动补全 |
| 颜色线索 | 灰色表示不存在，黄色表示位置错误，绿色表示位置正确 |
| 重复项处理 | 正确计算重复汉字和重复拼音成分 |
| 多行线索 | 可合并多次猜测得到的信息 |
| 本地筛选 | 无需上传输入即可搜索和复制结果 |

## 快速开始

1. 下载 [`idiom-trace-offline.html`](./idiom-trace-offline.html)，并用现代浏览器打开。
2. 输入完整成语、单个汉字、拼音成分或声调。
3. 点击线索颜色，在灰、黄、绿之间循环。
4. 对确定不会出现的声母或韵母使用全局排除面板。
5. 点击“搜索剩余成语”。

输入韵母时可用 `v` 代替 `ü`，例如 `ve` 会自动转换为 `üe`。

## 隐私与数据

- 不需要安装、账号、服务器、Cookie、统计服务或网络请求。
- 完整题库和筛选逻辑均内置在 HTML 文件中。
- 成语数据来源于 [`pwxcoo/chinese-xinhua`](https://github.com/pwxcoo/chinese-xinhua)。
