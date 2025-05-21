package net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders;

import com.google.common.collect.ImmutableList;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.attachment.entity.IDamageEventListener;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.PlayerAbilityHolder;
import net.arkadiyhimself.fantazia.data.criterion.ObtainTalentTrigger;
import net.arkadiyhimself.fantazia.data.talent.Talent;
import net.arkadiyhimself.fantazia.data.talent.TalentHelper;
import net.arkadiyhimself.fantazia.data.talent.TalentTreeData;
import net.arkadiyhimself.fantazia.data.talent.reload.ServerTalentManager;
import net.arkadiyhimself.fantazia.data.talent.reload.ServerWisdomRewardManager;
import net.arkadiyhimself.fantazia.data.talent.wisdom_reward.WisdomRewardsCombined;
import net.arkadiyhimself.fantazia.packets.IPacket;
import net.arkadiyhimself.fantazia.registries.FTZSoundEvents;
import net.arkadiyhimself.fantazia.util.library.hierarchy.ChainHierarchy;
import net.arkadiyhimself.fantazia.util.library.hierarchy.ChaoticHierarchy;
import net.arkadiyhimself.fantazia.util.library.hierarchy.IHierarchy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
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

import java.util.List;

public class TalentsHolder extends PlayerAbilityHolder implements IDamageEventListener {

    private static final int XP_PER_WISDOM = 25;

    private final List<WisdomRewardsCombined> wisdomRewards = Lists.newArrayList();
    private final List<Talent> talents = Lists.newArrayList();
    private int wisdom = 0;
    private int xpConverting = 0;

    public TalentsHolder(Player player) {
        super(player, Fantazia.res("talents"));
        wisdomRewards.addAll(ServerWisdomRewardManager.createWisdomRewards());
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("wisdom", wisdom);
        tag.putInt("xpConverting", xpConverting);
        ListTag talentTag = new ListTag();
        talents.forEach(talent -> talentTag.add(StringTag.valueOf(talent.id().toString())));
        tag.put("talents", talentTag);

        ListTag listTag = new ListTag();
        for (WisdomRewardsCombined wisdomRewards : wisdomRewards) listTag.add(wisdomRewards.serialize());
        tag.put("wisdomRewards", listTag);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag tag) {
        talents.clear();
        wisdomRewards.clear();

        wisdom = tag.getInt("wisdom");
        xpConverting = tag.getInt("xpConverting");

        ListTag talentTags = tag.getList("talents", Tag.TAG_STRING);

        for (Tag talentTag : talentTags) {
            ResourceLocation talentID = ResourceLocation.parse(talentTag.getAsString());
            Talent talent = ServerTalentManager.getTalent(talentID);
            if (talent == null) continue;
            talents.add(talent);
        }

        ListTag listTag = tag.getList("wisdomRewards", Tag.TAG_COMPOUND);
        for (int i = 0; i < listTag.size(); i++) wisdomRewards.add(WisdomRewardsCombined.deserialize(listTag.getCompound(i)));
    }

    @Override
    public CompoundTag serializeInitial() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("wisdom", wisdom);
        ListTag talentTag = new ListTag();
        talents.forEach(talent -> talentTag.add(StringTag.valueOf(talent.id().toString())));
        tag.put("talents", talentTag);
        return tag;
    }

    @Override
    public void deserializeInitial(CompoundTag compoundTag) {
        talents.clear();

        wisdom = compoundTag.getInt("wisdom");

        if (!compoundTag.contains("talents")) return;

        ListTag talentTags = compoundTag.getList("talents", Tag.TAG_STRING);

        for (Tag talentTag : talentTags) {
            ResourceLocation talentID = ResourceLocation.parse(talentTag.getAsString());
            Talent talent = ServerTalentManager.getTalent(talentID);
            if (talent == null) continue;
            talents.add(talent);
        }
    }

    @Override
    public void onHit(LivingIncomingDamageEvent event) {
        Holder<DamageType> holder = event.getSource().typeHolder();

        float damage = event.getAmount();
        float finalMultiplier = 1f;

        for (Talent talent : talents) {
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

    public ImmutableList<Talent> getTalents() {
        return ImmutableList.copyOf(talents);
    }

    public int getWisdom() {
        return wisdom;
    }

    public void grantWisdom(int amount) {
        if (amount <= 0) return;
        this.wisdom += amount;
        if (getPlayer() instanceof ServerPlayer serverPlayer) IPacket.wisdomObtained(serverPlayer, amount);
    }

    public void convertXP(int xp) {
        xp = Math.abs(xp);
        int newXP = xpConverting + xp;
        int addWis = newXP / XP_PER_WISDOM;
        int remaining = newXP % XP_PER_WISDOM;

        grantWisdom(addWis);
        xpConverting = remaining;
    }

    public boolean talentUnlocked(@NotNull Talent talent) {
        return talents.contains(talent);
    }

    public boolean isUnlockAble(@NotNull Talent talent) {
        Talent parent = talent.getParent();
        return parent == null || talentUnlocked(parent);
    }

    public boolean canBePurchased(@NotNull Talent talent) {
        return isUnlockAble(talent) && talent.purchasable() && talent.wisdom() <= wisdom;
    }

    public void buyTalent(@NotNull Talent talent) {
        if (!talent.purchasable()) return;
        int cost = talent.wisdom();
        if (cost > wisdom) return;
        boolean flag = tryObtainTalent(talent);
        if (getPlayer().level().isClientSide()) {
            if (flag) IPacket.talentBuying(talent.id());
            else Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(FTZSoundEvents.DENIED.value(), 1f, 1f));
        }
        if (flag) wisdom -= cost;
    }

    public boolean tryObtainTalent(@NotNull Talent talent) {
        if (talents.contains(talent)) return false;
        if (!isUnlockAble(talent)) return false;
        talents.add(talent);
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
        if (!talents.contains(talent)) return false;
        if (getPlayer() instanceof ServerPlayer serverPlayer) {
            IPacket.talentPossession(serverPlayer, talent, false);
            TalentHelper.onTalentRevoke(serverPlayer, talent);
        }
        return talents.remove(talent);
    }

    public void revokeAll() {
        talents.forEach(talent -> TalentHelper.onTalentRevoke(getPlayer(), talent));
        talents.clear();
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
        return talents.contains(talent);
    }

    public void setWisdom(int amount) {
        this.wisdom = amount;
        if (getPlayer() instanceof ServerPlayer serverPlayer) IPacket.setWisdom(serverPlayer, amount);
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

    private record TalentToast(Talent talent) implements Toast {
        private static final ResourceLocation BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("toast/advancement");

        @NotNull
        @Override
        public Talent getToken() {
                return talent;
            }

        @NotNull
        @Override
        public Visibility render(GuiGraphics graphics, ToastComponent toastGui, long delta) {
            graphics.blitSprite(BACKGROUND_SPRITE, 0, 0, width(), height());
            Font font = toastGui.getMinecraft().font;
            graphics.drawString(font, Component.translatable(talent.title() + ".name"), 30, 7, 0xfff000f0, false);
            graphics.drawString(font, Component.translatable("fantazia.gui.talent.toast.info"), 30, 17, 0xffffffff, false);
            graphics.blit(talent.icon(), 6, 6, 0, 0, 20, 20, 20, 20);
            return delta >= 5000L ? Visibility.HIDE : Visibility.SHOW;
        }
    }
}
