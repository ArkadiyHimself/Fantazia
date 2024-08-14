package net.arkadiyhimself.fantazia.data.talents;

import net.arkadiyhimself.fantazia.api.items.ITooltipBuilder;
import net.arkadiyhimself.fantazia.client.gui.GuiHelper;
import net.arkadiyhimself.fantazia.util.library.hierarchy.HierarchyException;
import net.arkadiyhimself.fantazia.util.library.hierarchy.IHierarchy;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class BasicTalent implements ITooltipBuilder {
    private final ResourceLocation iconTexture;
    private final String title;
    private final int wisdomCost;
    @Nullable
    private IHierarchy<BasicTalent> hierarchy = null;
    public BasicTalent(ResourceLocation iconTexture, String title, int wisdomCost) {
        this.iconTexture = iconTexture;
        this.title = title;
        this.wisdomCost = wisdomCost;
    }

    @Override
    public List<Component> buildIconTooltip() {
        List<Component> components = Lists.newArrayList();
        if (getID() == null) return components;
        GuiHelper.addComponent(components, title + ".name", null, null);
        int amo = 0;
        if (Screen.hasShiftDown()) {
            if (this.isPurchased()) {
                GuiHelper.addComponent(components, "fantazia.gui.talent.wisdom_cost", new ChatFormatting[]{ChatFormatting.BLUE}, new ChatFormatting[]{ChatFormatting.DARK_PURPLE}, wisdomCost);
            } else {
                String criteria = Component.translatable(title + ".criteria.lines").getString();
                try {
                    amo = Integer.parseInt(criteria);
                } catch (NumberFormatException ignored) {}
                if (amo > 0) for (int i = 1; i <= amo; i++) GuiHelper.addComponent(components, title + ".criteria." + i, new ChatFormatting[]{ChatFormatting.LIGHT_PURPLE}, null);
            }
            return components;
        }
        String desc = Component.translatable(title + ".desc.lines").getString();
        try {
            amo = Integer.parseInt(desc);
        } catch (NumberFormatException ignored) {}
        if (amo > 0) for (int i = 1; i <= amo; i++) GuiHelper.addComponent(components, title + ".desc." + i,new ChatFormatting[]{ChatFormatting.GOLD},null);
        return components;
    }

    public ResourceLocation getID() throws TalentDataException {
        for (Map.Entry<ResourceLocation, BasicTalent> entry : TalentLoad.getTalents().entrySet()) if (entry.getValue() == this) return entry.getKey();
        throw new TalentDataException("Could not find ID of a talent");
    }
    public ResourceLocation getIconTexture() {
        return iconTexture;
    }
    public String getTitle() {
        return title;
    }
    public int getWisdomCost() {
        return wisdomCost;
    }
    public static final class Builder {
        private final ResourceLocation iconTexture;
        private final String title;
        private final int wisdomCost;
        public Builder(ResourceLocation iconTexture, String title, int wisdomCost) {
            this.iconTexture = iconTexture;
            this.title = title;
            this.wisdomCost = wisdomCost;
        }
        public BasicTalent build() {
            return new BasicTalent(iconTexture, title, wisdomCost);
        }
    }
    @Nullable
    public BasicTalent getParent() {
        return this.hierarchy == null ? null : this.hierarchy.getParent(this);
    }
    @Nullable
    public BasicTalent getChild() {
        return this.hierarchy == null ? null : this.hierarchy.getChild(this);
    }
    public void setHierarchy(IHierarchy<BasicTalent> talentIHierarchy) {
        if (!talentIHierarchy.contains(this)) throw new HierarchyException("Hierarchy must contain this element");
        this.hierarchy = talentIHierarchy;
    }
    public boolean isPurchased() {
        return wisdomCost > 0;
    }
}

