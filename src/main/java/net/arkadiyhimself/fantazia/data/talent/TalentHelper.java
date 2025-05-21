package net.arkadiyhimself.fantazia.data.talent;

import com.google.common.collect.ImmutableList;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.PlayerAbilityHelper;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders.TalentsHolder;
import net.arkadiyhimself.fantazia.data.talent.reload.ServerTalentManager;
import net.arkadiyhimself.fantazia.util.library.hierarchy.IHierarchy;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class TalentHelper {

    private TalentHelper() {}

    public static void onTalentUnlock(@NotNull Player player, @NotNull Talent talent) {
        if (player instanceof ServerPlayer serverPlayer) {
            revokeAdvancement(serverPlayer, talent);
            talent.applyModifiers(serverPlayer);
        }
        talent.applyImpacts(player);
    }

    public static void onTalentRevoke(@NotNull Player player, @NotNull Talent talent) {
        if (player instanceof ServerPlayer serverPlayer) {
            revokeAdvancement(serverPlayer, talent);
            talent.removeModifiers(serverPlayer);
        }
        talent.removeImpacts(player);
    }

    private static void revokeAdvancement(@NotNull ServerPlayer player, @NotNull Talent talent) {
        AdvancementHolder advancement = ServerTalentManager.getAdvancement(talent, player.server);
        if (advancement == null) return;
        AdvancementProgress progress = player.getAdvancements().getOrStartProgress(advancement);
        for(String s : progress.getCompletedCriteria()) player.getAdvancements().revoke(advancement, s);
    }

    private static void revokeAdvancement(@NotNull ServerPlayer player, @NotNull AdvancementHolder advancement) {
        AdvancementProgress progress = player.getAdvancements().getOrStartProgress(advancement);
        for(String s : progress.getCompletedCriteria()) player.getAdvancements().revoke(advancement, s);
    }

    public static ImmutableList<Talent> getTalents(@NotNull Player player) {
        TalentsHolder talentsHolder = PlayerAbilityHelper.takeHolder(player, TalentsHolder.class);
        return talentsHolder == null ? ImmutableList.of() : talentsHolder.getTalents();
    }

    public static boolean hasTalent(@NotNull Player player, @NotNull Talent talent) {
        return getTalents(player).contains(talent);
    }

    public static boolean hasTalent(@NotNull Player player, @NotNull ResourceLocation location) {
        Talent talent = ServerTalentManager.getTalent(location);
        return talent != null && hasTalent(player, talent);
    }

    public static void onAdvancementObtain(@NotNull AdvancementHolder advancement, @NotNull Player player) throws TalentDataException {
        ResourceLocation id = advancement.id();
        List<IHierarchy<Talent>> upgradedHierarchies = Lists.newArrayList();

        TalentsHolder talentsHolder = PlayerAbilityHelper.takeHolder(player, TalentsHolder.class);
        if (talentsHolder == null) return;
        for (Talent talent : ServerTalentManager.getAllTalents().values()) {
            IHierarchy<Talent> hierarchy = talent.getHierarchy();
            Optional<ResourceLocation> adv = talent.advancement();
            if (adv.isEmpty() || !adv.get().equals(id)) continue;
            if (player instanceof ServerPlayer serverPlayer) revokeAdvancement(serverPlayer, advancement);
            if (upgradedHierarchies.contains(hierarchy) || talentsHolder.hasTalent(talent) || !talentsHolder.isUnlockAble(talent)) continue;
            upgradedHierarchies.add(hierarchy);
            talentsHolder.tryObtainTalent(talent);
        }
    }

    public static int getUnlockLevel(@NotNull Player player, @NotNull ResourceLocation hierarchyLocation) {
        TalentsHolder talentsHolder = PlayerAbilityHelper.takeHolder(player, TalentsHolder.class);
        return talentsHolder == null ? 0 : talentsHolder.upgradeLevel(hierarchyLocation);
    }
}
