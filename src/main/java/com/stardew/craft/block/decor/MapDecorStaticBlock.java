package com.stardew.craft.block.decor;

import com.stardew.craft.block.shape.ModelVoxelShapeCache;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("null")
public class MapDecorStaticBlock extends Block {
    public enum Part implements StringRepresentable {
        MAIN("main"),
        EXTENSION("extension");

        private final String name;

        Part(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }

    public static final EnumProperty<Part> PART = EnumProperty.create("part", Part.class);
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    private static final Map<String, VoxelShape> SHAPE_CACHE = new ConcurrentHashMap<>();
    private static final Map<String, VoxelShape> ORIENTED_SHAPE_CACHE = new ConcurrentHashMap<>();
    private static final double EPS = 1.0E-6;

    private final String modelId;
    private volatile Set<CellOffset> localOccupiedOffsets;
    private final Map<Direction, Set<CellOffset>> occupiedOffsetsByFacing = new ConcurrentHashMap<>();

    public MapDecorStaticBlock(Properties properties, String modelId) {
        super(properties);
        this.modelId = modelId;
        registerDefaultState(stateDefinition.any().setValue(PART, Part.MAIN).setValue(FACING, Direction.NORTH));
    }

    // Compatibility constructor to keep existing registrations unchanged.
    public MapDecorStaticBlock(Properties properties, String modelId, int extensionOffsetX, int extensionOffsetY, int extensionOffsetZ) {
        this(properties, modelId);
    }

    @Override
    protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PART, FACING);
    }

    @Override
    public BlockState rotate(@Nonnull BlockState state, @Nonnull Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(@Nonnull BlockState state, @Nonnull Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        return resolvePartShape(state, level, pos, state.getValue(PART));
    }

    @Override
    public VoxelShape getCollisionShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        return resolvePartShape(state, level, pos, state.getValue(PART));
    }

    private VoxelShape resolvePartShape(BlockState state, BlockGetter level, BlockPos pos, Part part) {
        Direction facing = state.hasProperty(FACING) ? state.getValue(FACING) : Direction.NORTH;
        if (part == Part.MAIN) {
            return clippedShapeForCell(facing, 0, 0, 0);
        }
        CellOffset offset = findOffsetForExtension(level, pos, state);
        if (offset == null) {
            return Shapes.empty();
        }
        return clippedShapeForCell(facing, offset.dx, offset.dy, offset.dz);
    }

    private VoxelShape clippedShapeForCell(Direction facing, int cx, int cy, int cz) {
        String key = modelId + "#" + facing.getSerializedName() + "#" + cx + "," + cy + "," + cz;
        return SHAPE_CACHE.computeIfAbsent(key, unused -> clipToCell(orientedShape(facing), cx, cy, cz));
    }

    private VoxelShape orientedShape(Direction facing) {
        String key = modelId + "#" + facing.getSerializedName();
        return ORIENTED_SHAPE_CACHE.computeIfAbsent(key, unused -> rotateShapeY(ModelVoxelShapeCache.shapeFromModelId(modelId), facing));
    }

    private static VoxelShape rotateShapeY(VoxelShape shape, Direction facing) {
        if (facing == Direction.NORTH || shape.isEmpty()) {
            return shape;
        }
        final VoxelShape[] out = new VoxelShape[] { Shapes.empty() };
        shape.forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> {
            Aabb rotated = rotateAabbY(minX, minY, minZ, maxX, maxY, maxZ, facing);
            out[0] = Shapes.or(out[0], Shapes.box(rotated.minX, rotated.minY, rotated.minZ, rotated.maxX, rotated.maxY, rotated.maxZ));
        });
        return out[0].optimize();
    }

    private static Aabb rotateAabbY(double minX, double minY, double minZ,
                                    double maxX, double maxY, double maxZ,
                                    Direction facing) {
        return switch (facing) {
            case EAST -> new Aabb(1.0 - maxZ, minY, minX, 1.0 - minZ, maxY, maxX);
            case SOUTH -> new Aabb(1.0 - maxX, minY, 1.0 - maxZ, 1.0 - minX, maxY, 1.0 - minZ);
            case WEST -> new Aabb(minZ, minY, 1.0 - maxX, maxZ, maxY, 1.0 - minX);
            default -> new Aabb(minX, minY, minZ, maxX, maxY, maxZ);
        };
    }

    private static VoxelShape clipToCell(VoxelShape shape, int cx, int cy, int cz) {
        if (shape.isEmpty()) {
            return Shapes.empty();
        }
        final VoxelShape[] out = new VoxelShape[] { Shapes.empty() };
        shape.forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> {
            double cMinX = Math.max(cx, minX);
            double cMinY = Math.max(cy, minY);
            double cMinZ = Math.max(cz, minZ);
            double cMaxX = Math.min(cx + 1.0, maxX);
            double cMaxY = Math.min(cy + 1.0, maxY);
            double cMaxZ = Math.min(cz + 1.0, maxZ);
            if (cMaxX > cMinX && cMaxY > cMinY && cMaxZ > cMinZ) {
                out[0] = Shapes.or(
                    out[0],
                    Shapes.box(cMinX - cx, cMinY - cy, cMinZ - cz, cMaxX - cx, cMaxY - cy, cMaxZ - cz)
                );
            }
        });
        return out[0].optimize();
    }

    private Set<CellOffset> localOccupiedOffsets() {
        Set<CellOffset> cached = localOccupiedOffsets;
        if (cached != null) {
            return cached;
        }
        synchronized (this) {
            if (localOccupiedOffsets != null) {
                return localOccupiedOffsets;
            }
            Set<CellOffset> discovered = new LinkedHashSet<>();
            VoxelShape shape = ModelVoxelShapeCache.shapeFromModelId(modelId);
            shape.forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> {
                int minCX = (int) Math.floor(minX + EPS);
                int minCY = (int) Math.floor(minY + EPS);
                int minCZ = (int) Math.floor(minZ + EPS);
                int maxCX = (int) Math.ceil(maxX - EPS) - 1;
                int maxCY = (int) Math.ceil(maxY - EPS) - 1;
                int maxCZ = (int) Math.ceil(maxZ - EPS) - 1;
                for (int x = minCX; x <= maxCX; x++) {
                    for (int y = minCY; y <= maxCY; y++) {
                        for (int z = minCZ; z <= maxCZ; z++) {
                            discovered.add(new CellOffset(x, y, z));
                        }
                    }
                }
            });
            if (!discovered.contains(CellOffset.ZERO)) {
                discovered.add(CellOffset.ZERO);
            }
            localOccupiedOffsets = discovered;
            return discovered;
        }
    }

    protected Set<CellOffset> occupiedOffsets(Direction facing) {
        return occupiedOffsetsByFacing.computeIfAbsent(facing, unused -> {
            Set<CellOffset> rotated = new LinkedHashSet<>();
            for (CellOffset offset : localOccupiedOffsets()) {
                rotated.add(offset.rotateY(facing));
            }
            if (!rotated.contains(CellOffset.ZERO)) {
                rotated.add(CellOffset.ZERO);
            }
            return rotated;
        });
    }

    private boolean hasExtensions() {
        return localOccupiedOffsets().size() > 1;
    }

    protected CellOffset findOffsetForExtension(BlockGetter level, BlockPos pos, BlockState extensionState) {
        Direction facing = extensionState.getValue(FACING);
        for (CellOffset offset : occupiedOffsets(facing)) {
            if (offset.isZero()) {
                continue;
            }
            BlockPos candidateMainPos = pos.offset(-offset.dx, -offset.dy, -offset.dz);
            BlockState candidate = level.getBlockState(candidateMainPos);
            if (candidate.is(this) && candidate.getValue(PART) == Part.MAIN && candidate.getValue(FACING) == facing) {
                return offset;
            }
        }
        return null;
    }

    protected BlockPos findMainPos(BlockGetter level, BlockPos pos, BlockState state) {
        if (state.getValue(PART) == Part.MAIN) {
            return pos;
        }
        CellOffset offset = findOffsetForExtension(level, pos, state);
        return offset == null ? null : pos.offset(-offset.dx, -offset.dy, -offset.dz);
    }

    protected boolean canPlaceAtFacing(Level level, BlockPos pos, Direction facing, BlockPlaceContext context) {
        for (CellOffset offset : occupiedOffsets(facing)) {
            if (offset.isZero()) {
                continue;
            }
            BlockPos extensionPos = pos.offset(offset.dx, offset.dy, offset.dz);
            if (!level.getWorldBorder().isWithinBounds(extensionPos)) {
                return false;
            }
            if (!level.getBlockState(extensionPos).canBeReplaced(context)) {
                return false;
            }
        }
        return true;
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(@Nonnull BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();
        Direction facing = context.getHorizontalDirection().getOpposite();
        if (!canPlaceAtFacing(level, pos, facing, context)) {
            return null;
        }
        return defaultBlockState().setValue(PART, Part.MAIN).setValue(FACING, facing);
    }

    @Override
    public void setPlacedBy(@Nonnull Level level,
                            @Nonnull BlockPos pos,
                            @Nonnull BlockState state,
                            @Nullable net.minecraft.world.entity.LivingEntity placer,
                            @Nonnull ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (level.isClientSide || !hasExtensions() || state.getValue(PART) != Part.MAIN) {
            return;
        }
        Direction facing = state.getValue(FACING);
        for (CellOffset offset : occupiedOffsets(facing)) {
            if (offset.isZero()) {
                continue;
            }
            BlockPos extensionPos = pos.offset(offset.dx, offset.dy, offset.dz);
            level.setBlock(extensionPos, state.setValue(PART, Part.EXTENSION), 3);
        }
    }

    @Override
    protected List<ItemStack> getDrops(@Nonnull BlockState state, @Nonnull LootParams.Builder params) {
        if (state.getValue(PART) == Part.EXTENSION) {
            return List.of();
        }
        return super.getDrops(state, params);
    }

    @Override
    protected boolean canSurvive(@Nonnull BlockState state, @Nonnull LevelReader level, @Nonnull BlockPos pos) {
        if (state.getValue(PART) == Part.MAIN) {
            return true;
        }
        return findOffsetForExtension(level, pos, state) != null;
    }

    @Override
    protected BlockState updateShape(@Nonnull BlockState state,
                                     @Nonnull net.minecraft.core.Direction direction,
                                     @Nonnull BlockState neighborState,
                                     @Nonnull net.minecraft.world.level.LevelAccessor level,
                                     @Nonnull BlockPos pos,
                                     @Nonnull BlockPos neighborPos) {
        return state.canSurvive(level, pos) ? state : Blocks.AIR.defaultBlockState();
    }

    @Override
    public void onRemove(@Nonnull BlockState state,
                         @Nonnull Level level,
                         @Nonnull BlockPos pos,
                         @Nonnull BlockState newState,
                         boolean isMoving) {
        if (!state.is(newState.getBlock()) && hasExtensions()) {
            BlockPos mainPos = findMainPos(level, pos, state);
            if (mainPos != null) {
                Direction facing = mainFacingForCleanup(level, mainPos, state);
                for (CellOffset offset : occupiedOffsets(facing)) {
                    BlockPos target = mainPos.offset(offset.dx, offset.dy, offset.dz);
                    if (target.equals(pos)) {
                        continue;
                    }
                    BlockState targetState = level.getBlockState(target);
                    if (targetState.is(this)) {
                        level.removeBlock(target, false);
                    }
                }
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public BlockState playerWillDestroy(@Nonnull Level level,
                                        @Nonnull BlockPos pos,
                                        @Nonnull BlockState state,
                                        @Nonnull net.minecraft.world.entity.player.Player player) {
        if (!level.isClientSide && hasExtensions() && state.getValue(PART) == Part.EXTENSION) {
            BlockPos mainPos = findMainPos(level, pos, state);
            if (mainPos != null) {
                if (!player.isCreative()) {
                    popResource(level, mainPos, new ItemStack(this));
                }
                Direction facing = mainFacingForCleanup(level, mainPos, state);
                for (CellOffset offset : occupiedOffsets(facing)) {
                    BlockPos target = mainPos.offset(offset.dx, offset.dy, offset.dz);
                    if (level.getBlockState(target).is(this)) {
                        level.setBlock(target, Blocks.AIR.defaultBlockState(), 35);
                    }
                }
                return state;
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    private Direction mainFacingForCleanup(BlockGetter level, BlockPos mainPos, BlockState fallbackState) {
        BlockState mainState = level.getBlockState(mainPos);
        if (mainState.is(this) && mainState.hasProperty(FACING)) {
            return mainState.getValue(FACING);
        }
        return fallbackState.hasProperty(FACING) ? fallbackState.getValue(FACING) : Direction.NORTH;
    }

    protected record CellOffset(int dx, int dy, int dz) {
        private static final CellOffset ZERO = new CellOffset(0, 0, 0);

        private boolean isZero() {
            return dx == 0 && dy == 0 && dz == 0;
        }

        private CellOffset rotateY(Direction facing) {
            return switch (facing) {
                case EAST -> new CellOffset(-dz, dy, dx);
                case SOUTH -> new CellOffset(-dx, dy, -dz);
                case WEST -> new CellOffset(dz, dy, -dx);
                default -> this;
            };
        }
    }

    private record Aabb(double minX, double minY, double minZ,
                        double maxX, double maxY, double maxZ) {
    }
}
