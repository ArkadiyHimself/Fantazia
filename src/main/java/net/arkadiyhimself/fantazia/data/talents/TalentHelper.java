package net.arkadiyhimself.fantazia.data.talents;

import com.google.common.collect.ImmutableList;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.PlayerAbilityGetter;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders.TalentsHolder;
import net.arkadiyhimself.fantazia.data.talents.reload.TalentManager;
import net.arkadiyhimself.fantazia.util.library.hierarchy.IHierarchy;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TalentHelper {
    private TalentHelper() {}
    public static void onTalentUnlock(@NotNull Player player, @NotNull BasicTalent talent) {
        PlayerAbilityGetter.getUnwrap(player).talentUnlocked(talent);
        if (talent instanceof AttributeTalent attributeTalent) attributeTalent.applyModifier(player);

    }
    public static void onTalentRevoke(@NotNull Player player, @NotNull BasicTalent talent) {
        PlayerAbilityGetter.getUnwrap(player).talentRevoked(talent);
        if (player instanceof ServerPlayer serverPlayer) revokeAdvancement(serverPlayer, talent);
        if (talent instanceof AttributeTalent attributeTalent) attributeTalent.removeModifier(player);
    }
    private static void revokeAdvancement(@NotNull ServerPlayer serverPlayer, @NotNull BasicTalent talent) {
        AdvancementHolder advancement = TalentManager.getAdvancement(talent, serverPlayer.server);
        if (advancement == null) return;
        AdvancementProgress progress = serverPlayer.getAdvancements().getOrStartProgress(advancement);
        for(String s : progress.getCompletedCriteria()) serverPlayer.getAdvancements().revoke(advancement, s);
    }
    private static void revokeAdvancement(@NotNull ServerPlayer serverPlayer, @NotNull AdvancementHolder advancement) {
        AdvancementProgress progress = serverPlayer.getAdvancements().getOrStartProgress(advancement);
        for(String s : progress.getCompletedCriteria()) serverPlayer.getAdvancements().revoke(advancement, s);
    }
    public static ImmutableList<BasicTalent> getTalents(@NotNull Player player) {
        TalentsHolder talentsHolder = PlayerAbilityGetter.takeHolder(player, TalentsHolder.class);
        return talentsHolder == null ? ImmutableList.of() : talentsHolder.getTalents();
    }
    public static boolean hasTalent(@NotNull Player player, @NotNull BasicTalent talent) {
        return getTalents(player).contains(talent);
    }
    public static boolean hasTalent(@NotNull Player player, @NotNull ResourceLocation location) {
        BasicTalent talent = TalentManager.getTalents().get(location);
        return talent != null && hasTalent(player, talent);
    }
    public static void onAdvancementObtain(@NotNull AdvancementHolder advancement, @NotNull Player player) throws TalentDataException {
        ResourceLocation id = advancement.id();
        List<IHierarchy<BasicTalent>> upgradedHierarchies = Lists.newArrayList();

        TalentsHolder talentsHolder = PlayerAbilityGetter.takeHolder(player, TalentsHolder.class);
        if (talentsHolder == null) return;
        for (BasicTalent talent : TalentManager.getTalents().values()) {
            IHierarchy<BasicTalent> hierarchy = talent.getHierarchy();
            if (!id.equals(talent.getAdvancement()) || hierarchy == null || upgradedHierarchies.contains(hierarchy) || talentsHolder.hasTalent(talent) || !talentsHolder.isUnlockAble(talent)) continue;
            if (player instanceof ServerPlayer serverPlayer) revokeAdvancement(serverPlayer, advancement);
            upgradedHierarchies.add(hierarchy);
            talentsHolder.obtainTalent(talent);
        }
    }
}
