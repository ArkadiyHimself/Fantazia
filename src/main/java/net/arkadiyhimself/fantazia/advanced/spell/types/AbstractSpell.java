package net.arkadiyhimself.fantazia.advanced.spell.types;

import net.arkadiyhimself.fantazia.advanced.cleansing.Cleanse;
import net.arkadiyhimself.fantazia.api.FantazicRegistries;
import net.arkadiyhimself.fantazia.api.type.item.ITooltipBuilder;
import net.arkadiyhimself.fantazia.client.gui.GuiHelper;
import net.arkadiyhimself.fantazia.registries.FTZAttributes;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.compress.utils.Lists;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public abstract class AbstractSpell implements ITooltipBuilder {

    private final float manacost;
    private final int recharge;
    private final @Nullable Holder<SoundEvent> castSound;
    private final @Nullable Holder<SoundEvent> rechargeSound;
    private final TickingConditions tickingConditions;
    private final Consumer<LivingEntity> ownerTick;
    private final Cleanse cleanse;
    private final boolean doCleanse;

    protected AbstractSpell(float manacost, int recharge, @Nullable Holder<SoundEvent> castSound, @Nullable Holder<SoundEvent> rechargeSound, TickingConditions tickingConditions, Consumer<LivingEntity> ownerTick, Cleanse cleanse, boolean doCleanse) {
        this.manacost = manacost;
        this.recharge = recharge;
        this.castSound = castSound;
        this.rechargeSound = rechargeSound;

        this.tickingConditions = tickingConditions;
        this.ownerTick = ownerTick;
        this.cleanse = cleanse;
        this.doCleanse = doCleanse;
    }

    public final float getManacost() {
        return manacost;
    }

    public final int getRecharge() {
        return recharge;
    }

    public @Nullable Holder<SoundEvent> getCastSound() {
        return castSound;
    }

    public @Nullable Holder<SoundEvent> getRechargeSound() {
        return rechargeSound;
    }

    public void tryToTick(LivingEntity livingEntity, boolean recharging) {
        if (tickingConditions == TickingConditions.ON_COOLDOWN && !recharging || tickingConditions == TickingConditions.NOT_ON_COOLDOWN && recharging) return;
        this.ownerTick.accept(livingEntity);
    }

    public final ResourceLocation getID() {
        return FantazicRegistries.SPELLS.getKey(this);
    }

    public final Component getName() {
        if (getID() == null) return null;
        return Component.translatable("spell." + getID().getNamespace() + "." + getID().getPath() + ".name");
    }

    protected Component bakeRechargeComponent(ChatFormatting[] heading, ChatFormatting[] ability) {
        Component deltaRechargeComponent = bakeModifiedRechargeComponent();

        String recharge = String.format("%.1f", ((float) this.getRecharge()) / 20);
        Component basicRecharge = Component.literal(recharge).withStyle(ability);
        Component rechargeComponent;
        if (deltaRechargeComponent != null) rechargeComponent = Component.translatable("tooltip.fantazia.common.recharge_modified", basicRecharge, deltaRechargeComponent).withStyle(heading);
        else rechargeComponent = GuiHelper.bakeComponent("tooltip.fantazia.common.recharge", heading, ability, basicRecharge);
        return rechargeComponent;
    }

    protected Component bakeModifiedRechargeComponent() {
        AttributeInstance instance = Minecraft.getInstance().player == null ? null : Minecraft.getInstance().player.getAttribute(FTZAttributes.RECHARGE_MULTIPLIER);
        if (instance == null) return null;
        float value = (float) instance.getValue();
        float delta = -(this.getRecharge() - this.getRecharge() * value / 100) / 20;
        if (delta == 0) return null;
        if (delta > 0) return Component.literal("+ " + delta).withStyle(ChatFormatting.RED, ChatFormatting.BOLD, ChatFormatting.ITALIC);
        else return Component.literal("- " + Math.min(this.getRecharge(), Math.abs(delta))).withStyle(ChatFormatting.BLUE, ChatFormatting.BOLD, ChatFormatting.ITALIC);
    }



    public Cleanse getCleanse() {
        return cleanse;
    }

    public boolean doCleanse() {
        return doCleanse;
    }

    @Override
    public List<Component> itemTooltip(@Nullable ItemStack itemStack) {
        return Lists.newArrayList();
    }

    public final boolean is(TagKey<AbstractSpell> tagKey) {
        if (getID() == null) return false;
        Optional<Holder.Reference<AbstractSpell>> holder = FantazicRegistries.SPELLS.getHolder(getID());
        return holder.map(abstractSpellReference -> abstractSpellReference.is(tagKey)).orElse(false);
    }

    public enum TickingConditions {
        ALWAYS, ON_COOLDOWN, NOT_ON_COOLDOWN
    }
}
