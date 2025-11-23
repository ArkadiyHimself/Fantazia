package net.arkadiyhimself.fantazia.common.item.casters;

import net.arkadiyhimself.fantazia.client.gui.GuiHelper;
import net.arkadiyhimself.fantazia.client.gui.TextComponents;
import net.arkadiyhimself.fantazia.common.advanced.aura.Aura;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

public class AuraCasterItem extends Item {

    private final Holder<Aura> basicAura;

    public AuraCasterItem(Holder<Aura> basicAura) {
        super(new Properties().stacksTo(1).fireResistant().rarity(Rarity.RARE));
        this.basicAura = basicAura;
    }

    public Holder<Aura> getAura() {
        return basicAura;
    }

    public List<Component> buildTooltip() {
        List<Component> components = Lists.newArrayList();

        if (!Screen.hasShiftDown()) {
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(this);
            String basicPath = "item." + id.getNamespace() + "." + id.getPath();
            int lines = 0;
            String desc = Component.translatable(basicPath + ".lines").getString();
            try {
                lines = Integer.parseInt(desc);
            } catch (NumberFormatException ignored) {}
            if (lines > 0) {
                components.add(Component.literal(" "));
                for (int i = 1; i <= lines; i++) components.add(GuiHelper.bakeComponent(basicPath + "." + i, null, null));
            }
            components.add(Component.literal(" "));
            components.add(TextComponents.HOLD_SHIFT_TO_SEE_MORE_COMPONENT);
        }
        else components.addAll(basicAura.value().buildTooltip());

        return components;
    }
}
