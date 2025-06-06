package net.arkadiyhimself.fantazia.advanced.spell.types;

import net.arkadiyhimself.fantazia.advanced.cleansing.Cleanse;
import net.arkadiyhimself.fantazia.advanced.cleansing.EffectCleansing;
import net.arkadiyhimself.fantazia.advanced.spell.SpellCastResult;
import net.arkadiyhimself.fantazia.client.gui.GuiHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import org.apache.commons.compress.utils.Lists;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class SelfSpell extends AbstractSpell {

    private final Predicate<LivingEntity> conditions;
    private final BiFunction<LivingEntity, Integer, SpellCastResult> onCast;

    protected SelfSpell(float manacost, int defaultRecharge, @Nullable Holder<SoundEvent> castSound, @Nullable Holder<SoundEvent> rechargeSound, TickingConditions tickingConditions, Consumer<LivingEntity> ownerTick, Consumer<LivingEntity> uponEquipping, Cleanse cleanse, boolean doCleanse, Function<LivingEntity, Integer> recharge, Predicate<LivingEntity> conditions, BiFunction<LivingEntity, Integer, SpellCastResult> onCast, Function<LivingEntity, List<Component>> extendTooltip) {
        super(manacost, defaultRecharge, castSound, rechargeSound, tickingConditions, ownerTick, uponEquipping, cleanse, doCleanse, recharge, extendTooltip);
        this.conditions = conditions;
        this.onCast = onCast;
    }

    public boolean conditions(LivingEntity livingEntity) {
        return conditions.test(livingEntity);
    }

    public SpellCastResult onCast(LivingEntity livingEntity, int ampl) {
        if (doCleanse()) EffectCleansing.tryCleanseAll(livingEntity, getCleanse(), MobEffectCategory.BENEFICIAL);
        return onCast.apply(livingEntity, ampl);
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @SubscribeEvent
    public List<Component> buildTooltip() {
        List<Component> components = Lists.newArrayList();
        String basicPath = "spell." + getID().getNamespace() + "." + getID().getPath();

        components.add(Component.literal(" "));
        ChatFormatting[] text = new ChatFormatting[]{ChatFormatting.GOLD};

        ChatFormatting[] ability = new ChatFormatting[]{ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD};
        ChatFormatting[] heading = new ChatFormatting[]{ChatFormatting.LIGHT_PURPLE};
        // spell name
        String namePath = basicPath + ".name";
        components.add(GuiHelper.bakeComponent("tooltip.fantazia.common.spell.active", heading, ability, Component.translatable(namePath).getString()));
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
        } catch (NumberFormatException ignored) {}
        if (lines > 0) for (int i = 1; i <= lines; i++) components.add(GuiHelper.bakeComponent(basicPath + "." + i, text, null));

        String pass = Component.translatable(basicPath + ".passive.lines").getString();
        lines = 0;
        try {
            lines = Integer.parseInt(pass);
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

        if (Minecraft.getInstance().player != null) {
            List<Component> extended = extendTooltip(Minecraft.getInstance().player);
            if (!extended.isEmpty()) {
                components.add(Component.literal(" "));
                components.addAll(extended);
            }
        }

        return components;
    }

    public static Builder builder(float manacost, int defaultRecharge, @Nullable Holder<SoundEvent> castSound, @Nullable Holder<SoundEvent> rechargeSound) {
        return new Builder(manacost, defaultRecharge, castSound, rechargeSound);
    }

    public static class Builder extends SpellBuilder<SelfSpell> {

        private Predicate<LivingEntity> conditions = livingEntity -> true;
        private BiFunction<LivingEntity, Integer, SpellCastResult> onCast = (livingEntity, integer) -> SpellCastResult.defaultResult();

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

        public Builder conditions(Predicate<LivingEntity> value) {
            this.conditions = value;
            return this;
        }

        public Builder onCast(BiFunction<LivingEntity, Integer, SpellCastResult> value) {
            this.onCast = value;
            return this;
        }

        public Builder onCast(Function<LivingEntity, SpellCastResult> value) {
            return onCast((livingEntity, integer) -> value.apply(livingEntity));
        }

        public Builder extendTooltip(Function<LivingEntity, List<Component>> extendTooltip) {
            this.extendTooltip = extendTooltip;
            return this;
        }

        public SelfSpell build() {
            return new SelfSpell(manacost, defaultRecharge, castSound, rechargeSound, tickingConditions, ownerTick, uponEquipping, cleanse, doCleanse, recharge, conditions, onCast, extendTooltip);
        }
    }
}
