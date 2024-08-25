package net.arkadiyhimself.fantazia.api.capability.entity.ability.abilities;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import net.arkadiyhimself.fantazia.api.capability.INBTwrite;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.AbilityGetter;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.AbilityHolder;
import net.arkadiyhimself.fantazia.data.talents.BasicTalent;
import net.arkadiyhimself.fantazia.data.talents.TalentHelper;
import net.arkadiyhimself.fantazia.data.talents.reload.TalentManager;
import net.arkadiyhimself.fantazia.data.talents.reload.WisdomRewardManager;
import net.minecraft.ReportedException;
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
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TalentsHolder extends AbilityHolder {
    private final NonNullList<BasicTalent> TALENTS = NonNullList.create();
    private final ProgressHolder progressHolder = new ProgressHolder();
    private int wisdom = 0;
    public TalentsHolder(Player player) {
        super(player);
    }
    @Override
    public String ID() {
        return "talents_holder";
    }

    @Override
    public CompoundTag serialize(boolean toDisk) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("wisdom", wisdom);
        ListTag talentTag = new ListTag();
        TALENTS.forEach(talent -> talentTag.add(StringTag.valueOf(talent.getID().toString())));
        tag.put("talents", talentTag);
        tag.put("progress", progressHolder.serialize(toDisk));
        return tag;
    }

    @Override
    public void deserialize(CompoundTag tag, boolean fromDisk) {
        TALENTS.clear();

        if (tag.contains("wisdom")) wisdom = tag.getInt("wisdom");
        if (tag.contains("progress")) progressHolder.deserialize(tag.getCompound("progress"), fromDisk);

        if (!tag.contains("talents")) return;

        ListTag talentTags = tag.getList("talents", Tag.TAG_STRING);

        for (Tag talentTag : talentTags) {
            ResourceLocation talentID = new ResourceLocation(talentTag.getAsString());
            BasicTalent talent = TalentManager.getTalents().get(talentID);
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
    public void grantWisdom(int amount) {
        this.wisdom += amount;
        AbilityGetter.abilityConsumer(getPlayer(), ClientValues.class, clientValues -> clientValues.obtainedWisdom(amount));
    }
    public ProgressHolder getProgressHolder() {
        return progressHolder;
    }

    public boolean talentUnlocked(@NotNull BasicTalent talent) {
        return TALENTS.contains(talent);
    }
    public boolean isUnlockAble(@NotNull BasicTalent talent) {
        BasicTalent parent = talent.getParent();
        return parent == null || TALENTS.contains(parent);
    }
    public boolean canBePurchased(@NotNull BasicTalent talent) {
        return isUnlockAble(talent) && talent.isPurchased() && talent.getWisdom() <= wisdom;
    }

    public boolean buyTalent(@NotNull BasicTalent talent) {
        if (!talent.isPurchased()) return false;
        int cost = talent.getWisdom();
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
        progressHolder.clear();
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
    public class ProgressHolder implements INBTwrite {
        private static final String ID = "progress:";
        private final HashMap<String, List<ResourceLocation>> PROGRESS = Maps.newHashMap();
        private CompoundTag TAGS = new CompoundTag();
        private List<ResourceLocation> getOrCreate(String id) {
            if (!PROGRESS.containsKey(id)) PROGRESS.put(id, Lists.newArrayList());
            return PROGRESS.get(id);
        }
        @SuppressWarnings("UnusedReturnValue")
        public boolean award(String id, ResourceLocation location) {
            if (getOrCreate(id).contains(location)) return false;
            getOrCreate(id).add(location);
            grantWisdom(WisdomRewardManager.getReward(id, location));
            return true;
        }
        @SuppressWarnings("UnusedReturnValue")
        public boolean award(String tag, int wisdom) {
            if (TAGS.contains(tag)) return false;
            TAGS.putBoolean(tag, true);
            grantWisdom(wisdom);
            return true;
        }
        public void clear() {
            TAGS = new CompoundTag();
            PROGRESS.clear();
        }

        @Override
        public CompoundTag serialize(boolean toDisk) {
            CompoundTag tag = new CompoundTag();

            tag.put(ID + "done", TAGS);

            CompoundTag progress = new CompoundTag();
            for (Map.Entry<String, List<ResourceLocation>> entry : PROGRESS.entrySet()) {
                ListTag listTag = new ListTag();
                for (ResourceLocation location : entry.getValue()) listTag.add(StringTag.valueOf(location.toString()));
                progress.put(entry.getKey(), listTag);
            }
            tag.put(ID + "lists", progress);

            return tag;
        }
        @Override
        public void deserialize(CompoundTag nbt, boolean fromDisk) {
            PROGRESS.clear();
            TAGS = nbt.contains(ID + "done") ? nbt.getCompound(ID + "done") : new CompoundTag();
            nbt.remove(ID + "done");

            if (!nbt.contains(ID + "lists")) return;
            CompoundTag progress = nbt.getCompound(ID + "lists");
            for (String name : progress.getAllKeys()) {
                ListTag listTag;

                try {
                    listTag = progress.getList(name, Tag.TAG_STRING);
                } catch (ReportedException exception) {
                    continue;
                }

                List<ResourceLocation> locations = Lists.newArrayList();
                for (Tag tag : listTag) locations.add(new ResourceLocation(tag.getAsString()));

                PROGRESS.put(name, locations);
            }
        }
    }
}
