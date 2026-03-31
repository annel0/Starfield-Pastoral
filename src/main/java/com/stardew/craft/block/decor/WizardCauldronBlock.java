package com.stardew.craft.block.decor;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

@SuppressWarnings("null")
public class WizardCauldronBlock extends MapDecorStaticBlock {

    public WizardCauldronBlock(Properties properties) {
        super(properties, "stardewcraft:decor/common/wizard_cauldron");
    }

    @Override
    public void animateTick(@Nonnull BlockState state,
                            @Nonnull Level level,
                            @Nonnull BlockPos pos,
                            @Nonnull RandomSource random) {
        super.animateTick(state, level, pos, random);
        if (state.getValue(PART) != Part.MAIN) {
            return;
        }

        // Green witch-effect particles over the cauldron surface
        // Model plane from [-10,25,-9] to [25,25,24] in 1/16 units
        // Convert to block coords: [-10/16, 25/16, -9/16] to [25/16, 25/16, 24/16]
        double minX = -10.0 / 16.0;
        double maxX = 25.0 / 16.0;
        double minZ = -9.0 / 16.0;
        double maxZ = 24.0 / 16.0;
        double y = 25.0 / 16.0;

        for (int i = 0; i < 3; i++) {
            double x = pos.getX() + minX + random.nextDouble() * (maxX - minX);
            double py = pos.getY() + y + random.nextDouble() * 0.1;
            double z = pos.getZ() + minZ + random.nextDouble() * (maxZ - minZ);
            // Green-tinted witch particle (WITCH uses green particles)
            level.addParticle(ParticleTypes.WITCH, x, py, z, 0.0, 0.05, 0.0);
        }
    }
}
