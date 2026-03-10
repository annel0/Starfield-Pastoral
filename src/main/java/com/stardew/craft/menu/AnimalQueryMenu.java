package com.stardew.craft.menu;

import com.stardew.craft.animal.data.AnimalWorldData;
import com.stardew.craft.entity.animal.BaseCoopAnimalEntity;
import com.stardew.craft.player.PlayerStardewDataAPI;
import com.stardew.craft.sound.ModSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.AABB;
import javax.annotation.Nonnull;

@SuppressWarnings("null")
public class AnimalQueryMenu extends AbstractContainerMenu {

    private static final AABB FULL_LEVEL_BOX = new AABB(-30_000_000, -64, -30_000_000, 30_000_000, 320, 30_000_000);

    private final Player player;
    private long animalId;
    private int ageDays;
    private int daysToMature;
    private int wasPetToday;
    private int friendship;
    private int allowReproduction;
    private int variantIndex;

    public AnimalQueryMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, -1L, 0, 5, false, 0, false, 0);
    }

    public AnimalQueryMenu(int containerId,
                           Inventory playerInventory,
                           long animalId,
                           int ageDays,
                           int daysToMature,
                           boolean wasPetToday,
                           int friendship,
                           boolean allowReproduction,
                           int variantIndex) {
        super(ModMenuTypes.ANIMAL_QUERY.get(), containerId);
        this.player = playerInventory.player;
        this.animalId = animalId;
        this.ageDays = Math.max(0, ageDays);
        this.daysToMature = Math.max(1, daysToMature);
        this.wasPetToday = wasPetToday ? 1 : 0;
        this.friendship = Math.max(0, friendship);
        this.allowReproduction = allowReproduction ? 1 : 0;
        this.variantIndex = Math.max(0, variantIndex);

        this.addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return (int) AnimalQueryMenu.this.animalId;
            }

            @Override
            public void set(int value) {
                AnimalQueryMenu.this.animalId = value;
            }
        });

        this.addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return AnimalQueryMenu.this.ageDays;
            }

            @Override
            public void set(int value) {
                AnimalQueryMenu.this.ageDays = Math.max(0, value);
            }
        });

        this.addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return AnimalQueryMenu.this.daysToMature;
            }

            @Override
            public void set(int value) {
                AnimalQueryMenu.this.daysToMature = Math.max(1, value);
            }
        });

        this.addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return AnimalQueryMenu.this.wasPetToday;
            }

            @Override
            public void set(int value) {
                AnimalQueryMenu.this.wasPetToday = value > 0 ? 1 : 0;
            }
        });

        this.addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return AnimalQueryMenu.this.friendship;
            }

            @Override
            public void set(int value) {
                AnimalQueryMenu.this.friendship = Math.max(0, value);
            }
        });

        this.addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return AnimalQueryMenu.this.allowReproduction;
            }

            @Override
            public void set(int value) {
                AnimalQueryMenu.this.allowReproduction = value > 0 ? 1 : 0;
            }
        });

        this.addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return AnimalQueryMenu.this.variantIndex;
            }

            @Override
            public void set(int value) {
                AnimalQueryMenu.this.variantIndex = Math.max(0, value);
            }
        });
    }

    public long getAnimalId() {
        return animalId;
    }

    public int getAgeDays() {
        return ageDays;
    }

    public int getDaysToMature() {
        return daysToMature;
    }

    public boolean isBaby() {
        return ageDays < daysToMature;
    }

    public boolean wasPetToday() {
        return wasPetToday > 0;
    }

    public int getFriendship() {
        return friendship;
    }

    public boolean allowReproduction() {
        return allowReproduction > 0;
    }

    public int getVariantIndex() {
        return variantIndex;
    }

    public int getEstimatedSellPrice() {
        int base = switch (variantIndex) {
            case 2 -> 1200; // duck
            case 4 -> 800;  // rabbit
            case 5 -> 10000; // ostrich
            case 6 -> 350;  // dinosaur
            case 7 -> 1500; // cow
            case 8 -> 4000; // goat
            case 9, 10 -> 8000; // sheep / sheared sheep
            case 11 -> 16000; // pig
            default -> 800; // chicken types
        };
        double friendshipRatio = Math.max(0.0, Math.min(1.0, friendship / 1000.0));
        return (int) Math.floor(base * (friendshipRatio + 0.3));
    }

    public void setAllowReproductionValue(boolean allow) {
        this.allowReproduction = allow ? 1 : 0;
    }

    public void handleToggleReproduction(boolean allow) {
        if (!(player instanceof ServerPlayer serverPlayer) || animalId <= 0L) {
            return;
        }
        AnimalWorldData data = AnimalWorldData.get(serverPlayer.serverLevel());
        if (data.setAllowReproduction(animalId, allow)) {
            this.allowReproduction = allow ? 1 : 0;
        }
    }

    public void handleSellAnimal() {
        if (!(player instanceof ServerPlayer serverPlayer) || animalId <= 0L) {
            return;
        }

        int sellPrice = getEstimatedSellPrice();
        AnimalWorldData data = AnimalWorldData.get(serverPlayer.serverLevel());
        if (!data.removeAnimal(animalId)) {
            return;
        }

        PlayerStardewDataAPI.addMoney(serverPlayer, sellPrice);

        for (BaseCoopAnimalEntity entity : serverPlayer.serverLevel().getEntitiesOfClass(BaseCoopAnimalEntity.class, FULL_LEVEL_BOX)) {
            if (entity.getManagedAnimalId() == animalId) {
                serverPlayer.serverLevel().sendParticles(
                    ParticleTypes.CLOUD,
                    entity.getX(),
                    entity.getY() + 0.6D,
                    entity.getZ(),
                    14,
                    0.6D,
                    0.4D,
                    0.6D,
                    0.02D
                );
                serverPlayer.serverLevel().playSound(null, entity.blockPosition(), ModSounds.NEW_RECIPE.get(), SoundSource.PLAYERS, 0.9f, 1.0f);
                serverPlayer.serverLevel().playSound(null, entity.blockPosition(), ModSounds.MONEY.get(), SoundSource.PLAYERS, 0.9f, 1.0f);
                entity.discard();
                break;
            }
        }
        serverPlayer.closeContainer();
    }

    @Override
    public ItemStack quickMoveStack(@Nonnull Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(@Nonnull Player player) {
        return true;
    }
}
