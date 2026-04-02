#!/usr/bin/env python3
"""Audit route points and add any missing ones."""
import json, os

base = '/Users/jiayuhan/游戏制作/StardewCraft/src/main/resources/data/stardewcraft/npc'
pts_path = os.path.join(base, 'events/npc_route_points.json')

with open(pts_path) as f:
    pts = json.load(f)

points = set(pts.get("points", {}).keys())

needed = {
    "seedshop_outdoor_door", "seedshop_indoor_entry",
    "abigail_seedshop_indoor_target", "lewis_seedshop_counter",
    "saloon_outdoor_door", "saloon_indoor_entry",
    "abigail_saloon_indoor_stay", "lewis_saloon_indoor_stay",
    "museum_outdoor_door", "museum_indoor_entry",
    "abigail_museum_indoor_stay", "lewis_museum_indoor_stay",
    "clinic_outdoor_door", "clinic_indoor_entry",
    "abigail_clinic_indoor_stay", "lewis_clinic_indoor_stay",
    "sciencehouse_outdoor_door", "sciencehouse_indoor_entry",
    "abigail_sciencehouse_indoor_stay", "lewis_sciencehouse_indoor_stay",
    "manorhouse_outdoor_door", "manorhouse_indoor_entry",
    "lewis_manorhouse_indoor_stay", "lewis_manorhouse_bedroom",
    "blacksmith_outdoor_door", "blacksmith_indoor_entry",
    "lewis_blacksmith_indoor_stay",
    "abigail_town_stay", "abigail_mountain_flute", "abigail_railroad_stay",
    "abigail_forest_stay", "abigail_beach_stay", "abigail_busstop_stay",
    "lewis_towngarden", "lewis_communitycenter_outdoor",
    "lewis_fishshop_outdoor", "lewis_animalshop_outdoor",
    "lewis_beach_outdoor", "lewis_forest_outdoor",
}

missing = needed - points
found = needed & points
print(f"Found {len(found)}/{len(needed)} route points")
if missing:
    print("MISSING:", sorted(missing))
else:
    print("All points present!")
