package net.arkadiyhimself.fantazia.api.attachment.entity.living_data.holders;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_data.LivingDataHolder;
import net.arkadiyhimself.fantazia.entities.ThrownHatchet;
import net.arkadiyhimself.fantazia.items.weapons.Range.HatchetItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public class StuckHatchetHolder extends LivingDataHolder {

    private int delay = 0;
    private ItemStack STACK = ItemStack.EMPTY;

    public StuckHatchetHolder(LivingEntity livingEntity) {
        super(livingEntity, Fantazia.res("stuck_hatchet"));
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("delay", delay);
        if (!STACK.isEmpty()) tag.put("hatchet", STACK.save(provider));
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag compoundTag) {
        delay = compoundTag.getInt("delay");
        STACK = ItemStack.parseOptional(provider, compoundTag.getCompound("hatchet"));
    }

    @Override
    public CompoundTag serializeInitial() {
        return new CompoundTag();
    }

    @Override
    public void deserializeInitial(CompoundTag tag) {
    }

    @Override
    public void serverTick() {
        if (delay > 0) delay--;
        else dropHatchet();
    }

    public boolean stuck(ThrownHatchet entity) {
        if (!STACK.isEmpty()) return false;
        ItemStack item = entity.getPickupItem();
        delay = 100;
        if (item.getItem() instanceof HatchetItem) STACK = item;
        return true;
    }
    public void dropHatchet() {
        if (STACK == ItemStack.EMPTY) return;
        ThrownHatchet hatchetEnt = new ThrownHatchet(getEntity().level(), getEntity().position(), STACK.copy());
         getEntity().level().addFreshEntity(hatchetEnt);
        STACK = ItemStack.EMPTY;
    }
}
