package net.arkadiyhimself.fantazia.util.wheremagichappens;

import it.unimi.dsi.fastutil.ints.IntList;
import net.arkadiyhimself.fantazia.common.enchantment.effects.RandomChanceOccurrence;
import net.arkadiyhimself.fantazia.common.item.casters.AuraCasterItem;
import net.arkadiyhimself.fantazia.common.item.casters.SpellCasterItem;
import net.arkadiyhimself.fantazia.common.registries.FTZDataMapTypes;
import net.arkadiyhimself.fantazia.common.registries.enchantment_effect_component.FTZEnchantmentEffectComponentTypes;
import net.arkadiyhimself.fantazia.util.simpleobjects.EntityChasingSoundInstance;
import net.arkadiyhimself.fantazia.util.simpleobjects.RegistryObjectList;
import net.minecraft.Util;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.TargetedConditionalEffect;
import net.minecraft.world.level.storage.loot.LootContext;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.SlotPredicate;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;
import top.theillusivec4.curios.common.util.EquipCurioTrigger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class FantazicUtil {

    public static void playSoundUI(Player player, SoundEvent soundEvent, float pitch, float volume) {
        if (player == Minecraft.getInstance().player) playSoundUI(soundEvent, pitch, volume);
    }

    public static void playSoundUI(SoundEvent soundEvent, float pitch, float volume) {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(soundEvent, pitch, volume));
    }

    public static void playSoundUI(SoundEvent soundEvent) {
        playSoundUI(soundEvent,1f,1f);
    }

    public static ItemStack getFirework(DyeColor color, int flightTime) {
        ItemStack itemstack = new ItemStack(Items.FIREWORK_ROCKET);
        itemstack.set(
                DataComponents.FIREWORKS,
                new Fireworks(
                        (byte)flightTime,
                        List.of(new FireworkExplosion(FireworkExplosion.Shape.BURST, IntList.of(color.getFireworkColor()), IntList.of(), false, false))
                )
        );
        return itemstack;
    }

    public static void summonRandomFirework(LivingEntity owner) {
        DyeColor dyecolor = Util.getRandom(DyeColor.values(), owner.getRandom());
        int i = owner.getRandom().nextInt(3);
        ItemStack itemstack = getFirework(dyecolor, i);

        double x = owner.getX() + RandomUtil.nextDouble(-0.5, 0.5);
        double z = owner.getZ() + RandomUtil.nextDouble(-0.5, 0.5);
        FireworkRocketEntity fireworkrocketentity = new FireworkRocketEntity(owner.level(), owner, x, owner.getEyeY(), z, itemstack);
        owner.level().addFreshEntity(fireworkrocketentity);
    }

    public static List<ItemStack> getAllCuriosOfItem(LivingEntity entity, Item item) {
        List<ItemStack> stacks = Lists.newArrayList();
        ICuriosItemHandler handler = CuriosApi.getCuriosInventory(entity).orElse(null);
        if (handler == null) return stacks;
        Map<String, ICurioStacksHandler> curios = handler.getCurios();
        for (ICurioStacksHandler stacksHandler : curios.values()) {
            IDynamicStackHandler dynamicStackHandler = stacksHandler.getStacks();
            for (int i = 0; i < dynamicStackHandler.getSlots(); i++) {
                ItemStack stack = dynamicStackHandler.getStackInSlot(i);
                if (stack.is(item)) stacks.add(stack);
            }
        }
        return stacks;
    }

    public static List<SlotResult> findAllCurios(LivingEntity entity, String ident) {
        List<SlotResult> result = new ArrayList<>();
        ICuriosItemHandler handler = CuriosApi.getCuriosInventory(entity).orElse(null);
        if (handler == null) return result;
        Map<String, ICurioStacksHandler> curios = handler.getCurios();
        for (String id : curios.keySet()) {
            if (id.contains(ident)) {
                ICurioStacksHandler stacksHandler = curios.get(id);
                IDynamicStackHandler stackHandler = stacksHandler.getStacks();

                for (int i = 0; i < stackHandler.getSlots(); i++) {
                    ItemStack stack = stackHandler.getStackInSlot(i);

                    NonNullList<Boolean> renderStates = stacksHandler.getRenders();
                    result.add(new SlotResult(new SlotContext(id, handler.getWearer(), i, false,
                            renderStates.size() > i && renderStates.get(i)), stack));
                }
            }
        }
        return result;
    }

    public static Optional<SlotResult> findCurio(LivingEntity entity, String ident, int id) {
        ICuriosItemHandler curiosItemHandler = CuriosApi.getCuriosInventory(entity).orElse(null);
        if (curiosItemHandler == null) return Optional.empty();
        return curiosItemHandler.findCurio(ident, id);
    }

    public static ItemPredicate itemTagPredicate(TagKey<Item> tagKey) {
        return ItemPredicate.Builder.item().of(tagKey).build();
    }

    public static <T> Criterion<EquipCurioTrigger.TriggerInstance> equipCurioTrigger(DataComponentType<T> component, T expected, String slot) {
        ItemPredicate itemPredicate = ItemPredicate.Builder.item().hasComponents(DataComponentPredicate.builder().expect(component, expected).build()).build();
        SlotPredicate slotPredicate = SlotPredicate.Builder.slot().of(slot).build();

        return EquipCurioTrigger.INSTANCE.createCriterion(new EquipCurioTrigger.TriggerInstance(Optional.empty(), Optional.of(itemPredicate), Optional.empty(), Optional.of(slotPredicate)));
    }

    public static boolean isActiveCaster(Item item) {
        return item instanceof SpellCasterItem spellCaster && spellCaster.getSpell().value().isActive();
    }

    public static boolean isPassiveCaster(Item item) {
        return item instanceof SpellCasterItem spellCaster && !spellCaster.getSpell().value().isActive() || item instanceof AuraCasterItem;
    }

    public static boolean holdsDataComponent(LivingEntity entity, DataComponentType<?> type) {
        return entity.getMainHandItem().has(type) || entity.getOffhandItem().has(type);
    }

    public static PotionContents getPotionContents(Arrow arrow) {
        ItemStack stack = arrow.getPickupItemStackOrigin();
        return stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
    }

    public static boolean hasEffect(Arrow arrow, Holder<MobEffect> effect) {
        PotionContents contents = getPotionContents(arrow);
        for (MobEffectInstance instance : contents.getAllEffects())
            if (instance.is(effect)) return true;
        return false;
    }

    public static boolean dropSkull(ServerLevel serverLevel, ItemStack weapon, LivingEntity target, DamageSource source) {
        MutableBoolean mutableBoolean = new MutableBoolean(false);
        EnchantmentHelper.runIterationOnItem(weapon, (holder, level) -> {
            LootContext lootContext = Enchantment.damageContext(serverLevel, level, target, source);
            Enchantment enchantment = holder.value();
            for (TargetedConditionalEffect<RandomChanceOccurrence> conditionalEffect : enchantment.getEffects(FTZEnchantmentEffectComponentTypes.DECAPITATION.value())) {
                if (conditionalEffect.matches(lootContext) && conditionalEffect.effect().attempt(level, target.getRandom())) {
                    mutableBoolean.setTrue();
                    return;
                }
            }
        });

        return mutableBoolean.booleanValue();
    }

    public static @Nullable Item getSkull(Entity entity) {
        Optional<ResourceKey<EntityType<?>>> key = BuiltInRegistries.ENTITY_TYPE.getResourceKey(entity.getType());
        return key.map(entityTypeResourceKey -> BuiltInRegistries.ENTITY_TYPE.getData(FTZDataMapTypes.SKULLS, entityTypeResourceKey)).orElse(null);
    }

    public static void entityChasingSound(Entity entity, SoundEvent soundEvent, SoundSource soundSource, float volume, float pitch) {
        Minecraft.getInstance().getSoundManager().play(
                new EntityChasingSoundInstance(
                        soundEvent, soundSource, volume, pitch, entity, RandomUtil.nextLong()
                )
        );
    }

    public static void entityChasingSound(Entity entity, SoundEvent soundEvent, SoundSource soundSource) {
        entityChasingSound(entity, soundEvent, soundSource, 1f, 1f);
    }

    public static boolean isAffected(LivingEntity livingEntity, Holder<MobEffect> mobEffect) {
        RegistryObjectList<EntityType<?>> whiteList = mobEffect.getData(FTZDataMapTypes.MOB_EFFECT_WHITE_LIST);
        if (whiteList != null && !whiteList.isEmpty() && !whiteList.contains(livingEntity.getType(), (EntityType::is))) return false;
        RegistryObjectList<EntityType<?>> blackList = mobEffect.getData(FTZDataMapTypes.MOB_EFFECT_BLACK_LIST);
        if (blackList != null && blackList.contains(livingEntity.getType(), EntityType::is)) return false;
        return true;
    }

    public static int howManyItems(Player player, Item item, int required) {
        if (player == null) return 0;
        Inventory inventory = player.getInventory();
        int m = 0;
        for (int i = 0; i <= inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack.is(item)) m += stack.getCount();
        }
        return Mth.floor((float) m / required);
    }
}
