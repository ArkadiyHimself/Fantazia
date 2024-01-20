package net.arkadiyhimself.combatimprovement.Items.Weapons.Melee;

import net.arkadiyhimself.combatimprovement.util.Capability.ItemStack.FragileSword.AttachFragileBlade;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.*;
import org.jetbrains.annotations.Nullable;

public class FragileBlade extends MeleeWeaponItem {
    public FragileBlade() {
        super(new Item.Properties().stacksTo(1).defaultDurability(1024),-1.5f, 3, "fragile_blade");
    }
    @Override
    public @Nullable CompoundTag getShareTag(ItemStack stack) {
        super.getShareTag(stack);
        return AttachFragileBlade.getUnwrap(stack).serializeNBT(true);
    }
    @Override
    public void readShareTag(ItemStack stack, @Nullable CompoundTag nbt) {
        AttachFragileBlade.get(stack).ifPresent(fragileBladeCap -> fragileBladeCap.deserializeNBT(nbt, true));
        super.readShareTag(stack,nbt);
    }
}
