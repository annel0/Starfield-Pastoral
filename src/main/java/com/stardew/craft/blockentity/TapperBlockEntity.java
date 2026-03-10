package com.stardew.craft.blockentity;

import com.stardew.craft.item.ModItems;
import com.stardew.craft.time.StardewTimeManager;
import com.stardew.craft.block.utility.TapperBlock;
import com.stardew.craft.tree.WildTrees;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class TapperBlockEntity extends TimedProductionBlockEntity {
	private static final int OAK_RESIN_DAYS = 7;
	private static final int MAPLE_SYRUP_DAYS = 9;
	private static final int PINE_TAR_DAYS = 5;
	private static final int MAHOGANY_SAP_DAYS = 1;
	private static final int MYSTIC_SYRUP_DAYS = 7;

	private String treeId;

	public record RemainingTime(int days, int hours, int minutes) {}

	public TapperBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.TAPPER.get(), pos, state);
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, TapperBlockEntity be) {
		if (level.isClientSide) {
			return;
		}
		boolean newReady = be.refreshReady();
		if (newReady != be.ready) {
			be.ready = newReady;
			be.setChanged();
			be.syncToClient();
		}
	}

	public boolean hasProduct() {
		return !product.isEmpty();
	}

	public ItemStack getProduct() {
		return product;
	}

	public boolean isReady() {
		return ready;
	}

	public boolean canApplyFairyDust() {
		return !product.isEmpty() && !ready && readyAtAbsMinute >= 0;
	}

	@SuppressWarnings("null")
	public void ensureCycleStarted(BlockState state) {
		Level currentLevel = level;
		if (currentLevel == null || currentLevel.isClientSide) {
			return;
		}
		if (product != null && !product.isEmpty()) {
			return;
		}
		if (!(state.getBlock() instanceof TapperBlock)) {
			return;
		}
		@SuppressWarnings("null")
		Direction supportDir = state.getValue(TapperBlock.FACING);
		@SuppressWarnings("null")
		BlockPos supportPos = worldPosition.relative(supportDir);
		WildTrees.Def def = WildTrees.findByTrunk0(currentLevel.getBlockState(supportPos));
		if (def == null) {
			return;
		}
		startCycleIfEmpty(def.id());
	}

	public boolean applyFairyDust() {
		if (!canApplyFairyDust()) {
			return false;
		}
		Level currentLevel = level;
		if (currentLevel == null || currentLevel.isClientSide) {
			return false;
		}
		readyAtAbsMinute = getCurrentAbsMinute();
		ready = true;
		setChanged();
		syncToClient();
		return true;
	}

	public RemainingTime getRemainingTime() {
		long remaining = getRemainingAbsMinutes();
		int days = (int) (remaining / EFFECTIVE_MINUTES_PER_DAY);
		int minutesRemainder = (int) (remaining % EFFECTIVE_MINUTES_PER_DAY);
		int hours = minutesRemainder / StardewTimeManager.MINUTES_PER_HOUR;
		int minutes = minutesRemainder % StardewTimeManager.MINUTES_PER_HOUR;
		return new RemainingTime(days, hours, minutes);
	}

	@SuppressWarnings("null")
	public void startCycleIfEmpty(String treeId) {
		if (!product.isEmpty()) {
			return;
		}
		if (level == null || level.isClientSide) {
			return;
		}
		if (treeId == null || treeId.isBlank()) {
			return;
		}

		Cycle cycle = getCycleForTreeId(treeId);
		if (cycle == null) {
			return;
		}

		this.treeId = treeId;
		product = cycle.createProduct(level);
		long dayIndex = getCurrentDayIndex();
		// Tapper output in Stardew appears in the morning after N nights.
		readyAtAbsMinute = (dayIndex + cycle.daysUntilReady) * EFFECTIVE_MINUTES_PER_DAY;
		ready = false;
		setChanged();
		syncToClient();
	}

	public void startCycleIfEmpty() {
		if (treeId == null || treeId.isBlank()) {
			return;
		}
		startCycleIfEmpty(treeId);
	}

	public ItemStack harvestOne() {
		if (!isReady()) {
			return ItemStack.EMPTY;
		}
		ItemStack out = product.copy();
		product = ItemStack.EMPTY;
		readyAtAbsMinute = -1;
		ready = false;
		setChanged();
		syncToClient();
		// Immediately start next cycle.
		startCycleIfEmpty();
		return out;
	}

	@Override
	public ItemStack getAutomationInput() {
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack getAutomationOutput() {
		return ready ? product : ItemStack.EMPTY;
	}

	@Override
	public ItemStack insertAutomation(ItemStack stack, boolean simulate) {
		return stack;
	}

	@Override
	public ItemStack extractAutomation(int amount, boolean simulate) {
		if (!ready || product.isEmpty()) {
			return ItemStack.EMPTY;
		}
		ItemStack out = AutomationStackHelper.extractUpTo(product, amount);
		if (simulate) {
			return out;
		}
		if (out.getCount() >= product.getCount()) {
			return harvestOne();
		}
		product.shrink(out.getCount());
		setChanged();
		syncToClient();
		return out;
	}

	private record Cycle(String treeId, int daysUntilReady) {
		@SuppressWarnings("null")
		ItemStack createProduct(Level level) {
			return switch (treeId) {
				case "oak" -> new ItemStack(ModItems.OAK_RESIN.get());
				case "maple" -> new ItemStack(ModItems.MAPLE_SYRUP.get());
				case "pine" -> new ItemStack(ModItems.PINE_TAR.get());
				case "mahogany" -> {
					@SuppressWarnings("null")
					ItemStack stack = new ItemStack(ModItems.SAP.get());
					int count = 3 + level.random.nextInt(6); // 3-8
					stack.setCount(count);
					yield stack;
				}
				case "mystic_tree" -> new ItemStack(ModItems.MYSTIC_SYRUP.get());
				default -> ItemStack.EMPTY;
			};
		}
	}

	private static Cycle getCycleForTreeId(String treeId) {
		return switch (treeId) {
			case "oak" -> new Cycle(treeId, OAK_RESIN_DAYS);
			case "maple" -> new Cycle(treeId, MAPLE_SYRUP_DAYS);
			case "pine" -> new Cycle(treeId, PINE_TAR_DAYS);
			case "mahogany" -> new Cycle(treeId, MAHOGANY_SAP_DAYS);
			case "mystic_tree" -> new Cycle(treeId, MYSTIC_SYRUP_DAYS);
			default -> null;
		};
	}

	@SuppressWarnings("null")
	@Override
	protected void saveAdditional(@SuppressWarnings("null") CompoundTag tag, @SuppressWarnings("null") net.minecraft.core.HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		if (!product.isEmpty()) {
			tag.put("product", product.save(registries));
		}
		if (treeId != null && !treeId.isBlank()) {
			tag.putString("treeId", treeId);
		}
		tag.putLong("readyAtAbsMinute", readyAtAbsMinute);
		tag.putBoolean("ready", ready);
	}

	@SuppressWarnings("null")
	@Override
	protected void loadAdditional(@SuppressWarnings("null") CompoundTag tag, @SuppressWarnings("null") net.minecraft.core.HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		product = tag.contains("product") ? ItemStack.parse(registries, tag.getCompound("product")).orElse(ItemStack.EMPTY) : ItemStack.EMPTY;
		treeId = tag.contains("treeId") ? tag.getString("treeId") : null;
		readyAtAbsMinute = tag.getLong("readyAtAbsMinute");
		ready = tag.getBoolean("ready");
	}

	@Override
	public CompoundTag getUpdateTag(@SuppressWarnings("null") net.minecraft.core.HolderLookup.Provider registries) {
		CompoundTag tag = new CompoundTag();
		saveAdditional(tag, registries);
		return tag;
	}

	@Override
	public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}
}
