# 原版故事事件迁移台账

## 使用规则
- 这是进度台账，不是批量转换结果。
- 每条剧情仍必须单独设计分镜/音效/走位/对白。
- 不从原版 tile/旧坐标推算坐标。
- 可读邮件必须注册，状态用 flags。

## 当前覆盖概览
- 总计: 258
- 已映射: 5
- 待处理: 253

## 项目已有 Cutscene 清单
Cutscene ID | Trigger Type | Location | NPC
--- | --- | --- | ---
clint_furnace_wake_up | wake_up | unknown | N/A
demetrius_cave_choice_wake_up | wake_up | unknown | N/A
forest_sewer_unlock | enter_area | unknown | N/A
gunther_rusty_key_wake_up | wake_up | unknown | N/A
gus_mini_jukebox_wake_up | wake_up | unknown | N/A
leah_sculpture_wake_up | wake_up | unknown | N/A
lewis_cc_tour | enter_area | unknown | N/A
marlon_mine_intro | enter_area | unknown | N/A
pierre_year_one_seed_notice_wake_up | wake_up | unknown | N/A
willy_fishing_rod | enter_area | unknown | N/A
wizard_e112 | manual | unknown | N/A
wizard_intro | manual | unknown | N/A

## 原版 Location 计数
| Location | Count |
| --- | --- |
| Town | 28 |
| Farm | 27 |
| SebastianRoom | 24 |
| Forest | 16 |
| FarmHouse | 14 |
| Mountain | 13 |
| Beach | 12 |
| Saloon | 12 |
| HaleyHouse | 11 |
| LeahHouse | 8 |
| SeedShop | 8 |
| SamHouse | 7 |
| ScienceHouse | 7 |
| Temp | 7 |
| JoshHouse | 6 |
| ElliottHouse | 5 |
| Hospital | 5 |
| AnimalShop | 4 |
| ArchaeologyHouse | 4 |
| BusStop | 4 |
| IslandSouth | 3 |
| Mine | 3 |
| Railroad | 3 |
| Trailer | 3 |
| BathHouse_Pool | 2 |
| ManorHouse | 2 |
| Trailer_Big | 2 |
| WizardHouse | 2 |
| Woods | 2 |
| AbandonedJojaMart | 1 |
| Backwoods | 1 |
| BoatTunnel | 1 |
| CommunityCenter | 1 |
| DesertFestival | 1 |
| FishShop | 1 |
| HarveyRoom | 1 |
| IslandHut | 1 |
| IslandNorth | 1 |
| IslandWest | 1 |
| QiNutRoom | 1 |
| SandyHouse | 1 |
| Sewer | 1 |
| Sunroom | 1 |

## Precondition Token 频率 (Top 30)
| Token | Frequency |
| --- | --- |
| f | 132 |
| t | 98 |
| e | 61 |
| p | 53 |
| o | 52 |
| w | 47 |
| n | 28 |
| O | 28 |
| z | 20 |
| d | 18 |
| A | 15 |
| H | 14 |
| Hl | 11 |
| Hn | 11 |
| y | 8 |
| k | 8 |
| i | 7 |
| j | 7 |
| L | 5 |
| a | 4 |
| m | 4 |
| G | 3 |
| l | 2 |
| h | 2 |
| D | 2 |
| B | 2 |
| F | 2 |
| *n | 1 |
| u | 1 |
| v | 1 |

## Event Command Token 频率 (Top 40)
| Token | Frequency |
| --- | --- |
| pause | 4375 |
| faceDirection | 1643 |
| speak | 1573 |
| move | 1046 |
| playSound | 794 |
| showFrame | 672 |
| positionOffset | 669 |
| emote | 499 |
| viewport | 347 |
| animate | 341 |
| warp | 277 |
| jump | 277 |
| stopAnimation | 253 |
| end | 232 |
| farmer | 182 |
| specificTemporarySprite | 181 |
| playMusic | 175 |
| skippable | 156 |
| textAboveHead | 153 |
| shake | 145 |
| message | 124 |
| speed | 109 |
| globalFade | 106 |
| -1000 | 75 |
| fork | 58 |
| fade | 50 |
| question | 46 |
| advancedMove | 41 |
| doAction | 40 |
| updateMinigame | 35 |
| quickQuestion | 33 |
| continue | 28 |
| startJittering | 27 |
| stopJittering | 27 |
| switchEvent | 27 |
| makeInvisible | 26 |
| glow | 26 |
| 64 | 21 |
| addConversationTopic | 20 |
| changeLocation | 19 |

## 详细事件总账
| ID | 状态 | 目标脚本 | 源文件 | 预条件摘要 | 命令流预览 | 下一步 |
| --- | --- | --- | --- | --- | --- | --- |
| missingBundleComplete | 待处理 | - | AbandonedJojaMart.json |  | communityCenter -2000 farmer skippable pause positionOffset viewport animate pause playSound | - |
| 92 | 待处理 | - | AnimalShop.json | e, i, t | jaunty 13 farmer move removeQuest removeItem friendship skippable emote speed | - |
| 3910674 | 待处理 | - | AnimalShop.json | f | shaneTheme -2000 farmer skippable specificTemporarySprite viewport pause speak pause doAction | - |
| 3910974 | 待处理 | - | AnimalShop.json | f, e, p | shaneTheme -2000 farmer skippable showFrame viewport pause playSound warp move | - |
| 3900074 | 待处理 | - | AnimalShop.json | f, e, p | distantBanjo 27 farmer skippable pause faceDirection pause jump pause move | - |
| 1848481 | 待处理 | - | ArchaeologyHouse.json | f, t, n | breezy 14 Elliott skippable move move move faceDirection speak pause | - |
| mysteryBook | 待处理 | - | ArchaeologyHouse.json |  | pause speak animate pause speak playMusic globalFade viewport pause warp | - |
| romanceBook | 待处理 | - | ArchaeologyHouse.json |  | pause speak animate pause speak playMusic globalFade viewport pause warp | - |
| 0 | 待处理 | - | ArchaeologyHouse.json | n, l | libraryTheme 29 farmer removeQuest addQuest skippable pause showFrame pause showFrame | - |
| 6963327 | 待处理 | - | Backwoods.json | f, O, t | continue -100 farmer addWorldState makeInvisible skippable pause viewport move doAction | - |
| 38 | 待处理 | - | BathHouse_Pool.json | f, t, n | echos -1000 farmer swimming swimming skippable showFrame viewport pause playSound | - |
| pennyHeartbroken | 待处理 | - | BathHouse_Pool.json |  | move faceDirection shake globalFade viewport stopSwimming stopSwimming end | - |
| 13 | 待处理 | - | Beach.json | f, z, t | desolate 35 farmer speed skippable move pause faceDirection move pause | - |
| 20 | 待处理 | - | Beach.json | f, z, p, w | playful -100 farmer showFrame skippable animate viewport move stopAnimation showFrame | - |
| arrogantJosh | 待处理 | - | Beach.json |  | pause faceDirection emote faceDirection speak move speak faceDirection end | - |
| 288847 | 待处理 | - | Beach.json | f, p, w, G | ocean -100 farmer showFrame skippable viewport pause speak move pause | - |
| 29 | 待处理 | - | Beach.json | f, t, w | rain 12 farmer skippable move faceDirection pause speak pause move | - |
| 43 | 待处理 | - | Beach.json | f, w, t, G | ocean -1000 farmer specificTemporarySprite skippable viewport move faceDirection speak move | - |
| NoToElliott | 待处理 | - | Beach.json |  | emote pause faceDirection showFrame pause emote pause speak move pause | - |
| 733330 | 待处理 | - | Beach.json | f, w, t, z, y | ocean -1000 farmer skippable animate specificTemporarySprite viewport move faceDirection speak | - |
| 739330 | 待处理 | - | Beach.json | t, *n | ocean -1000 farmer removeQuest addMailReceived skippable animate viewport move playSound | 核对是否对应 willy_fishing_rod |
| 711130 | 待处理 | - | Beach.json | t, f | ocean 30 farmer skippable addConversationTopic showFrame move showFrame pause speak | - |
| 7771191 | 待处理 | - | Beach.json | t, f, w | moonlightJellies -1000 farmer showFrame addTemporaryActor addLantern positionOffset viewport pause animate | - |
| 3131209 | 待处理 | - | Beach.json | n | ocean -2000 farmer addTemporaryActor addCraftingRecipe skippable specificTemporarySprite animate pause speak | - |
| 9348571 | 待处理 | - | BoatTunnel.json | Hl | ocean 6 farmer skippable pause warp playSound move emote pause | - |
| 60367 | 待处理 | - | BusStop.json | u | none -1000 farmer skippable pause playSound pause viewport move playMusic | - |
| 4081148 | 待处理 | - | BusStop.json | e, t | none -1000 farmer viewport skippable move move proceedPosition warp proceedPosition | - |
| 9581348 | 待处理 | - | BusStop.json | e, t | continue -1000 farmer viewport skippable move move proceedPosition playSound warp | - |
| 520702 | 待处理 | - | BusStop.json | a, t, z, z, z | winter_day_ambient -1000 farmer viewport addQuest skippable move pause emote jump | - |
| Punch | 待处理 | - | CommunityCenter.json |  | pause move showFrame textAboveHead pause speak shake pause move speak | - |
| PlayerKilled | 待处理 | - | DesertFestival.json |  | none -100 farmer pause showItemsLost pause end | - |
| 39 | 待处理 | - | ElliottHouse.json | f, p | 50s 2 farmer skippable move pause faceDirection emote speak pause | - |
| 423502 | 待处理 | - | ElliottHouse.json | f, p | elliottPiano -200 farmer skippable positionOffset animate pause viewport pause stopAnimation | - |
| howLong | 待处理 | - | ElliottHouse.json |  | pause resetVariable speak switchEvent | - |
| elliottPianoJoin | 待处理 | - | ElliottHouse.json |  | pause showFrame animate playSound positionOffset pause positionOffset pause positionOffset pause | - |
| extraHelp | 待处理 | - | ElliottHouse.json |  | pause emote speak faceDirection pause speak animate pause emote pause | - |
| 5 | 待处理 | - | Farm.json | e, v, t | continue 64 farmer pause speak pause end | - |
| 47 | 待处理 | - | Farm.json | f, t, e | continue 64 farmer pause speak pause end | - |
| 2146991 | 待处理 | - | Farm.json | y, H | spring_day_ambient 8 farmer broadcastEvent globalFade viewport pause grandpaCandles pause end | - |
| 55 | 待处理 | - | Farm.json | f, t, z, n | continue 64 farmer pause speak pause end | - |
| 63 | 待处理 | - | Farm.json | y, t, H | continue 64 farmer broadcastEvent skippable pause speak pause speak pause | - |
| 992553 | 已迁移-待复核 | clint_furnace_wake_up.json | Farm.json | t, n | continue 64 farmer skippable pause speak pause speak pause speak | 补剧情设计卡 |
| 900553 | 待处理 | - | Farm.json | t, Hn, A, w | continue 64 farmer skippable pause speak pause faceDirection pause faceDirection | - |
| 992253 | 待处理 | - | Farm.json | t, f, w | continue 64 farmer pause speak pause speak pause pause itemAboveHead | - |
| 65 | 已迁移-待复核 | demetrius_cave_choice_wake_up.json | Farm.json | m, t, H | continue 64 farmer pause speak cave speak end | 补剧情设计卡 |
| 66 | 已迁移-待复核 | gunther_rusty_key_wake_up.json | Farm.json | e, t, H | continue 64 farmer broadcastEvent rustyKey skippable pause speak pause faceDirection | 补剧情设计卡 |
| 690006 | 待处理 | - | Farm.json | n, H | continue 64 farmer broadcastEvent skippable pause move move pause showFrame | - |
| 1590166 | 待处理 | - | Farm.json | m, t, d, w, h, H | continue 64 farmer skippable faceDirection pause animate pause animate pause | - |
| 897405 | 待处理 | - | Farm.json | m, t, d, w, h, H | continue 64 farmer skippable faceDirection pause animate pause animate pause | - |
| 91 | 待处理 | - | Farm.json | f, t | continue 64 farmer pause addQuest skippable speak pause end | - |
| 93 | 待处理 | - | Farm.json | f, t, d | continue 64 farmer pause addQuest skippable speak pause emote pause | - |
| 102 | 待处理 | - | Farm.json | z, z, z, d, y, H | continue 64 farmer broadcastEvent skippable pause speak pause move pause | - |
| 2118991 | 待处理 | - | Farm.json | e, t | continue 64 farmer pause skippable speak pause faceDirection pause faceDirection | - |
| 2128292 | 待处理 | - | Farm.json | e, t, f, D | continue 64 farmer pause skippable speak pause speak pause speak | - |
| 3917587 | 待处理 | - | Farm.json | f, O, t, M, d, y | continue -10000 farmer warp warp faceDirection viewport pause playSound warp | - |
| giveAlexMoney | 待处理 | - | Farm.json |  | pause addMailReceived money emote pause speak pause end | - |
| 3917600 | 待处理 | - | Farm.json | f, O, t | continue -10000 farmer warp warp faceDirection viewport pause playSound warp | - |
| 3911124 | 待处理 | - | Farm.json | f, O, t, w, z, d | continue -10000 farmer warp addConversationTopic warp faceDirection viewport pause playSound | - |
| 3912125 | 待处理 | - | Farm.json | f, O, t, p, U | continue -10000 farmer addConversationTopic addConversationTopic addWorldState skippable warp warp faceDirection | - |
| 992559 | 待处理 | - | Farm.json | t, n, o, w | continue 64 farmer skippable pause speak pause faceDirection pause faceDirection | - |
| 992559 | 待处理 | - | Farm.json | t, n, O, w | continue 64 farmer skippable pause speak pause speak pause speak | - |
| 980558 | 待处理 | - | Farm.json | t, w, f | playful 64 farmer skippable pause speak pause faceDirection pause speak | - |
| 980559 | 待处理 | - | Farm.json | t, w, e, j, !Skill | spring_day_ambient 64 farmer skippable pause speak pause speak pause emote | - |
| 558291 | 待处理 | - | FarmHouse.json | y, H | grandpas_theme -2000 farmer broadcastEvent addTemporaryActor specificTemporarySprite viewport pause speak specificTemporarySprite | - |
| 558292 | 待处理 | - | FarmHouse.json | e, t, H | grandpas_theme -2000 farmer broadcastEvent addTemporaryActor skippable specificTemporarySprite viewport pause speak | - |
| 3917601 | 待处理 | - | FarmHouse.json | f, O, n, A, t, p | EmilyTheme -1000 farmer makeInvisible viewport pause speak pause emote pause | - |
| 3918600 | 待处理 | - | FarmHouse.json | f, O, t, p, L | distantBanjo -1000 farmer addConversationTopic skippable makeInvisible makeInvisible makeInvisible viewport pause | - |
| 3918601 | 待处理 | - | FarmHouse.json | e, O, t, A, p, L | distantBanjo -1000 farmer addConversationTopic skippable makeInvisible animate viewport pause stopAnimation | - |
| 3918602 | 待处理 | - | FarmHouse.json | e, O, t, A, p, L | none -1000 farmer addConversationTopic skippable makeInvisible viewport pause speak pause | - |
| 3918603 | 待处理 | - | FarmHouse.json | e, O, t, A, p, L | distantBanjo -1000 farmer skippable makeInvisible makeInvisible makeInvisible specificTemporarySprite viewport pause | - |
| 3917666 | 待处理 | - | FarmHouse.json | f, O, t, p, d, z | sweet -1000 farmer makeInvisible viewport pause speak pause emote speak | - |
| 3912132 | 待处理 | - | FarmHouse.json | e, O, A, B | spring_day_ambient -1000 farmer fade makeInvisible makeInvisible makeInvisible makeInvisible eyes viewport | - |
| 3917626 | 待处理 | - | FarmHouse.json | f, O, t, p, L | harveys_theme_jazz -1000 farmer skippable ambientLight makeInvisible specificTemporarySprite viewport pause showFrame | - |
| 4325434 | 待处理 | - | FarmHouse.json | f, O, t, p | sweet -1000 farmer animate makeInvisible makeInvisible viewport move pause stopAnimation | - |
| 4324303 | 待处理 | - | FarmHouse.json | e, O, A, l, B | spring_day_ambient -1000 farmer fade makeInvisible makeInvisible eyes positionOffset viewport pause | - |
| 8675611 | 待处理 | - | FarmHouse.json | e, O, A, t, p | continue -1000 farmer makeInvisible addQuest addConversationTopic skippable viewport pause speak | - |
| 9333220 | 待处理 | - | FarmHouse.json | e, O, A, t | distantBanjo -1000 farmer addWorldState addConversationTopic specificTemporarySprite skippable makeInvisible makeInvisible viewport | - |
| 16253595 | 待处理 | - | FishShop.json | j, X | distantBanjo 6 farmer skippable move pause warp playsound pause textAboveHead | - |
| 14 | 待处理 | - | Forest.json | f, z, t, w | spring_day_ambient 100 farmer skippable pause showFrame pause playSound shake screenFlash | - |
| 52 | 待处理 | - | Forest.json | f, p, z | playful -1000 farmer skippable specificTemporarySprite animate viewport move move move | - |
| 54 | 待处理 | - | Forest.json | f, t, z, w | 50s -1000 farmer skippable specificTemporarySprite viewport move speak faceDirection pause | - |
| choseInternet | 待处理 | - | Forest.json |  | pause message pause speak move message pause message emote faceDirection | - |
| noPunch | 待处理 | - | Forest.json |  | pause message speed move move message faceDirection showFrame pause showFrame | - |
| 181928 | 待处理 | - | Forest.json | f, t, w, G | 50s -1000 farmer skippable specificTemporarySprite positionOffset animate viewport move pause | - |
| choseFarming | 待处理 | - | Forest.json |  | pause question fork animate playSound pause faceDirection pause stopAnimation speak | - |
| choseAnimals | 待处理 | - | Forest.json |  | pause animate playSound pause faceDirection pause stopAnimation speak speak speak | - |
| choseMinerals | 待处理 | - | Forest.json |  | pause animate playSound pause faceDirection pause stopAnimation speak speak jump | - |
| fieldTripEnd | 待处理 | - | Forest.json |  | pause emote speak speed move speed move faceDirection jump pause | - |
| eventEnd | 待处理 | - | Forest.json |  | faceDirection emote end | - |
| 318560 | 待处理 | - | Forest.json | j, t, d | communityCenter 94 farmer playMusic skippable move pause speak pause jump | - |
| 611944 | 待处理 | - | Forest.json | f, t | nightTime -1000 farmer skippable addLantern animate viewport move pause speak | - |
| 3910975 | 待处理 | - | Forest.json | f, e, t, w | echos -2000 farmer skippable specificTemporarySprite viewport pause move pause speak | - |
| 3910979 | 待处理 | - | Forest.json | f, f, t, z, z, z, w | playful -2000 farmer skippable specificTemporarySprite move viewport move faceDirection faceDirection | - |
| 3091462 | 待处理 | - | Forest.json | e, O, w, t, A | breezy -1000 farmer skippable specificTemporarySprite animate makeInvisible animate viewport pause | - |
| 11 | 待处理 | - | HaleyHouse.json | f, p, p | 50s 6 farmer skippable pause shake pause speak emote speak | - |
| haleyWontDoIt | 待处理 | - | HaleyHouse.json |  | move move emote faceDirection emote speak pause end | - |
| 12 | 待处理 | - | HaleyHouse.json | f, p | ragtime -1000 farmer skippable showFrame shake viewport pause shake pause | - |
| 15 | 待处理 | - | HaleyHouse.json | f, p | Hospital_Ambient -1000 farmer skippable specificTemporarySprite viewport pause emote move move | - |
| 917409 | 待处理 | - | HaleyHouse.json | e, f, p | none -1000 farmer specificTemporarySprite skippable viewport move move speak pause | - |
| 471942 | 待处理 | - | HaleyHouse.json | f, p | none -1000 farmer skippable showFrame specificTemporarySprite viewport emote pause emote | - |
| 195019 | 待处理 | - | HaleyHouse.json | f, f, f, f, f, f, o, o, o, o, o, o, o, o, o, o, o, o, e, e, e, e, e, e, i, k | playful -1000 farmer pause message pause message move viewport move | - |
| 195012 | 待处理 | - | HaleyHouse.json | f, f, f, f, f, f, o, o, o, o, o, o, o, o, o, o, o, o, e, e, e, e, e, e, k | playful -1000 farmer move viewport move pause startJittering faceDirection animate | - |
| choseToExplain | 待处理 | - | HaleyHouse.json |  | pause textAboveHead pause resetVariable question pause fork textAboveHead textAboveHead speak | - |
| lifestyleChoice | 待处理 | - | HaleyHouse.json |  | pause speak speak pause speak pause faceDirection playMusic speak pause | - |
| 150938 | 待处理 | - | HaleyHouse.json | n | SunRoom -1000 farmer skippable positionOffset showFrame showFrame showFrame hideShadow showFrame | - |
| normal | 待处理 | - | HarveyRoom.json |  | pause speak move pause speak move move move speak move | - |
| 7 | 待处理 | - | Hospital.json | f, p | Hospital_Ambient -1000 farmer skippable changeSprite changePortrait showFrame viewport pause speak | - |
| toldTruth | 待处理 | - | Hospital.json |  | move speak pause emote pause speak pause speak pause emote | - |
| 57 | 待处理 | - | Hospital.json | f, p | sweet -100 farmer skippable doAction viewport move pause faceDirection speak | - |
| 571102 | 待处理 | - | Hospital.json | f | Hospital_Ambient -100 farmer skippable changeLocation showFrame viewport pause speak pause | - |
| PlayerKilled | 待处理 | - | Hospital.json |  | none -100 farmer pause showFrame message pause message viewport pause | - |
| 1039573 | 待处理 | - | IslandHut.json | N, Hl | continue -1000 farmer broadcastEvent skippable specificTemporarySprite addTemporaryActor pause viewport move | - |
| 6497421 | 待处理 | - | IslandNorth.json | e, f, w, t, Hl | none 19 farmer playMusic skippable move pause playSound shake pause | - |
| IslandDepart | 待处理 | - | IslandSouth.json |  | none -1000 farmer playMusic skippable playSound beginSimultaneousCommand viewport globalFadeToClear endSimultaneousCommand | - |
| 6497428 | 待处理 | - | IslandSouth.json | e, f, w, t, Hl | tropical_island_day_ambient -1000 farmer addConversationTopic skippable move move viewport proceedPosition emote | - |
| PlayerKilled | 待处理 | - | IslandSouth.json |  | none -100 farmer pause showFrame message pause message viewport pause | - |
| 6497423 | 待处理 | - | IslandWest.json | e, f, w, t, Hl | sad_kid 79 farmer skippable move pause faceDirection pause faceDirection pause | - |
| 18 | 待处理 | - | JoshHouse.json | f, p | sadpiano 17 farmer skippable emote speak move move move pause | - |
| 19 | 待处理 | - | JoshHouse.json | f, p | jaunty 3 farmer skippable move move emote faceDirection speak emote | - |
| 21 | 待处理 | - | JoshHouse.json | f, p | sadpiano 12 farmer skippable pause speak doAction move move pause | - |
| 2119820 | 待处理 | - | JoshHouse.json | f, p | jaunty -1000 farmer skippable showFrame animate viewport pause stopAnimation pause | - |
| 56 | 待处理 | - | JoshHouse.json | f, p | jaunty 16 farmer skippable move move speak pause speak pause | - |
| 5837189 | 待处理 | - | JoshHouse.json | n | jaunty -1000 farmer skippable animate positionOffset specificTemporarySprite specificTemporarySprite specificTemporarySprite specificTemporarySprite | - |
| 50 | 待处理 | - | LeahHouse.json | f, p | breezy 9 farmer skippable animate pause playSound pause playSound pause | - |
| internet | 待处理 | - | LeahHouse.json |  | speak addMailReceived faceDirection emote pause speak pause speak pause move | - |
| creepySexualPass | 待处理 | - | LeahHouse.json |  | stopMusic faceDirection speed move speed move animate pause playSound playSound | - |
| 51 | 待处理 | - | LeahHouse.json | f, p | sadpiano -1000 farmer skippable showFrame changeMapTile viewport move speak playSound | - |
| 584059 | 待处理 | - | LeahHouse.json | f, p, n | breezy -1000 farmer skippable specificTemporarySprite showFrame positionOffset viewport move move | - |
| angry | 待处理 | - | LeahHouse.json |  | emote faceDirection speak emote speak pause faceDirection pause speak pause | - |
| internet2 | 待处理 | - | LeahHouse.json |  | pause speak emote speak pause globalFade viewport end | - |
| artShowSuggest | 待处理 | - | LeahHouse.json |  | pause speak pause end | - |
| 2123243 | 待处理 | - | ManorHouse.json | e | EmilyTheme -1000 farmer skippable specificTemporarySprite viewport pause speak faceDirection emote | - |
| prizeTicketIntro | 待处理 | - | ManorHouse.json | n | distantBanjo 3 farmer skippable pause warp playSound pause move move | - |
| 901756 | 待处理 | - | Mine.json | f, t, o | Upper_Ambient 18 farmer skippable move move pause animate pause playSound | - |
| 100162 | 待处理 | - | Mine.json | t | MarlonsTheme 21 farmer addQuest skippable pause move pause showFrame pause | - |
| PlayerKilled | 待处理 | - | Mine.json |  | none -100 farmer pause showFrame message pause message viewport pause | - |
| 2 | 待处理 | - | Mountain.json | f, w, t, z | AbigailFlute -1000 farmer skippable ambientLight specificTemporarySprite animate viewport pause move | - |
| 8 | 待处理 | - | Mountain.json | f, w, t | nightTime -1000 farmer skippable bgColor changeToTemporaryMap ambientLight viewport pause playSound | - |
| 26 | 待处理 | - | Mountain.json | f, w, t | nightTime -1000 farmer skippable specificTemporarySprite viewport move faceDirection emote speak | - |
| BadAnswer | 待处理 | - | Mountain.json |  | speak faceDirection emote pause faceDirection speak globalFade viewport changeLocation end | - |
| 384883 | 待处理 | - | Mountain.json | f, t | spring_day_ambient -1000 farmer skippable specificTemporarySprite showFrame animate positionOffset viewport move | - |
| 384882 | 待处理 | - | Mountain.json | f, t | nightTime -1000 farmer skippable specificTemporarySprite viewport pause move speak pause | - |
| 404798 | 待处理 | - | Mountain.json | Hn | spring_day_ambient -1000 farmer skippable specificTemporarySprite viewport emote move move faceDirection | - |
| 371652 | 待处理 | - | Mountain.json | f, w, t, a | sweet 18 farmer skippable animate pause doAction pause doAction pause | - |
| linusWell | 待处理 | - | Mountain.json |  | pause friendship move move faceDirection faceDirection playMusic speak move faceDirection | - |
| 5183338 | 待处理 | - | Mountain.json | e, O, w, t | nightTime -1000 farmer skippable changeToTemporaryMap bgColor ambientLight viewport pause move | - |
| 9333219 | 待处理 | - | Mountain.json | O, f, w, t | rain 48 farmer makeInvisible addConversationTopic skippable move faceDirection speak emote | - |
| 8357109 | 待处理 | - | Mountain.json | w, t, n | spring_day_ambient -1000 farmer skippable hideShadow showFrame specificTemporarySprite viewport pause speak | - |
| 8959199 | 待处理 | - | Mountain.json | w, t, e, f, F | sad_kid -1000 farmer skippable specificTemporarySprite animate viewport viewport pause textAboveHead | - |
| 10040609 | 待处理 | - | QiNutRoom.json |  | clubloop 7 farmer skippable move pause speak pause faceDirection pause | - |
| 528052 | 待处理 | - | Railroad.json | f, t, n | playful 26 Harvey skippable move faceDirection speak faceDirection jump pause | - |
| afraid | 待处理 | - | Railroad.json |  | pause speak pause move faceDirection move move jump move move | - |
| 529952 | 待处理 | - | Railroad.json | C | WizardSong 54 Wizard addQuest skippable move move faceDirection move pause | - |
| 40 | 待处理 | - | Saloon.json | f, p, t | playful 11 farmer skippable move faceDirection move faceDirection move faceDirection | - |
| 96 | 待处理 | - | Saloon.json | f, f, p | jaunty 10 farmer skippable pause emote pause playSound warp pause | - |
| 97 | 待处理 | - | Saloon.json | f, t, d | none 4 farmer skippable move emote pause move move pause | - |
| 911526 | 待处理 | - | Saloon.json | f, t, n | gusviolin -1000 farmer skippable showFrame showFrame positionOffset positionOffset animate viewport | - |
| rejectJosh | 待处理 | - | Saloon.json |  | pause playMusic shake emote speak pause speak viewport globalFade viewport | - |
| 195099 | 待处理 | - | Saloon.json | f, f, f, f, f, f, o, o, o, o, o, o, o, o, o, o, o, o, e, e, e, e, e, e, i, k | playful -1000 farmer animate pause message pause message move viewport | - |
| 195013 | 待处理 | - | Saloon.json | f, f, f, f, f, f, o, o, o, o, o, o, o, o, o, o, o, o, e, e, e, e, e, e, k | playful -1000 farmer animate move viewport move pause startJittering faceDirection | - |
| choseToExplain | 待处理 | - | Saloon.json |  | pause textAboveHead pause resetVariable question pause fork textAboveHead textAboveHead speak | - |
| crying | 待处理 | - | Saloon.json |  | pause faceDirection emote pause emote emote emote emote emote emote | - |
| wewereworried | 待处理 | - | Saloon.json |  | emote faceDirection pause faceDirection emote speak pause emote emote pause | - |
| 3917590 | 待处理 | - | Saloon.json | n, O | distantBanjo -100 farmer skippable specificTemporarySprite viewport move pause textAboveHead pause | - |
| 3206194 | 待处理 | - | Saloon.json | n | distantBanjo -1000 farmer skippable specificTemporarySprite viewport pause speak pause speak | - |
| 95 | 待处理 | - | SamHouse.json | e, k, t, i, y | 50s 6 farmer removeItem removeQuest pause faceDirection speak move speed | - |
| 94 | 待处理 | - | SamHouse.json | e, k, t, i, y | 50s 6 farmer removeItem removeQuest speed move speak move move | - |
| 44 | 待处理 | - | SamHouse.json | f, p | sampractice -1000 farmer positionOffset skippable animate showFrame viewport move pause | - |
| 46 | 待处理 | - | SamHouse.json | f, p | playful 7 farmer skippable move faceDirection pause speak pause speak | - |
| 100 | 待处理 | - | SamHouse.json | f, p, p | none 6 farmer skippable playSound move playSound move playSound move | - |
| stayPut | 待处理 | - | SamHouse.json |  | playMusic pause move pause move positionOffset positionOffset pause positionOffset positionOffset | - |
| rejectSam | 待处理 | - | SamHouse.json |  | pause move faceDirection pause showFrame emote speak pause faceDirection speak | - |
| 67 | 待处理 | - | SandyHouse.json | m | none 4 farmer skippable move emote speak speed move speed | - |
| 6 | 待处理 | - | ScienceHouse.json | f, p | breezy 20 farmer skippable move faceDirection faceDirection speak move speak | - |
| DadWeird | 待处理 | - | ScienceHouse.json |  | speak pause faceDirection emote speak faceDirection pause emote move speak | - |
| 9 | 待处理 | - | ScienceHouse.json | f, p | musicboxsong -500 farmer skippable pause animate viewport move move animate | - |
| 25 | 待处理 | - | ScienceHouse.json | f, p, p | jaunty -1000 farmer skippable showFrame viewport pause speak emote speak | - |
| 33 | 待处理 | - | ScienceHouse.json | f, p | jaunty 5 farmer skippable move move move pause faceDirection speak | - |
| 10 | 待处理 | - | ScienceHouse.json | f, t | none -1000 farmer skippable showFrame viewport move pause speak pause | - |
| 1053978 | 待处理 | - | ScienceHouse.json | n | distantBanjo -1000 farmer skippable specificTemporarySprite positionOffset viewport pause speak pause | - |
| 2794460 | 待处理 | - | SebastianRoom.json | f, p | Hospital_Ambient -1000 farmer skippable doAction removeTile removeTile playSound showFrame animate | - |
| didntLeave | 待处理 | - | SebastianRoom.json |  | pause stopAnimation speak switchEvent | - |
| sebastianRoom | 待处理 | - | SebastianRoom.json |  | resetVariable move playSound move playSound move playSound move pause faceDirection | - |
| decor | 待处理 | - | SebastianRoom.json |  | pause stopAnimation speak switchEvent | - |
| enterRobin | 待处理 | - | SebastianRoom.json |  | pause playSound pause playSound pause playSound pause emote speak pause | - |
| noFriends | 待处理 | - | SebastianRoom.json |  | pause emote speak pause playSound pause playSound pause emote speak | - |
| 27 | 待处理 | - | SebastianRoom.json | f, p | jaunty -1000 farmer skippable showFrame showFrame doAction viewport move pause | - |
| warrior | 待处理 | - | SebastianRoom.json |  | pause speak emote speak addMailReceived switchEvent | - |
| healer | 待处理 | - | SebastianRoom.json |  | pause speak speak addMailReceived switchEvent | - |
| opening | 待处理 | - | SebastianRoom.json |  | pause speak globalFade viewport playMusic cutscene pause message pause message | - |
| swungWeapons | 待处理 | - | SebastianRoom.json |  | pause message playSound updateMinigame pause message pause playSound pause switchEvent | - |
| ranAway | 待处理 | - | SebastianRoom.json |  | pause message pause switchEvent | - |
| backEntrance | 待处理 | - | SebastianRoom.json |  | pause playSound pause playSound pause playSound pause updateMinigame pause message | - |
| sewer | 待处理 | - | SebastianRoom.json |  | updateMinigame resetVariable playMusic pause message pause question fork switchEvent | - |
| podRoom | 待处理 | - | SebastianRoom.json |  | pause updateMinigame resetVariable playMusic pause message pause message pause question | - |
| wizardDoor | 待处理 | - | SebastianRoom.json |  | pause updateMinigame resetVariable playMusic pause message pause message pause playSound | - |
| Necromancer | 待处理 | - | SebastianRoom.json |  | pause updateMinigame resetVariable playMusic pause message pause message pause message | - |
| healedSam | 待处理 | - | SebastianRoom.json |  | playSound pause speak speak pause playMusic message pause playSound pause | - |
| finalBossWizard | 待处理 | - | SebastianRoom.json |  | pause resetVariable message pause question fork pause playSound message pause | - |
| castBeam | 待处理 | - | SebastianRoom.json |  | pause message pause playSound pause playSound updateMinigame pause message pause | - |
| chargeAhead | 待处理 | - | SebastianRoom.json |  | pause message pause playSound pause playSound updateMinigame pause message pause | - |
| finalBossWarrior | 待处理 | - | SebastianRoom.json |  | pause resetVariable message pause question fork pause message pause playSound | - |
| leave | 待处理 | - | SebastianRoom.json |  | pause message pause playSound pause playSound pause playSound pause switchEvent | - |
| end | 待处理 | - | SebastianRoom.json |  | playMusic pause viewport pause showFrame speak emote pause speak globalFade | - |
| 1 | 待处理 | - | SeedShop.json | f, p, d | none -9999 farmer skippable showFrame specificTemporarySprite viewport pause playSound pause | - |
| beatGame | 待处理 | - | SeedShop.json |  | playMusic speak pause emote pause end | - |
| 3 | 待处理 | - | SeedShop.json | f, p, t, n | echos -1000 farmer skippable specificTemporarySprite viewport speak move move faceDirection | - |
| 16 | 待处理 | - | SeedShop.json | f, p | Hospital_Ambient 20 farmer skippable doAction move move move pause faceDirection | - |
| 17 | 待处理 | - | SeedShop.json | f, p, p | 50s 35 farmer skippable move pause faceDirection speak pause speak | - |
| 58 | 待处理 | - | SeedShop.json | f, t | aerobics -1000 farmer skippable showFrame viewport pause faceDirection pause screenFlash | - |
| 3102768 | 待处理 | - | SeedShop.json | j, H | playful 6 farmer broadcastEvent skippable move move move move pause | - |
| 963113 | 待处理 | - | SeedShop.json | n | distantBanjo 5 farmer hostMail skippable pause speak speak speak speak | - |
| 691039 | 待处理 | - | Sewer.json | f, n, d | Overcast 16 farmer skippable pause emote speak move emote playSound | - |
| 719926 | 待处理 | - | Sunroom.json | t, w | spring_day_ambient -885 farmer mail skippable specificTemporarySprite viewport pause showFrame doAction | - |
| decorate | 待处理 | - | Temp.json |  | speak faceDirection speak faceDirection faceDirection showFrame pause end | - |
| leave | 待处理 | - | Temp.json |  | speak pause move faceDirection move move playSound warp pause showFrame | - |
| tooBold | 待处理 | - | Temp.json |  | showFrame playMusic positionOffset positionOffset showFrame jump pause faceDirection pause end | - |
| poppy | 待处理 | - | Temp.json |  | pause speak speak playMusic animate pause animate animate animate animate | - |
| heavy | 待处理 | - | Temp.json |  | pause speak speak playMusic animate pause animate animate animate animate | - |
| techno | 待处理 | - | Temp.json |  | pause speak speak playMusic faceDirection pause animate pause animate animate | - |
| honkytonk | 待处理 | - | Temp.json |  | pause speak speak playMusic pause pause animate pause animate animate | - |
| 4 | 待处理 | - | Town.json | f, t, w | nightTime -4442 farmer skippable addLantern specificTemporarySprite faceDirection animate viewport pause | - |
| 831125 | 待处理 | - | Town.json | f, f, f, t, w | spring_day_ambient -100 farmer skippable showFrame move viewport pause speak faceDirection | - |
| 2481135 | 待处理 | - | Town.json | f, t | sweet -1000 farmer skippable specificTemporarySprite viewport pause speak pause speak | - |
| didntHear | 待处理 | - | Town.json |  | pause pause faceDirection speak faceDirection pause showFrame pause playMusic emote | - |
| 34 | 待处理 | - | Town.json | f, t, w | 50s 49 farmer skippable pause emote pause speak move move | - |
| 45 | 待处理 | - | Town.json | f, t, w | breezy -1000 farmer skippable showFrame move viewport specificTemporarySprite null pause | - |
| 53 | 待处理 | - | Town.json | e, t | none -100 farmer specificTemporarySprite skippable animate viewport move faceDirection faceDirection | - |
| 639373 | 待处理 | - | Town.json | f, f, t, w | nightTime 68 farmer skippable pause faceDirection pause emote speak pause | - |
| 101 | 待处理 | - | Town.json | f, e, k, k, o, t, a, !D | jaunty 11 farmer move skippable emote faceDirection emote speak pause | - |
| 233104 | 待处理 | - | Town.json | f, t, w, n | nightTime -1000 farmer viewport skippable move move pause speak faceDirection | - |
| 611439 | 已迁移-待复核 | lewis_cc_tour.json | Town.json | j, t, w, a, H | distantBanjo 53 farmer broadcastEvent pause addMailReceived addQuest skippable move emote | 补剧情设计卡 |
| 191393 | 待处理 | - | Town.json | Hn, Hn, Hn, Hn, Hn, Hn, Hl, Hl, Hl, Hl, Hl, Hl, w, H | junimoStarSong -1000 farmer broadcastEvent addConversationTopic specificTemporarySprite animate animate viewport viewport | - |
| 502261 | 待处理 | - | Town.json | J, w, H | sweet -1000 farmer broadcastEvent addTemporaryActor pause speak specificTemporarySprite viewport viewport | - |
| 502969 | 待处理 | - | Town.json | w, f, j, t | nightTime -1000 farmer skippable specificTemporarySprite pause viewport pause move pause | - |
| 463391 | 待处理 | - | Town.json | f, e, w, z, t | EmilyTheme 22 farmer skippable pause playSound warp move pause speak | - |
| 611173 | 待处理 | - | Town.json | Hn, o | 50s 71 farmer skippable move faceDirection speak move faceDirection emote | - |
| choseToBeKnown | 待处理 | - | Town.json |  | pause addConversationTopic speak pause move move move move move faceDirection | - |
| itsagift | 待处理 | - | Town.json |  | pause pause faceDirection showFrame pause faceDirection speak pause speak pause | - |
| 611173 | 待处理 | - | Town.json | Hn | 50s 71 farmer skippable move faceDirection speak move faceDirection emote | - |
| choseToBeKnown_pennySpouse | 待处理 | - | Town.json |  | pause addConversationTopic speak pause move move move move move faceDirection | - |
| itsagift_pennySpouse | 待处理 | - | Town.json |  | pause pause faceDirection showFrame pause faceDirection speak pause speak pause | - |
| 3917584 | 待处理 | - | Town.json | f, O, d, t | distantBanjo 45 farmer addConversationTopic skippable move warp playSound faceDirection faceDirection | - |
| 3917585 | 待处理 | - | Town.json | e, O, A | distantBanjo 45 farmer addConversationTopic skippable move warp playSound move jump | - |
| 3917586 | 待处理 | - | Town.json | e, O, A | spring_day_ambient 45 farmer addWorldState skippable emote move pause speak pause | - |
| 3917589 | 待处理 | - | Town.json | n, O, d, d, d, d, d, d | continue -1000 farmer addWorldState addMailReceived message end | - |
| 6184643 | 待处理 | - | Town.json | f, O, t, w | sweet 20 farmer addConversationTopic skippable move move emote emote faceDirection | - |
| 6184644 | 待处理 | - | Town.json | e, O, A, t, w, i | ragtime -100 farmer removeQuest removeItem specificTemporarySprite beginSimultaneousCommand advancedMove advancedMove advancedMove | - |
| 15389722 | 待处理 | - | Town.json | j | springtown 62 farmer skippable move animate pause playSound pause playSound | - |
| 35 | 待处理 | - | Trailer.json | f, p | 50s -1000 farmer skippable specificTemporarySprite viewport pause speak pause warp | - |
| 36 | 待处理 | - | Trailer.json | f, p | musicboxsong 9 farmer skippable pause playSound warp pause faceDirection pause | - |
| 963313 | 待处理 | - | Trailer.json | n | playful 10 farmer mail skippable pause speak pause speak pause | - |
| 503180 | 待处理 | - | Trailer_Big.json | f, A, A, Hn | shaneTheme -1000 farmer skippable specificTemporarySprite viewport move move faceDirection pause | - |
| positive | 待处理 | - | Trailer_Big.json |  | pause emote pause speak pause move move faceDirection viewport textAboveHead | - |
| 112 | 已迁移-待复核 | wizard_e112.json | WizardHouse.json | n | WizardSong -1000 farmer skippable addConversationTopic showFrame viewport move pause speak | 补剧情设计卡 |
| 418172 | 待处理 | - | WizardHouse.json | n | WizardSong 2 farmer skippable pause speak move pause speak faceDirection | - |
| 2123343 | 待处理 | - | Woods.json | e, t, w, F, D | nightTime -1000 farmer skippable specificTemporarySprite viewport pause faceDirection pause faceDirection | - |
| 2120303 | 待处理 | - | Woods.json | S, i, t | nightTime -1000 farmer removeQuest removeItem skippable addTemporaryActor faceDirection viewport move | - |
