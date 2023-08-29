package net.arkadiyhimself.combatimprovement.Registries.Items.Weapons;

import net.arkadiyhimself.combatimprovement.CombatImprovement;
import net.arkadiyhimself.combatimprovement.HandlersAndHelpers.UsefulMethods;
import net.arkadiyhimself.combatimprovement.util.Capability.ItemStack.FragileSword.AttachFragileBlade;
import net.arkadiyhimself.combatimprovement.util.Capability.ItemStack.FragileSword.FragileBladeCap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FragileBlade extends WeaponItem {
    public FragileBlade() {
        super(new Item.Properties().stacksTo(1).defaultDurability(1024),-1.5f, 4);
    }
    @Override
    public @Nullable CompoundTag getShareTag(ItemStack stack) {
        super.getShareTag(stack);
        return AttachFragileBlade.getUnwrap(stack).serializeNBT(true);
    }
    @Override
    public void readShareTag(ItemStack stack, @Nullable CompoundTag nbt) {
        AttachFragileBlade.getUnwrap(stack).deserializeNBT(nbt, true);
        super.readShareTag(stack,nbt);
    }
}
