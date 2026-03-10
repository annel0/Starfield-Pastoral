package com.stardew.craft.player;

/**
 * 职业类型枚举
 * 对应星露谷物语的职业系统（5级和10级选择）
 */
public enum ProfessionType {
    // 农业职业 (Farming)
    RANCHER(0, "rancher", "牧场主", SkillType.FARMING, 5),
    TILLER(1, "tiller", "耕种者", SkillType.FARMING, 5),
    COOPMASTER(2, "coopmaster", "养鸡达人", SkillType.FARMING, 10),
    SHEPHERD(3, "shepherd", "牧羊人", SkillType.FARMING, 10),
    ARTISAN(4, "artisan", "工匠", SkillType.FARMING, 10),
    AGRICULTURIST(5, "agriculturist", "农业学家", SkillType.FARMING, 10),
    
    // 钓鱼职业 (Fishing)
    FISHER(6, "fisher", "渔夫", SkillType.FISHING, 5),
    TRAPPER(7, "trapper", "捕手", SkillType.FISHING, 5),
    ANGLER(8, "angler", "垂钓者", SkillType.FISHING, 10),
    PIRATE(9, "pirate", "海盗", SkillType.FISHING, 10),
    MARINER(10, "mariner", "水手", SkillType.FISHING, 10),
    LUREMASTER(11, "luremaster", "诱饵大师", SkillType.FISHING, 10),
    
    // 觅食职业 (Foraging)
    FORESTER(12, "forester", "护林人", SkillType.FORAGING, 5),
    GATHERER(13, "gatherer", "采集者", SkillType.FORAGING, 5),
    LUMBERJACK(14, "lumberjack", "伐木工", SkillType.FORAGING, 10),
    TAPPER(15, "tapper", "树液采集者", SkillType.FORAGING, 10),
    BOTANIST(16, "botanist", "植物学家", SkillType.FORAGING, 10),
    TRACKER(17, "tracker", "追踪者", SkillType.FORAGING, 10),
    
    // 采矿职业 (Mining)
    MINER(18, "miner", "矿工", SkillType.MINING, 5),
    GEOLOGIST(19, "geologist", "地质学家", SkillType.MINING, 5),
    BLACKSMITH(20, "blacksmith", "铁匠", SkillType.MINING, 10),
    PROSPECTOR(21, "prospector", "勘探者", SkillType.MINING, 10),
    EXCAVATOR(22, "excavator", "挖掘专家", SkillType.MINING, 10),
    GEMOLOGIST(23, "gemologist", "宝石专家", SkillType.MINING, 10),
    
    // 战斗职业 (Combat)
    FIGHTER(24, "fighter", "战士", SkillType.COMBAT, 5),
    SCOUT(25, "scout", "侦察兵", SkillType.COMBAT, 5),
    BRUTE(26, "brute", "野蛮人", SkillType.COMBAT, 10),
    DEFENDER(27, "defender", "防御者", SkillType.COMBAT, 10),
    ACROBAT(28, "acrobat", "杂技演员", SkillType.COMBAT, 10),
    DESPERADO(29, "desperado", "亡命徒", SkillType.COMBAT, 10);
    
    private final int id;
    private final String name;
    private final String displayName;
    private final SkillType skillType;
    private final int level;  // 需要的技能等级（5或10）
    
    ProfessionType(int id, String name, String displayName, SkillType skillType, int level) {
        this.id = id;
        this.name = name;
        this.displayName = displayName;
        this.skillType = skillType;
        this.level = level;
    }
    
    public int getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public SkillType getSkillType() {
        return skillType;
    }
    
    public int getRequiredLevel() {
        return level;
    }
    
    /**
     * 根据ID获取职业类型
     */
    public static ProfessionType fromId(int id) {
        for (ProfessionType profession : values()) {
            if (profession.id == id) {
                return profession;
            }
        }
        return null;
    }
    
    /**
     * 获取某个技能的5级职业选项
     */
    public static ProfessionType[] getLevel5Options(SkillType skill) {
        return switch (skill) {
            case FARMING -> new ProfessionType[]{RANCHER, TILLER};
            case FISHING -> new ProfessionType[]{FISHER, TRAPPER};
            case FORAGING -> new ProfessionType[]{FORESTER, GATHERER};
            case MINING -> new ProfessionType[]{MINER, GEOLOGIST};
            case COMBAT -> new ProfessionType[]{FIGHTER, SCOUT};
        };
    }
    
    /**
     * 获取某个技能的10级职业选项（基于5级选择）
     */
    public static ProfessionType[] getLevel10Options(SkillType skill, ProfessionType level5Choice) {
        return switch (skill) {
            case FARMING -> {
                if (level5Choice == RANCHER) {
                    yield new ProfessionType[]{COOPMASTER, SHEPHERD};
                } else {
                    yield new ProfessionType[]{ARTISAN, AGRICULTURIST};
                }
            }
            case FISHING -> {
                if (level5Choice == FISHER) {
                    yield new ProfessionType[]{ANGLER, PIRATE};
                } else {
                    yield new ProfessionType[]{MARINER, LUREMASTER};
                }
            }
            case FORAGING -> {
                if (level5Choice == FORESTER) {
                    yield new ProfessionType[]{LUMBERJACK, TAPPER};
                } else {
                    yield new ProfessionType[]{BOTANIST, TRACKER};
                }
            }
            case MINING -> {
                if (level5Choice == MINER) {
                    yield new ProfessionType[]{BLACKSMITH, PROSPECTOR};
                } else {
                    yield new ProfessionType[]{EXCAVATOR, GEMOLOGIST};
                }
            }
            case COMBAT -> {
                if (level5Choice == FIGHTER) {
                    yield new ProfessionType[]{BRUTE, DEFENDER};
                } else {
                    yield new ProfessionType[]{ACROBAT, DESPERADO};
                }
            }
        };
    }
}
