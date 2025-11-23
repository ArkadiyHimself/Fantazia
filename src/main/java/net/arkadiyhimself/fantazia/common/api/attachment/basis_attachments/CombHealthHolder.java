package net.arkadiyhimself.fantazia.common.api.attachment.basis_attachments;

import net.arkadiyhimself.fantazia.common.advanced.spell.SpellHelper;
import net.arkadiyhimself.fantazia.common.api.attachment.ISyncEveryTick;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.IDamageEventListener;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.IHealEventListener;
import net.arkadiyhimself.fantazia.common.api.attachment.level.LevelAttributesHelper;
import net.arkadiyhimself.fantazia.common.api.attachment.level.holders.HealingSourcesHolder;
import net.arkadiyhimself.fantazia.common.api.custom_events.VanillaEventsExtension;
import net.arkadiyhimself.fantazia.common.registries.custom.Spells;
import net.arkadiyhimself.fantazia.data.tags.FTZDamageTypeTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public class CombHealthHolder implements INBTSerializable<CompoundTag>, IDamageEventListener, ISyncEveryTick, IHealEventListener {

    public static final int RESTORE_TICKS = 200;

    private final IAttachmentHolder holder;
    private int ticks = 0;
    private float toHeal = 0f;

    public CombHealthHolder(IAttachmentHolder holder) {
        this.holder = holder;
    }

    public void tick() {
        if (this.ticks > 0) this.ticks--;
        else {
            this.ticks = 0;
            if (toHeal > 0f && holder instanceof LivingEntity living) {
                LevelAttributesHelper.healEntity(living, toHeal, HealingSourcesHolder::comb);
                toHeal = 0f;
            }
        }
    }

    @Override
    public void onHit(LivingDamageEvent.Post event) {
        DamageSource source = event.getSource();
        if (source.is(FTZDamageTypeTags.IGNORED_BY_RESTORE_SPELL)) return;
        if (!SpellHelper.castPassiveSpell(event.getEntity(), Spells.RESTORE).success()) {
            this.ticks = 0;
            this.toHeal = 0;
            return;
        }
        int ampl = SpellHelper.getSpellAmplifier((LivingEntity) holder, Spells.RESTORE);
        this.ticks = RESTORE_TICKS - ampl * 20;
        this.toHeal = event.getNewDamage();
    }

    @Override
    public void onHeal(VanillaEventsExtension.AdvancedHealEvent event) {
        float amount = event.getAmount();
        toHeal = Math.max(0, toHeal - amount);
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("ticks", ticks);
        tag.putFloat("toHeal", toHeal);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag tag) {
        this.ticks = tag.getInt("ticks");
        this.toHeal = tag.getFloat("toHeal");
    }

    @Override
    public CompoundTag serializeTick() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("ticks", ticks);
        tag.putFloat("heal", toHeal);
        return tag;
    }

    @Override
    public void deserializeTick(CompoundTag tag) {
        this.ticks = tag.getInt("ticks");
        this.toHeal = tag.getFloat("heal");
    }

    public int ticks() {
        return ticks;
    }

    public float toHeal() {
        return toHeal;
    }
}
