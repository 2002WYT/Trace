import path from "node:path";
import { mkdir } from "node:fs/promises";
import sharp from "sharp";

const [resourceRoot] = process.argv.slice(2);
if (!resourceRoot) {
  throw new Error("Usage: node tools/generate-android-icons.mjs RES_ROOT");
}

const densities = [
  ["mdpi", 1],
  ["hdpi", 1.5],
  ["xhdpi", 2],
  ["xxhdpi", 3],
  ["xxxhdpi", 4],
];

function iconSvg(size) {
  const corner = Math.round(size * 0.22);
  const innerCorner = Math.round(size * 0.14);
  const margin = Math.round(size * 0.11);
  const fontSize = Math.round(size * 0.42);
  return Buffer.from(`
    <svg xmlns="http://www.w3.org/2000/svg" width="${size}" height="${size}" viewBox="0 0 ${size} ${size}">
      <rect width="${size}" height="${size}" rx="${corner}" fill="#f3efe4"/>
      <circle cx="${size * 0.82}" cy="${size * 0.18}" r="${size * 0.31}" fill="#cbe83c" opacity=".55"/>
      <rect x="${margin}" y="${margin}" width="${size - margin * 2}" height="${size - margin * 2}" rx="${innerCorner}" fill="#102d25"/>
      <circle cx="${size * 0.76}" cy="${size * 0.26}" r="${size * 0.045}" fill="#cbe83c"/>
      <text x="50%" y="54%" dominant-baseline="middle" text-anchor="middle" fill="#ffffff"
        font-family="KaiTi, STKaiti, Noto Serif CJK SC, serif" font-size="${fontSize}" font-weight="700">迹</text>
      <path d="M${size * 0.32} ${size * 0.74} H${size * 0.68}" stroke="#cbe83c" stroke-width="${Math.max(4, size * 0.018)}" stroke-linecap="round"/>
    </svg>
  `);
}

for (const [density, scale] of densities) {
  const launcherSize = Math.round(48 * scale);
  const launcherDirectory = path.join(resourceRoot, `mipmap-${density}`);
  await mkdir(launcherDirectory, { recursive: true });
  const launcher = await sharp(iconSvg(launcherSize)).png().toBuffer();
  await sharp(launcher).toFile(path.join(launcherDirectory, "ic_launcher.png"));

  const mask = Buffer.from(
    `<svg width="${launcherSize}" height="${launcherSize}"><circle cx="50%" cy="50%" r="50%" fill="white"/></svg>`,
  );
  await sharp(launcher)
    .composite([{ input: mask, blend: "dest-in" }])
    .png()
    .toFile(path.join(launcherDirectory, "ic_launcher_round.png"));

  const splashSize = Math.round(288 * scale);
  const splashDirectory = path.join(resourceRoot, `drawable-${density}`);
  await mkdir(splashDirectory, { recursive: true });
  await sharp(iconSvg(splashSize))
    .png()
    .toFile(path.join(splashDirectory, "splash_icon.png"));
}
