package net.arkadiyhimself.fantazia.api.capability.entity.ability.abilities;

import com.google.common.collect.ImmutableList;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.AbilityHolder;
import net.arkadiyhimself.fantazia.data.talents.BasicTalent;
import net.arkadiyhimself.fantazia.data.talents.TalentHelper;
import net.arkadiyhimself.fantazia.data.talents.TalentLoad;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class TalentsHolder extends AbilityHolder {
    private static final String ID = "talent_data:";
    private final NonNullList<BasicTalent> TALENTS = NonNullList.create();
    private int wisdom = 100;
    public TalentsHolder(Player player) {
        super(player);
    }
    @Override
    public CompoundTag serialize() {
        CompoundTag tag = super.serialize();
        tag.putInt(ID + "wisdom", wisdom);
        ListTag talentTag = new ListTag();
        TALENTS.forEach(talent -> talentTag.add(StringTag.valueOf(talent.getID().toString())));
        tag.put(ID + "talents", talentTag);
        return tag;
    }

    @Override
    public void deserialize(CompoundTag tag) {
        super.deserialize(tag);
        TALENTS.clear();
        if (tag.contains(ID + "wisdom")) wisdom = tag.getInt(ID + "wisdom");
        if (!tag.contains(ID + "talents")) return;
        ListTag talentTags = tag.getList(ID + "talents", Tag.TAG_STRING);
        for (Tag talentTag : talentTags) {
            ResourceLocation talentID = new ResourceLocation(talentTag.getAsString());
            BasicTalent talent = TalentLoad.getTalents().get(talentID);
            if (talent == null) continue;
            TALENTS.add(talent);
        }
    }
    public ImmutableList<BasicTalent> getTalents() {
        return ImmutableList.copyOf(TALENTS);
    }
    public int getWisdom() {
        return wisdom;
    }
    public boolean talentUnlocked(@NotNull BasicTalent talent) {
        return TALENTS.contains(talent);
    }
    public boolean isUnlockAble(@NotNull BasicTalent talent) {
        BasicTalent parent = talent.getParent();
        return parent == null || TALENTS.contains(parent);
    }
    public boolean canBePurchased(@NotNull BasicTalent talent) {
        return isUnlockAble(talent) && talent.isPurchased() && talent.getWisdomCost() <= wisdom;
    }

    public boolean buyTalent(@NotNull BasicTalent talent) {
        if (!talent.isPurchased()) return false;
        int cost = talent.getWisdomCost();
        if (cost > wisdom) return false;
        boolean flag = obtainTalent(talent);
        if (flag) wisdom -= cost;
        return flag;
    }
    public boolean obtainTalent(@NotNull BasicTalent talent) {
        if (TALENTS.contains(talent)) return false;
        if (!isUnlockAble(talent)) return false;

        TalentHelper.onTalentUnlock(getPlayer(), talent);
        sendTalentToast(talent);
        return TALENTS.add(talent);
    }
    public boolean revokeTalent(@NotNull BasicTalent talent) {
        if (!TALENTS.contains(talent)) return false;
        TalentHelper.onTalentRevoke(getPlayer(), talent);
        return TALENTS.remove(talent);
    }
    public void revokeAll() {
        TALENTS.forEach(talent -> TalentHelper.onTalentRevoke(getPlayer(), talent));
        TALENTS.clear();
    }
    public void sendTalentToast(BasicTalent talent) {
        ToastComponent gui = Minecraft.getInstance().getToasts();
        if (gui.getToast(TalentToast.class, talent) == null) gui.addToast(new TalentToast(talent));

    }
    public boolean hasTalent(@NotNull BasicTalent talent) {
        return TALENTS.contains(talent);
    }
    public void setWisdom(int amount) {
        this.wisdom = amount;
    }
    private record TalentToast(BasicTalent talent) implements Toast {
        @NotNull
        @Override
        public BasicTalent getToken() {
                return talent;
            }
        @NotNull
        @Override
        public Visibility render(GuiGraphics graphics, ToastComponent toastGui, long delta) {
            graphics.blit(TEXTURE, 0, 0, 0, 0, width(), height());
            Font font = toastGui.getMinecraft().font;
            graphics.drawString(font, Component.translatable(talent.getTitle() + ".name"), 30, 7, 0xfff000f0, false);
            graphics.drawString(font, Component.translatable("fantazia.gui.talent.toast.info"), 30, 17, 0xffffffff, false);
            graphics.blit(talent.getIconTexture(), 6, 6, 0, 0, 20, 20, 20, 20);
            return delta >= 5000L ? Visibility.HIDE : Visibility.SHOW;
        }
    }
}
