package net.arkadiyhimself.fantazia.advanced.spell;

import net.arkadiyhimself.fantazia.advanced.cleansing.Cleanse;
import net.arkadiyhimself.fantazia.api.FantazicRegistry;
import net.arkadiyhimself.fantazia.api.items.ITooltipBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.tags.ITagManager;
import org.apache.commons.compress.utils.Lists;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

public abstract class AbstractSpell implements ITooltipBuilder {
    private final float manacost;
    private final int recharge;
    private final Supplier<SoundEvent> castSound;
    private Cleanse strength = null;
    protected boolean hasCleanse = false;
    protected AbstractSpell(float manacost, int recharge, @Nullable Supplier<SoundEvent> castSound) {
        this.recharge = recharge;
        this.manacost = manacost;
        this.castSound = castSound == null ? () -> null : castSound;
    }
    public AbstractSpell cleanse(Cleanse cleanse) {
        strength = cleanse;
        hasCleanse = true;
        return this;
    }
    public float getManacost() {
        return manacost;
    }
    public int getRecharge() {
        return recharge;
    }
    @Nullable
    public ResourceLocation getID() {
        List<RegistryObject<AbstractSpell>> registryObjects = FantazicRegistry.SPELLS.getEntries().stream().toList();
        for (RegistryObject<AbstractSpell> basicAuraRegistryObject : registryObjects) if (basicAuraRegistryObject.get() == this) return basicAuraRegistryObject.getId();
        return null;
    }
    public Component getName() {
        if (getID() == null) return null;
        return Component.translatable("spell." + getID().getNamespace() + "." + getID().getPath() + ".name");
    }
    @Nullable
    public SoundEvent getCastSound() {
        return castSound.get();
    }
    public Cleanse getStrength() {
        return strength;
    }
    public boolean hasCleanse() {
        return hasCleanse;
    }
    @Override
    public List<Component> itemTooltip(@Nullable ItemStack itemStack) {
        return Lists.newArrayList();
    }
    public boolean is(TagKey<AbstractSpell> tagKey) {
        ITagManager<AbstractSpell> tagManager = FantazicRegistry.BakedRegistries.SPELL.get().tags();
        return tagManager != null && tagManager.getTag(tagKey).contains(this);
    }
}
