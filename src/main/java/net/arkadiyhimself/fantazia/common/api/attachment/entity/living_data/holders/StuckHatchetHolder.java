package net.arkadiyhimself.fantazia.common.api.attachment.entity.living_data.holders;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.living_data.LivingDataHolder;
import net.arkadiyhimself.fantazia.common.entity.ThrownHatchet;
import net.arkadiyhimself.fantazia.common.item.weapons.Range.HatchetItem;
import net.arkadiyhimself.fantazia.networking.IPacket;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public class StuckHatchetHolder extends LivingDataHolder {

    private int delay = 0;
    private ItemStack itemStack = ItemStack.EMPTY;

    public StuckHatchetHolder(LivingEntity livingEntity) {
        super(livingEntity, Fantazia.location("stuck_hatchet"));
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("delay", delay);
        if (!itemStack.isEmpty()) tag.put("hatchet", itemStack.save(provider));
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag compoundTag) {
        delay = compoundTag.getInt("delay");
        itemStack = ItemStack.parseOptional(provider, compoundTag.getCompound("hatchet"));
    }

    @Override
    public CompoundTag serializeInitial() {
        CompoundTag tag = new CompoundTag();
        if (!itemStack.isEmpty()) tag.put("hatchet", itemStack.save(getEntity().registryAccess()));
        return tag;
    }

    @Override
    public void deserializeInitial(CompoundTag tag) {
        itemStack = ItemStack.parseOptional(getEntity().registryAccess(), tag.getCompound("hatchet"));
    }

    @Override
    public void serverTick() {
        if (delay > 0) delay--;
        else dropHatchet();
    }

    public void tryGetStuck(ThrownHatchet entity) {
        tryGetStuck(entity.getPickupItem());
    }

    public void tryGetStuck(ItemStack stack) {
        if (!itemStack.isEmpty()) return;
        if (stack.getItem() instanceof HatchetItem) {
            this.itemStack = stack;
            this.delay = 100;
        }
        if (!getEntity().level().isClientSide()) IPacket.hatchetStuck(getEntity(), stack);
    }

    public void removeHatchet() {
        itemStack = ItemStack.EMPTY;
        if (!getEntity().level().isClientSide()) IPacket.hatchetRemoved(getEntity());
    }

    public void dropHatchet() {
        if (itemStack == ItemStack.EMPTY) return;
        ThrownHatchet hatchetEnt = new ThrownHatchet(getEntity().level(), getEntity().getEyePosition().add(0,0.15,0), itemStack.copy());
        getEntity().level().addFreshEntity(hatchetEnt);
        removeHatchet();
    }

    public ItemStack getStack() {
        return itemStack;
    }
}
