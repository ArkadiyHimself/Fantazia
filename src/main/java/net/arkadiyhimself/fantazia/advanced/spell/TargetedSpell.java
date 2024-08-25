package net.arkadiyhimself.fantazia.advanced.spell;

import net.arkadiyhimself.fantazia.advanced.cleansing.Cleanse;
import net.arkadiyhimself.fantazia.client.gui.GuiHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Supplier;

public class TargetedSpell<T extends LivingEntity> extends Spell {
    private BiPredicate<LivingEntity, T> conditions = ((livingEntity, t) -> true);
    private BiConsumer<LivingEntity, T> beforeDeflect = (entity, t) -> {};
    private BiConsumer<LivingEntity, T> afterDeflect = (entity, t) -> {};
    private final Class<T> affected;
    private final float range;
    public TargetedSpell(Class<T> affected, float range, float MANACOST, int recharge, Supplier<SoundEvent> soundEvent) {
        super(MANACOST, recharge, soundEvent);
        this.range = range;
        this.affected = affected;
    }
    public TargetedSpell(Class<T> affected, float range, float MANACOST, int recharge) {
        super(MANACOST, recharge, () -> null);
        this.range = range;
        this.affected = affected;
    }
    public TargetedSpell<T> setConditions(BiPredicate<LivingEntity, T> conditions) {
        this.conditions = conditions;
        return this;
    }

    public TargetedSpell<T> setBefore(BiConsumer<LivingEntity, T> onCast) {
        this.beforeDeflect = onCast;
        return this;
    }
    public void before(LivingEntity caster, T target) {
        beforeDeflect.accept(caster, target);
    }
    public TargetedSpell<T> setAfter(BiConsumer<LivingEntity, T> onCast) {
        this.afterDeflect = onCast;
        return this;
    }
    public void after(LivingEntity caster, T target) {
        afterDeflect.accept(caster, target);
    }
    public boolean conditions(LivingEntity caster, T target) {
        return this.conditions.test(caster, target);
    }
    public boolean canAffect(LivingEntity entity) {
        return affected.isInstance(entity);
    }
    public float getRange() {
        return range;
    }

    @Override
    public TargetedSpell<T> cleanse(Cleanse cleanse) {
        super.cleanse(cleanse);
        return this;
    }

    @Override
    public List<Component> buildItemTooltip(@javax.annotation.Nullable ItemStack itemStack) {
        List<Component> components = Lists.newArrayList();
        if (this.getID() == null) return components;
        String basicPath = "ability." + this.getID().getNamespace() + "." + this.getID().getPath();
        int lines = 0;
        if (!Screen.hasShiftDown()) {
            String desc = Component.translatable(basicPath + ".desc.lines").getString();
            try {
                lines = Integer.parseInt(desc);
            } catch (NumberFormatException ignored) {}
            if (lines > 0) {
                components.add(Component.translatable(" "));
                for (int i = 1; i <= lines; i++) components.add(GuiHelper.bakeComponent(basicPath + ".desc." + i, null, null));
            }
            return components;
        }
        components.add(Component.translatable(" "));
        ChatFormatting[] text = new ChatFormatting[]{ChatFormatting.GOLD};

        ChatFormatting[] ability = new ChatFormatting[]{ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD};
        ChatFormatting[] head = new ChatFormatting[]{ChatFormatting.LIGHT_PURPLE};
        // spell name
        String namePath = basicPath + ".name";
        components.add(GuiHelper.bakeComponent("tooltip.fantazia.common.targeted", head, ability, Component.translatable(namePath).getString()));
        // spell recharge
        String recharge = String.format("%.1f", ((float) this.getRecharge()) / 20);
        components.add(GuiHelper.bakeComponent("tooltip.fantazia.common.recharge", head, ability, recharge));
        // spell manacost
        String manacost = String.format("%.1f", this.getManacost());
        components.add(GuiHelper.bakeComponent("tooltip.fantazia.common.manacost", head, ability, manacost));
        // spell range
        String range = String.format("%.1f", this.getRange());
        components.add(GuiHelper.bakeComponent("tooltip.fantazia.common.range", head, ability, range));
        // spell cleanse
        if (this.hasCleanse()) components.add(GuiHelper.bakeComponent("tooltip.fantazia.common.cleanse_strength", head, ability, this.getStrength().getName()));

        components.add(Component.translatable(" "));

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
            components.add(Component.translatable(" "));
            components.add(GuiHelper.bakeComponent("tooltip.fantazia.common.active.passive", head, null));
            for (int i = 1; i <= lines; i++) components.add(GuiHelper.bakeComponent(basicPath + ".passive." + i, null, null));

        }

        return components;
    }
}
