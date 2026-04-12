package com.stardew.craft.item;

import com.stardew.craft.blockentity.BeeHouseBlockEntity;
import com.stardew.craft.blockentity.CheesePressBlockEntity;
import com.stardew.craft.blockentity.CrystalariumBlockEntity;
import com.stardew.craft.blockentity.CharcoalKilnBlockEntity;
import com.stardew.craft.blockentity.BaitMakerBlockEntity;
import com.stardew.craft.blockentity.FurnaceBlockEntity;
import com.stardew.craft.blockentity.FishSmokerBlockEntity;
import com.stardew.craft.blockentity.KegBlockEntity;
import com.stardew.craft.blockentity.MayonnaiseMachineBlockEntity;
import com.stardew.craft.blockentity.OilMakerBlockEntity;
import com.stardew.craft.blockentity.RecyclingMachineBlockEntity;
import com.stardew.craft.blockentity.CaskBlockEntity;
import com.stardew.craft.blockentity.PreservesJarBlockEntity;
import com.stardew.craft.blockentity.SeedMakerBlockEntity;
import com.stardew.craft.blockentity.TapperBlockEntity;
import com.stardew.craft.blockentity.LoomBlockEntity;
import com.stardew.craft.blockentity.SolarPanelBlockEntity;
import com.stardew.craft.blockentity.WormBinBlockEntity;
import com.stardew.craft.blockentity.DeluxeWormBinBlockEntity;
import com.stardew.craft.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Fairy dust item: accelerates compatible machines to finish soon.
 */
public class FairyDustItem extends SimpleStardewItem {
    public FairyDustItem(int sellPrice, Properties properties) {
        super("stardewcraft.type.craftable", sellPrice, properties);
    }

    @SuppressWarnings("null")
    @Override
    public InteractionResult useOn(@SuppressWarnings("null") UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        var state = level.getBlockState(pos);
        BlockEntity be = level.getBlockEntity(pos);

        if (!(be instanceof KegBlockEntity
                || be instanceof CaskBlockEntity
            || be instanceof PreservesJarBlockEntity
                || be instanceof BaitMakerBlockEntity
                || be instanceof CheesePressBlockEntity
                || be instanceof MayonnaiseMachineBlockEntity
                || be instanceof CrystalariumBlockEntity
                || be instanceof CharcoalKilnBlockEntity
                || be instanceof FurnaceBlockEntity
                || be instanceof FishSmokerBlockEntity
                || be instanceof RecyclingMachineBlockEntity
                || be instanceof SeedMakerBlockEntity
                || be instanceof OilMakerBlockEntity
                || be instanceof BeeHouseBlockEntity
                || be instanceof TapperBlockEntity
                || be instanceof LoomBlockEntity
                || be instanceof SolarPanelBlockEntity
                || be instanceof WormBinBlockEntity
                || be instanceof DeluxeWormBinBlockEntity)) {
            return InteractionResult.PASS;
        }

        boolean canApply = false;
        if (be instanceof KegBlockEntity keg) {
            canApply = keg.canApplyFairyDust();
        } else if (be instanceof CaskBlockEntity cask) {
            canApply = cask.canApplyFairyDust();
        } else if (be instanceof PreservesJarBlockEntity preservesJar) {
            canApply = preservesJar.canApplyFairyDust();
        } else if (be instanceof BaitMakerBlockEntity baitMaker) {
            canApply = baitMaker.canApplyFairyDust();
        } else if (be instanceof CheesePressBlockEntity press) {
            canApply = press.canApplyFairyDust();
        } else if (be instanceof MayonnaiseMachineBlockEntity machine) {
            canApply = machine.canApplyFairyDust();
        } else if (be instanceof CrystalariumBlockEntity crystalarium) {
            canApply = crystalarium.canApplyFairyDust();
        } else if (be instanceof CharcoalKilnBlockEntity kiln) {
            canApply = kiln.canApplyFairyDust();
        } else if (be instanceof FurnaceBlockEntity furnace) {
            canApply = furnace.canApplyFairyDust();
        } else if (be instanceof FishSmokerBlockEntity fishSmoker) {
            canApply = fishSmoker.canApplyFairyDust();
        } else if (be instanceof RecyclingMachineBlockEntity recyclingMachine) {
            canApply = recyclingMachine.canApplyFairyDust();
        } else if (be instanceof SeedMakerBlockEntity seedMaker) {
            canApply = seedMaker.canApplyFairyDust();
        } else if (be instanceof OilMakerBlockEntity oilMaker) {
            canApply = oilMaker.canApplyFairyDust();
        } else if (be instanceof BeeHouseBlockEntity beeHouse) {
            canApply = beeHouse.canApplyFairyDust();
        } else if (be instanceof LoomBlockEntity loom) {
            canApply = loom.canApplyFairyDust();
        } else if (be instanceof SolarPanelBlockEntity panel) {
            canApply = panel.canApplyFairyDust();
        } else if (be instanceof WormBinBlockEntity wormBin) {
            canApply = wormBin.canApplyFairyDust();
        } else if (be instanceof DeluxeWormBinBlockEntity wormBin) {
            canApply = wormBin.canApplyFairyDust();
        } else if (be instanceof TapperBlockEntity tapper) {
            tapper.ensureCycleStarted(state);
            canApply = tapper.canApplyFairyDust();
        }

        if (!canApply) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide) {
            boolean applied = false;
            if (be instanceof KegBlockEntity keg) {
                applied = keg.applyFairyDust();
            } else if (be instanceof CaskBlockEntity cask) {
                applied = cask.applyFairyDust();
            } else if (be instanceof PreservesJarBlockEntity preservesJar) {
                applied = preservesJar.applyFairyDust();
            } else if (be instanceof BaitMakerBlockEntity baitMaker) {
                applied = baitMaker.applyFairyDust();
            } else if (be instanceof CheesePressBlockEntity press) {
                applied = press.applyFairyDust();
            } else if (be instanceof MayonnaiseMachineBlockEntity machine) {
                applied = machine.applyFairyDust();
            } else if (be instanceof CrystalariumBlockEntity crystalarium) {
                applied = crystalarium.applyFairyDust();
            } else if (be instanceof CharcoalKilnBlockEntity kiln) {
                applied = kiln.applyFairyDust();
            } else if (be instanceof FurnaceBlockEntity furnace) {
                applied = furnace.applyFairyDust();
            } else if (be instanceof FishSmokerBlockEntity fishSmoker) {
                applied = fishSmoker.applyFairyDust();
            } else if (be instanceof RecyclingMachineBlockEntity recyclingMachine) {
                applied = recyclingMachine.applyFairyDust();
            } else if (be instanceof SeedMakerBlockEntity seedMaker) {
                applied = seedMaker.applyFairyDust();
            } else if (be instanceof OilMakerBlockEntity oilMaker) {
                applied = oilMaker.applyFairyDust();
            } else if (be instanceof BeeHouseBlockEntity beeHouse) {
                applied = beeHouse.applyFairyDust();
            } else if (be instanceof LoomBlockEntity loom) {
                applied = loom.applyFairyDust();
            } else if (be instanceof SolarPanelBlockEntity panel) {
                applied = panel.applyFairyDust();
            } else if (be instanceof WormBinBlockEntity wormBin) {
                applied = wormBin.applyFairyDust();
            } else if (be instanceof DeluxeWormBinBlockEntity wormBin) {
                applied = wormBin.applyFairyDust();
            } else if (be instanceof TapperBlockEntity tapper) {
                applied = tapper.applyFairyDust();
            }

            if (!applied) {
                return InteractionResult.PASS;
            }

            if (context.getPlayer() == null || !context.getPlayer().isCreative()) {
                context.getItemInHand().shrink(1);
            }

            if (level instanceof ServerLevel serverLevel) {
                double x = pos.getX() + 0.5;
                double y = pos.getY() + 0.8;
                double z = pos.getZ() + 0.5;
                serverLevel.sendParticles(ParticleTypes.END_ROD, x, y, z, 12, 0.3, 0.2, 0.3, 0.02);
                serverLevel.sendParticles(ParticleTypes.ENCHANT, x, y, z, 10, 0.25, 0.15, 0.25, 0.02);
            }

            level.playSound(null, pos, ModSounds.YOBA.get(), SoundSource.BLOCKS, 0.7f, 1.0f);
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
