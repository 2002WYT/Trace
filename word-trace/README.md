<div align="center">

# Word Trace

### A private, fully offline English word solver

Filter candidate words from gray, yellow, and green clues.

[![Offline](https://img.shields.io/badge/runtime-100%25_offline-2f6f55?style=flat-square)](./word-trace-offline-en.html)
[![Words](https://img.shields.io/badge/words-16%2C533-9a6700?style=flat-square)](./word-trace-offline-en.html)
[![Dictionaries](https://img.shields.io/badge/dictionaries-6-656d76?style=flat-square)](./word-trace-offline-en.html)
[![Single file](https://img.shields.io/badge/package-single_HTML-24292f?style=flat-square)](./word-trace-offline-en.html)

[**Open the app**](./word-trace-offline-en.html) · [**Download the ZIP**](./word-trace-offline-en.zip) · [**Short guide**](./README-EN.txt)

</div>

## What it does

Word Trace is a candidate solver for Wordle-style color-clue games. Enter clues from previous guesses and it finds every matching word across six exam dictionaries.

The interface supports English, Simplified Chinese, and Traditional Chinese. It follows the browser language and system light or dark appearance when opened as a standalone page. In the Trace Android app, both are selectable in Settings.

## Main features

| Feature | Behavior |
| --- | --- |
| Exact filtering | Combines fixed, misplaced, and absent letters |
| Duplicate handling | Infers minimum and maximum copies of repeated letters |
| Six dictionaries | CET4, CET6, IELTS, TOEFL, GMAT, and TEM8 |
| Word lengths | Supports 2–18 letters |
| Multiple rows | Combines up to six previous guesses |
| Flexible input | Type a complete word or fill individual tiles |
| Quick exclusions | Mark absent letters with the on-screen keyboard |
| Source labels | Shows each dictionary containing a candidate |
| One-click copy | Click a candidate to copy it |
| Local processing | No input, clue, or history is uploaded |

## Color rules

| Color | Meaning |
| --- | --- |
| Green | Correct letter in the correct position |
| Yellow | Correct letter in the wrong position |
| Gray | Letter is absent, subject to duplicate-letter counts |

When one copy of a repeated letter is colored and another is gray, Word Trace keeps the colored copy and limits the total count instead of excluding the letter entirely.

## Quick start

1. Download [`word-trace-offline-en.html`](./word-trace-offline-en.html) or the [ZIP package](./word-trace-offline-en.zip).
2. Open the HTML file in Chrome, Edge, Firefox, Safari, or another modern browser.
3. Choose a word length and one or more dictionaries.
4. Enter previous guesses and set each tile's color.
5. Select **Find remaining words**.

GitHub displays HTML source instead of running it directly. Download the file or use the [Trace online version](https://2002wyt.github.io/Trace/).

## Dictionary coverage

The merged dataset contains 16,533 unique words from 2 to 18 letters. Words may belong to more than one source.

| Dictionary | Entries |
| --- | ---: |
| CET4 | 4,536 |
| CET6 | 2,972 |
| IELTS | 3,427 |
| TOEFL | 9,212 |
| GMAT | 3,254 |
| TEM8 | 12,408 |

## Privacy and data

- No installation, account, server, cookies, analytics, or trackers.
- No remote fonts, styles, scripts, or network requests.
- The complete vocabulary dataset and solver are embedded in the HTML file.
- The generation workflow references public lists from [`araea/koishi-plugin-wordle-game`](https://github.com/araea/koishi-plugin-wordle-game).

---

# Word Trace · 英语单词筛选

### 完全离线、注重隐私的英语单词筛选工具

根据灰色、黄色和绿色线索筛选候选单词。

[**打开工具**](./word-trace-offline-en.html) · [**下载 ZIP**](./word-trace-offline-en.zip) · [**简明指南**](./README-EN.txt)

## 功能说明

Word Trace 适用于 Wordle 类颜色线索猜词游戏。输入之前猜测得到的线索，即可从六套英语考试词表中找出所有符合条件的单词。

界面支持英语、简体中文和繁体中文。独立网页会跟随浏览器语言和系统深浅色外观；在词迹 Android 应用中，可直接在设置里选择语言和外观。

## 主要功能

| 功能 | 说明 |
| --- | --- |
| 精确筛选 | 组合处理位置正确、位置错误和不存在的字母 |
| 重复字母 | 推导重复字母的最少和最多数量 |
| 六套词表 | CET4、CET6、IELTS、TOEFL、GMAT 和 TEM8 |
| 单词长度 | 支持 2～18 个字母 |
| 多行线索 | 可合并最多六次猜测 |
| 灵活输入 | 可输入完整单词，也可逐格输入字母 |
| 快速排除 | 使用屏幕键盘标记不存在的字母 |
| 来源标签 | 显示候选词所属的每套词表 |
| 一键复制 | 点击候选词即可复制 |
| 本地处理 | 不上传输入、线索或历史记录 |

## 颜色规则

| 颜色 | 含义 |
| --- | --- |
| 绿色 | 字母和位置都正确 |
| 黄色 | 字母正确但位置错误 |
| 灰色 | 字母不存在，但会结合重复字母数量判断 |

当重复字母中一格有颜色、另一格为灰色时，Word Trace 会保留有颜色的字母并限制总数量，而不是错误地把该字母完全排除。

## 快速开始

1. 下载 [`word-trace-offline-en.html`](./word-trace-offline-en.html) 或 [ZIP 压缩包](./word-trace-offline-en.zip)。
2. 使用 Chrome、Edge、Firefox、Safari 或其他现代浏览器打开 HTML 文件。
3. 选择单词长度和至少一套词表。
4. 输入之前猜过的单词，并设置每格颜色。
5. 点击“查找剩余单词”。

GitHub 会显示 HTML 源码，而不是直接运行页面。请下载文件，或使用[词迹在线版](https://2002wyt.github.io/Trace/)。

## 词表范围

合并后的数据包含 16,533 个不重复单词，长度为 2～18 个字母。同一个单词可能属于多套词表。

| 词表 | 条目数 |
| --- | ---: |
| CET4 | 4,536 |
| CET6 | 2,972 |
| IELTS | 3,427 |
| TOEFL | 9,212 |
| GMAT | 3,254 |
| TEM8 | 12,408 |

## 隐私与数据

- 不需要安装、账号、服务器、Cookie、统计服务或跟踪器。
- 不加载远程字体、样式、脚本，也不发起网络请求。
- 完整词表和筛选逻辑均内置在 HTML 文件中。
- 词表生成流程参考了 [`araea/koishi-plugin-wordle-game`](https://github.com/araea/koishi-plugin-wordle-game) 的公开词表。
