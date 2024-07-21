package net.arkadiyhimself.fantazia.advanced.capacity.AbilityProviding;

import net.arkadiyhimself.fantazia.util.interfaces.ITooltipBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Talent implements ITooltipBuilder {
    private final ResourceLocation id;
    private boolean hidden;
    public Talent(ResourceLocation id) {
        this.id = id;
        this.hidden = false;
    }
    public ResourceLocation getIcon() {
        return this.id.withPrefix("textures/talents/").withSuffix(".png");
    }
    public Component getName() {
        return Component.translatable("talent." + id.getNamespace() + "." + id.getPath());
    }
    @Override
    public List<Component> buildTooltip(@Nullable ItemStack stack) {
        return null;
    }
    public Talent hidden() {
        this.hidden = true;
        return this;
    }

}
