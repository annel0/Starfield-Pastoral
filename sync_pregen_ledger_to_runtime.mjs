import fs from "node:fs";
import path from "node:path";
import { fileURLToPath } from "node:url";

const repoRoot = path.dirname(fileURLToPath(import.meta.url));
const ledgerPath = path.join(repoRoot, "PREGEN_COORDINATE_MIGRATION_LEDGER.md");
const routePointsPath = path.join(repoRoot, "src/main/resources/data/stardewcraft/npc/events/npc_route_points.json");

const ledger = fs.readFileSync(ledgerPath, "utf8");
const routePoints = JSON.parse(fs.readFileSync(routePointsPath, "utf8"));
if (!routePoints.points || typeof routePoints.points !== "object") {
  throw new Error("npc_route_points.json has no points object");
}

let updated = 0;
let added = 0;

for (const row of parseRows(sectionText("## NPC 路线点和基础地点"))) {
  const pointId = row[0];
  if (!pointId || pointId === "npc_route_points_full_file") continue;
  if (!mentions(row[11], "npc_route_points.json")) continue;
  const position = readPosition(row[6], row[7], row[8]);
  if (!position) continue;
  upsertPoint(pointId, position, { indoor: /indoor\s*=\s*true|室内=true/.test(row[12] ?? "") });
}

for (const row of parseRows(sectionText("## 固定建筑门点、室内落点和提示点"))) {
  const pointId = row[0];
  if (!pointId || !pointId.endsWith("_outdoor_door") && pointId !== "fishshop_outer" && pointId !== "communitycenter_outer") {
    continue;
  }
  const position = readPosition(row[6], row[7], row[8]);
  if (!position) continue;
  upsertPoint(pointId, position, { indoor: false });
  for (const alias of outdoorDoorAliases(pointId)) {
    upsertPoint(alias, position, { indoor: false });
  }
}

fs.writeFileSync(routePointsPath, `${JSON.stringify(routePoints, null, 2)}\n`, "utf8");
console.log(`npc_route_points.json synced: ${updated} updated, ${added} added`);

function sectionText(header) {
  const start = ledger.indexOf(header);
  if (start < 0) return "";
  const next = ledger.indexOf("\n## ", start + header.length);
  return next < 0 ? ledger.slice(start) : ledger.slice(start, next);
}

function parseRows(text) {
  return text
    .split(/\r?\n/)
    .filter((line) => line.startsWith("|") && !line.includes("---"))
    .map((line) => line.slice(1, -1).split("|").map((cell) => cell.trim()))
    .filter((row) => row.length > 3 && !isHeaderRow(row));
}

function isHeaderRow(row) {
  return /^(id|点位 id)$/i.test(row[0].replace(/\s+/g, " "));
}

function mentions(value, needle) {
  return (value ?? "").toLowerCase().includes(needle.toLowerCase());
}

function readPosition(xRaw, yRaw, zRaw) {
  const x = readNumberOrRange(xRaw);
  const y = readNumberOrRange(yRaw);
  const z = readNumberOrRange(zRaw);
  if (x == null || y == null || z == null) return null;
  return { x, y, z };
}

function readNumberOrRange(raw) {
  const value = (raw ?? "").trim();
  if (!value || value === "见文件") return null;
  const range = value.match(/^(-?\d+(?:\.\d+)?)\s*\.\.\s*(-?\d+(?:\.\d+)?)$/);
  if (range) {
    return (Number(range[1]) + Number(range[2])) / 2;
  }
  if (!/^-?\d+(?:\.\d+)?$/.test(value)) return null;
  return Number(value);
}

function upsertPoint(pointId, position, options) {
  const existing = routePoints.points[pointId];
  if (existing) {
    existing.x = position.x;
    existing.y = position.y;
    existing.z = position.z;
    if (typeof options.indoor === "boolean") {
      existing.indoor = options.indoor;
    }
    updated++;
    return;
  }
  routePoints.points[pointId] = {
    x: position.x,
    y: position.y,
    z: position.z,
    indoor: !!options.indoor
  };
  added++;
}

function outdoorDoorAliases(pointId) {
  return {
    seedshop_outdoor_door: ["seedshop_outer"],
    riverroad1_outdoor_door: ["joshhouse_outdoor_door"],
    elliott_cabin_outdoor_door: ["elliotthouse_outdoor_door"]
  }[pointId] ?? [];
}