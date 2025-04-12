package net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.attachment.entity.IDamageEventListener;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.PlayerAbilityGetter;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.PlayerAbilityHolder;
import net.arkadiyhimself.fantazia.data.criterion.ObtainTalentTrigger;
import net.arkadiyhimself.fantazia.data.talent.TalentHelper;
import net.arkadiyhimself.fantazia.data.talent.TalentTreeData;
import net.arkadiyhimself.fantazia.data.talent.reload.TalentManager;
import net.arkadiyhimself.fantazia.data.talent.reload.WisdomRewardManager;
import net.arkadiyhimself.fantazia.data.talent.types.ITalent;
import net.arkadiyhimself.fantazia.util.library.hierarchy.ChainHierarchy;
import net.arkadiyhimself.fantazia.util.library.hierarchy.ChaoticHierarchy;
import net.arkadiyhimself.fantazia.util.library.hierarchy.IHierarchy;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TalentsHolder extends PlayerAbilityHolder implements IDamageEventListener {

    private static final int XP_PER_WISDOM = 25;

    private final NonNullList<ITalent> TALENTS = NonNullList.create();
    private final ProgressHolder progressHolder = new ProgressHolder();
    private int wisdom = 0;
    private int xpConverting = 0;

    public TalentsHolder(Player player) {
        super(player, Fantazia.res("talents"));
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("wisdom", wisdom);
        tag.putInt("xpConverting", xpConverting);
        ListTag talentTag = new ListTag();
        TALENTS.forEach(talent -> talentTag.add(StringTag.valueOf(talent.getID().toString())));
        tag.put("talents", talentTag);
        tag.put("progress", progressHolder.serializeNBT(provider));
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag compoundTag) {
        TALENTS.clear();

        wisdom = compoundTag.getInt("wisdom");
        xpConverting = compoundTag.getInt("xpConverting");
        if (compoundTag.contains("progress")) progressHolder.deserializeNBT(provider, compoundTag.getCompound("progress"));

        if (!compoundTag.contains("talents")) return;

        ListTag talentTags = compoundTag.getList("talents", Tag.TAG_STRING);

        for (Tag talentTag : talentTags) {
            ResourceLocation talentID = ResourceLocation.parse(talentTag.getAsString());
            ITalent talent = TalentManager.getTalents().get(talentID);
            if (talent == null) continue;
            TALENTS.add(talent);
        }

        for (ITalent talent : getTalents()) talent.applyModifiers(getPlayer());
    }

    @Override
    public void onHit(LivingIncomingDamageEvent event) {
        Holder<DamageType> damageTypeHolder = event.getSource().typeHolder();

        float damage = event.getAmount();
        float finalMultiplier = 1f;

        for (ITalent talent : TALENTS) {
            if (talent.getProperties().containsImmunityTo(damageTypeHolder)) {
                event.setCanceled(true);
                return;
            }

            // The damage multipliers from talents are supposed to be additive;
            // e.g. you have two talents that reduce fall damage by 10% each (so, the multiplier is 0.9)
            // instead of getting 0.9 * 0.9 = 0.81 you are going to get 1 - 0.1 - 0.1 = 0.8
            finalMultiplier += talent.getProperties().getDamageMultiplier(damageTypeHolder) - 1f;
        }

        event.setAmount(damage * finalMultiplier);
    }

    public ImmutableList<ITalent> getTalents() {
        return ImmutableList.copyOf(TALENTS);
    }

    public int getWisdom() {
        return wisdom;
    }

    public void grantWisdom(int amount) {
        if (amount <= 0) return;
        this.wisdom += amount;
        PlayerAbilityGetter.acceptConsumer(getPlayer(), ClientValuesHolder.class, clientValues -> clientValues.obtainedWisdom(amount));
    }

    public void convertXP(int xp) {
        xp = Math.abs(xp);
        int newXP = xpConverting + xp;
        int addWis = newXP / XP_PER_WISDOM;
        int remaining = newXP % XP_PER_WISDOM;

        grantWisdom(addWis);
        xpConverting = remaining;
    }

    public ProgressHolder getProgressHolder() {
        return progressHolder;
    }

    public boolean talentUnlocked(@NotNull ITalent talent) {
        return TALENTS.contains(talent);
    }

    public boolean isUnlockAble(@NotNull ITalent talent) {
        ITalent parent = talent.getParent();
        return parent == null || TALENTS.contains(parent);
    }

    public boolean canBePurchased(@NotNull ITalent talent) {
        return isUnlockAble(talent) && talent.toBePurchased() && talent.getProperties().wisdom() <= wisdom;
    }

    public boolean buyTalent(@NotNull ITalent talent) {
        if (!talent.toBePurchased()) return false;
        int cost = talent.getProperties().wisdom();
        if (cost > wisdom) return false;
        boolean flag = obtainTalent(talent);
        if (flag) wisdom -= cost;
        return flag;
    }

    public boolean buyTalent(ResourceLocation id) {
        ITalent talent = TalentManager.getTalents().get(id);
        return talent != null && buyTalent(talent);
    }

    public boolean obtainTalent(@NotNull ITalent talent) {
        if (TALENTS.contains(talent)) return false;
        if (!isUnlockAble(talent)) return false;

        TalentHelper.onTalentUnlock(getPlayer(), talent);
        TALENTS.add(talent);
        if (getPlayer() instanceof ServerPlayer serverPlayer) ObtainTalentTrigger.INSTANCE.trigger(serverPlayer, this);
        sendTalentToast(talent);
        return true;
    }

    public boolean obtainTalent(ResourceLocation id) {
        ITalent talent = TalentManager.getTalents().get(id);
        return talent != null && obtainTalent(talent);
    }

    public boolean revokeTalent(@NotNull ITalent talent) {
        if (!TALENTS.contains(talent)) return false;
        TalentHelper.onTalentRevoke(getPlayer(), talent);
        return TALENTS.remove(talent);
    }

    public void revokeAll() {
        TALENTS.forEach(talent -> TalentHelper.onTalentRevoke(getPlayer(), talent));
        TALENTS.clear();
        progressHolder.clear();
    }

    public void sendTalentToast(ITalent talent) {
        ToastComponent gui = Minecraft.getInstance().getToasts();
        if (gui.getToast(TalentToast.class, talent) == null) gui.addToast(new TalentToast(talent));

    }

    public boolean hasTalent(@NotNull ITalent talent) {
        return TALENTS.contains(talent);
    }

    public void setWisdom(int amount) {
        this.wisdom = amount;
    }

    public int upgradeLevel(ResourceLocation location) {
        IHierarchy<ITalent> talentIHierarchy = TalentTreeData.getLocationToHierarchy().get(location);
        if (!(talentIHierarchy instanceof ChainHierarchy<ITalent> chainHierarchy) || chainHierarchy instanceof ChaoticHierarchy<ITalent>) return 0;
        int lvl = 0;
        for (ITalent talent : chainHierarchy.getElements()) {
            if (!hasTalent(talent)) break;
            lvl++;
        }
        return lvl;
    }

    private record TalentToast(ITalent talent) implements Toast {
        private static final ResourceLocation BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("toast/advancement");

        @NotNull
        @Override
        public ITalent getToken() {
                return talent;
            }

        @NotNull
        @Override
        public Visibility render(GuiGraphics graphics, ToastComponent toastGui, long delta) {
            graphics.blitSprite(BACKGROUND_SPRITE, 0, 0, width(), height());
            Font font = toastGui.getMinecraft().font;
            graphics.drawString(font, Component.translatable(talent.getProperties().title() + ".name"), 30, 7, 0xfff000f0, false);
            graphics.drawString(font, Component.translatable("fantazia.gui.talent.toast.info"), 30, 17, 0xffffffff, false);
            graphics.blit(talent.getProperties().iconTexture(), 6, 6, 0, 0, 20, 20, 20, 20);
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
