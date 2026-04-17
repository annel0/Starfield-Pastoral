package com.stardew.craft.entity;

import com.stardew.craft.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class FallenOakTreeEntity extends Entity {
	private static final String TAG_PIECES = "pieces";
	private static final String TAG_WOOD = "wood";
	private static final String TAG_DROPS = "drops";
	private static final String TAG_HAS_DROPPED = "HasDropped";

	private static final String P_DX = "dx";
	private static final String P_DY = "dy";
	private static final String P_DZ = "dz";
	private static final String P_BLOCK = "block";
	private static final String P_FACING = "facing";

	@SuppressWarnings("null")
	private static final EntityDataAccessor<CompoundTag> TREE_DATA = SynchedEntityData.defineId(FallenOakTreeEntity.class, EntityDataSerializers.COMPOUND_TAG);
	@SuppressWarnings("null")
	private static final EntityDataAccessor<Integer> DURATION_TICKS = SynchedEntityData.defineId(FallenOakTreeEntity.class, EntityDataSerializers.INT);
	@SuppressWarnings("null")
	private static final EntityDataAccessor<Byte> FALL_DIR = SynchedEntityData.defineId(FallenOakTreeEntity.class, EntityDataSerializers.BYTE);

	public FallenOakTreeEntity(EntityType<? extends FallenOakTreeEntity> type, Level level) {
		super(type, level);
		this.noPhysics = true;
	}

	@SuppressWarnings("null")
	public static void spawn(ServerLevel level, BlockPos pivotTrunk0Pos, Direction fallDirection, List<Piece> piecesAboveTrunk0, int durationTicks, List<net.minecraft.world.item.ItemStack> fallDrops) {
		FallenOakTreeEntity e = new FallenOakTreeEntity(ModEntities.FALLEN_OAK_TREE.get(), level);
		e.setPos(pivotTrunk0Pos.getX(), pivotTrunk0Pos.getY(), pivotTrunk0Pos.getZ());

		CompoundTag tag = new CompoundTag();
		ListTag list = new ListTag();
		for (Piece piece : piecesAboveTrunk0) {
			CompoundTag p = new CompoundTag();
			p.putInt(P_DX, piece.dx);
			p.putInt(P_DY, piece.dy);
			p.putInt(P_DZ, piece.dz);
			p.putInt(P_BLOCK, piece.blockId);
			p.putInt(P_FACING, piece.facing2d);
			list.add(p);
		}
		tag.put(TAG_PIECES, list);
		// Keep legacy field for debugging/compat, but drops are now explicit.
		tag.putInt(TAG_WOOD, 0);
		ListTag drops = new ListTag();
		if (fallDrops != null) {
			for (net.minecraft.world.item.ItemStack s : fallDrops) {
				if (s == null || s.isEmpty()) {
					continue;
				}
				drops.add(s.copy().save(level.registryAccess()));
			}
		}
		tag.put(TAG_DROPS, drops);

		e.entityData.set(TREE_DATA, tag);
		e.entityData.set(DURATION_TICKS, Math.max(1, durationTicks));
		e.entityData.set(FALL_DIR, (byte) fallDirection.get3DDataValue());

		playStartEffects(level, pivotTrunk0Pos);

		level.addFreshEntity(e);
	}

	@SuppressWarnings("null")
	private BlockPos computeImpactPos() {
		BlockPos pivot = this.blockPosition();
		Direction dir = this.getFallDirection();
		int len = 1;
		for (Piece p : this.getPieces()) {
			len = Math.max(len, p.dy);
		}
		return pivot.relative(dir, len);
	}

	@SuppressWarnings("null")
	private static void playStartEffects(ServerLevel level, BlockPos pivot) {
		// Stardew cue: treecrack
		level.playSound(null, pivot, ModSounds.TREE_CRACK.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
		level.sendParticles(
				new BlockParticleOption(ParticleTypes.BLOCK, net.minecraft.world.level.block.Blocks.OAK_LOG.defaultBlockState()),
				pivot.getX() + 0.5,
				pivot.getY() + 1.0,
				pivot.getZ() + 0.5,
				14,
				0.5,
				0.5,
				0.5,
				0.05
		);
	}

	private static Vec3 rotatePoint(Vec3 p, float angleRad, Direction dir) {
		// Pivot is at the bottom-center of the trunk0 block in local space.
		double px = 0.5;
		double py = 0.0;
		double pz = 0.5;
		double x = p.x - px;
		double y = p.y - py;
		double z = p.z - pz;

		double c = Math.cos(angleRad);
		double s = Math.sin(angleRad);

		double rx = x;
		double ry = y;
		double rz = z;

		switch (dir) {
			case EAST, WEST -> {
				// Rotate around Z axis.
				rx = x * c - y * s;
				ry = x * s + y * c;
				rz = z;
			}
			case SOUTH, NORTH -> {
				// Rotate around X axis.
				rx = x;
				ry = y * c - z * s;
				rz = y * s + z * c;
			}
			default -> {
			}
		}

		return new Vec3(rx + px, ry + py, rz + pz);
	}

	@SuppressWarnings("null")
	private void emitFallParticles(float progress01) {
		if (!(this.level() instanceof ServerLevel serverLevel)) {
			return;
		}
		List<Piece> pieces = this.getPieces();
		if (pieces.isEmpty()) {
			return;
		}

		// Match renderer curve: ease-in (slow start, faster later).
		float eased = progress01 * progress01;
		float angleDeg = 90.0F * eased;
		Direction dir = this.getFallDirection();
		float signedDeg = switch (dir) {
			case EAST -> -angleDeg;
			case WEST -> angleDeg;
			case SOUTH -> angleDeg;
			case NORTH -> -angleDeg;
			default -> angleDeg;
		};
		float angleRad = signedDeg * ((float) Math.PI / 180.0F);

		BlockState dustState = net.minecraft.world.level.block.Blocks.OAK_LOG.defaultBlockState();
		@SuppressWarnings("null")
		BlockParticleOption dust = new BlockParticleOption(ParticleTypes.BLOCK, dustState);

		int samples = Math.min(6, Math.max(2, pieces.size() / 6));
		for (int i = 0; i < samples; i++) {
			Piece piece = pieces.get(this.random.nextInt(pieces.size()));
			// Local point at the center of the block.
			Vec3 local = new Vec3(piece.dx + 0.5, piece.dy + 0.5, piece.dz + 0.5);
			Vec3 rotated = rotatePoint(local, angleRad, dir);
			Vec3 world = rotated.add(this.getX(), this.getY(), this.getZ());

			serverLevel.sendParticles(dust, world.x, world.y, world.z, 4, 0.18, 0.18, 0.18, 0.02);
			// Less "ugly" than white poof: ash/smoke drifting off the falling trunk.
			serverLevel.sendParticles(ParticleTypes.ASH, world.x, world.y, world.z, 1, 0.12, 0.12, 0.12, 0.005);
		}
	}

	@SuppressWarnings("null")
	private void playImpactEffects() {
		BlockPos impact = computeImpactPos();
		Direction dir = this.getFallDirection();
		// Stardew cue: treethud (played exactly when reaching 90 degrees)
		this.level().playSound(null, impact, ModSounds.TREE_THUD.get(), SoundSource.BLOCKS, 1.0F, 1.0F);

		if (this.level() instanceof ServerLevel serverLevel) {
			BlockState dustState = net.minecraft.world.level.block.Blocks.OAK_LOG.defaultBlockState();
			@SuppressWarnings("null")
			BlockParticleOption dust = new BlockParticleOption(ParticleTypes.BLOCK, dustState);
			// Emit particles along the whole fallen body (angle = 90deg).
			float angleDeg = 90.0F;
			float signedDeg = switch (dir) {
				case EAST -> -angleDeg;
				case WEST -> angleDeg;
				case SOUTH -> angleDeg;
				case NORTH -> -angleDeg;
				default -> angleDeg;
			};
			float angleRad = signedDeg * ((float) Math.PI / 180.0F);
			List<Piece> pieces = this.getPieces();
			int samples = Math.min(18, Math.max(6, pieces.size() / 2));
			for (int i = 0; i < samples; i++) {
				Piece piece = pieces.get(this.random.nextInt(pieces.size()));
				Vec3 local = new Vec3(piece.dx + 0.5, piece.dy + 0.5, piece.dz + 0.5);
				Vec3 rotated = rotatePoint(local, angleRad, dir);
				Vec3 world = rotated.add(this.getX(), this.getY(), this.getZ());
				serverLevel.sendParticles(dust, world.x, world.y, world.z, 6, 0.35, 0.25, 0.35, 0.06);
				serverLevel.sendParticles(ParticleTypes.ASH, world.x, world.y, world.z, 2, 0.3, 0.2, 0.3, 0.01);
			}
		}
	}

	@SuppressWarnings("null")
	private void dropAtImpactOnce() {
		if (!(this.level() instanceof ServerLevel serverLevel)) {
			return;
		}
		CompoundTag persisted = this.getPersistentData();
		if (persisted.getBoolean(TAG_HAS_DROPPED)) {
			return;
		}
		persisted.putBoolean(TAG_HAS_DROPPED, true);

		BlockPos impact = computeImpactPos();
		for (net.minecraft.world.item.ItemStack s : this.getDrops(serverLevel)) {
			if (s.isEmpty()) {
				continue;
			}
			Block.popResource(serverLevel, impact, s);
		}
	}

	@SuppressWarnings("null")
	private List<net.minecraft.world.item.ItemStack> getDrops(ServerLevel level) {
		@SuppressWarnings("null")
		CompoundTag tag = this.entityData.get(TREE_DATA);
		ListTag list = tag.getList(TAG_DROPS, Tag.TAG_COMPOUND);
		List<net.minecraft.world.item.ItemStack> out = new ArrayList<>(list.size());
		for (int i = 0; i < list.size(); i++) {
			CompoundTag t = list.getCompound(i);
			if (t.contains("id")) {
				out.add(net.minecraft.world.item.ItemStack.parse(level.registryAccess(), t).orElse(net.minecraft.world.item.ItemStack.EMPTY));
			}
		}
		return out;
	}

	@SuppressWarnings("null")
	@Override
	protected void defineSynchedData(@SuppressWarnings("null") SynchedEntityData.Builder builder) {
		builder.define(TREE_DATA, new CompoundTag());
		builder.define(DURATION_TICKS, 20);
		builder.define(FALL_DIR, (byte) Direction.NORTH.get3DDataValue());
	}

	@SuppressWarnings("null")
	@Override
	protected void readAdditionalSaveData(@SuppressWarnings("null") CompoundTag tag) {
		if (tag.contains("TreeData", Tag.TAG_COMPOUND)) {
			this.entityData.set(TREE_DATA, tag.getCompound("TreeData"));
		}
		if (tag.contains("Duration", Tag.TAG_INT)) {
			this.entityData.set(DURATION_TICKS, tag.getInt("Duration"));
		}
		if (tag.contains("FallDir", Tag.TAG_BYTE)) {
			this.entityData.set(FALL_DIR, tag.getByte("FallDir"));
		}
	}

	@SuppressWarnings("null")
	@Override
	protected void addAdditionalSaveData(@SuppressWarnings("null") CompoundTag tag) {
		tag.put("TreeData", this.entityData.get(TREE_DATA));
		tag.putInt("Duration", this.entityData.get(DURATION_TICKS));
		tag.putByte("FallDir", this.entityData.get(FALL_DIR));
	}

	@Override
	public void tick() {
		super.tick();

		if (this.level().isClientSide) {
			return;
		}

		int duration = this.getDurationTicks();
		if (this.tickCount < duration) {
			if (this.tickCount % 2 == 0) {
				emitFallParticles(this.getProgress(0.0F));
			}
			return;
		}

		// End of animation: strong impact feedback.
		dropAtImpactOnce();
		playImpactEffects();
		this.discard();
	}

	public float getProgress(float partialTick) {
		int duration = Math.max(1, this.getDurationTicks());
		return Mth.clamp(((float) this.tickCount + partialTick) / (float) duration, 0.0F, 1.0F);
	}

	@SuppressWarnings("null")
	public Direction getFallDirection() {
		return Direction.from3DDataValue(this.entityData.get(FALL_DIR));
	}

	@SuppressWarnings("null")
	public int getDurationTicks() {
		return this.entityData.get(DURATION_TICKS);
	}

	public int getWoodDrop() {
		@SuppressWarnings("null")
		CompoundTag tag = this.entityData.get(TREE_DATA);
		return tag.getInt(TAG_WOOD);
	}

	public List<Piece> getPieces() {
		@SuppressWarnings("null")
		CompoundTag tag = this.entityData.get(TREE_DATA);
		ListTag list = tag.getList(TAG_PIECES, Tag.TAG_COMPOUND);
		List<Piece> out = new ArrayList<>(list.size());
		for (int i = 0; i < list.size(); i++) {
			CompoundTag p = list.getCompound(i);
			int dx = p.getInt(P_DX);
			int dy = p.getInt(P_DY);
			int dz = p.getInt(P_DZ);
			int blockId = p.getInt(P_BLOCK);
			int facing2d = p.getInt(P_FACING);
			out.add(new Piece(dx, dy, dz, blockId, facing2d));
		}
		return out;
	}

	@SuppressWarnings("null")
	public BlockState resolveBlockState(Piece piece) {
		Block block = BuiltInRegistries.BLOCK.byId(piece.blockId);
		BlockState state = block.defaultBlockState();
		if (state.hasProperty(HorizontalDirectionalBlock.FACING)) {
			Direction dir = Direction.from2DDataValue(piece.facing2d);
			state = state.setValue(HorizontalDirectionalBlock.FACING, dir);
		} else if (state.hasProperty(net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING)) {
			Direction dir = Direction.from2DDataValue(piece.facing2d);
			state = state.setValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING, dir);
		}
		return state;
	}

	public static final class Piece {
		public final int dx;
		public final int dy;
		public final int dz;
		public final int blockId;
		public final int facing2d;

		public Piece(int dx, int dy, int dz, int blockId, int facing2d) {
			this.dx = dx;
			this.dy = dy;
			this.dz = dz;
			this.blockId = blockId;
			this.facing2d = facing2d;
		}
	}
}
