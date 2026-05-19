package com.stardew.craft.mixin;

import com.stardew.craft.mastery.MasteryDoorShapes;
import com.stardew.craft.mastery.MasterySite;
import com.stardew.craft.player.PlayerDataManager;
import com.stardew.craft.player.PlayerStardewData;
import com.stardew.craft.player.SkillType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 精通山洞门：即便门是打开状态，对未达成 5×Lv10 的玩家仍返回完整碰撞箱。
 */
@Mixin(DoorBlock.class)
public class DoorBlockMasteryCollisionMixin {

    @Inject(method = "getShape", at = @At("HEAD"), cancellable = true)
    private void stardewcraft$masteryDoorBlocking(BlockState state, BlockGetter level, BlockPos pos,
                                                  CollisionContext context,
                                                  CallbackInfoReturnable<VoxelShape> cir) {
        if (!MasterySite.isDoorPos(pos)) return;
        if (!(level instanceof Level l) || !MasterySite.isMasteryDimension(l)) return;
        if (!(context instanceof EntityCollisionContext ecc)) return;
        if (!(ecc.getEntity() instanceof Player p)) return;

        // 仅服务端有 PlayerStardewData；客户端的本地玩家可走客户端缓存（阶段后续可换接口）。
        if (!(p instanceof ServerPlayer sp)) return;
        PlayerStardewData data = PlayerDataManager.getPlayerData(sp);
        if (data == null) return;
        for (SkillType s : SkillType.values()) {
            if (data.getLevelByExperience(s) < 10) {
                cir.setReturnValue(MasteryDoorShapes.closedShape(state));
                return;
            }
        }
    }
}
