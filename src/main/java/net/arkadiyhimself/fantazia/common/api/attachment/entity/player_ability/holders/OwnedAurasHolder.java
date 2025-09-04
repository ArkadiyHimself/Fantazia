package net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.holders;

import com.google.common.collect.Maps;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.common.advanced.aura.Aura;
import net.arkadiyhimself.fantazia.common.advanced.aura.AuraInstance;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.ICurioListener;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.IDimensionChangeListener;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.PlayerAbilityHolder;
import net.arkadiyhimself.fantazia.common.api.attachment.level.LevelAttributesHelper;
import net.arkadiyhimself.fantazia.common.enchantment.effects.Amplification;
import net.arkadiyhimself.fantazia.common.item.casters.AuraCasterItem;
import net.arkadiyhimself.fantazia.util.wheremagichappens.FantazicUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.List;
import java.util.Map;

public class OwnedAurasHolder extends PlayerAbilityHolder implements ICurioListener, IDimensionChangeListener {

    private final Map<Holder<Aura>, AuraInstance> curioAuras = Maps.newHashMap();

    public OwnedAurasHolder(Player player) {
        super(player, Fantazia.location("owned_auras"));
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        return new CompoundTag();
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag tag) {}

    @Override
    public void onCurioEquip(ItemStack stack) {
        if (!(getPlayer().level() instanceof ServerLevel serverLevel)) return;
        if (stack.getItem() instanceof AuraCasterItem auraCaster) {
            Holder<Aura> auraHolder = auraCaster.getAura();
            List<ItemStack> itemStacks = FantazicUtil.getAllCuriosOfItem(getPlayer(), auraCaster);

            int ampl = 0;
            for (ItemStack stack1 : itemStacks) {
                int i = Amplification.getAmplifier(stack1);
                if (i > ampl) ampl = i;
            }

            if (curioAuras.containsKey(auraHolder)) {
                curioAuras.get(auraHolder).discard();
                curioAuras.remove(auraHolder);
            }

            AuraInstance instance = new AuraInstance(getPlayer(), auraCaster.getAura(), ampl);
            LevelAttributesHelper.addAuraInstance(serverLevel, instance);
            curioAuras.put(auraHolder, instance);
        }
    }

    @Override
    public void onCurioUnEquip(ItemStack stack) {
        if (!(getPlayer().level() instanceof ServerLevel serverLevel)) return;
        if (stack.getItem() instanceof AuraCasterItem auraCaster) {
            Holder<Aura> auraHolder = auraCaster.getAura();
            List<ItemStack> itemStacks = FantazicUtil.getAllCuriosOfItem(getPlayer(), auraCaster);

            if (curioAuras.containsKey(auraHolder)) {
                curioAuras.get(auraHolder).discard();
                curioAuras.remove(auraHolder);
            }

            if (itemStacks.isEmpty()) return;

            int ampl = -1;
            for (ItemStack stack1 : itemStacks) {
                int i = Amplification.getAmplifier(stack1);
                if (i > ampl) ampl = i;
            }
            if (ampl == -1) return;
            AuraInstance instance = new AuraInstance(getPlayer(), auraCaster.getAura(), ampl);
            LevelAttributesHelper.addAuraInstance(serverLevel, instance);
            curioAuras.put(auraHolder, instance);
        }
    }

    @Override
    public void onChangeDimension(ResourceKey<Level> form, ResourceKey<Level> to) {
        if (!(getPlayer().level() instanceof ServerLevel serverLevel)) return;
        if (curioAuras.isEmpty()) return;
        Map<Holder<Aura>, AuraInstance> copied = Map.copyOf(curioAuras);
        curioAuras.clear();
        for (Map.Entry<Holder<Aura>, AuraInstance> entry : copied.entrySet()) {
            AuraInstance oldInstance = entry.getValue();
            AuraInstance newInstance = oldInstance.copy(getPlayer());
            oldInstance.discard();
            LevelAttributesHelper.addAuraInstance(serverLevel, newInstance);
            curioAuras.put(entry.getKey(), newInstance);
        }
    }
}
