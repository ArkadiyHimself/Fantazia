package net.arkadiyhimself.fantazia.registries;

import com.mojang.serialization.Codec;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.attachment.basis_attachments.ReflectLayerRenderHolder;
import net.arkadiyhimself.fantazia.api.attachment.basis_attachments.RewindParametersHolder;
import net.arkadiyhimself.fantazia.api.attachment.basis_attachments.TickingIntegerHolder;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_data.LivingDataManager;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.LivingEffectManager;
import net.arkadiyhimself.fantazia.api.attachment.entity.niche_data_holders.AddedAurasHolder;
import net.arkadiyhimself.fantazia.api.attachment.entity.niche_data_holders.ArmorStandCommandAuraHolder;
import net.arkadiyhimself.fantazia.api.attachment.entity.niche_data_holders.ArrowEnchantmentsHolder;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.PlayerAbilityManager;
import net.arkadiyhimself.fantazia.api.attachment.level.LevelAttributes;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class FTZAttachmentTypes {
    private FTZAttachmentTypes() {}
    private static final DeferredRegister<AttachmentType<?>> REGISTER = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Fantazia.MODID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<PlayerAbilityManager>> ABILITY_MANAGER = REGISTER.register("player.ability_manager", () -> AttachmentType.serializable(PlayerAbilityManager::new).copyOnDeath().build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<LivingDataManager>> DATA_MANAGER = REGISTER.register("living.data_manager", () -> AttachmentType.serializable(LivingDataManager::new).build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<LivingEffectManager>> EFFECT_MANAGER = REGISTER.register("living.effect_manager", () -> AttachmentType.serializable(LivingEffectManager::new).build());

    // niche features
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<ArrowEnchantmentsHolder>> ARROW_ENCHANTMENTS = REGISTER.register("niche.arrow_enchantments", () -> AttachmentType.builder(ArrowEnchantmentsHolder::new).serialize(new ArrowEnchantmentsHolder.Serializer()).build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<ArmorStandCommandAuraHolder>> ARMOR_STAND_COMMAND_AURA = REGISTER.register("niche.armor_stand_command_aura", () -> AttachmentType.builder(ArmorStandCommandAuraHolder::new).serialize(new ArmorStandCommandAuraHolder.Serializer()).build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<AddedAurasHolder>> ADDED_AURAS = REGISTER.register("niche.added_auras", () -> AttachmentType.serializable(AddedAurasHolder::new).build());

    // simple features
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<TickingIntegerHolder>> ANCIENT_FLAME_TICKS = REGISTER.register("generic.ancient_flame", () -> AttachmentType.serializable(iAttachmentHolder -> new TickingIntegerHolder(iAttachmentHolder, Fantazia.res("generic.ancient_flame"), false)).build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Integer>> ALL_IN_PREVIOUS_OUTCOME = REGISTER.register("spell.all_in.previous_outcome", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT).build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Vec3>> WANDERERS_SPIRIT_LOCATION = REGISTER.register("spell.wanderers_spirit.location", () -> AttachmentType.builder(() -> Vec3.ZERO).serialize(Vec3.CODEC).build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<TickingIntegerHolder>> MURASAMA_TAUNT_TICKS = REGISTER.register("weapon.murasama.taunt_ticks", () -> AttachmentType.serializable(iAttachmentHolder -> new TickingIntegerHolder(iAttachmentHolder, Fantazia.res("weapon.murasama.taunt_ticks"),false)).build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<ReflectLayerRenderHolder>> REFLECT_RENDER_VALUES = REGISTER.register("spell.reflect.render_values", () -> AttachmentType.builder(ReflectLayerRenderHolder::new).build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Float>> ENTANGLE_PREVIOUS_HEALTH = REGISTER.register("spell.entangle.previous_health", () -> AttachmentType.builder(() -> 20F).serialize(Codec.FLOAT).build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<TickingIntegerHolder>> TRANQUILIZE_DAMAGE_TICKS = REGISTER.register("aura.tranquilize.damage_ticks", () -> AttachmentType.serializable(iAttachmentHolder -> new TickingIntegerHolder(iAttachmentHolder, Fantazia.res("aura.tranquilize.damage_ticks"), false)).build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<RewindParametersHolder>> REWIND_PARAMETERS = REGISTER.register("spell.rewind.parameters", () -> AttachmentType.serializable(RewindParametersHolder::new).build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Float>> HAEMORRHAGE_TO_HEAL = REGISTER.register("effect.haemorrhage.to_heal", () -> AttachmentType.builder(() -> 0f).serialize(Codec.FLOAT).build());

    // level
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<LevelAttributes>> LEVEL_ATTRIBUTES = REGISTER.register("level.attributes", () -> AttachmentType.serializable(LevelAttributes::new).build());

    public static void register(IEventBus iEventBus) {
        REGISTER.register(iEventBus);
    }
}
