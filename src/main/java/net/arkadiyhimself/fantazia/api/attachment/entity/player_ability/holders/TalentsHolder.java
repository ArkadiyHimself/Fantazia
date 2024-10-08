package net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.PlayerAbilityGetter;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.PlayerAbilityHolder;
import net.arkadiyhimself.fantazia.data.criteritas.ObtainTalentTrigger;
import net.arkadiyhimself.fantazia.data.talents.AttributeTalent;
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
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TalentsHolder extends PlayerAbilityHolder {
    private final NonNullList<BasicTalent> TALENTS = NonNullList.create();
    private final ProgressHolder progressHolder = new ProgressHolder();
    private int wisdom = 0;
    public TalentsHolder(Player player) {
        super(player, Fantazia.res("talents"));
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("wisdom", wisdom);
        ListTag talentTag = new ListTag();
        TALENTS.forEach(talent -> talentTag.add(StringTag.valueOf(talent.getID().toString())));
        tag.put("talents", talentTag);
        tag.put("progress", progressHolder.serializeNBT(provider));
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag compoundTag) {
        TALENTS.clear();

        if (compoundTag.contains("wisdom")) wisdom = compoundTag.getInt("wisdom");
        if (compoundTag.contains("progress")) progressHolder.deserializeNBT(provider, compoundTag.getCompound("progress"));

        if (!compoundTag.contains("talents")) return;

        ListTag talentTags = compoundTag.getList("talents", Tag.TAG_STRING);

        for (Tag talentTag : talentTags) {
            ResourceLocation talentID = ResourceLocation.parse(talentTag.getAsString());
            BasicTalent talent = TalentManager.getTalents().get(talentID);
            if (talent == null) continue;
            TALENTS.add(talent);
        }

        for (BasicTalent talent : getTalents()) if (talent instanceof AttributeTalent attributeTalent) attributeTalent.applyModifier(getPlayer());
    }

    public ImmutableList<BasicTalent> getTalents() {
        return ImmutableList.copyOf(TALENTS);
    }
    public int getWisdom() {
        return wisdom;
    }
    public void grantWisdom(int amount) {
        this.wisdom += amount;
        PlayerAbilityGetter.acceptConsumer(getPlayer(), ClientValuesHolder.class, clientValues -> clientValues.obtainedWisdom(amount));
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
    public boolean buyTalent(ResourceLocation id) {
        BasicTalent talent = TalentManager.getTalents().get(id);
        return talent != null && buyTalent(talent);
    }
    public boolean obtainTalent(@NotNull BasicTalent talent) {
        if (TALENTS.contains(talent)) return false;
        if (!isUnlockAble(talent)) return false;

        TalentHelper.onTalentUnlock(getPlayer(), talent);
        TALENTS.add(talent);
        if (getPlayer() instanceof ServerPlayer serverPlayer) ObtainTalentTrigger.INSTANCE.trigger(serverPlayer, this);
        sendTalentToast(talent);
        return true;
    }
    public boolean obtainTalent(ResourceLocation id) {
        BasicTalent talent = TalentManager.getTalents().get(id);
        return talent != null && obtainTalent(talent);
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
        private static final ResourceLocation BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("toast/advancement");

        @NotNull
        @Override
        public BasicTalent getToken() {
                return talent;
            }

        @NotNull
        @Override
        public Visibility render(GuiGraphics graphics, ToastComponent toastGui, long delta) {
            graphics.blitSprite(BACKGROUND_SPRITE, 0, 0, width(), height());
            Font font = toastGui.getMinecraft().font;
            graphics.drawString(font, Component.translatable(talent.getTitle() + ".name"), 30, 7, 0xfff000f0, false);
            graphics.drawString(font, Component.translatable("fantazia.gui.talent.toast.info"), 30, 17, 0xffffffff, false);
            graphics.blit(talent.getIconTexture(), 6, 6, 0, 0, 20, 20, 20, 20);
            return delta >= 5000L ? Visibility.HIDE : Visibility.SHOW;
        }
    }
    public class ProgressHolder implements INBTSerializable<CompoundTag> {

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
        public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
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
        public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag compoundTag) {
            PROGRESS.clear();
            TAGS = compoundTag.contains(ID + "done") ? compoundTag.getCompound(ID + "done") : new CompoundTag();
            compoundTag.remove(ID + "done");

            if (!compoundTag.contains(ID + "lists")) return;
            CompoundTag progress = compoundTag.getCompound(ID + "lists");
            for (String name : progress.getAllKeys()) {
                ListTag listTag;

                try {
                    listTag = progress.getList(name, Tag.TAG_STRING);
                } catch (ReportedException exception) {
                    continue;
                }

                List<ResourceLocation> locations = Lists.newArrayList();
                for (Tag tag : listTag) locations.add(ResourceLocation.parse(tag.getAsString()));

                PROGRESS.put(name, locations);
            }
        }
    }
}
