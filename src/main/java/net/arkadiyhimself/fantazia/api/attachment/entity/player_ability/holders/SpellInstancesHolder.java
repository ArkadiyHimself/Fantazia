package net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders;

import com.google.common.collect.Maps;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.advanced.spell.SpellCastResult;
import net.arkadiyhimself.fantazia.advanced.spell.SpellInstance;
import net.arkadiyhimself.fantazia.advanced.spell.types.AbstractSpell;
import net.arkadiyhimself.fantazia.api.attachment.ISyncEveryTick;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.ICurioListener;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.PlayerAbilityHolder;
import net.arkadiyhimself.fantazia.api.custom_registry.FantazicRegistries;
import net.arkadiyhimself.fantazia.items.casters.SpellCasterItem;
import net.arkadiyhimself.fantazia.util.wheremagichappens.FantazicUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Map;

public class SpellInstancesHolder extends PlayerAbilityHolder implements ICurioListener, ISyncEveryTick {

    private final Map<SpellCasterItem, Holder<AbstractSpell>> curioSpells = Maps.newHashMap();
    private final Map<Holder<AbstractSpell>, SpellInstance> spellInstances = Maps.newHashMap();

    public SpellInstancesHolder(@NotNull Player player) {
        super(player, Fantazia.res("spell_instances"));
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();

        ListTag listTag = new ListTag();
        for (SpellInstance spellInstance : spellInstances.values()) listTag.add(spellInstance.serializeNBT(provider));
        tag.put("instances", listTag);

        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag tag) {
        ListTag listTag = tag.getList("instances", ListTag.TAG_COMPOUND);
        if (listTag.isEmpty()) return;

        for (int i = 0; i < listTag.size(); i++) {
            CompoundTag instance = listTag.getCompound(i);
            ResourceLocation id = ResourceLocation.parse(instance.getString("id"));
            Holder<AbstractSpell> abstractSpellHolder = FantazicRegistries.SPELLS.getHolder(id).orElseThrow(() -> new IllegalStateException("Tried to deserialize unknown spell: " + id));
            getOrCreate(abstractSpellHolder).deserializeTick(instance);
        }
    }

    @Override
    public CompoundTag serializeTick() {
        CompoundTag tag = new CompoundTag();

        ListTag listTag = new ListTag();
        for (SpellInstance spellInstance : spellInstances.values()) listTag.add(spellInstance.serializeTick());
        tag.put("instances", listTag);

        return tag;
    }

    @Override
    public void deserializeTick(CompoundTag tag) {
        ListTag listTag = tag.getList("instances", ListTag.TAG_COMPOUND);
        if (listTag.isEmpty()) return;

        for (int i = 0; i < listTag.size(); i++) {
            CompoundTag instance = listTag.getCompound(i);
            ResourceLocation id = ResourceLocation.parse(instance.getString("id"));
            Holder<AbstractSpell> abstractSpellHolder = FantazicRegistries.SPELLS.getHolder(id).orElseThrow(() -> new IllegalStateException("Tried to deserialize unknown spell: " + id));
            getOrCreate(abstractSpellHolder).deserializeTick(instance);
        }
    }

    @Override
    public void serverTick() {
        spellInstances.values().forEach(SpellInstance::serverTick);
    }

    @Override
    public void clientTick() {
        spellInstances.values().forEach(SpellInstance::clientTick);
    }

    @Override
    public void respawn() {
        spellInstances.values().forEach(SpellInstance::resetRecharge);
    }

    @Override
    public void onCurioEquip(ItemStack stack) {
        if (stack.getItem() instanceof SpellCasterItem caster && !curioSpells.containsKey(caster)) {
            Holder<AbstractSpell> spell = caster.getSpell();
            getOrCreate(spell).setAvailable(true);
            curioSpells.put(caster, spell);
            spell.value().uponEquipping(getPlayer());
        }
    }

    @Override
    public void onCurioUnEquip(ItemStack stack) {
        if (stack.getItem() instanceof SpellCasterItem caster && curioSpells.containsKey(caster) && FantazicUtil.duplicatingCurio(getPlayer(), caster) <= 1) {
            Holder<AbstractSpell> spell = caster.getSpell();
            getOrCreate(spell).setAvailable(false);
            curioSpells.remove(caster);
        }
    }

    public @NotNull SpellInstance getOrCreate(Holder<AbstractSpell> spell) {
        return spellInstances.computeIfAbsent(spell,holder -> new SpellInstance(holder, getPlayer()));
    }

    public SpellCastResult tryToUse(Holder<AbstractSpell> spellHolder) {
        return getOrCreate(spellHolder).attemptCast();
    }

    public SpellCastResult tryToCast(Holder<AbstractSpell> spellHolder) {
        SpellInstance instance = getOrCreate(spellHolder);
        return instance.isAvailable() ? instance.attemptCast() : SpellCastResult.fail();
    }

    public boolean hasSpell(Holder<AbstractSpell> spellHolder) {
        return getOrCreate(spellHolder).isAvailable();
    }

    public boolean hasActiveSpell(Holder<AbstractSpell> spellHolder) {
        SpellInstance spellInstance = getOrCreate(spellHolder);
        if (!spellInstance.isAvailable() || spellInstance.recharge() > 0) return false;
        if (!getPlayer().hasInfiniteMaterials()) spellInstance.attemptCast();
        return true;
    }

    public Map<Holder<AbstractSpell>, SpellInstance> availableSpells() {
        Map<Holder<AbstractSpell>, SpellInstance> instanceMap = Maps.newHashMap();
        for (Map.Entry<Holder<AbstractSpell>, SpellInstance> entry : spellInstances.entrySet())
            if (entry.getValue().isAvailable()) instanceMap.put(entry.getKey(), entry.getValue());
        return instanceMap;
    }
}
