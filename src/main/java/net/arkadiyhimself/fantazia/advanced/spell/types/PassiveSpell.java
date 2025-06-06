package net.arkadiyhimself.fantazia.advanced.spell.types;

import net.arkadiyhimself.fantazia.advanced.cleansing.Cleanse;
import net.arkadiyhimself.fantazia.advanced.cleansing.EffectCleansing;
import net.arkadiyhimself.fantazia.advanced.spell.SpellCastResult;
import net.arkadiyhimself.fantazia.client.gui.GuiHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.apache.commons.compress.utils.Lists;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class PassiveSpell extends AbstractSpell {

    private final BiFunction<LivingEntity, Integer, SpellCastResult> onActivation;

    protected PassiveSpell(float manacost, int defaultRecharge, @Nullable Holder<SoundEvent> castSound, @Nullable Holder<SoundEvent> rechargeSound, TickingConditions tickingConditions, Consumer<LivingEntity> ownerTick, Consumer<LivingEntity> uponEquipping, Cleanse cleanse, boolean doCleanse, Function<LivingEntity, Integer> recharge, BiFunction<LivingEntity, Integer, SpellCastResult> onActivation) {
        super(manacost, defaultRecharge, castSound, rechargeSound, tickingConditions, ownerTick, uponEquipping, cleanse, doCleanse, recharge, (owner) -> Lists.newArrayList());
        this.onActivation = onActivation;
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public List<Component> buildTooltip() {
        List<Component> components = Lists.newArrayList();
        if (getID() == null) return components;
        String basicPath = "spell." + getID().getNamespace() + "." + getID().getPath();

        components.add(Component.literal(" "));
        ChatFormatting[] text = new ChatFormatting[]{ChatFormatting.GOLD};

        ChatFormatting[] ability = new ChatFormatting[]{ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD};
        ChatFormatting[] heading = new ChatFormatting[]{ChatFormatting.LIGHT_PURPLE};
        // spell name
        String namePath = basicPath + ".name";
        components.add(GuiHelper.bakeComponent("tooltip.fantazia.common.spell.passive", heading, ability, Component.translatable(namePath).getString()));
        // spell recharge
        components.add(bakeRechargeComponent(heading, ability));
        // spell manacost
        String manacost = String.format("%.1f", getManacost());
        components.add(GuiHelper.bakeComponent("tooltip.fantazia.common.spell.manacost", heading, ability, manacost));
        // spell cleanse
        if (doCleanse()) components.add(GuiHelper.bakeComponent("tooltip.fantazia.common.spell.cleanse_strength", heading, ability, getCleanse().getDescription()));

        components.add(Component.literal(" "));

        String desc = Component.translatable(basicPath + ".lines").getString();
        int lines = 0;
        try {
            lines = Integer.parseInt(desc);
        } catch (NumberFormatException ignored){}

        if (lines > 0) for (int i = 1; i <= lines; i++) components.add(GuiHelper.bakeComponent(basicPath + "." + i, text, null));

        String alter = Component.translatable(basicPath + ".passive.lines").getString();
        lines = 0;
        try {
            lines = Integer.parseInt(alter);
        } catch (NumberFormatException ignored) {}

        if (lines > 0) {
            components.add(Component.literal(" "));
            components.add(GuiHelper.bakeComponent("tooltip.fantazia.common.spell.alterations", heading, null));
            for (int i = 1; i <= lines; i++) components.add(GuiHelper.bakeComponent(basicPath + ".passive." + i, null, null));
        }

        String tweaks = Component.translatable(basicPath + ".tweaks.lines").getString();
        lines = 0;
        try {
            lines = Integer.parseInt(tweaks);
        } catch (NumberFormatException ignored){}

        if (lines > 0) {
            components.add(Component.literal(" "));
            components.add(GuiHelper.bakeComponent(basicPath + ".tweaks", heading, null));
            for (int i = 1; i <= lines; i++) components.add(GuiHelper.bakeComponent(basicPath + ".tweaks." + i, null, null));
        }

        return components;
    }

    public SpellCastResult onActivation(LivingEntity livingEntity, int ampl) {
        if (doCleanse()) EffectCleansing.tryCleanseAll(livingEntity, getCleanse(), MobEffectCategory.BENEFICIAL);
        return onActivation.apply(livingEntity, ampl);
    }

    public static Builder builder(float manacost, int defaultRecharge, @Nullable Holder<SoundEvent> castSound, @Nullable Holder<SoundEvent> rechargeSound) {
        return new Builder(manacost, defaultRecharge, castSound, rechargeSound);
    }

    public static class Builder extends SpellBuilder<PassiveSpell> {

        private BiFunction<LivingEntity, Integer, SpellCastResult> onActivation = (owner, integer) -> SpellCastResult.defaultResult();

        private Builder(float manacost, int defaultRecharge, @Nullable Holder<SoundEvent> castSound, @Nullable Holder<SoundEvent> rechargeSound) {
            super(manacost, defaultRecharge, castSound, rechargeSound);
        }

        public Builder tickingConditions(TickingConditions value) {
            this.tickingConditions = value;
            return this;
        }

        public Builder ownerTick(Consumer<LivingEntity> value) {
            this.ownerTick = value;
            return this;
        }

        public Builder uponEquipping(Consumer<LivingEntity> uponEquipping) {
            this.uponEquipping = uponEquipping;
            return this;
        }

        public Builder cleanse() {
            this.doCleanse = true;
            return this;
        }

        public Builder cleanse(Cleanse value) {
            this.cleanse = value;
            this.doCleanse = true;
            return this;
        }

        public Builder recharge(Function<LivingEntity, Integer> recharge) {
            this.recharge = recharge;
            return this;
        }

        public Builder recharge(int recharge) {
            this.recharge = livingEntity -> recharge;
            return this;
        }

        public Builder onActivation(BiFunction<LivingEntity, Integer, SpellCastResult> onActivation) {
            this.onActivation = onActivation;
            return this;
        }

        public Builder onActivation(Function<LivingEntity, SpellCastResult> onActivation) {
            return onActivation((livingEntity, integer) -> onActivation.apply(livingEntity));
        }

        public PassiveSpell build() {
            return new PassiveSpell(manacost, defaultRecharge, castSound, rechargeSound, tickingConditions, ownerTick, uponEquipping, cleanse, doCleanse, recharge, onActivation);
        }
    }
}
