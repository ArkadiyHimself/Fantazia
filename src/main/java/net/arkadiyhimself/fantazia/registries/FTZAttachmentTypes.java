package net.arkadiyhimself.fantazia.registries;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_data.LivingDataManager;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.LivingEffectManager;
import net.arkadiyhimself.fantazia.api.attachment.entity.niche_data_holders.AddedAurasHolder;
import net.arkadiyhimself.fantazia.api.attachment.entity.niche_data_holders.ArmorStandCommandAuraHolder;
import net.arkadiyhimself.fantazia.api.attachment.entity.niche_data_holders.ArrowEnchantmentsHolder;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.PlayerAbilityManager;
import net.arkadiyhimself.fantazia.api.attachment.level.LevelAttributes;
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

    // level
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<LevelAttributes>> LEVEL_ATTRIBUTES = REGISTER.register("level.attributes", () -> AttachmentType.serializable(LevelAttributes::new).build());

    public static void register(IEventBus iEventBus) {
        REGISTER.register(iEventBus);
    }
}
