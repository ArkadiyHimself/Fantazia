package net.arkadiyhimself.fantazia.data.talents;

import com.google.common.collect.ImmutableList;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.AbilityGetter;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.AbilityManager;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.abilities.TalentsHolder;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class TalentHelper {
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
        ResourceLocation advID = talent.getID().withPrefix("ftz_talents/");
        Advancement advancement = serverPlayer.server.getAdvancements().getAdvancement(advID);
        if (advancement == null) return;
        AdvancementProgress progress = serverPlayer.getAdvancements().getOrStartProgress(advancement);
        for(String s : progress.getCompletedCriteria()) serverPlayer.getAdvancements().revoke(advancement, s);
    }
    public static ImmutableList<BasicTalent> getTalents(@NotNull Player player) {
        AbilityManager abilityManager = AbilityGetter.getUnwrap(player);
        if (abilityManager == null) return ImmutableList.of();
        TalentsHolder talentsHolder = abilityManager.takeAbility(TalentsHolder.class);
        return talentsHolder == null ? ImmutableList.of() : talentsHolder.getTalents();
    }
    public static boolean hasTalent(@NotNull Player player, @NotNull BasicTalent talent) {
        return getTalents(player).contains(talent);
    }
    public static boolean hasTalent(@NotNull Player player, @NotNull ResourceLocation location) {
        BasicTalent talent = TalentLoad.getTalents().get(location);
        return talent != null && hasTalent(player, talent);
    }
    public static void onAdvancementObtain(@NotNull Advancement advancement, @NotNull Player player) throws TalentDataException {
        ResourceLocation id = advancement.getId();
        boolean isTalent = id.getPath().startsWith("ftz_talents/");
        if (!isTalent) return;
        String modid = id.getNamespace();
        String path = id.getPath().substring(12);
        ResourceLocation talentID = new ResourceLocation(modid, path);
        BasicTalent talent = TalentLoad.getTalents().get(talentID);
        if (talent == null) throw new TalentDataException("A talent is missing: " + talentID);
        AbilityManager abilityManager = AbilityGetter.getUnwrap(player);
        if (abilityManager == null) return;
        abilityManager.getAbility(TalentsHolder.class).ifPresent(talentsHolder -> talentsHolder.obtainTalent(talent));
    }
}
