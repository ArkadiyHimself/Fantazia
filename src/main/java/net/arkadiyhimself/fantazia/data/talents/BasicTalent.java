package net.arkadiyhimself.fantazia.data.talents;

import net.arkadiyhimself.fantazia.api.type.item.ITooltipBuilder;
import net.arkadiyhimself.fantazia.client.gui.GuiHelper;
import net.arkadiyhimself.fantazia.data.talents.reload.TalentManager;
import net.arkadiyhimself.fantazia.util.library.hierarchy.ComplexHierarchy;
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
    private final int wisdom;
    @Nullable
    private IHierarchy<BasicTalent> hierarchy = null;
    @Nullable
    private final ResourceLocation advancement;
    public BasicTalent(ResourceLocation iconTexture, String title, int wisdom, @Nullable ResourceLocation advancement) {
        this.iconTexture = iconTexture;
        this.title = title;
        this.wisdom = wisdom;
        this.advancement = advancement;
    }

    @Override
    public List<Component> buildIconTooltip() {
        List<Component> components = Lists.newArrayList();
        int amo = 0;
        if (Screen.hasShiftDown()) {
            if (this.isPurchased()) components.add(GuiHelper.bakeComponent("fantazia.gui.talent.wisdom_cost", new ChatFormatting[]{ChatFormatting.BLUE}, new ChatFormatting[]{ChatFormatting.DARK_PURPLE}, wisdom));
            else {
                String criteria = Component.translatable(title + ".criteria.lines").getString();
                try {
                    amo = Integer.parseInt(criteria);
                } catch (NumberFormatException ignored) {}
                if (amo > 0) {
                    components.add(GuiHelper.bakeComponent("fantazia.gui.talent.requirement", new ChatFormatting[]{ChatFormatting.DARK_PURPLE}, null));
                    for (int i = 1; i <= amo; i++) components.add(GuiHelper.bakeComponent(title + ".criteria." + i, new ChatFormatting[]{ChatFormatting.LIGHT_PURPLE}, null));
                }
            }
            return components;
        }
        components.add(GuiHelper.bakeComponent(title + ".name", null, null));
        String desc = Component.translatable(title + ".desc.lines").getString();
        try {
            amo = Integer.parseInt(desc);
        } catch (NumberFormatException ignored) {}

        if (amo > 0) for (int i = 1; i <= amo; i++) components.add(GuiHelper.bakeComponent(title + ".desc." + i, new ChatFormatting[]{ChatFormatting.GOLD},null));
        return components;
    }

    public ResourceLocation getID() throws TalentDataException {
        for (Map.Entry<ResourceLocation, BasicTalent> entry : TalentManager.getTalents().entrySet()) if (entry.getValue() == this) return entry.getKey();
        throw new TalentDataException("Could not find ID of a talent");
    }
    public ResourceLocation getIconTexture() {
        return iconTexture;
    }
    public String getTitle() {
        return title;
    }
    public int getWisdom() {
        return wisdom;
    }
    @Nullable
    public BasicTalent getParent() {
        return this.hierarchy == null ? null : this.hierarchy.getParent(this);
    }
    public boolean isComplexHierarchy() {
        return this.hierarchy != null && this.hierarchy instanceof ComplexHierarchy<BasicTalent>;
    }
    public @Nullable IHierarchy<BasicTalent> getHierarchy() {
        return this.hierarchy;
    }
    public void setHierarchy(IHierarchy<BasicTalent> talentIHierarchy) {
        if (!talentIHierarchy.contains(this)) throw new HierarchyException("Hierarchy must contain this element");
        this.hierarchy = talentIHierarchy;
    }
    public boolean isPurchased() {
        return wisdom > 0;
    }
    public @Nullable ResourceLocation getAdvancement() {
        return advancement;
    }
    public static final class Builder {
        private final ResourceLocation iconTexture;
        private final String title;
        private final int wisdom;
        private final ResourceLocation advancement;
        public Builder(ResourceLocation iconTexture, String title, int wisdom, ResourceLocation advancement) {
            this.iconTexture = iconTexture;
            this.title = title;
            this.wisdom = wisdom;
            this.advancement = advancement;
        }
        public BasicTalent build() {
            return new BasicTalent(iconTexture, title, wisdom, advancement);
        }
    }
}

