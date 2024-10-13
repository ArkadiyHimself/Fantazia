package net.arkadiyhimself.fantazia.advanced.spell.types;

import net.arkadiyhimself.fantazia.advanced.cleansing.Cleanse;
import net.arkadiyhimself.fantazia.advanced.cleansing.EffectCleansing;
import net.arkadiyhimself.fantazia.client.gui.GuiHelper;
import net.arkadiyhimself.fantazia.registries.FTZAttributes;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.compress.utils.Lists;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

public class TargetedSpell<T extends LivingEntity> extends AbstractSpell {

    private final Class<T> affected;
    private final float range;

    private final BiPredicate<LivingEntity, T> conditions;
    private final BiConsumer<LivingEntity, T> beforeBlockCheck;
    private final BiConsumer<LivingEntity, T> afterBlockCheck;

    protected TargetedSpell(float manacost, int recharge, @Nullable Holder<SoundEvent> castSound, @Nullable Holder<SoundEvent> rechargeSound, Class<T> affected, float range, TickingConditions tickingConditions, Consumer<LivingEntity> ownerTick, Cleanse cleanse, boolean doCleanse, BiPredicate<LivingEntity, T> conditions, BiConsumer<LivingEntity, T> beforeBlockCheck, BiConsumer<LivingEntity, T> afterBlockCheck) {
        super(manacost, recharge, castSound, rechargeSound, tickingConditions, ownerTick, cleanse, doCleanse);
        this.affected = affected;
        this.range = range;
        this.conditions = conditions;
        this.beforeBlockCheck = beforeBlockCheck;
        this.afterBlockCheck = afterBlockCheck;
    }

    public boolean canAffect(LivingEntity entity) {
        return affected.isInstance(entity);
    }

    public boolean conditions(LivingEntity caster, T target) {
        return this.conditions.test(caster, target);
    }

    public void beforeBlockCheck(LivingEntity caster, T target) {
        beforeBlockCheck.accept(caster, target);
    }

    public void afterBlockCheck(LivingEntity caster, T target) {
        if (doCleanse()) EffectCleansing.tryCleanseAll(target, this.getCleanse(), MobEffectCategory.BENEFICIAL);
        afterBlockCheck.accept(caster, target);
    }

    public float range() {
        return range;
    }

    @Override
    public List<Component> itemTooltip(@Nullable ItemStack itemStack) {
        List<Component> components = Lists.newArrayList();
        if (this.getID() == null) return components;
        String basicPath = "spell." + this.getID().getNamespace() + "." + this.getID().getPath();
        int lines = 0;
        if (!Screen.hasShiftDown()) {
            String desc = Component.translatable(basicPath + ".desc.lines").getString();
            try {
                lines = Integer.parseInt(desc);
            } catch (NumberFormatException ignored) {}
            if (lines > 0) {
                components.add(Component.literal(" "));
                for (int i = 1; i <= lines; i++) components.add(GuiHelper.bakeComponent(basicPath + ".desc." + i, null, null));
            }
            return components;
        }
        components.add(Component.literal(" "));
        ChatFormatting[] text = new ChatFormatting[]{ChatFormatting.GOLD};

        ChatFormatting[] ability = new ChatFormatting[]{ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD};
        ChatFormatting[] heading = new ChatFormatting[]{ChatFormatting.LIGHT_PURPLE};

        // spell name
        String namePath = basicPath + ".name";
        components.add(GuiHelper.bakeComponent("tooltip.fantazia.common.targeted", heading, ability, Component.translatable(namePath).getString()));

        // spell recharge
        Component deltaRechargeComponent = bakeRechargeComponent(heading, ability);

        String recharge = String.format("%.1f", ((float) this.getRecharge()) / 20);
        Component basicRecharge = Component.literal(recharge).withStyle(ability);
        Component rechargeComponent;
        if (deltaRechargeComponent != null) rechargeComponent = Component.translatable("tooltip.fantazia.common.recharge_modified", basicRecharge, deltaRechargeComponent).withStyle(heading);
        else rechargeComponent = GuiHelper.bakeComponent("tooltip.fantazia.common.recharge", heading, ability, basicRecharge);

        components.add(bakeRechargeComponent(heading, ability));
      //  components.add(GuiHelper.bakeComponent("tooltip.fantazia.common.recharge", heading, ability, recharge));

        // spell manacost
        String manacost = String.format("%.1f", this.getManacost());
        components.add(GuiHelper.bakeComponent("tooltip.fantazia.common.manacost", heading, ability, manacost));

        // spell range
        Component addRangeComponent = bakeRangeComponent();

        String range = String.format("%.1f", this.range());
        Component basicRange = Component.literal(range).withStyle(ability);
        Component rangeComponent;
        if (addRangeComponent != null) rangeComponent = Component.translatable("tooltip.fantazia.common.range_modified", basicRange, addRangeComponent).withStyle(heading);
        else rangeComponent = GuiHelper.bakeComponent("tooltip.fantazia.common.range", heading, ability, basicRange);

        components.add(rangeComponent);

        // spell cleanse
        if (this.doCleanse()) components.add(GuiHelper.bakeComponent("tooltip.fantazia.common.cleanse_strength", heading, ability, this.getCleanse().getName()));

        components.add(Component.literal(" "));

        String desc = Component.translatable(basicPath + ".lines").getString();
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
            components.add(GuiHelper.bakeComponent("tooltip.fantazia.common.active.passive", heading, null));
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
        else return Component.literal("- " + Math.min(this.range, Math.abs(value))).withStyle(ChatFormatting.RED, ChatFormatting.BOLD, ChatFormatting.ITALIC);
    }

    public static class Builder<T extends LivingEntity> {

        private final float manacost;
        private final int recharge;
        private final @Nullable Holder<SoundEvent> castSound;
        private final @Nullable Holder<SoundEvent> rechargeSound;
        private final Class<T> targetClass;
        private final float range;

        private TickingConditions tickingConditions = TickingConditions.ALWAYS;
        private Consumer<LivingEntity> ownerTick = owner -> {};
        private Cleanse targetCleanse = Cleanse.BASIC;
        private boolean doCleanse = false;

        private BiPredicate<LivingEntity, T> conditions = ((livingEntity, t) -> true);
        private BiConsumer<LivingEntity, T> beforeBlockCheck = (entity, t) -> {};
        private BiConsumer<LivingEntity, T> afterBlockCheck = (entity, t) -> {};

        public Builder(float manacost, int recharge, @Nullable Holder<SoundEvent> castSound, @Nullable Holder<SoundEvent> rechargeSound, Class<T> targetClass, float range) {
            this.manacost = manacost;
            this.targetClass = targetClass;
            this.range = range;
            this.recharge = recharge;
            this.castSound = castSound;
            this.rechargeSound = rechargeSound;
        }

        public Builder<T> tickingConditions(TickingConditions value) {
            this.tickingConditions = value;
            return this;
        }
        public Builder<T> ownerTick(Consumer<LivingEntity> value) {
            this.ownerTick = value;
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

        public Builder<T> conditions(BiPredicate<LivingEntity, T> value) {
            this.conditions = value;
            return this;
        }

        public Builder<T> beforeBlockChecking(BiConsumer<LivingEntity, T> value) {
            this.beforeBlockCheck = value;
            return this;
        }

        public Builder<T> afterBlockChecking(BiConsumer<LivingEntity, T> value) {
            this.afterBlockCheck = value;
            return this;
        }

        public TargetedSpell<T> build() {
            return new TargetedSpell<>(manacost, recharge, castSound, rechargeSound, targetClass, range, tickingConditions, ownerTick, targetCleanse, doCleanse, conditions, beforeBlockCheck, afterBlockCheck);
        }
    }
}
