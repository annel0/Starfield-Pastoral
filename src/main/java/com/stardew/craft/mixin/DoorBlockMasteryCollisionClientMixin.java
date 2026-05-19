package com.stardew.craft.mixin;

import com.stardew.craft.client.ClientPlayerDataCache;
import com.stardew.craft.mastery.MasteryDoorShapes;
import com.stardew.craft.mastery.MasterySite;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
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
 * 客户端侧门碰撞：让本地玩家走出 vanilla 预测，与服务端一致。
 */
@Mixin(DoorBlock.class)
public class DoorBlockMasteryCollisionClientMixin {

    @Inject(method = "getShape", at = @At("HEAD"), cancellable = true)
    private void stardewcraft$masteryDoorBlockingClient(BlockState state, BlockGetter level, BlockPos pos,
                                                        CollisionContext context,
                                                        CallbackInfoReturnable<VoxelShape> cir) {
        if (!MasterySite.isDoorPos(pos)) return;
        if (!(level instanceof Level l) || !MasterySite.isMasteryDimension(l)) return;
        if (!(context instanceof EntityCollisionContext ecc)) return;
        if (!(ecc.getEntity() instanceof LocalPlayer)) return;
        if (!ClientPlayerDataCache.isSynced()) return;
        if (!ClientPlayerDataCache.hasAllSkillsMaxed()) {
            cir.setReturnValue(MasteryDoorShapes.closedShape(state));
        }
    }
}
