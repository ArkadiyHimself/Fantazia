package net.arkadiyhimself.fantazia.api.capability.entity.data.newdata;

import com.google.common.collect.Maps;
import net.arkadiyhimself.fantazia.advanced.aura.AuraInstance;
import net.arkadiyhimself.fantazia.advanced.aura.BasicAura;
import net.arkadiyhimself.fantazia.api.capability.entity.data.DataHolder;
import net.arkadiyhimself.fantazia.items.casters.AuraCaster;
import net.arkadiyhimself.fantazia.util.wheremagichappens.InventoryHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.Map;

public class AuraOwning<M extends LivingEntity> extends DataHolder {
    private final Map<AuraCaster, AuraInstance<? extends Entity, Entity>> CURIO_AURAS = Maps.newHashMap();
    private final M livingEntity;
    public AuraOwning(M livingEntity) {
        super(livingEntity);
        this.livingEntity = livingEntity;
    }
    @Override
    public M getEntity() {
        return livingEntity;
    }
    @SuppressWarnings("unchecked")
    public void onCurioEquip(ItemStack stack) {
        if (stack.getItem() instanceof AuraCaster caster && caster.getBasicAura() != null && !CURIO_AURAS.containsKey(caster)) CURIO_AURAS.put(caster, new AuraInstance<>(getEntity(), (BasicAura<? extends Entity, Entity>) caster.getBasicAura(), getEntity().level()));
    }
    public void onCurioUnEquip(ItemStack stack) {
        if (stack.getItem() instanceof AuraCaster caster && caster.getBasicAura() != null && CURIO_AURAS.containsKey(caster) && InventoryHelper.duplicatingCurio(getEntity(), caster) <= 1) {
            CURIO_AURAS.get(caster).discard();
            CURIO_AURAS.remove(caster);
        }
    }
    public void clearAll() {
        CURIO_AURAS.values().forEach(AuraInstance::discard);
        CURIO_AURAS.clear();
    }
}
