package com.stardew.craft.mixin;

import com.stardew.craft.combat.WeaponStats;
import com.stardew.craft.combat.WeaponType;
import com.stardew.craft.item.weapon.IStardewWeapon;
import com.stardew.craft.item.weapon.WeaponData;
import com.stardew.craft.item.weapon.WeaponRegistry;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerSweepAttackMixin {
    @Inject(method = "sweepAttack", at = @At("HEAD"), cancellable = true)
    private void stardewcraft$blockDaggerSweep(CallbackInfo ci) {
        Player player = (Player) (Object) this;
        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof IStardewWeapon IStardewWeapon)) {
            return;
        }

        WeaponType weaponType = WeaponType.SWORD;
        WeaponData weaponData = IStardewWeapon.getWeaponData();
        if (weaponData == null) {
            String weaponId = IStardewWeapon.getWeaponId();
            if (weaponId != null && !weaponId.isEmpty()) {
                weaponData = WeaponRegistry.get(weaponId);
            }
        }
        if (weaponData != null) {
            weaponType = weaponData.getWeaponType();
        } else {
            WeaponStats stats = WeaponStats.fromItemStack(stack);
            if (stats != null) {
                weaponType = stats.getWeaponType();
            }
        }

        if (weaponType == WeaponType.DAGGER) {
            ci.cancel();
        }
    }
}

