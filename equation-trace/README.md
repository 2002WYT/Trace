<div align="center">

# Equation Trace · 猜算式

### A fully offline arithmetic-expression solver

Choose an expression length and target result, then filter possible equations with gray, yellow, and green clues.

[**Open the tool**](./equation-trace-offline.html) · [**Download the ZIP**](./equation-trace-offline.zip) · [**Short guide**](./README-QUICKSTART.txt)

</div>

## What it does

Equation Trace finds arithmetic expressions that:

- have the selected character length;
- evaluate to the selected whole-number result;
- match every gray, yellow, and green clue entered by the user.

The solver uses the digits `0–9` and the operators `+`, `−`, `×`, and `÷`. It applies standard operator precedence, rejects leading zeros and division by zero, and handles repeated characters with the same two-pass rule used by Wordle-style games.

The expression length does not include `= result`. For example, `6+2×2 = 10` has an expression length of five.

## Quick start

1. Download [`equation-trace-offline.html`](./equation-trace-offline.html) or the [ZIP package](./equation-trace-offline.zip).
2. Open the HTML file in Chrome, Edge, Firefox, Safari, or another modern browser.
3. Choose an expression length from 3 to 8 characters.
4. Enter the target whole-number result.
5. Enter each full guess and tap its tiles to cycle through gray, yellow, and green.
6. Select **Find equations**.
7. Select any result to copy the complete equation.

## Calculation and clue rules

| Rule | Behavior |
| --- | --- |
| Precedence | Multiplication and division are evaluated before addition and subtraction |
| Associativity | Operators at the same level are evaluated from left to right |
| Numbers | Multi-digit numbers cannot start with zero |
| Division | Division by zero is rejected; fractional intermediate values are supported |
| Green | Correct character in the correct position |
| Yellow | Correct character in a different position |
| Gray | Character is absent after repeated-character counts are applied |
| Candidate limit | The exact match count is calculated; up to 3,000 expressions are retained for display |

## Language, appearance, and privacy

- The interface supports English, Simplified Chinese, and Traditional Chinese.
- The standalone page follows the saved Trace language and the browser or system appearance.
- The Android app uses the language, text size, motion, haptic, and appearance settings selected on the Trace home screen.
- The interface, generator, evaluator, and filtering logic are contained in one HTML file.
- No clues, results, or usage data are uploaded.
- No account, server, remote script, analytics, or network connection is required.

---

# 猜算式 · Equation Trace

### 完全离线的四则运算算式筛选工具

选择算式字符长度和计算结果，再根据灰色、黄色和绿色线索筛选可能的算式。

[**打开工具**](./equation-trace-offline.html) · [**下载 ZIP**](./equation-trace-offline.zip) · [**简明指南**](./README-QUICKSTART.txt)

## 功能说明

猜算式会找出同时满足以下条件的四则运算表达式：

- 字符数量与所选长度一致；
- 运算值与所选整数结果一致；
- 符合用户录入的每一行灰、黄、绿线索。

工具使用数字 `0～9` 和算子 `+`、`−`、`×`、`÷`。计算遵循标准四则运算优先级，不允许前导零或除以零，并使用与 Wordle 类游戏一致的两轮判定方式处理重复字符。

字符长度不包含“`= 结果`”。例如 `6+2×2 = 10` 的算式字符长度是 5。

## 快速开始

1. 下载 [`equation-trace-offline.html`](./equation-trace-offline.html) 或 [ZIP 压缩包](./equation-trace-offline.zip)。
2. 使用 Chrome、Edge、Firefox、Safari 或其他现代浏览器打开 HTML 文件。
3. 选择 3～8 个字符的算式长度。
4. 输入目标整数结果。
5. 输入每次完整猜测，再点击方块，在灰、黄、绿三种颜色之间切换。
6. 点击“查找算式”。
7. 点击任意结果即可复制完整算式。

## 计算与线索规则

| 规则 | 行为 |
| --- | --- |
| 运算优先级 | 先乘除，后加减 |
| 同级运算 | 从左到右计算 |
| 数字格式 | 多位数不能以零开头 |
| 除法 | 不允许除以零；允许中间结果为分数 |
| 绿色 | 字符和位置都正确 |
| 黄色 | 字符存在，但位置不正确 |
| 灰色 | 结合重复字符数量后，该字符不存在 |
| 候选上限 | 精确统计总数，最多保留 3,000 条算式用于显示 |

## 语言、外观与隐私

- 界面支持英语、简体中文和繁体中文。
- 独立网页会读取已保存的词迹语言，并跟随浏览器或系统深浅色外观。
- Android 应用会使用词迹首页中选择的语言、文字大小、动画、触感和外观设置。
- 界面、候选生成器、计算器和筛选逻辑均内置在一个 HTML 文件中。
- 不上传线索、结果或使用数据。
- 不需要账号、服务器、远程脚本、统计服务或网络连接。
