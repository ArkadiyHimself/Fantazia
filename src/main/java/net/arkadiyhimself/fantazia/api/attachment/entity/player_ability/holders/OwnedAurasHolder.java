package net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders;

import com.google.common.collect.Maps;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.advanced.aura.AuraInstance;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.ICurioListener;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.PlayerAbilityHolder;
import net.arkadiyhimself.fantazia.items.casters.AuraCasterItem;
import net.arkadiyhimself.fantazia.util.wheremagichappens.InventoryHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Map;

public class OwnedAurasHolder extends PlayerAbilityHolder implements ICurioListener {

    private final Map<AuraCasterItem, AuraInstance<? extends Entity>> curioAuras = Maps.newHashMap();

    public OwnedAurasHolder(Player player) {
        super(player, Fantazia.res("owned_auras"));
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        return new CompoundTag();
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag compoundTag) {
    }

    @Override
    public void onCurioEquip(ItemStack stack) {
        if (stack.getItem() instanceof AuraCasterItem caster && !curioAuras.containsKey(caster)) curioAuras.put(caster, new AuraInstance<>(getPlayer(), caster.getBasicAura()));
    }

    @Override
    public void onCurioUnEquip(ItemStack stack) {
        if (stack.getItem() instanceof AuraCasterItem caster && curioAuras.containsKey(caster) && InventoryHelper.duplicatingCurio(getPlayer(), caster) <= 1) {
            curioAuras.get(caster).discard();
            curioAuras.remove(caster);
        }
    }

    public void clearAll() {
        curioAuras.values().forEach(AuraInstance::discard);
        curioAuras.clear();
    }
}
