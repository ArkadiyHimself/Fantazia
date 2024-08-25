package net.arkadiyhimself.fantazia.api.capability.itemstack;

import dev._100media.capabilitysyncer.core.ItemStackCapability;
import net.arkadiyhimself.fantazia.api.capability.IDamageReacting;
import net.arkadiyhimself.fantazia.api.capability.ITicking;
import net.arkadiyhimself.fantazia.api.capability.itemstack.stackdata.CommonStackData;
import net.arkadiyhimself.fantazia.api.capability.itemstack.stackdata.HiddenPotential;
import net.arkadiyhimself.fantazia.registries.FTZItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.apache.commons.compress.utils.Lists;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;

public class StackDataManager extends ItemStackCapability {
    private final List<StackDataHolder> STACK_DATA = Lists.newArrayList();
    public StackDataManager(ItemStack itemStack) {
        super(itemStack);
        StackDataProvider.provide(this);
    }

    @Override
    public CompoundTag serializeNBT(boolean toDisk) {
        CompoundTag tag = new CompoundTag();

        for (StackDataHolder holder : STACK_DATA) if (holder.ID() != null) tag.put(holder.ID(), holder.serialize(toDisk));

        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt, boolean fromDisk) {
        for (StackDataHolder holder : STACK_DATA) if (nbt.contains(holder.ID())) holder.deserialize(nbt.getCompound(holder.ID()), fromDisk);
    }

    public void tick() {
        STACK_DATA.stream().filter(ITicking.class::isInstance).forEach(stackDataHolder -> ((ITicking)stackDataHolder).tick());
    }
    public void onHit(LivingHurtEvent event) {
        STACK_DATA.forEach(stackDataHolder -> {
            if (stackDataHolder instanceof IDamageReacting iDamageReacting) iDamageReacting.onHit(event);
        });
    }
    public void grantData(Function<ItemStack, StackDataHolder> stackData) {
        StackDataHolder dataHolder = stackData.apply(itemStack);
        if (hasData(dataHolder.getClass())) return;
        STACK_DATA.add(dataHolder);
    }
    public <T extends StackDataHolder> LazyOptional<T> getData(Class<T> tClass) {
        T ability = takeData(tClass);
        return ability == null ? LazyOptional.empty() : LazyOptional.of(() -> ability);
    }
    @Nullable
    public <T extends StackDataHolder> T takeData(Class<T> tClass) {
        for (StackDataHolder dataHolder : STACK_DATA) if (tClass == dataHolder.getClass()) return tClass.cast(dataHolder);
        return null;
    }
    public <T extends StackDataHolder> boolean hasData(Class<T> tClass) {
        for (StackDataHolder dataHolder : STACK_DATA) if (tClass.isInstance(dataHolder)) return true;
        return false;
    }
    private static class StackDataProvider {
        private static void provide(StackDataManager stackDataManager) {
            Item item = stackDataManager.itemStack.getItem();
            if (item == FTZItems.FRAGILE_BLADE.get()) stackDataManager.grantData(HiddenPotential::new);
            stackDataManager.grantData(CommonStackData::new);
        }
    }
}
