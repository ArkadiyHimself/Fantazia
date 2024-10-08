package net.arkadiyhimself.fantazia.api.attachment.level.holders;

import com.google.common.collect.Maps;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.attachment.level.LevelAttributeHolder;
import net.arkadiyhimself.fantazia.data.talents.AttributeTalent;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.HashMap;
import java.util.Map;

public class TalentAttributeModifiersHolder extends LevelAttributeHolder {
    private final HashMap<ResourceLocation, AttributeModifier> talentAttributeModifiers = Maps.newHashMap();
    public TalentAttributeModifiersHolder(Level level) {
        super(level, Fantazia.res("talent_attribute_modifiers"));
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        ListTag talents = new ListTag();
        ListTag modifiers = new ListTag();

        for (Map.Entry<ResourceLocation, AttributeModifier> entry : talentAttributeModifiers.entrySet()) {
            talents.add(StringTag.valueOf(entry.getKey().toString()));
            modifiers.add(entry.getValue().save());
        }

        tag.put("talents", talents);
        tag.put("modifiers", modifiers);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag compoundTag) {
        talentAttributeModifiers.clear();
        if (!compoundTag.contains("talents") || !compoundTag.contains("modifiers")) return;

        ListTag talents = compoundTag.getList("talents", Tag.TAG_STRING);
        ListTag modifiers = compoundTag.getList("modifiers", Tag.TAG_COMPOUND);

        if (talents.size() != modifiers.size()) return;

        for (int i = 0; i < talents.size(); i++) talentAttributeModifiers.put(ResourceLocation.parse(talents.getString(i)), AttributeModifier.load(modifiers.getCompound(i)));
    }

    public AttributeModifier getOrCreateModifier(AttributeTalent talent) {
        ResourceLocation id = talent.getID();
        talentAttributeModifiers.computeIfAbsent(id, res -> new AttributeModifier(talent.getID(), talent.getAmount(), talent.getOperation()));
        return talentAttributeModifiers.get(id);
    }
}
