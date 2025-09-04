package net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.holders;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.IDamageEventListener;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.PlayerAbilityHolder;
import net.arkadiyhimself.fantazia.common.api.prompt.Prompts;
import net.arkadiyhimself.fantazia.data.criterion.ObtainTalentTrigger;
import net.arkadiyhimself.fantazia.data.talent.Talent;
import net.arkadiyhimself.fantazia.data.talent.TalentHelper;
import net.arkadiyhimself.fantazia.data.talent.TalentTreeData;
import net.arkadiyhimself.fantazia.data.talent.reload.ServerTalentManager;
import net.arkadiyhimself.fantazia.data.talent.reload.ServerWisdomRewardManager;
import net.arkadiyhimself.fantazia.data.talent.wisdom_reward.WisdomRewardsCombined;
import net.arkadiyhimself.fantazia.networking.IPacket;
import net.arkadiyhimself.fantazia.util.library.hierarchy.ChainHierarchy;
import net.arkadiyhimself.fantazia.util.library.hierarchy.ChaoticHierarchy;
import net.arkadiyhimself.fantazia.util.library.hierarchy.IHierarchy;
import net.arkadiyhimself.fantazia.util.wheremagichappens.FantazicUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.ArrayList;
import java.util.List;

public class TalentsHolder extends PlayerAbilityHolder implements IDamageEventListener {

    public static final int XP_PER_WISDOM = 10;

    private final List<WisdomRewardsCombined> wisdomRewards = Lists.newArrayList();
    private final List<Talent> allObtainedTalents = Lists.newArrayList();
    private final List<Talent> disabledTalents = Lists.newArrayList();
    private int wisdom = 0;
    private int xpConverting = 0;

    public TalentsHolder(Player player) {
        super(player, Fantazia.location("talents"));
        wisdomRewards.addAll(ServerWisdomRewardManager.createWisdomRewards());
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("wisdom", wisdom);
        tag.putInt("xpConverting", xpConverting);

        ListTag talentTag = new ListTag();
        allObtainedTalents.forEach(talent -> talentTag.add(StringTag.valueOf(talent.id().toString())));
        tag.put("talents", talentTag);

        ListTag disabledTag = new ListTag();
        disabledTalents.forEach(talent -> disabledTag.add(StringTag.valueOf(talent.id().toString())));
        tag.put("disabled", disabledTag);

        ListTag listTag = new ListTag();
        for (WisdomRewardsCombined wisdomRewards : wisdomRewards) listTag.add(wisdomRewards.serialize());
        tag.put("wisdomRewards", listTag);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag tag) {
        allObtainedTalents.clear();
        wisdomRewards.clear();
        disabledTalents.clear();

        wisdom = tag.getInt("wisdom");
        xpConverting = tag.getInt("xpConverting");

        ListTag talentTags = tag.getList("talents", Tag.TAG_STRING);

        for (Tag talentTag : talentTags) {
            ResourceLocation talentID = ResourceLocation.parse(talentTag.getAsString());
            Talent talent = ServerTalentManager.getTalent(talentID);
            if (talent == null) continue;
            allObtainedTalents.add(talent);
        }

        ListTag disabledTags = tag.getList("disabled", Tag.TAG_STRING);

        for (Tag talentTag : disabledTags) {
            ResourceLocation talentID = ResourceLocation.parse(talentTag.getAsString());
            Talent talent = ServerTalentManager.getTalent(talentID);
            if (talent == null) continue;
            disabledTalents.add(talent);
        }

        ListTag listTag = tag.getList("wisdomRewards", Tag.TAG_COMPOUND);
        for (int i = 0; i < listTag.size(); i++) wisdomRewards.add(WisdomRewardsCombined.deserialize(listTag.getCompound(i)));

        if (getPlayer() instanceof ServerPlayer serverPlayer) {
            for (Talent talent : getAllObtainedTalents())
                talent.applyModifiers(serverPlayer);
        }
    }

    @Override
    public CompoundTag serializeInitial() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("wisdom", wisdom);

        ListTag talentTag = new ListTag();
        allObtainedTalents.forEach(talent -> talentTag.add(StringTag.valueOf(talent.id().toString())));
        tag.put("talents", talentTag);

        ListTag disabledTag = new ListTag();
        disabledTalents.forEach(talent -> disabledTag.add(StringTag.valueOf(talent.id().toString())));
        tag.put("disabled", disabledTag);

        return tag;
    }

    @Override
    public void deserializeInitial(CompoundTag tag) {
        allObtainedTalents.clear();

        wisdom = tag.getInt("wisdom");

        if (!tag.contains("talents")) return;

        ListTag talentTags = tag.getList("talents", Tag.TAG_STRING);

        for (Tag talentTag : talentTags) {
            ResourceLocation talentID = ResourceLocation.parse(talentTag.getAsString());
            Talent talent = ServerTalentManager.getTalent(talentID);
            if (talent == null) continue;
            allObtainedTalents.add(talent);
        }

        ListTag disabledTags = tag.getList("disabled", Tag.TAG_STRING);

        for (Tag talentTag : disabledTags) {
            ResourceLocation talentID = ResourceLocation.parse(talentTag.getAsString());
            Talent talent = ServerTalentManager.getTalent(talentID);
            if (talent == null) continue;
            disabledTalents.add(talent);
        }
    }

    @Override
    public void onHit(LivingIncomingDamageEvent event) {
        Holder<DamageType> holder = event.getSource().typeHolder();

        float damage = event.getAmount();
        float finalMultiplier = 1f;

        for (Talent talent : allObtainedTalents) {
            if (talent.containsImmunity(holder)) {
                event.setCanceled(true);
                return;
            }

            // The damage multipliers from talents are supposed to be additive;
            // e.g. you have two talents that reduce fall damage by 10% each (so, the multiplier is 0.9)
            // instead of getting 0.9 * 0.9 = 0.81 you are going to get 1 - 0.1 - 0.1 = 0.8
            finalMultiplier *= talent.getMultiplier(holder);
        }

        event.setAmount(damage * finalMultiplier);
    }

    @Override
    public void respawn() {
        if (getPlayer() instanceof ServerPlayer serverPlayer) for (Talent talent : allObtainedTalents) talent.applyModifiers(serverPlayer);
    }

    public List<Talent> getAllObtainedTalents() {
        return new ArrayList<>(allObtainedTalents);
    }

    public int getWisdom() {
        return wisdom;
    }

    public void grantWisdom(int amount) {
        if (amount <= 0) return;
        this.wisdom += amount;
        if (getPlayer() instanceof ServerPlayer serverPlayer) {
            IPacket.wisdomObtained(serverPlayer, amount);
            if (hasPurchasableTalents()) Prompts.OPEN_TALENT_SCREEN.maybePromptPlayer(serverPlayer);
        }
    }

    public boolean hasPurchasableTalents() {
        for (Talent talent : ServerTalentManager.getAllTalents().values())
            if (talent.purchasable() && talent.wisdom() <= wisdom && !hasTalent(talent) && isUnlockAble(talent)) return true;
        return false;
    }

    public void convertExp(int xp) {
        xp = Math.abs(xp);
        int newXP = xpConverting + xp;
        int addWis = newXP / XP_PER_WISDOM;
        int remaining = newXP % XP_PER_WISDOM;

        grantWisdom(addWis);
        xpConverting = remaining;
    }


    public void convertWisdomIntoExp(int rate) {
        rate = Math.min(rate, this.wisdom);
        int newWisdom = wisdom - rate;
        FantazicUtil.addPlayerXP(getPlayer(), XP_PER_WISDOM * rate);
        setWisdom(newWisdom);
    }

    public void convertExpIntoWisdom(int rate) {
        rate = Math.min(rate, getPlayer().totalExperience);
        if (rate <= 0) return;
        FantazicUtil.drainPlayerXP(getPlayer(), rate);
        convertExp(rate);
    }

    public boolean talentUnlocked(@NotNull Talent talent) {
        return allObtainedTalents.contains(talent);
    }

    public boolean isUnlockAble(@NotNull Talent talent) {
        Talent parent = talent.getParent();
        return parent == null || talentUnlocked(parent);
    }

    public boolean enoughWisdom(@NotNull Talent talent) {
        return talent.wisdom() <= wisdom;
    }

    public boolean canBePurchased(@NotNull Talent talent) {
        return isUnlockAble(talent) && talent.purchasable() && enoughWisdom(talent);
    }

    public boolean tryBuyTalent(@NotNull Talent talent) {
        if (!talent.purchasable()) return false;
        int cost = talent.wisdom();
        if (cost > wisdom) return false;
        boolean flag = tryObtainTalent(talent);
        if (getPlayer().level().isClientSide()) {
            if (flag) IPacket.talentBuying(talent.id());
        }
        if (flag) wisdom -= cost;
        return flag;
    }

    public boolean tryObtainTalent(@NotNull Talent talent) {
        if (hasTalent(talent)) return false;
        if (!isUnlockAble(talent)) return false;
        allObtainedTalents.add(talent);
        TalentHelper.onTalentUnlock(getPlayer(), talent);

        if (getPlayer() instanceof ServerPlayer serverPlayer) {
            ObtainTalentTrigger.INSTANCE.trigger(serverPlayer, this);
            IPacket.talentPossession(serverPlayer, talent, true);
        }
        else sendTalentToast(talent);

        return true;
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean tryRevokeTalent(@NotNull Talent talent) {
        if (!allObtainedTalents.contains(talent)) return false;
        if (getPlayer() instanceof ServerPlayer serverPlayer) {
            IPacket.talentPossession(serverPlayer, talent, false);
            TalentHelper.onTalentRevoke(serverPlayer, talent);
        }
        return allObtainedTalents.remove(talent);
    }

    public void revokeAll() {
        allObtainedTalents.forEach(talent -> TalentHelper.onTalentRevoke(getPlayer(), talent));
        allObtainedTalents.clear();
        if (getPlayer() instanceof ServerPlayer serverPlayer) IPacket.revokeAllTalents(serverPlayer);
    }

    public void resetWisdomRewards() {
        wisdomRewards.clear();
        wisdomRewards.addAll(ServerWisdomRewardManager.createWisdomRewards());
    }

    public void sendTalentToast(Talent talent) {
        ToastComponent gui = Minecraft.getInstance().getToasts();
        if (gui.getToast(TalentToast.class, talent) == null) gui.addToast(new TalentToast(talent));
    }

    public boolean hasTalent(@NotNull Talent talent) {
        return allObtainedTalents.contains(talent);
    }

    public void setWisdom(int value) {
        this.wisdom = value;
        if (getPlayer() instanceof ServerPlayer serverPlayer) IPacket.setWisdom(serverPlayer, value);
    }

    public void spendWisdom(int amount) {
        int newValue = Math.max(0, this.wisdom - amount);
        setWisdom(newValue);
    }

    public int upgradeLevel(ResourceLocation location) {
        IHierarchy<Talent> talentIHierarchy = TalentTreeData.getLocationToHierarchy().get(location);
        if (!(talentIHierarchy instanceof ChainHierarchy<Talent> chainHierarchy) || chainHierarchy instanceof ChaoticHierarchy<Talent>) return 0;
        int lvl = 0;
        for (Talent talent : chainHierarchy.getElements()) {
            if (!hasTalent(talent)) break;
            lvl++;
        }
        return lvl;
    }

    public void tryAwardWisdom(ResourceLocation category, ResourceLocation instance) {
        for (WisdomRewardsCombined wisdomReward : wisdomRewards) {
            if (!wisdomReward.category().equals(category)) continue;
            grantWisdom(wisdomReward.getReward(instance).award());
        }
    }

    public void disableTalent(Talent talent) {
        if (!allObtainedTalents.contains(talent) || disabledTalents.contains(talent)) return;
        talent.disableTalent(getPlayer());
        disabledTalents.add(talent);
    }

    public void enableTalent(Talent talent) {
        if (!allObtainedTalents.contains(talent) || !disabledTalents.contains(talent)) return;
        talent.enableTalent(getPlayer());
        disabledTalents.remove(talent);
    }

    public void clickedTalent(Talent talent) {
        if (!talent.canBeDisabled()) return;
        if (disabledTalents.contains(talent)) enableTalent(talent);
        else disableTalent(talent);
        if (getPlayer().level().isClientSide()) IPacket.talentDisable(talent.id());
    }

    public boolean isDisabled(Talent talent) {
        return disabledTalents.contains(talent);
    }

    public List<Talent> getDisabledTalents() {
        return new ArrayList<>(disabledTalents);
    }

    private record TalentToast(Talent talent) implements Toast {

        private static final ResourceLocation BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("toast/advancement");

        @NotNull
        @Override
        public Talent getToken() {
                return talent;
            }

        @NotNull
        @Override
        public Visibility render(GuiGraphics graphics, ToastComponent toastComponent, long delta) {
            graphics.blitSprite(BACKGROUND_SPRITE, 0, 0, width(), height());
            Font font = toastComponent.getMinecraft().font;
            graphics.drawString(font, Component.translatable(talent.title() + ".name"), 30, 7, 0xfff000f0);
            graphics.drawString(font, Component.translatable("fantazia.gui.talent.toast.info"), 30, 17, 0xffffffff);
            graphics.blit(talent.icon(), 6, 6, 0, 0, 20, 20, 20, 20);
            return delta >= 5000L ? Visibility.HIDE : Visibility.SHOW;
        }
    }
}
