package net.arkadiyhimself.fantazia.common.advanced.blueprint;

import net.arkadiyhimself.fantazia.client.gui.GuiHelper;
import net.arkadiyhimself.fantazia.common.api.custom_registry.FantazicRegistries;
import net.arkadiyhimself.fantazia.common.item.ITooltipBuilder;
import net.arkadiyhimself.fantazia.common.registries.custom.Blueprints;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.network.chat.Component;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

public class Blueprint implements ITooltipBuilder {

    private final ModelResourceLocation icon;
    private final String ident;
    private String descriptionId;
    private final ChatFormatting[] nameFormatting = null;
    private final ChatFormatting[] descFormatting = null;

    public Blueprint(
            ModelResourceLocation icon,
            String ident
    ) {
        this.icon = icon;
        this.ident = ident;
    }

    public String getDescriptionId() {
        if (descriptionId == null) descriptionId = Util.makeDescriptionId("blueprint", FantazicRegistries.BLUEPRINTS.getKey(this));
        return descriptionId;
    }

    public Component getNameComponent() {
        return Component.translatable(getDescriptionId()).withStyle();
    }

    public boolean isEmpty() {
        return this == Blueprints.EMPTY.value();
    }

    public ModelResourceLocation getIcon() {
        return icon;
    }

    public String getIdent() {
        return ident;
    }

    @Override
    public List<Component> buildTooltip() {
        List<Component> components = Lists.newArrayList();

        String desc = getDescriptionId();

        int lines = 0;
        try {
            String amo = Component.translatable(desc + ".lines").getString();
            lines = Integer.parseInt(amo);
        } catch (NumberFormatException ignored) {}

        if (lines > 0)
            for (int i = 1; i <= lines; i++) components.add(Component.translatable(desc + "." + i).withStyle());

        return components;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private ModelResourceLocation icon = null;
        private String ident = null;

        public Builder icon(ModelResourceLocation icon) {
            this.icon = icon;
            return this;
        }

        public Builder ident(String ident) {
            this.ident = ident;
            return this;
        }

        public Blueprint build() {
            return new Blueprint(
                    icon,
                    ident
            );
        }
    }
}
