<div align="center">

# 语迹 · Idiom Trace

### A fast, private, and fully offline Chinese idiom solver

用汉字、声母、韵母与声调线索，快速筛选仍然可能的四字成语。

[![Offline](https://img.shields.io/badge/works-100%25_offline-15764b?style=for-the-badge)](./idiom-trace-offline.html)
[![Dictionary](https://img.shields.io/badge/dictionary-29%2C474_idioms-c79819?style=for-the-badge)](./idiom-trace-offline.html)
[![Single file](https://img.shields.io/badge/app-single_HTML-102d25?style=for-the-badge)](./idiom-trace-offline.html)
[![Privacy](https://img.shields.io/badge/privacy-local_only-747b78?style=for-the-badge)](#privacy-first)

[Open the app](./idiom-trace-offline.html) · [Download the ZIP](./idiom-trace-offline.zip) · [中文说明](#中文快速上手)

</div>

---

## What is Idiom Trace?

**Idiom Trace** is a Wordle-style deduction assistant for four-character Chinese idioms. Describe what you already know about each character — its character, initial, final, or tone — and the app instantly narrows a dictionary of **29,474 idioms** to the remaining possibilities.

There is no installation, server, account, or build step. The complete dictionary and search engine live inside one HTML file.

## Why it feels fast

| Feature | What it gives you |
| --- | --- |
| **Tone-first workflow** | Enter `1`–`4` directly; use `0` or `5` for the neutral tone |
| **Quick pinyin panel** | Initials and finals are available as large, convenient buttons |
| **Direct tile input** | Click a tile and enter an individual Chinese character |
| **Automatic pinyin fill** | Enter a known four-character idiom to populate all pinyin clues |
| **Gray / yellow / green clues** | Mark a component as absent, present in another position, or exactly correct |
| **Duplicate-aware filtering** | Repeated characters and repeated pinyin components are counted correctly |
| **Multiple guess rows** | Combine evidence from several attempts |
| **Instant local search** | Browse and copy matching idioms without sending anything online |

## The clue system

| Color | Meaning | Rule |
| :---: | --- | --- |
| ⬛ **Gray** | Not present | Exclude the character or pinyin component |
| 🟨 **Yellow** | Present, wrong position | Keep it, but not in this tile |
| 🟩 **Green** | Correct position | Lock it to this tile |

Every newly entered clue starts as **gray**. Click its color control to cycle:

```text
Gray  →  Yellow  →  Green  →  Gray
```

## Quick start

1. Download [`idiom-trace-offline.html`](./idiom-trace-offline.html).
2. Open it with a modern browser such as Chrome, Edge, or Firefox.
3. Start with the first tone field:
   - `1` = level tone
   - `2` = rising tone
   - `3` = dipping tone
   - `4` = falling tone
   - `0` or `5` = neutral tone
4. Add initials, finals, characters, and their color states.
5. Select **搜索剩余成语** to calculate every remaining candidate.

> Tip: If you already know a complete idiom, type it into the row input. Idiom Trace will automatically fill its four initials, finals, and tones.

## 中文快速上手

1. 下载并双击打开 [`idiom-trace-offline.html`](./idiom-trace-offline.html)。
2. 每个字按照 **声调 → 声母 → 韵母** 的顺序快速录入。
3. 声调栏支持直接输入数字 `1`、`2`、`3`、`4`，输入 `0` 或 `5` 代表轻声。
4. 新线索默认显示为灰色；点击颜色按钮可按 **灰 → 黄 → 绿** 循环。
5. 也可以直接点击汉字方块输入单个汉字，或输入完整四字成语自动补全拼音。
6. 点击 **搜索剩余成语**，查看所有符合当前线索的候选结果。

## Download options

| File | Best for |
| --- | --- |
| [`idiom-trace-offline.html`](./idiom-trace-offline.html) | Open immediately in a browser |
| [`idiom-trace-offline.zip`](./idiom-trace-offline.zip) | Smaller download and easy sharing |

Both downloads contain the same standalone offline app.

## Privacy first

Idiom Trace performs all filtering in your browser:

- no analytics
- no cookies
- no account
- no database
- no network requests
- no uploaded clues or search history

Once downloaded, it continues to work without an internet connection.

## Dictionary and validation

- **29,474** deduplicated four-character idioms
- Idiom data derived from the open-source [`pwxcoo/chinese-xinhua`](https://github.com/pwxcoo/chinese-xinhua) dataset
- Embedded pinyin is split into initial, final, and tone components
- The release was checked with automated interaction tests covering numeric tone input, neutral tone shortcuts, color cycling, per-position updates, idiom autofill, and candidate filtering

## Project philosophy

The app is deliberately simple to keep:

- one file to run
- one click to open
- zero setup
- zero data collection

---

<div align="center">

Made for language learners, puzzle solvers, and lovers of Chinese idioms.

**by 2002WYT**

</div>
