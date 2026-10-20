import fs from "fs";
import path from "path";
import { fileURLToPath } from "url";
import { spawnSync } from "child_process";

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const root = path.resolve(__dirname, "..");
const mockups = path.join(root, "srs", "mockups");
const pagesDir = path.join(mockups, "pages");
const wireframes = fs.readFileSync(path.join(mockups, "wireframes.html"), "utf8");

const styleMatch = wireframes.match(/<style>([\s\S]*?)<\/style>/);
const style = styleMatch ? styleMatch[1] : "";

const screens = [
  { id: "home", out: "01-home.png" },
  { id: "setup", out: "02-setup.png" },
  { id: "training", out: "03-training.png" },
  { id: "result", out: "04-result.png" },
  { id: "history", out: "05-history-leaderboard.png" },
];

fs.mkdirSync(pagesDir, { recursive: true });

const edgeCandidates = [
  "C:\\Program Files (x86)\\Microsoft\\Edge\\Application\\msedge.exe",
  "C:\\Program Files\\Microsoft\\Edge\\Application\\msedge.exe",
];
const edge = edgeCandidates.find((p) => fs.existsSync(p));
if (!edge) {
  console.error("msedge.exe not found");
  process.exit(1);
}

for (const s of screens) {
  const re = new RegExp(
    `<section class="screen" id="${s.id}">[\\s\\S]*?<\\/section>`,
    "m"
  );
  const section = wireframes.match(re)?.[0];
  if (!section) {
    console.error("section not found:", s.id);
    process.exit(1);
  }
  const html = `<!DOCTYPE html>
<html lang="ru"><head><meta charset="UTF-8"/>
<style>${style}
body{margin:0;background:#e8eef5}
.screen{margin:0 !important;border:none !important}
h1.page-title,.hint{display:none}
</style></head><body>${section}</body></html>`;
  const pagePath = path.join(pagesDir, `${s.id}.html`);
  fs.writeFileSync(pagePath, html, "utf8");

  const outPath = path.join(mockups, s.out);
  const fileUrl = "file:///" + pagePath.replace(/\\/g, "/");
  const args = [
    "--headless=new",
    "--disable-gpu",
    "--hide-scrollbars",
    `--window-size=960,540`,
    `--screenshot=${outPath}`,
    fileUrl,
  ];
  const r = spawnSync(edge, args, { encoding: "utf8" });
  if (r.status !== 0) {
    console.error(r.stderr || r.stdout || "edge failed");
    process.exit(r.status || 1);
  }
  console.log("wrote", outPath);
}
