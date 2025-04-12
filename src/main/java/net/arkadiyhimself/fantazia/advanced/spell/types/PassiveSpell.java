package net.arkadiyhimself.fantazia.advanced.spell.types;

import net.arkadiyhimself.fantazia.advanced.cleansing.Cleanse;
import net.arkadiyhimself.fantazia.advanced.cleansing.EffectCleansing;
import net.arkadiyhimself.fantazia.client.gui.GuiHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.compress.utils.Lists;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class PassiveSpell extends AbstractSpell {

    private final Consumer<LivingEntity> onActivation;

    protected PassiveSpell(float manacost, int defaultRecharge, @Nullable Holder<SoundEvent> castSound, @Nullable Holder<SoundEvent> rechargeSound, TickingConditions tickingConditions, Consumer<LivingEntity> ownerTick, Cleanse cleanse, boolean doCleanse, Function<LivingEntity, Integer> recharge, Consumer<LivingEntity> onActivation) {
        super(manacost, defaultRecharge, castSound, rechargeSound, tickingConditions, ownerTick, cleanse, doCleanse, recharge);
        this.onActivation = onActivation;
    }

    @Override
    public List<Component> itemTooltip(@Nullable ItemStack itemStack) {
        List<Component> components = Lists.newArrayList();
        if (getID() == null) return components;
        String basicPath = "spell." + this.getID().getNamespace() + "." + this.getID().getPath();
        int lines = 0;
        if (!Screen.hasShiftDown()) {
            String desc = Component.translatable(basicPath + ".desc.lines").getString();
            try {
                lines = Integer.parseInt(desc);
            } catch (NumberFormatException ignored){}
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
        components.add(GuiHelper.bakeComponent("tooltip.fantazia.common.passive", heading, ability, Component.translatable(namePath).getString()));
        // spell recharge
        components.add(bakeRechargeComponent(heading, ability));
        // spell manacost
        String manacost = String.format("%.1f", getManacost());
        components.add(GuiHelper.bakeComponent("tooltip.fantazia.common.manacost", heading, ability, manacost));
        // spell cleanse
        if (doCleanse()) components.add(GuiHelper.bakeComponent("tooltip.fantazia.common.cleanse_strength", heading, ability, getCleanse().getName()));

        components.add(Component.literal(" "));

        String desc = Component.translatable(basicPath + ".lines").getString();
        try {
            lines = Integer.parseInt(desc);
        } catch (NumberFormatException ignored){}

        if (lines > 0) for (int i = 1; i <= lines; i++) components.add(GuiHelper.bakeComponent(basicPath + "." + i, text, null));

        String pass = Component.translatable(basicPath + ".tweaks.lines").getString();
        lines = 0;
        try {
            lines = Integer.parseInt(pass);
        } catch (NumberFormatException ignored){}

        if (lines > 0) {
            components.add(Component.literal(" "));
            components.add(GuiHelper.bakeComponent(basicPath + ".tweaks", heading, null));
            for (int i = 1; i <= lines; i++) components.add(GuiHelper.bakeComponent(basicPath + ".tweaks." + i, null, null));
        }

        String altern = Component.translatable(basicPath + ".passive.lines").getString();
        lines = 0;
        try {
            lines = Integer.parseInt(altern);
        } catch (NumberFormatException ignored) {}

        if (lines > 0) {
            components.add(Component.literal(" "));
            components.add(GuiHelper.bakeComponent("tooltip.fantazia.common.active.passive", heading, null));
            for (int i = 1; i <= lines; i++) components.add(GuiHelper.bakeComponent(basicPath + ".passive." + i, null, null));
        }

        return components;
    }

    public void onActivation(LivingEntity livingEntity) {
        if (doCleanse()) EffectCleansing.tryCleanseAll(livingEntity, getCleanse(), MobEffectCategory.BENEFICIAL);
        this.onActivation.accept(livingEntity);
    }

    public static class Builder {

        private final float manacost;
        private final int defaultRecharge;
        private final @Nullable Holder<SoundEvent> castSound;
        private final @Nullable Holder<SoundEvent> rechargeSound;

        private TickingConditions tickingConditions = TickingConditions.ALWAYS;
        private Consumer<LivingEntity> ownerTick = owner -> {};
        private Cleanse cleanse = Cleanse.BASIC;
        private boolean doCleanse = false;
        private Function<LivingEntity, Integer> recharge;
        private Consumer<LivingEntity> onActivation = owner -> {};

        public Builder(float manacost, int defaultRecharge, @Nullable Holder<SoundEvent> castSound, @Nullable Holder<SoundEvent> rechargeSound) {
            this.manacost = manacost;
            this.defaultRecharge = defaultRecharge;
            this.castSound = castSound;
            this.rechargeSound = rechargeSound;

            // safety measure
            this.recharge = livingEntity -> defaultRecharge;
        }

        public Builder tickingConditions(TickingConditions value) {
            this.tickingConditions = value;
            return this;
        }

        public Builder ownerTick(Consumer<LivingEntity> value) {
            this.ownerTick = value;
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

        public Builder onActivation(Consumer<LivingEntity> onActivation) {
            this.onActivation = onActivation;
            return this;
        }

        public PassiveSpell build() {
            return new PassiveSpell(manacost, defaultRecharge, castSound, rechargeSound, tickingConditions, ownerTick, cleanse, doCleanse, recharge, onActivation);
        }
    }
}
