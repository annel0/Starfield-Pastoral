
$content = Get-Content src/main/java/com/stardew/craft/network/payload/CookingPotCookSubmitPayload.java -Raw

if ($content -notmatch "ClientboundContainerSetSlotPacket") {
    $content = $content -replace "import net.minecraft.world.item.ItemStack;", "import net.minecraft.world.item.ItemStack;`nimport net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;"
}

$old1 = "player.containerMenu.setCarried(onCursor);"
$new1 = @"
                player.containerMenu.setCarried(onCursor);
                player.connection.send(new ClientboundContainerSetSlotPacket(-1, player.containerMenu.getStateId(), -1, onCursor));
"@

$old2 = "player.containerMenu.setCarried(carried);"
$new2 = @"
        player.containerMenu.setCarried(carried);
        player.connection.send(new ClientboundContainerSetSlotPacket(-1, player.containerMenu.getStateId(), -1, carried));
"@

$content = $content.Replace($old1, $new1)
$content = $content.Replace($old2, $new2)

Set-Content -Path src/main/java/com/stardew/craft/network/payload/CookingPotCookSubmitPayload.java -Value $content


