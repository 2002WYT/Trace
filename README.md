<div align="center">

# WORD TRACE

### Turn gray, yellow, and green clues into a smarter next guess.

**A fast, privacy-friendly word puzzle solver for serious vocabulary players.**

<br>

![T](https://img.shields.io/badge/%20-T-6b7470?style=for-the-badge)
![R](https://img.shields.io/badge/%20-R-c99b20?style=for-the-badge)
![A](https://img.shields.io/badge/%20-A-6b7470?style=for-the-badge)
![C](https://img.shields.io/badge/%20-C-18794e?style=for-the-badge)
![E](https://img.shields.io/badge/%20-E-18794e?style=for-the-badge)

<br>

![Offline](https://img.shields.io/badge/MODE-100%25%20OFFLINE-16392f?style=flat-square)
![Words](https://img.shields.io/badge/WORDS-16%2C533-c99b20?style=flat-square)
![Dictionaries](https://img.shields.io/badge/DICTIONARIES-6-68736d?style=flat-square)
![Languages](https://img.shields.io/badge/UI-CHINESE%20%7C%20ENGLISH-16392f?style=flat-square)
![Runtime](https://img.shields.io/badge/OFFLINE%20RUNTIME-ZERO%20DEPENDENCIES-68736d?style=flat-square)


**by 2002WYT**

</div>

---

## What is Word Trace?

Word Trace is a candidate-word solver for Wordle and other color-clue word games. Enter the gray, yellow, and green clues from your previous guesses, and it instantly finds every answer that is still possible.

Its built-in vocabulary covers **CET4, CET6, IELTS, TOEFL, GMAT, and TEM8**. You can use the full web application or download a single-file offline edition that opens directly in any modern browser.

> No blind guessing. No uploaded input. Only words that actually match your clues.

## Highlights

| | Feature | Description |
|:--:|---|---|
| Target | Exact candidate filtering | Combines correct positions, wrong positions, and absent letters |
| Brain | Duplicate-letter aware | Correctly handles clues where one copy is colored and another is gray |
| Books | Six exam dictionaries | CET4, CET6, IELTS, TOEFL, GMAT, and TEM8 |
| Length | 2–18 letters | Supports short answers and unusually long vocabulary |
| Clues | Multiple guess rows | Combine up to six previous guesses |
| Input | Two input methods | Type a complete word or enter letters directly into individual tiles |
| Exclude | Quick exclusions | Mark known-absent letters using the on-screen keyboard |
| Sources | Dictionary labels | See every exam dictionary associated with each result |
| Copy | One-click copy | Click a candidate to copy it back into your game |
| Global | Two interface languages | Full-featured Chinese and English offline editions |
| Private | Local processing | No uploads, analytics, database, or account required |
| Package | Single-file edition | Dictionary, design, and solver are embedded in one HTML file |

## How the colors work

| Color | Meaning | Solver behavior |
|:---:|---|---|
| Green | Correct letter, correct position | Locks the letter to that position |
| Yellow | Correct letter, wrong position | Requires the letter but excludes that position |
| Gray | Letter is not present | Excludes the letter, with duplicate counts handled precisely |

### Duplicate-letter example

Suppose a guess contains several copies of `E`:

- one `E` is yellow;
- the remaining copies are gray.

Word Trace does not exclude `E` completely. It infers that the answer contains **exactly one `E`**. This is one of the most common cases that simpler word filters get wrong.

## Quick start

### Option 1: use the offline edition

No installation is required:

1. Download or clone this repository.
2. Open the [`local`](./local) directory.
3. Double-click `word-trace-offline-en.html`.
4. Open it with Chrome, Edge, Firefox, or another modern browser.

| Edition | HTML | Archive | Guide |
|---|---|---|---|
| English | [Open file](./local/word-trace-offline-en.html) | [Download ZIP](./local/word-trace-offline-en.zip) | [Read the guide](./local/README-EN.txt) |
| Chinese | [Open file](./local/word-trace-offline.html) | [Download ZIP](./local/word-trace-offline.zip) | Included in the ZIP |

> GitHub normally displays HTML source instead of running it. Download the file and open it locally in your browser.

### Option 2: run the full web project

Requirements:

- Node.js `>= 22.13.0`
- pnpm

```bash
pnpm install
pnpm dev
```

Open the local address printed in your terminal.

## Dictionary coverage

The merged dataset contains **16,533 unique words** with lengths from 2 to 18 letters.

| Dictionary | Entries |
|---|---:|
| CET4 | 4,536 |
| CET6 | 2,972 |
| IELTS | 3,427 |
| TOEFL | 9,212 |
| GMAT | 3,254 |
| TEM8 | 12,408 |

Words may appear in more than one exam dictionary, so the sum of source counts is larger than the deduplicated total. Word Trace preserves all matching source labels for every candidate.

## Solver model

Each clue row is compiled into four constraint types:

```text
fixed positions        correct-position letters
forbidden positions    present letters in the wrong position
minimum counts         the fewest required copies of a letter
maximum counts         the most allowed copies of a letter
```

These constraints are combined with quick exclusions and applied to the selected word length and dictionaries. All filtering happens locally in the browser.

## Project structure

```text
word-trace/
|-- app/
|   |-- page.tsx                 # Main interface and interactions
|   |-- globals.css              # Visual system and responsive layout
|   |-- lib/solver.ts            # Color-clue and duplicate-letter logic
|   `-- data/words.ts            # Embedded exam dictionaries
|-- local/
|   |-- word-trace-offline.html
|   `-- word-trace-offline-en.html
|-- scripts/
|   `-- build-offline-html.mjs   # Generates both offline editions
|-- tests/
|   `-- solver-logic.test.ts     # Core solver tests
`-- public/
    `-- og.png                   # Social preview artwork
```

## Build and test

Build the full web application:

```bash
pnpm build
```

Regenerate both offline editions:

```bash
node scripts/build-offline-html.mjs
```

Run the project test suite:

```bash
pnpm test
```

## Privacy by design

The offline editions:

- make no network requests;
- never upload user input;
- use no cookies, analytics, or trackers;
- load no remote fonts, styles, or scripts;
- require no server, account, or database.

They continue to work normally after the device is disconnected from the internet.

## Data and acknowledgements

The exam vocabulary dataset is assembled, merged, and deduplicated from public word lists. Its generation workflow references the public exam lists in [`araea/koishi-plugin-wordle-game`](https://github.com/araea/koishi-plugin-wordle-game).

Thanks to everyone contributing to open vocabulary resources and the wider word-game community.

---

<div align="center">

### If Word Trace helped narrow the answer, consider leaving a star.

**WORD TRACE · OFFLINE EDITION**

**by 2002WYT**

</div>
