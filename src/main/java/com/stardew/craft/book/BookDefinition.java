package com.stardew.craft.book;

import com.stardew.craft.player.SkillType;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public record BookDefinition(
        String stardewId,
        String registryName,
        int category,
        int price,
        int spriteIndex,
        BookKind kind,
        boolean wellReadPower,
        @Nullable SkillType skill,
        @Nullable SkillType repeatSkill,
        boolean repeatAllSkills) {
    public static final int CATEGORY_POWER_BOOK = -102;
    public static final int CATEGORY_SKILL_BOOK = -103;

    private static final List<BookDefinition> ALL = List.of(
            skill("SkillBook_0", "skill_book_0", 500, 90, SkillType.FARMING),
            skill("SkillBook_1", "skill_book_1", 500, 92, SkillType.FISHING),
            skill("SkillBook_2", "skill_book_2", 500, 91, SkillType.FORAGING),
            skill("SkillBook_3", "skill_book_3", 500, 93, SkillType.MINING),
            skill("SkillBook_4", "skill_book_4", 500, 94, SkillType.COMBAT),
            purple("PurpleBook", "purple_book", 2500, 89),
            animalCatalogue("Book_AnimalCatalogue", "book_animal_catalogue", 2000, 118),
            powerAll("Book_Artifact", "book_artifact", 500, 140),
            powerSkill("Book_Bombs", "book_bombs", 1000, 104, SkillType.MINING),
            powerSkill("Book_Crabbing", "book_crabbing", 1000, 103, SkillType.FISHING),
            powerSkill("Book_Defense", "book_defense", 500, 108, SkillType.COMBAT),
            powerSkill("Book_Diamonds", "book_diamonds", 1000, 116, SkillType.MINING),
            powerAll("Book_Friendship", "book_friendship", 3000, 109),
            powerAll("Book_Grass", "book_grass", 1000, 145),
            powerAll("Book_Horse", "book_horse", 1000, 141),
            powerSkill("Book_Marlon", "book_marlon", 4000, 112, SkillType.COMBAT),
            powerAll("Book_Mystery", "book_mystery", 3000, 117),
            queen("Book_QueenOfSauce", "book_queen_of_sauce", 10000, 115),
            powerSkill("Book_Roe", "book_roe", 800, 105, SkillType.FISHING),
            powerAll("Book_Speed", "book_speed", 5000, 111),
            powerAll("Book_Speed2", "book_speed2", 10000, 119),
            powerSkill("Book_Trash", "book_trash", 3000, 102, SkillType.FORAGING),
            powerSkill("Book_Void", "book_void", 2000, 110, SkillType.COMBAT),
            powerSkill("Book_WildSeeds", "book_wild_seeds", 1000, 106, SkillType.FORAGING),
            powerSkill("Book_Woodcutting", "book_woodcutting", 500, 107, SkillType.FORAGING)
    );

    private static final Map<String, BookDefinition> BY_REGISTRY_NAME = ALL.stream()
            .collect(Collectors.toUnmodifiableMap(BookDefinition::registryName, Function.identity()));

    public static List<BookDefinition> all() {
        return ALL;
    }

    @Nullable
    public static BookDefinition byRegistryName(String registryName) {
        return BY_REGISTRY_NAME.get(registryName);
    }

    public String statKey() {
        return stardewId;
    }

    private static BookDefinition skill(String stardewId, String registryName, int price, int spriteIndex, SkillType skill) {
        return new BookDefinition(stardewId, registryName, CATEGORY_SKILL_BOOK, price, spriteIndex,
                BookKind.SKILL, false, skill, null, false);
    }

    private static BookDefinition purple(String stardewId, String registryName, int price, int spriteIndex) {
        return new BookDefinition(stardewId, registryName, CATEGORY_SKILL_BOOK, price, spriteIndex,
                BookKind.PURPLE, false, null, null, false);
    }

    private static BookDefinition powerSkill(String stardewId, String registryName, int price, int spriteIndex, SkillType repeatSkill) {
        return new BookDefinition(stardewId, registryName, CATEGORY_POWER_BOOK, price, spriteIndex,
                BookKind.POWER, true, null, repeatSkill, false);
    }

    private static BookDefinition powerAll(String stardewId, String registryName, int price, int spriteIndex) {
        return new BookDefinition(stardewId, registryName, CATEGORY_POWER_BOOK, price, spriteIndex,
                BookKind.POWER, true, null, null, true);
    }

    private static BookDefinition animalCatalogue(String stardewId, String registryName, int price, int spriteIndex) {
        return new BookDefinition(stardewId, registryName, CATEGORY_POWER_BOOK, price, spriteIndex,
                BookKind.ANIMAL_CATALOGUE, false, null, null, false);
    }

    private static BookDefinition queen(String stardewId, String registryName, int price, int spriteIndex) {
        return new BookDefinition(stardewId, registryName, CATEGORY_POWER_BOOK, price, spriteIndex,
                BookKind.QUEEN_OF_SAUCE, false, null, null, false);
    }

    public enum BookKind {
        SKILL,
        PURPLE,
        POWER,
        ANIMAL_CATALOGUE,
        QUEEN_OF_SAUCE
    }
}