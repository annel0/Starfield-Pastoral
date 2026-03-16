# -*- coding: utf-8 -*-
import io

path = 'src/main/java/com/stardew/craft/event/MiningBlockBreakHandler.java'
with io.open(path, 'r', encoding='utf-8') as f:
    text = f.read()

old_logic = '''                // 生成高光实体 (BlockDisplay不会碰撞，且支持发光)
                var display = net.minecraft.world.entity.EntityType.BLOCK_DISPLAY.create(serverLevel);
                if (display != null) {
                    display.setPos(ladderPos.getX(), ladderPos.getY(), ladderPos.getZ());
                    display.setGlowingTag(true);
                    display.addTag("mine_ladder_highlight");
                    net.minecraft.nbt.CompoundTag tag = new net.minecraft.nbt.CompoundTag();
                    display.saveWithoutId(tag);
                    tag.put("block_state", net.minecraft.nbt.NbtUtils.writeBlockState(ModBlocks.MINE_LADDER.get().defaultBlockState()));
                    display.load(tag);
                    serverLevel.addFreshEntity(display);
                }'''

new_logic = '''                // 发送高光包给挖掘梯子的玩家
                net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(player, new com.stardew.craft.network.MiningLadderHighlightPacket(ladderPos));'''

text = text.replace(old_logic, new_logic)

with io.open(path, 'w', encoding='utf-8') as f:
    f.write(text)
print('Replaced')
