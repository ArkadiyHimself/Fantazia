package net.arkadiyhimself.fantazia.api.attachment.entity.living_data.holders;

import com.google.common.collect.Maps;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.advanced.dynamicattributemodifying.DynamicAttributeModifier;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_data.LivingDataHolder;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders.EuphoriaHolder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Map;

public class DAMHolder extends LivingDataHolder {

    private final Map<ResourceLocation, DynamicAttributeModifier> modifierMap = Maps.newHashMap();

    public DAMHolder(LivingEntity livingEntity) {
        super(livingEntity, Fantazia.res("dynamic_attribute_modifiers"));

        addDAM(new DynamicAttributeModifier(Attributes.ATTACK_SPEED, Fantazia.res("euphoria_attack_speed"),0.7f, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, EuphoriaHolder.MODIFIER));
        addDAM(new DynamicAttributeModifier(Attributes.MOVEMENT_SPEED, Fantazia.res("euphoria_movement_speed"),0.35f, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, EuphoriaHolder.MODIFIER));
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        return new CompoundTag();
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag compoundTag) {

    }

    @Override
    public void serverTick() {
        modifierMap.values().forEach(dynamicAttributeModifier -> dynamicAttributeModifier.tick(getEntity()));
    }

    public void addDAM(DynamicAttributeModifier dynamicAttributeModifier) {
        ResourceLocation id = dynamicAttributeModifier.getId();
        if (!modifierMap.containsKey(id)) modifierMap.put(id, dynamicAttributeModifier);
    }

    public void removeDAM(ResourceLocation id) {
        if (!modifierMap.containsKey(id)) return;
        modifierMap.get(id).tryRemove(getEntity());
        modifierMap.remove(id);
    }

    public void removeDAM(DynamicAttributeModifier dynamicAttributeModifier) {
        removeDAM(dynamicAttributeModifier.getId());
    }
}
