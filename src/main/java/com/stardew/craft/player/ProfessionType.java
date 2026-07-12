package com.stardew.craft.player;

/**
 * Enum of professions, mirroring Stardew Valley's profession system (level 5 and level 10 choices).
 */
public enum ProfessionType {
    // Farming
    RANCHER(0, "rancher", "Скотовод", SkillType.FARMING, 5),
    TILLER(1, "tiller", "Земледелец", SkillType.FARMING, 5),
    COOPMASTER(2, "coopmaster", "Птицевод", SkillType.FARMING, 10),
    SHEPHERD(3, "shepherd", "Пастух", SkillType.FARMING, 10),
    ARTISAN(4, "artisan", "Ремесленник", SkillType.FARMING, 10),
    AGRICULTURIST(5, "agriculturist", "Агроном", SkillType.FARMING, 10),

    // Fishing
    FISHER(6, "fisher", "Рыбак", SkillType.FISHING, 5),
    TRAPPER(7, "trapper", "Ловец", SkillType.FISHING, 5),
    ANGLER(8, "angler", "Удильщик", SkillType.FISHING, 10),
    PIRATE(9, "pirate", "Пират", SkillType.FISHING, 10),
    MARINER(10, "mariner", "Мореход", SkillType.FISHING, 10),
    LUREMASTER(11, "luremaster", "Мастер приманки", SkillType.FISHING, 10),

    // Foraging
    FORESTER(12, "forester", "Лесник", SkillType.FORAGING, 5),
    GATHERER(13, "gatherer", "Собиратель", SkillType.FORAGING, 5),
    LUMBERJACK(14, "lumberjack", "Лесоруб", SkillType.FORAGING, 10),
    TAPPER(15, "tapper", "Смолокур", SkillType.FORAGING, 10),
    BOTANIST(16, "botanist", "Ботаник", SkillType.FORAGING, 10),
    TRACKER(17, "tracker", "Следопыт", SkillType.FORAGING, 10),

    // Mining
    MINER(18, "miner", "Шахтёр", SkillType.MINING, 5),
    GEOLOGIST(19, "geologist", "Геолог", SkillType.MINING, 5),
    BLACKSMITH(20, "blacksmith", "Кузнец", SkillType.MINING, 10),
    PROSPECTOR(21, "prospector", "Старатель", SkillType.MINING, 10),
    EXCAVATOR(22, "excavator", "Землекоп", SkillType.MINING, 10),
    GEMOLOGIST(23, "gemologist", "Геммолог", SkillType.MINING, 10),

    // Combat
    FIGHTER(24, "fighter", "Боец", SkillType.COMBAT, 5),
    SCOUT(25, "scout", "Разведчик", SkillType.COMBAT, 5),
    BRUTE(26, "brute", "Громила", SkillType.COMBAT, 10),
    DEFENDER(27, "defender", "Защитник", SkillType.COMBAT, 10),
    ACROBAT(28, "acrobat", "Акробат", SkillType.COMBAT, 10),
    DESPERADO(29, "desperado", "Головорез", SkillType.COMBAT, 10);
    
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
