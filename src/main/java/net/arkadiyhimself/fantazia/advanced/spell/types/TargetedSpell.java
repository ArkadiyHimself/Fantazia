package net.arkadiyhimself.fantazia.advanced.spell.types;

import net.arkadiyhimself.fantazia.advanced.cleansing.Cleanse;
import net.arkadiyhimself.fantazia.advanced.cleansing.EffectCleansing;
import net.arkadiyhimself.fantazia.advanced.spell.SpellCastResult;
import net.arkadiyhimself.fantazia.client.gui.GuiHelper;
import net.arkadiyhimself.fantazia.registries.FTZAttributes;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import org.apache.commons.compress.utils.Lists;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.*;

public class TargetedSpell<T extends LivingEntity> extends AbstractSpell {

    private final Class<T> affected;
    private final float range;

    private final BiPredicate<LivingEntity, T> conditions;
    private final BiFunction<LivingEntity, T, SpellCastResult> beforeBlockCheck;
    private final BiConsumer<LivingEntity, T> afterBlockCheck;

    protected TargetedSpell(float manacost, int defaultRecharge, @Nullable Holder<SoundEvent> castSound, @Nullable Holder<SoundEvent> rechargeSound, Class<T> affected, float range, TickingConditions tickingConditions, Consumer<LivingEntity> ownerTick, Consumer<LivingEntity> uponEquipping, Cleanse cleanse, boolean doCleanse, Function<LivingEntity, Integer> recharge, BiPredicate<LivingEntity, T> conditions, BiFunction<LivingEntity, T, SpellCastResult> beforeBlockCheck, BiConsumer<LivingEntity, T> afterBlockCheck) {
        super(manacost, defaultRecharge, castSound, rechargeSound, tickingConditions, ownerTick, uponEquipping, cleanse, doCleanse, recharge, (owner) -> Lists.newArrayList());
        this.affected = affected;
        this.range = range;
        this.conditions = conditions;
        this.beforeBlockCheck = beforeBlockCheck;
        this.afterBlockCheck = afterBlockCheck;
    }

    public boolean canAffect(LivingEntity entity) {
        return affected.isInstance(entity);
    }

    @SuppressWarnings("unchecked")
    public boolean conditions(LivingEntity caster, LivingEntity target) {
        try {
            return this.conditions.test(caster, (T) target);
        } catch (ClassCastException ignored) {
            return false;
        }
    }

    public SpellCastResult beforeBlockCheck(LivingEntity caster, T target) {
        return beforeBlockCheck.apply(caster, target);
    }

    public void afterBlockCheck(LivingEntity caster, T target) {
        if (doCleanse()) EffectCleansing.tryCleanseAll(target, this.getCleanse(), MobEffectCategory.BENEFICIAL);
        afterBlockCheck.accept(caster, target);
    }

    public float range() {
        return range;
    }

    @Override
    public boolean isActive() {
        return true;
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
        components.add(GuiHelper.bakeComponent("tooltip.fantazia.common.spell.targeted", heading, ability, Component.translatable(namePath).getString()));

        // spell recharge
        components.add(bakeRechargeComponent(heading, ability));

        // spell manacost
        String manacost = String.format("%.1f", this.getManacost());
        components.add(GuiHelper.bakeComponent("tooltip.fantazia.common.spell.manacost", heading, ability, manacost));

        // spell range
        Component addRangeComponent = bakeRangeComponent();

        String range = String.format("%.1f", this.range());
        Component basicRange = Component.literal(range).withStyle(ability);
        Component rangeComponent;
        if (addRangeComponent != null) rangeComponent = Component.translatable("tooltip.fantazia.common.spell.range_modified", basicRange, addRangeComponent).withStyle(heading);
        else rangeComponent = GuiHelper.bakeComponent("tooltip.fantazia.common.spell.range", heading, ability, basicRange);

        components.add(rangeComponent);

        // spell cleanse
        if (this.doCleanse()) components.add(GuiHelper.bakeComponent("tooltip.fantazia.common.spell.cleanse_strength", heading, ability, this.getCleanse().getName()));

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

        return components;
    }

    private @Nullable Component bakeRangeComponent() {
        AttributeInstance instance = Minecraft.getInstance().player == null ? null : Minecraft.getInstance().player.getAttribute(FTZAttributes.CAST_RANGE_ADDITION);
        if (instance == null) return null;
        double value = instance.getValue();
        if (value == 0) return null;
        if (value > 0) return Component.literal("+ " + value).withStyle(ChatFormatting.BLUE, ChatFormatting.BOLD, ChatFormatting.ITALIC);
        else return Component.literal("- " + Math.min(range, Math.abs(value))).withStyle(ChatFormatting.RED, ChatFormatting.BOLD, ChatFormatting.ITALIC);
    }

    public static <T extends LivingEntity> Builder<T> builder(float manacost, int defaultRecharge, @Nullable Holder<SoundEvent> castSound, @Nullable Holder<SoundEvent> rechargeSound, Class<T> targetClass, float range) {
        return new Builder<>(manacost, defaultRecharge, castSound, rechargeSound, targetClass, range);
    }

    public static class Builder<T extends LivingEntity> extends SpellBuilder<TargetedSpell<T>> {

        private final Class<T> targetClass;
        private final float range;

        private Cleanse targetCleanse = Cleanse.BASIC;

        private BiPredicate<LivingEntity, T> conditions = (livingEntity, target) -> true;
        private BiFunction<LivingEntity, T, SpellCastResult> beforeBlockCheck = (entity, target) -> SpellCastResult.defaultResult();
        private BiConsumer<LivingEntity, T> afterBlockCheck = (entity, target) -> {};

        private Builder(float manacost, int defaultRecharge, @Nullable Holder<SoundEvent> castSound, @Nullable Holder<SoundEvent> rechargeSound, Class<T> targetClass, float range) {
            super(manacost, defaultRecharge, castSound, rechargeSound);
            this.targetClass = targetClass;
            this.range = range;
        }

        public Builder<T> tickingConditions(TickingConditions value) {
            this.tickingConditions = value;
            return this;
        }

        public Builder<T> ownerTick(Consumer<LivingEntity> value) {
            this.ownerTick = value;
            return this;
        }

        public Builder<T> uponEquipping(Consumer<LivingEntity> uponEquipping) {
            this.uponEquipping = uponEquipping;
            return this;
        }

        public Builder<T> cleanse(Cleanse value) {
            this.targetCleanse = value;
            this.doCleanse = true;
            return this;
        }

        public Builder<T> cleanse() {
            this.doCleanse = true;
            return this;
        }

        public Builder<T> recharge(Function<LivingEntity, Integer> recharge) {
            this.recharge = recharge;
            return this;
        }

        public Builder<T> recharge(int recharge) {
            this.recharge = livingEntity -> recharge;
            return this;
        }

        public Builder<T> conditions(BiPredicate<LivingEntity, T> value) {
            this.conditions = value;
            return this;
        }

        public Builder<T> beforeBlockChecking(BiFunction<LivingEntity, T, SpellCastResult> value) {
            this.beforeBlockCheck = value;
            return this;
        }

        public Builder<T> afterBlockChecking(BiConsumer<LivingEntity, T> value) {
            this.afterBlockCheck = value;
            return this;
        }

        public TargetedSpell<T> build() {
            return new TargetedSpell<>(manacost, defaultRecharge, castSound, rechargeSound, targetClass, range, tickingConditions, ownerTick, uponEquipping, targetCleanse, doCleanse, recharge, conditions, beforeBlockCheck, afterBlockCheck);
        }
    }
}
