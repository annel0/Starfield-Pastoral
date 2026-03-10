package com.stardew.craft.mining;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

/**
 * 矿井楼层数据
 * 
 * 管理每层的状态：
 * - stonesLeft: 剩余的"可计数石头"数量（用于梯子概率计算）
 * - ladderFound: 本层是否已生成梯子
 * - enemyCount: 本层敌人数量（用于梯子加成判断）
 * - isMonsterArea: 是否为清怪层
 */
public class MineFloorData {
    private int stonesLeft;
    private boolean ladderFound;
    private BlockPos ladderPos;
    private int enemyCount;
    private boolean isMonsterArea;
    private int generationVersion;
    
    public MineFloorData() {
        this.stonesLeft = 0;
        this.ladderFound = false;
    this.ladderPos = null;
        this.enemyCount = 0;
        this.isMonsterArea = false;
        this.generationVersion = 0;
    }
    
    public MineFloorData(int initialStones) {
        this();
        this.stonesLeft = initialStones;
    }
    
    // Getters
    public int getStonesLeft() {
        return stonesLeft;
    }
    
    public boolean hasLadderFound() {
        return ladderFound;
    }

    public BlockPos getLadderPos() {
        return ladderPos;
    }

    
    public int getEnemyCount() {
        return enemyCount;
    }
    
    public boolean isMonsterArea() {
        return isMonsterArea;
    }

    public int getGenerationVersion() {
        return generationVersion;
    }
    
    // Setters
    public void setStonesLeft(int stonesLeft) {
        this.stonesLeft = Math.max(0, stonesLeft);
    }
    
    public void setLadderFound(boolean ladderFound) {
        this.ladderFound = ladderFound;
    }

    public void setLadderPos(BlockPos ladderPos) {
        this.ladderPos = ladderPos;
    }

    
    public void setEnemyCount(int enemyCount) {
        this.enemyCount = Math.max(0, enemyCount);
    }
    
    public void setMonsterArea(boolean isMonsterArea) {
        this.isMonsterArea = isMonsterArea;
    }

    public void setGenerationVersion(int generationVersion) {
        this.generationVersion = generationVersion;
    }
    
    /**
     * 减少一个石头计数
     */
    public void decrementStone() {
        if (stonesLeft > 0) {
            stonesLeft--;
        }
    }
    
    /**
     * 减少一个敌人计数
     */
    public void decrementEnemy() {
        if (enemyCount > 0) {
            enemyCount--;
        }
    }
    
    /**
     * 序列化到NBT
     */
    public CompoundTag toNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("stonesLeft", stonesLeft);
        tag.putBoolean("ladderFound", ladderFound);
        if (ladderPos != null) {
            tag.putLong("ladderPos", ladderPos.asLong());
        }
        tag.putInt("enemyCount", enemyCount);
        tag.putBoolean("isMonsterArea", isMonsterArea);
        tag.putInt("generationVersion", generationVersion);
        return tag;
    }
    
    /**
     * 从NBT反序列化
     */
    public static MineFloorData fromNBT(CompoundTag tag) {
        MineFloorData data = new MineFloorData();
        data.stonesLeft = tag.getInt("stonesLeft");
        data.ladderFound = tag.getBoolean("ladderFound");
        if (tag.contains("ladderPos")) {
            data.ladderPos = BlockPos.of(tag.getLong("ladderPos"));
        }
        data.enemyCount = tag.getInt("enemyCount");
        data.isMonsterArea = tag.getBoolean("isMonsterArea");
        data.generationVersion = tag.getInt("generationVersion");
        return data;
    }
}
