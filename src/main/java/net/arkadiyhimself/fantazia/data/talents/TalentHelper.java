package net.arkadiyhimself.fantazia.data.talents;

import com.google.common.collect.ImmutableList;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.AbilityGetter;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.AbilityManager;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.abilities.TalentsHolder;
import net.arkadiyhimself.fantazia.data.talents.reload.TalentManager;
import net.arkadiyhimself.fantazia.util.library.hierarchy.IHierarchy;
import net.minecraft.advancements.Advancement;
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
        AbilityManager abilityManager = AbilityGetter.getUnwrap(player);
        if (abilityManager != null) abilityManager.talentUnlocked(talent);
        if (talent instanceof AttributeTalent attributeTalent) attributeTalent.applyModifier(player);

    }
    public static void onTalentRevoke(@NotNull Player player, @NotNull BasicTalent talent) {
        AbilityManager abilityManager = AbilityGetter.getUnwrap(player);
        if (abilityManager != null) abilityManager.talentRevoked(talent);
        if (player instanceof ServerPlayer serverPlayer) revokeAdvancement(serverPlayer, talent);
        if (talent instanceof AttributeTalent attributeTalent) attributeTalent.removeModifier(player);
    }
    private static void revokeAdvancement(@NotNull ServerPlayer serverPlayer, @NotNull BasicTalent talent) {
        Advancement advancement = TalentManager.getAdvancement(talent, serverPlayer.server);
        if (advancement == null) return;
        AdvancementProgress progress = serverPlayer.getAdvancements().getOrStartProgress(advancement);
        for(String s : progress.getCompletedCriteria()) serverPlayer.getAdvancements().revoke(advancement, s);
    }
    private static void revokeAdvancement(@NotNull ServerPlayer serverPlayer, @NotNull Advancement advancement) {
        AdvancementProgress progress = serverPlayer.getAdvancements().getOrStartProgress(advancement);
        for(String s : progress.getCompletedCriteria()) serverPlayer.getAdvancements().revoke(advancement, s);
    }
    public static ImmutableList<BasicTalent> getTalents(@NotNull Player player) {
        TalentsHolder talentsHolder = AbilityGetter.takeAbilityHolder(player, TalentsHolder.class);
        return talentsHolder == null ? ImmutableList.of() : talentsHolder.getTalents();
    }
    public static boolean hasTalent(@NotNull Player player, @NotNull BasicTalent talent) {
        return getTalents(player).contains(talent);
    }
    public static boolean hasTalent(@NotNull Player player, @NotNull ResourceLocation location) {
        BasicTalent talent = TalentManager.getTalents().get(location);
        return talent != null && hasTalent(player, talent);
    }
    public static void onAdvancementObtain(@NotNull Advancement advancement, @NotNull Player player) throws TalentDataException {
        ResourceLocation id = advancement.getId();
        if (!id.getNamespace().equals(Fantazia.MODID)) return;
        List<IHierarchy<BasicTalent>> upgradedHierarchies = Lists.newArrayList();

        TalentsHolder talentsHolder = AbilityGetter.takeAbilityHolder(player, TalentsHolder.class);
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
