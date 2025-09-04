package net.arkadiyhimself.fantazia.data.talent;

import com.google.common.collect.Maps;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.PlayerAbilityHelper;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.holders.DashHolder;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.holders.DoubleJumpHolder;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.holders.EuphoriaHolder;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.holders.MeleeBlockHolder;
import net.arkadiyhimself.fantazia.common.registries.FTZAttachmentTypes;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class TalentImpacts {

    private static final Map<ResourceLocation, TalentImpact> IMPACT_MAP = Maps.newHashMap();

    public static final TalentImpact MELEE_BLOCK_UNLOCK;
    public static final TalentImpact MELEE_BLOCK_BLOODLOSS;
    public static final TalentImpact MELEE_BLOCK_DISARM;
    public static final TalentImpact DASH_UPGRADE;
    public static final TalentImpact DOUBLE_JUMP_UNLOCK;
    public static final TalentImpact DOUBLE_JUMP_ELYTRA;
    public static final TalentImpact EUPHORIA_RELENTLESS;
    public static final TalentImpact EUPHORIA_SAVAGE;
    public static final TalentImpact MANA_RECYCLE_UPGRADE;
    public static final TalentImpact WALL_CLIMBING_UNLOCKED;
    public static final TalentImpact WALL_CLIMBING_COBWEB;
    public static final TalentImpact WALL_CLIMBING_POISON;

    public static TalentImpact register(TalentImpact.Builder builder) {
        TalentImpact impact = builder.build();
        IMPACT_MAP.put(impact.id(), impact);
        return impact;
    }

    public static TalentImpact getImpact(ResourceLocation id) {
        return IMPACT_MAP.get(id);
    }

    static {
        MELEE_BLOCK_UNLOCK = register(TalentImpact.builder(Fantazia.location("melee_block.unlock"))
                .apply(player -> PlayerAbilityHelper.acceptConsumer(player, MeleeBlockHolder.class, holder -> holder.setUnlocked(true)))
                .remove(player -> PlayerAbilityHelper.acceptConsumer(player, MeleeBlockHolder.class, holder -> holder.setUnlocked(false)))
                .defaultDisabling());

        MELEE_BLOCK_BLOODLOSS = register(TalentImpact.builder(Fantazia.location("melee_block.bloodloss"))
                .apply(player -> PlayerAbilityHelper.acceptConsumer(player, MeleeBlockHolder.class, holder -> holder.setBloodloss(true)))
                .remove(player -> PlayerAbilityHelper.acceptConsumer(player, MeleeBlockHolder.class, holder -> holder.setBloodloss(false)))
                .defaultDisabling());

        MELEE_BLOCK_DISARM = register(TalentImpact.builder(Fantazia.location("melee_block.disarm"))
                .apply(player -> PlayerAbilityHelper.acceptConsumer(player, MeleeBlockHolder.class, holder -> holder.setDisarm(true)))
                .remove(player -> PlayerAbilityHelper.acceptConsumer(player, MeleeBlockHolder.class, holder -> holder.setDisarm(false)))
                .defaultDisabling());

        DASH_UPGRADE = register(TalentImpact.builder(Fantazia.location("dash.upgrade"))
                .apply(player -> PlayerAbilityHelper.acceptConsumer(player, DashHolder.class, DashHolder::upgrade))
                .remove(player -> PlayerAbilityHelper.acceptConsumer(player, DashHolder.class, DashHolder::downgrade)));

        DOUBLE_JUMP_UNLOCK = register(TalentImpact.builder(Fantazia.location("double_jump.unlock"))
                .apply(player -> PlayerAbilityHelper.acceptConsumer(player, DoubleJumpHolder.class, DoubleJumpHolder::unlock))
                .remove(player -> PlayerAbilityHelper.acceptConsumer(player, DoubleJumpHolder.class, DoubleJumpHolder::lock))
                .enable(player -> PlayerAbilityHelper.acceptConsumer(player, DoubleJumpHolder.class, DoubleJumpHolder::setEnabled))
                .disable(player -> PlayerAbilityHelper.acceptConsumer(player, DoubleJumpHolder.class, DoubleJumpHolder::setDisabled)));

        DOUBLE_JUMP_ELYTRA = register(TalentImpact.builder(Fantazia.location("double_jump.elytra"))
                .apply(player -> PlayerAbilityHelper.acceptConsumer(player, DoubleJumpHolder.class, holder -> holder.setBoostElytra(true)))
                .remove(player -> PlayerAbilityHelper.acceptConsumer(player, DoubleJumpHolder.class, holder -> holder.setBoostElytra(false))));

        EUPHORIA_RELENTLESS = register(TalentImpact.builder(Fantazia.location("euphoria.relentless"))
                .apply(player -> PlayerAbilityHelper.acceptConsumer(player, EuphoriaHolder.class, holder -> holder.setRelentless(true)))
                .remove(player -> PlayerAbilityHelper.acceptConsumer(player, EuphoriaHolder.class, holder -> holder.setRelentless(false))));

        EUPHORIA_SAVAGE = register(TalentImpact.builder(Fantazia.location("euphoria.savage"))
                .apply(player -> PlayerAbilityHelper.acceptConsumer(player, EuphoriaHolder.class, holder -> holder.setSavage(true)))
                .remove(player -> PlayerAbilityHelper.acceptConsumer(player, EuphoriaHolder.class, holder -> holder.setSavage(false))));

        MANA_RECYCLE_UPGRADE = register(TalentImpact.builder(Fantazia.location("mana_recycle.upgrade"))
                .apply(player -> {
                    int prev = player.getData(FTZAttachmentTypes.MANA_RECYCLE_LEVEL);
                    player.setData(FTZAttachmentTypes.MANA_RECYCLE_LEVEL, prev + 1);
                })
                .remove(player -> {
                    int prev = player.getData(FTZAttachmentTypes.MANA_RECYCLE_LEVEL);
                    player.setData(FTZAttachmentTypes.MANA_RECYCLE_LEVEL, prev - 1);
                }));

        WALL_CLIMBING_UNLOCKED = register(TalentImpact.builder(Fantazia.location("wall_climbing.unlock"))
                .apply(player -> player.setData(FTZAttachmentTypes.WALL_CLIMBING_UNLOCKED, true))
                .remove(player -> player.setData(FTZAttachmentTypes.WALL_CLIMBING_UNLOCKED, false))
                .defaultDisabling());

        WALL_CLIMBING_COBWEB = register(TalentImpact.builder(Fantazia.location("wall_climbing.cobweb"))
                .apply(player -> player.setData(FTZAttachmentTypes.WALL_CLIMBING_COBWEB, true))
                .remove(player -> player.setData(FTZAttachmentTypes.WALL_CLIMBING_COBWEB, false))
                .defaultDisabling());

        WALL_CLIMBING_POISON = register(TalentImpact.builder(Fantazia.location("wall_climbing.poison"))
                .apply(player -> player.setData(FTZAttachmentTypes.WALL_CLIMBING_POISON, true))
                .remove(player -> player.setData(FTZAttachmentTypes.WALL_CLIMBING_POISON, false))
                .defaultDisabling());
    }
}
