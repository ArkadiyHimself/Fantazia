package net.arkadiyhimself.fantazia.common.api.attachment.entity.living_data.holders;

import com.google.common.collect.Maps;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.common.advanced.dynamic_attribute_modifier.DynamicAttributeModifier;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.living_data.LivingDataHolder;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.holders.EuphoriaHolder;
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
        super(livingEntity, Fantazia.location("dynamic_attribute_modifiers"));

        addOrReplaceDAM(new DynamicAttributeModifier(Attributes.ATTACK_SPEED, Fantazia.location("euphoria_attack_speed"),0.7f, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, EuphoriaHolder.MODIFIER));
        addOrReplaceDAM(new DynamicAttributeModifier(Attributes.MOVEMENT_SPEED, Fantazia.location("euphoria_movement_speed"),0.35f, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, EuphoriaHolder.MODIFIER));
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
        modifierMap.values().forEach(dynamicAttributeModifier -> dynamicAttributeModifier.tickOn(getEntity()));
    }

    public void addOrReplaceDAM(DynamicAttributeModifier dynamicAttributeModifier) {
        ResourceLocation id = dynamicAttributeModifier.getId();
        removeDAM(id);
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
