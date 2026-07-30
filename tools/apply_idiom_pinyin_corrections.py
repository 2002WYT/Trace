#!/usr/bin/env python3
"""Apply the reviewed idiom-pinyin corrections to every source HTML copy."""

from __future__ import annotations

import argparse
import json
import pathlib
import re


REPO_ROOT = pathlib.Path(__file__).resolve().parents[1]
DEFAULT_MANIFEST = (
    REPO_ROOT / "idiom-trace" / "pinyin-corrections.json"
)
DEFAULT_REPORT = (
    REPO_ROOT / "idiom-trace" / "PINYIN_CORRECTIONS.md"
)
HTML_PATHS = (
    REPO_ROOT / "idiom-trace" / "idiom-trace-offline.html",
    REPO_ROOT
    / "android"
    / "app"
    / "src"
    / "main"
    / "assets"
    / "idiom-trace-offline.html",
)
DATA_PATTERN = re.compile(r"var IDIOM_DATA=(.*?);\r?\n")
INITIALS = (
    "zh",
    "ch",
    "sh",
    "b",
    "p",
    "m",
    "f",
    "d",
    "t",
    "n",
    "l",
    "g",
    "k",
    "h",
    "j",
    "q",
    "x",
    "r",
    "z",
    "c",
    "s",
    "y",
    "w",
)
TONE_MARKS = {
    "a": "āáǎà",
    "e": "ēéěè",
    "i": "īíǐì",
    "o": "ōóǒò",
    "u": "ūúǔù",
    "v": "ǖǘǚǜ",
}


def numbered_to_display(syllable: str) -> str:
    match = re.fullmatch(r"([a-zv]+)([1-5])", syllable)
    if not match:
        raise ValueError(f"Invalid numbered pinyin syllable: {syllable}")
    body, tone_text = match.groups()
    tone = int(tone_text)
    if tone == 5:
        return body.replace("v", "ü")

    if "a" in body:
        mark_index = body.index("a")
    elif "e" in body:
        mark_index = body.index("e")
    elif "ou" in body:
        mark_index = body.index("o")
    else:
        vowel_indexes = [
            index
            for index, char in enumerate(body)
            if char in TONE_MARKS
        ]
        if not vowel_indexes:
            raise ValueError(f"Pinyin has no vowel: {syllable}")
        mark_index = vowel_indexes[-1]

    vowel = body[mark_index]
    marked = TONE_MARKS[vowel][tone - 1]
    return (
        body[:mark_index]
        + marked
        + body[mark_index + 1 :]
    ).replace("v", "ü")


def numbered_to_packed(syllable: str) -> str:
    match = re.fullmatch(r"([a-zv]+)([1-5])", syllable)
    if not match:
        raise ValueError(f"Invalid numbered pinyin syllable: {syllable}")
    body, tone_text = match.groups()
    body = body.replace("v", "ü")
    initial = next(
        (item for item in INITIALS if body.startswith(item)),
        "Ø",
    )
    final = body if initial == "Ø" else body[len(initial) :]
    tone = "0" if tone_text == "5" else tone_text
    return f"{initial},{final},{tone}"


def corrected_row(
    row: list[str],
    correction: dict[str, object],
) -> list[str]:
    syllables = correction["new"]
    if not isinstance(syllables, list) or len(syllables) != len(row[0]):
        raise ValueError(
            f"Correction length does not match {row[0]}: {syllables}",
        )
    display = " ".join(numbered_to_display(item) for item in syllables)
    packed = "|".join(numbered_to_packed(item) for item in syllables)
    return [row[0], display, packed]


def apply_to_html(
    html_path: pathlib.Path,
    corrections: dict[str, dict[str, object]],
) -> int:
    text = html_path.read_text(encoding="utf-8")
    match = DATA_PATTERN.search(text)
    if not match:
        raise RuntimeError(f"IDIOM_DATA was not found in {html_path}")
    data = json.loads(match.group(1))
    found: set[str] = set()
    changed = 0
    for index, row in enumerate(data):
        correction = corrections.get(row[0])
        if correction is None:
            continue
        if row[0] in found:
            raise RuntimeError(f"Duplicate idiom in data: {row[0]}")
        found.add(row[0])
        replacement = corrected_row(row, correction)
        if row != replacement:
            data[index] = replacement
            changed += 1

    missing = set(corrections) - found
    if missing:
        sample = "、".join(sorted(missing)[:10])
        raise RuntimeError(
            f"{len(missing)} correction entries were not found: {sample}",
        )

    encoded = json.dumps(
        data,
        ensure_ascii=False,
        separators=(",", ":"),
    )
    updated = text[: match.start(1)] + encoded + text[match.end(1) :]
    html_path.write_text(updated, encoding="utf-8", newline="")
    return changed


def write_report(
    report_path: pathlib.Path,
    manifest: dict[str, object],
) -> None:
    corrections = manifest["corrections"]
    lines = [
        "# Pinyin Corrections / 拼音修正",
        "",
        (
            f"Reviewed entries / 已复核条目：{manifest['reviewedEntries']:,}"
        ),
        "",
        f"Corrected idioms / 修正成语：{len(corrections):,}",
        "",
        (
            "The list records every pinyin string changed in the offline "
            "idiom data. Tone-mark placement fixes are included."
        ),
        "",
        "以下列出离线成语数据中修改过的每一条拼音，"
        "包含声调符号位置和异常字符修正。",
        "",
        "| # | Idiom / 成语 | Before / 修改前 | After / 修改后 |",
        "|---:|---|---|---|",
    ]
    for index, item in enumerate(corrections, start=1):
        lines.append(
            f"| {index} | {item['word']} | "
            f"{item['oldDisplay']} | {item['newDisplay']} |",
        )
    report_path.write_text(
        "\n".join(lines) + "\n",
        encoding="utf-8",
        newline="",
    )


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser()
    parser.add_argument(
        "--manifest",
        type=pathlib.Path,
        default=DEFAULT_MANIFEST,
    )
    parser.add_argument(
        "--report",
        type=pathlib.Path,
        default=DEFAULT_REPORT,
    )
    return parser.parse_args()


def main() -> None:
    args = parse_args()
    manifest = json.loads(args.manifest.read_text(encoding="utf-8"))
    correction_list = manifest["corrections"]
    corrections = {item["word"]: item for item in correction_list}
    if len(corrections) != len(correction_list):
        raise RuntimeError("Correction manifest contains duplicate idioms")

    for html_path in HTML_PATHS:
        changed = apply_to_html(html_path, corrections)
        print(f"{html_path}: {changed} row(s) updated")
    write_report(args.report, manifest)
    print(f"{args.report}: report written")


if __name__ == "__main__":
    main()
