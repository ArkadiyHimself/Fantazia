package net.arkadiyhimself.fantazia.mixin;

import net.arkadiyhimself.fantazia.events.ClientEvents;
import net.arkadiyhimself.fantazia.registries.FTZAttributes;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Inventory.class)
public abstract class MixinInventory {

    @Shadow @Final public NonNullList<ItemStack> items;

    @Shadow @Final public Player player;

    @Inject(at = @At("HEAD"), method = "swapPaint")
    private void updateBlocks(double direction, CallbackInfo ci) {
        BlockPos blockPos = player.blockPosition();
        int x0 = blockPos.getX();
        int y0 = blockPos.getY();
        int z0 = blockPos.getZ();

        if (ClientEvents.heldAuraCaster != null) {
            float radius = ClientEvents.heldAuraCaster.getAura().value().getRadius();

            AttributeInstance instance = player.getAttribute(FTZAttributes.AURA_RANGE_ADDITION);
            float range = instance == null ? 0 : (float) instance.getValue();

            int finalRange = (int) (radius + Math.max(0, range));
            Minecraft.getInstance().levelRenderer.setBlocksDirty(x0 - finalRange, y0 - finalRange, z0 - finalRange, x0 + finalRange, y0 + finalRange, z0 + finalRange);
        }
    }
}
