package net.arkadiyhimself.fantazia.api.attachment.entity.living_data.holders;

import com.google.common.collect.Maps;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.advanced.aura.AuraInstance;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_data.LivingDataHolder;
import net.arkadiyhimself.fantazia.items.casters.AuraCasterItem;
import net.arkadiyhimself.fantazia.util.wheremagichappens.InventoryHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Map;

public class OwnedAurasHolder extends LivingDataHolder {
    private final Map<AuraCasterItem, AuraInstance<? extends Entity>> CURIO_AURAS = Maps.newHashMap();

    public OwnedAurasHolder(LivingEntity livingEntity) {
        super(livingEntity, Fantazia.res("owned_auras"));
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        return new CompoundTag();
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag compoundTag) {
    }

    public void onCurioEquip(ItemStack stack) {
        if (stack.getItem() instanceof AuraCasterItem caster && caster.getBasicAura() != null && !CURIO_AURAS.containsKey(caster)) CURIO_AURAS.put(caster, new AuraInstance<>(getEntity(), caster.getBasicAura()));
    }
    public void onCurioUnEquip(ItemStack stack) {
        if (stack.getItem() instanceof AuraCasterItem caster && caster.getBasicAura() != null && CURIO_AURAS.containsKey(caster) && InventoryHelper.duplicatingCurio(getEntity(), caster) <= 1) {
            CURIO_AURAS.get(caster).discard();
            CURIO_AURAS.remove(caster);
        }
    }
    public void clearAll() {
        CURIO_AURAS.values().forEach(AuraInstance::discard);
        CURIO_AURAS.clear();
    }
}
