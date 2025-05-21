package net.arkadiyhimself.fantazia.advanced.spell.types;

import net.arkadiyhimself.fantazia.advanced.cleansing.Cleanse;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import org.apache.commons.compress.utils.Lists;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class SpellBuilder<T extends AbstractSpell> {

    protected final float manacost;
    protected final int defaultRecharge;
    protected final @Nullable Holder<SoundEvent> castSound;
    protected final @Nullable Holder<SoundEvent> rechargeSound;

    // ticking
    protected AbstractSpell.TickingConditions tickingConditions = AbstractSpell.TickingConditions.ALWAYS;
    protected Consumer<LivingEntity> ownerTick = owner -> {};
    protected Consumer<LivingEntity> uponEquipping = owner -> {};
    protected Cleanse cleanse = Cleanse.BASIC;
    protected boolean doCleanse = false;
    protected Function<LivingEntity, Integer> recharge;

    // extra
    protected Function<LivingEntity, List<Component>> extendTooltip = livingEntity -> Lists.newArrayList();

    protected SpellBuilder(float manacost, int defaultRecharge, @Nullable Holder<SoundEvent> castSound, @Nullable Holder<SoundEvent> rechargeSound) {
        this.manacost = manacost;
        this.defaultRecharge = defaultRecharge;
        this.castSound = castSound;
        this.rechargeSound = rechargeSound;

        this.recharge =livingEntity -> defaultRecharge;
    }

    public SpellBuilder<T> tickingConditions(AbstractSpell.TickingConditions value) {
        this.tickingConditions = value;
        return this;
    }

    public SpellBuilder<T> ownerTick(Consumer<LivingEntity> value) {
        this.ownerTick = value;
        return this;
    }

    public SpellBuilder<T> uponEquipping(Consumer<LivingEntity> uponEquipping) {
        this.uponEquipping = uponEquipping;
        return this;
    }

    public SpellBuilder<T> cleanse() {
        this.doCleanse = true;
        return this;
    }

    public SpellBuilder<T> cleanse(Cleanse value) {
        this.cleanse = value;
        return this;
    }

    public SpellBuilder<T> recharge(Function<LivingEntity, Integer> recharge) {
        this.recharge = recharge;
        return this;
    }

    public SpellBuilder<T> recharge(int recharge) {
        this.recharge = livingEntity -> recharge;
        return this;
    }

    public SpellBuilder<T>  extendTooltip(Function<LivingEntity, List<Component>> extendTooltip) {
        this.extendTooltip = extendTooltip;
        return this;
    }

    public abstract T build();
}
