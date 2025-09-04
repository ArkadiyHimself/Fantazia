package net.arkadiyhimself.fantazia.common.registries;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.common.advanced.aura.Aura;
import net.arkadiyhimself.fantazia.common.advanced.aura.AuraInstance;
import net.arkadiyhimself.fantazia.common.api.attachment.basis_attachments.LocationHolder;
import net.arkadiyhimself.fantazia.common.api.attachment.basis_attachments.ReflectLayerRenderHolder;
import net.arkadiyhimself.fantazia.common.api.attachment.basis_attachments.RewindParametersHolder;
import net.arkadiyhimself.fantazia.common.api.attachment.basis_attachments.TickingIntegerHolder;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.living_data.LivingDataManager;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.living_effect.LivingEffectManager;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.niche_data_holders.AddedAurasHolder;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.niche_data_holders.ArmorStandCommandAuraHolder;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.PlayerAbilityManager;
import net.arkadiyhimself.fantazia.common.api.attachment.level.LevelAttributesManager;
import net.arkadiyhimself.fantazia.common.api.prompt.Prompt;
import net.arkadiyhimself.fantazia.common.api.prompt.Prompts;
import net.arkadiyhimself.fantazia.data.FTZCodecs;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.ArrayList;
import java.util.HashMap;

public class FTZAttachmentTypes {

    private static final DeferredRegister<AttachmentType<?>> REGISTER = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Fantazia.MODID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<PlayerAbilityManager>> ABILITY_MANAGER = register("player.ability_manager", AttachmentType.serializable(PlayerAbilityManager::new).copyOnDeath());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<LivingDataManager>> DATA_MANAGER = register("living.data_manager", AttachmentType.serializable(LivingDataManager::new));
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<LivingEffectManager>> EFFECT_MANAGER = register("living.effect_manager", AttachmentType.serializable(LivingEffectManager::new));

    // niche features
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<ArmorStandCommandAuraHolder>> ARMOR_STAND_COMMAND_AURA = register("niche.armor_stand_command_aura", AttachmentType.builder(ArmorStandCommandAuraHolder::new).serialize(new ArmorStandCommandAuraHolder.Serializer()));
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<AddedAurasHolder>> ADDED_AURAS = register("niche.added_auras", AttachmentType.serializable(AddedAurasHolder::new));
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<ArrayList<Prompt>>> USED_PROMPTS = register("niche.used_prompts", AttachmentType.<ArrayList<Prompt>>builder(() -> Lists.newArrayList()).serialize(FTZCodecs.arrayListCodec(Prompts.CODEC)).copyOnDeath());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Boolean>> GUARDIAN_NO_THORNS = register("niche.guardian_no_thorns", AttachmentType.builder(() -> false));

    // simple features
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<TickingIntegerHolder>> ANCIENT_FLAME_TICKS = register("generic.ancient_flame_ticks", AttachmentType.serializable(iAttachmentHolder -> new TickingIntegerHolder(iAttachmentHolder, Fantazia.location("generic.ancient_flame_ticks"), true)));
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Integer>> ALL_IN_PREVIOUS_OUTCOME = register("spell.all_in.previous_outcome", AttachmentType.builder(() -> 0).serialize(Codec.INT));
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<LocationHolder>> WANDERERS_SPIRIT_LOCATION = register("spell.wanderers_spirit.location", AttachmentType.builder(LocationHolder::new).serialize(LocationHolder.CODEC).copyOnDeath());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<TickingIntegerHolder>> MURASAMA_TAUNT_TICKS = register("weapon.murasama.taunt_ticks", AttachmentType.serializable(iAttachmentHolder -> new TickingIntegerHolder(iAttachmentHolder, Fantazia.location("weapon.murasama.taunt_ticks"),false)));
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<ReflectLayerRenderHolder>> REFLECT_RENDER_VALUES = register("spell.reflect.render_values", AttachmentType.builder(ReflectLayerRenderHolder::new));
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Float>> ENTANGLE_PREVIOUS_HEALTH = register("spell.entangle.previous_health", AttachmentType.builder(iAttachmentHolder -> iAttachmentHolder instanceof LivingEntity entity ? entity.getMaxHealth() : 20F).serialize(Codec.FLOAT));
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<TickingIntegerHolder>> TRANQUILIZE_DAMAGE_TICKS = register("aura.tranquilize.damage_ticks", AttachmentType.serializable(iAttachmentHolder -> new TickingIntegerHolder(iAttachmentHolder, Fantazia.location("aura.tranquilize.damage_ticks"), false)));
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<RewindParametersHolder>> REWIND_PARAMETERS = register("spell.rewind.parameters", AttachmentType.serializable(RewindParametersHolder::new));
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Float>> HAEMORRHAGE_TO_HEAL = register("effect.haemorrhage.to_heal", AttachmentType.builder(() -> 0f).serialize(Codec.FLOAT));
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Float>> BARRIER_HEALTH = register("effect.barrier.health", AttachmentType.builder(() -> 0f).serialize(Codec.FLOAT));
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Float>> BARRIER_COLOR = register("effect.barrier.color", AttachmentType.builder(() -> 0f).serialize(Codec.FLOAT));
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Integer>> LAYERED_BARRIER_LAYERS = register("effect.layered_barrier.layers", AttachmentType.builder(() -> 0).serialize(Codec.INT));
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Float>> LAYERED_BARRIER_COLOR = register("effect.layered_barrier.color", AttachmentType.builder(() -> 0f).serialize(Codec.FLOAT));
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Boolean>> DASHSTONE_MINION = register("generic.dashstone_minion", AttachmentType.builder(() -> false).serialize(Codec.BOOL));
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Boolean>> OBTAINED_DASHSTONE = register("generic.obtained_dashstone", AttachmentType.builder(() -> false).serialize(Codec.BOOL).copyOnDeath());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Integer>> MANA_RECYCLE_LEVEL = register("talent.mana_recycle.level", AttachmentType.builder(() -> 0).serialize(Codec.INT).copyOnDeath());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Boolean>> WALL_CLIMBING_UNLOCKED = register("talent.wall_climbing.unlocked", AttachmentType.builder(() -> false).serialize(Codec.BOOL).copyOnDeath());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Boolean>> WALL_CLIMBING_COBWEB = register("talent.wall_climbing.cobweb", AttachmentType.builder(() -> false).serialize(Codec.BOOL).copyOnDeath());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Boolean>> WALL_CLIMBING_POISON = register("talent.wall_climbing.poison", AttachmentType.builder(() -> false).serialize(Codec.BOOL).copyOnDeath());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Boolean>> HAS_FURY = register("arrow.fury_effect", AttachmentType.builder(() -> false).serialize(Codec.BOOL));

    // a map of auras that entity is inside, prioritising those that affect the entity
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<HashMap<Holder<Aura>, AuraInstance>>> AFFECTING_AURAS = register("generic.affecting_auras", AttachmentType.builder(() -> Maps.newHashMap()));

    // level
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<LevelAttributesManager>> LEVEL_ATTRIBUTES = register("level.attributes", AttachmentType.serializable(LevelAttributesManager::new));

    private static <T> DeferredHolder<AttachmentType<?>, AttachmentType<T>> register(String name, AttachmentType.Builder<T> builder) {
        return REGISTER.register(name, builder::build);
    }

    public static void register(IEventBus iEventBus) {
        REGISTER.register(iEventBus);
    }
}
