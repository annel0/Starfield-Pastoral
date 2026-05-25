package com.stardew.craft.item.weapon;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.combat.WeaponForgeData;
import com.stardew.craft.combat.WeaponStats;
import com.stardew.craft.combat.skill.FemurSlamTracker;
import com.stardew.craft.combat.skill.WeaponSkillCooldowns;
import com.stardew.craft.item.IStardewItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

import java.util.List;

public class StardewClubItem extends Item implements IStardewItem, IStardewWeapon {

    private static final int CHARGE_TICKS = 20;
    private static final float BASE_ATTACK_RANGE = 3.0f;

    private final String weaponId;
    private final WeaponData weaponData;

    public StardewClubItem(String weaponId, Properties properties) {
        super(properties);
        this.weaponId = weaponId;
        this.weaponData = WeaponRegistry.get(weaponId);
    }

    @SuppressWarnings("null")
    @Override
    public Component getName(@SuppressWarnings("null") ItemStack stack) {
        if (weaponData != null) {
            return Component.translatable(this.getDescriptionId())
                .withStyle(weaponData.getRarity().getColor());
        }
        return super.getName(stack);
    }

    @Override
    public ItemStack getDefaultInstance() {
        ItemStack stack = super.getDefaultInstance();
        ensureWeaponStats(stack);
        return stack;
    }

    @SuppressWarnings({"null", "deprecation"})
    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers() {
        if (weaponData == null) {
            return super.getDefaultAttributeModifiers();
        }

        float avgDamage = (float) ((weaponData.getDamageMin() + weaponData.getDamageMax()) / 2.0 - 1);
        float baseAps = weaponData.getWeaponType().getAttackSpeed();
        float speedBonusAps = weaponData.getSpeed() * 0.1f;
        float attackSpeed = (baseAps + speedBonusAps) - 4.0f;
        float desiredRange = weaponData.getWeaponType().getAttackRange();
        float attackRangeBonus = desiredRange - BASE_ATTACK_RANGE;

        return ItemAttributeModifiers.builder()
            .add(
                Attributes.ATTACK_DAMAGE,
                new AttributeModifier(
                    ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "weapon." + weaponId + ".attack_damage"),
                    avgDamage,
                    AttributeModifier.Operation.ADD_VALUE
                ),
                EquipmentSlotGroup.MAINHAND
            )
            .add(
                Attributes.ATTACK_SPEED,
                new AttributeModifier(
                    ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "weapon." + weaponId + ".attack_speed"),
                    attackSpeed,
                    AttributeModifier.Operation.ADD_VALUE
                ),
                EquipmentSlotGroup.MAINHAND
            )
            .add(
                Attributes.ENTITY_INTERACTION_RANGE,
                new AttributeModifier(
                    ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "weapon." + weaponId + ".attack_range"),
                    attackRangeBonus,
                    AttributeModifier.Operation.ADD_VALUE
                ),
                EquipmentSlotGroup.MAINHAND
            )
            .build();
    }

    @Override
    public boolean isDamageable(@SuppressWarnings("null") ItemStack stack) {
        return false;
    }

    @Override
    public boolean isBarVisible(@SuppressWarnings("null") ItemStack stack) {
        return false;
    }

    @Override
    public boolean hurtEnemy(@SuppressWarnings("null") ItemStack stack, @SuppressWarnings("null") LivingEntity target, @SuppressWarnings("null") LivingEntity attacker) {
        return true;
    }

    @Override
    public boolean canPerformAction(@SuppressWarnings("null") ItemStack stack, @SuppressWarnings("null") ItemAbility ability) {
        return ItemAbilities.DEFAULT_SWORD_ACTIONS.contains(ability);
    }

    @Override
    public boolean mineBlock(@SuppressWarnings("null") ItemStack stack, @SuppressWarnings("null") Level level, @SuppressWarnings("null") BlockState state, @SuppressWarnings("null") BlockPos pos, @SuppressWarnings("null") LivingEntity entityLiving) {
        return true;
    }

    @Override
    public String getItemTypeKey() {
        if (weaponData == null) return "stardewcraft.type.weapon";
        return switch (weaponData.getWeaponType()) {
            case SWORD -> "stardewcraft.type.weapon.sword";
            case DAGGER -> "stardewcraft.type.weapon.dagger";
            case CLUB -> "stardewcraft.type.weapon.club";
            case SLINGSHOT -> "stardewcraft.type.weapon.slingshot";
        };
    }

    @Override
    public int getSellPrice(ItemStack stack) {
        return weaponData == null ? -1 : weaponData.getLevel() * 50;
    }

    @Override
    public boolean isEnchantable(@SuppressWarnings("null") ItemStack stack) {
        return stack.getMaxStackSize() == 1;
    }

    @Override
    public int getEnchantmentValue() {
        return weaponData == null ? 10 : Math.max(1, weaponData.getLevel() * 2);
    }

    @Override
    public void appendHoverText(@SuppressWarnings("null") ItemStack stack, @SuppressWarnings("null") Item.TooltipContext context, @SuppressWarnings("null") List<Component> tooltipComponents, @SuppressWarnings("null") TooltipFlag tooltipFlag) {
        if (weaponData != null) {
            ensureWeaponStats(stack);
            boolean expanded = net.minecraft.client.gui.screens.Screen.hasShiftDown();
            WeaponTooltipBuilder builder = new WeaponTooltipBuilder(stack, weaponData, expanded);
            tooltipComponents.addAll(builder.build());
        }
    }

    @SuppressWarnings("null")
    @Override
    public void inventoryTick(@SuppressWarnings("null") ItemStack stack, @SuppressWarnings("null") Level level, @SuppressWarnings("null") Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
        if (!level.isClientSide && entity instanceof net.minecraft.world.entity.player.Player) {
            ensureWeaponStats(stack);
        }
    }

    @SuppressWarnings("null")
    @Override
    public InteractionResultHolder<ItemStack> use(@SuppressWarnings("null") Level level, @SuppressWarnings("null") net.minecraft.world.entity.player.Player player, @SuppressWarnings("null") InteractionHand hand) {
        @SuppressWarnings("null")
        ItemStack stack = player.getItemInHand(hand);
        return InteractionResultHolder.pass(stack);
    }

    @Override
    public int getUseDuration(@SuppressWarnings("null") ItemStack stack, @SuppressWarnings("null") LivingEntity entity) {
        return CHARGE_TICKS;
    }

    @Override
    public UseAnim getUseAnimation(@SuppressWarnings("null") ItemStack stack) {
        return UseAnim.BOW;
    }

    @SuppressWarnings("null")
    public InteractionResultHolder<ItemStack> useSkill(Level level, net.minecraft.world.entity.player.Player player, InteractionHand hand, boolean majorSkill) {
        @SuppressWarnings("null")
        ItemStack stack = player.getItemInHand(hand);
        if (weaponData == null) {
            ensureWeaponStats(stack);
        }
        if (weaponData == null) {
            return InteractionResultHolder.pass(stack);
        }

        WeaponSkillData skill = majorSkill ? weaponData.getSkill2() : weaponData.getSkill1();
        if (skill == null) {
            return InteractionResultHolder.pass(stack);
        }

        long nowTick = level.getGameTime();
        String skillId = skill.getId();
        int cooldownTicks = skill.getCooldown() * 20;

        boolean isFemurSlam = "femur_slam".equals(skillId);
        if (!isFemurSlam) {
            return InteractionResultHolder.pass(stack);
        }

        if (level.isClientSide
            && com.stardew.craft.client.weapon.WeaponSkillCooldownsClient.isOnCooldown(weaponId, skillId)) {
            return InteractionResultHolder.fail(stack);
        }

        if (WeaponSkillCooldowns.isOnCooldown(player, weaponId, skillId, nowTick)
            || (player instanceof ServerPlayer serverPlayer && FemurSlamTracker.isCharging(serverPlayer))) {
            if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
                boolean mainHand = (hand == InteractionHand.MAIN_HAND);
                net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(
                    serverPlayer,
                    new com.stardew.craft.combat.network.SkillFailFeedbackPayload(mainHand)
                );
            }
            return InteractionResultHolder.fail(stack);
        }

        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            player.startUsingItem(hand);
            FemurSlamTracker.start(serverPlayer, nowTick, CHARGE_TICKS, weaponId, skillId,
                skill.getDamagePercent() / 100.0f, cooldownTicks);
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    public String getWeaponId() {
        return weaponId;
    }

    public WeaponData getWeaponData() {
        return weaponData;
    }

    @SuppressWarnings("null")
    private void ensureWeaponStats(ItemStack stack) {
        if (weaponData == null) return;
        if (stack.has(DataComponents.CUSTOM_DATA)) {
            @SuppressWarnings("null")
            CustomData data = stack.get(DataComponents.CUSTOM_DATA);
            if (data != null && data.copyTag().contains(WeaponStats.TAG_STARDEW_WEAPON)) {
                WeaponForgeData.ensure(stack);
                return;
            }
        }

        WeaponStats.builder()
            .weaponType(weaponData.getWeaponType())
            .minDamage(weaponData.getDamageMin())
            .maxDamage(weaponData.getDamageMax())
            .critChance((float) weaponData.getCritChance())
            .bonusCritPower((float) Math.max(0, (weaponData.getCritPower() - 1.0) * 100.0))
            .speed(weaponData.getSpeed())
            .defense(weaponData.getDefense())
            .knockback((float) weaponData.getWeight())
            .build()
            .writeToItemStack(stack);
        WeaponForgeData.ensure(stack);
    }
}
