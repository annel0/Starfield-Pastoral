#!/usr/bin/env python3
"""Write npc_route_profiles.json with full indoor routing for Abigail and Lewis."""
import json, os

data = {
  "event_id": "npc_route_profiles",
  "_format_note": "npc_id -> location_name -> [{mode: walk|warp, point: route_point_id}]. walk=pathfind; warp=teleport. Last step is final stay target.",
  "profiles": {
    "abigail": {
      "seedshop": [
        {"mode":"walk","point":"seedshop_outdoor_door"},
        {"mode":"warp","point":"seedshop_indoor_entry"},
        {"mode":"walk","point":"abigail_seedshop_indoor_target"}
      ],
      "pierreshop": [
        {"mode":"walk","point":"seedshop_outdoor_door"},
        {"mode":"warp","point":"seedshop_indoor_entry"},
        {"mode":"walk","point":"abigail_seedshop_indoor_target"}
      ],
      "saloon": [
        {"mode":"walk","point":"saloon_outdoor_door"},
        {"mode":"warp","point":"saloon_indoor_entry"},
        {"mode":"walk","point":"abigail_saloon_indoor_stay"}
      ],
      "archaeologyhouse": [
        {"mode":"walk","point":"museum_outdoor_door"},
        {"mode":"warp","point":"museum_indoor_entry"},
        {"mode":"walk","point":"abigail_museum_indoor_stay"}
      ],
      "museum": [
        {"mode":"walk","point":"museum_outdoor_door"},
        {"mode":"warp","point":"museum_indoor_entry"},
        {"mode":"walk","point":"abigail_museum_indoor_stay"}
      ],
      "hospital": [
        {"mode":"walk","point":"clinic_outdoor_door"},
        {"mode":"warp","point":"clinic_indoor_entry"},
        {"mode":"walk","point":"abigail_clinic_indoor_stay"}
      ],
      "sciencehouse": [
        {"mode":"walk","point":"sciencehouse_outdoor_door"},
        {"mode":"warp","point":"sciencehouse_indoor_entry"},
        {"mode":"walk","point":"abigail_sciencehouse_indoor_stay"}
      ],
      "sebastianroom": [
        {"mode":"walk","point":"sciencehouse_outdoor_door"},
        {"mode":"warp","point":"sciencehouse_indoor_entry"},
        {"mode":"walk","point":"abigail_sciencehouse_indoor_stay"}
      ],
      "carpenter": [
        {"mode":"walk","point":"sciencehouse_outdoor_door"},
        {"mode":"warp","point":"sciencehouse_indoor_entry"},
        {"mode":"walk","point":"abigail_sciencehouse_indoor_stay"}
      ],
      "town":       [{"mode":"walk","point":"abigail_town_stay"}],
      "towngarden": [{"mode":"walk","point":"abigail_town_stay"}],
      "mountain":   [{"mode":"walk","point":"abigail_mountain_flute"}],
      "railroad":   [{"mode":"walk","point":"abigail_railroad_stay"}],
      "forest":     [{"mode":"walk","point":"abigail_forest_stay"}],
      "beach":      [{"mode":"walk","point":"abigail_beach_stay"}],
      "busstop":    [{"mode":"walk","point":"abigail_busstop_stay"}]
    },
    "lewis": {
      "manorhouse": [
        {"mode":"walk","point":"manorhouse_outdoor_door"},
        {"mode":"warp","point":"manorhouse_indoor_entry"},
        {"mode":"walk","point":"lewis_manorhouse_indoor_stay"}
      ],
      "saloon": [
        {"mode":"walk","point":"saloon_outdoor_door"},
        {"mode":"warp","point":"saloon_indoor_entry"},
        {"mode":"walk","point":"lewis_saloon_indoor_stay"}
      ],
      "blacksmith": [
        {"mode":"walk","point":"blacksmith_outdoor_door"},
        {"mode":"warp","point":"blacksmith_indoor_entry"},
        {"mode":"walk","point":"lewis_blacksmith_indoor_stay"}
      ],
      "archaeologyhouse": [
        {"mode":"walk","point":"museum_outdoor_door"},
        {"mode":"warp","point":"museum_indoor_entry"},
        {"mode":"walk","point":"lewis_museum_indoor_stay"}
      ],
      "museum": [
        {"mode":"walk","point":"museum_outdoor_door"},
        {"mode":"warp","point":"museum_indoor_entry"},
        {"mode":"walk","point":"lewis_museum_indoor_stay"}
      ],
      "sciencehouse": [
        {"mode":"walk","point":"sciencehouse_outdoor_door"},
        {"mode":"warp","point":"sciencehouse_indoor_entry"},
        {"mode":"walk","point":"lewis_sciencehouse_indoor_stay"}
      ],
      "carpenter": [
        {"mode":"walk","point":"sciencehouse_outdoor_door"},
        {"mode":"warp","point":"sciencehouse_indoor_entry"},
        {"mode":"walk","point":"lewis_sciencehouse_indoor_stay"}
      ],
      "hospital": [
        {"mode":"walk","point":"clinic_outdoor_door"},
        {"mode":"warp","point":"clinic_indoor_entry"},
        {"mode":"walk","point":"lewis_clinic_indoor_stay"}
      ],
      "seedshop": [
        {"mode":"walk","point":"seedshop_outdoor_door"},
        {"mode":"warp","point":"seedshop_indoor_entry"},
        {"mode":"walk","point":"lewis_seedshop_counter"}
      ],
      "pierreshop": [
        {"mode":"walk","point":"seedshop_outdoor_door"},
        {"mode":"warp","point":"seedshop_indoor_entry"},
        {"mode":"walk","point":"lewis_seedshop_counter"}
      ],
      "town":           [{"mode":"walk","point":"lewis_towngarden"}],
      "towngarden":     [{"mode":"walk","point":"lewis_towngarden"}],
      "communitycenter":[{"mode":"walk","point":"lewis_communitycenter_outdoor"}],
      "fishshop":       [{"mode":"walk","point":"lewis_fishshop_outdoor"}],
      "willyshop":      [{"mode":"walk","point":"lewis_fishshop_outdoor"}],
      "animalshop":     [{"mode":"walk","point":"lewis_animalshop_outdoor"}],
      "marnieranch":    [{"mode":"walk","point":"lewis_animalshop_outdoor"}],
      "beach":          [{"mode":"walk","point":"lewis_beach_outdoor"}],
      "forest":         [{"mode":"walk","point":"lewis_forest_outdoor"}]
    }
  }
}

out = os.path.join(os.path.dirname(__file__), '..', 'src', 'main', 'resources', 'data', 'stardewcraft', 'npc', 'events', 'npc_route_profiles.json')
out = os.path.normpath(out)
with open(out, 'w', encoding='utf-8') as f:
    json.dump(data, f, indent=2, ensure_ascii=False)
print("Written:", out)

# verify
with open(out, encoding='utf-8') as f:
    d = json.load(f)
print("OK - profiles:", list(d['profiles'].keys()))
