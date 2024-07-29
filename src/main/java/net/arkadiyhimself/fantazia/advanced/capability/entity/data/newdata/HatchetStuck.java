package net.arkadiyhimself.fantazia.advanced.capability.entity.data.newdata;

import net.arkadiyhimself.fantazia.advanced.capability.entity.data.DataHolder;
import net.arkadiyhimself.fantazia.advanced.capability.itemstack.StackDataGetter;
import net.arkadiyhimself.fantazia.advanced.capability.itemstack.StackDataManager;
import net.arkadiyhimself.fantazia.advanced.capability.itemstack.stackdata.CommonStackData;
import net.arkadiyhimself.fantazia.entities.ThrownHatchet;
import net.arkadiyhimself.fantazia.items.weapons.Range.HatchetItem;
import net.arkadiyhimself.fantazia.util.interfaces.ITicking;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class HatchetStuck extends DataHolder implements ITicking {
    private int delay = 0;
    private ItemStack STACK = ItemStack.EMPTY;
    public HatchetStuck(LivingEntity livingEntity) {
        super(livingEntity);
    }

    @Override
    public void tick() {
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
        StackDataManager stackDataManager = StackDataGetter.getUnwrap(hatchetEnt.getPickupItem());
        if (stackDataManager != null) stackDataManager.getData(CommonStackData.class).ifPresent(CommonStackData::dropped);
        getEntity().level().addFreshEntity(hatchetEnt);
        STACK = ItemStack.EMPTY;
    }
}
