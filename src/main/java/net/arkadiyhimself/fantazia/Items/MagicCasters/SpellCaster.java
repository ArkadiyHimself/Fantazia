package net.arkadiyhimself.fantazia.Items.MagicCasters;

import net.arkadiyhimself.fantazia.AdvancedMechanics.Abilities.*;
import net.arkadiyhimself.fantazia.AdvancedMechanics.Auras.BasicAura;
import net.arkadiyhimself.fantazia.api.ItemRegistry;
import net.arkadiyhimself.fantazia.api.SoundRegistry;
import net.arkadiyhimself.fantazia.util.Capability.Entity.AbilityManager.Abilities.ManaData;
import net.arkadiyhimself.fantazia.util.Capability.Entity.AbilityManager.AbilityGetter;
import net.arkadiyhimself.fantazia.util.Capability.Entity.AbilityManager.AbilityManager;
import net.arkadiyhimself.fantazia.util.Interfaces.IChangingIcon;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SpellCaster extends Item implements IChangingIcon {
    @Nullable
    private Spell spell = null;
    @Nullable
    private BasicAura<? extends LivingEntity, ? extends Entity> basicAura = null;
    public SpellCaster() {
        super(new Properties().stacksTo(1).fireResistant().rarity(Rarity.RARE));
    }
    public boolean tryCast(@NotNull ServerPlayer serverPlayer) {
        if (serverPlayer.getCooldowns().isOnCooldown(this) || this.spell == null) return false;
        boolean flag = false;
        AbilityManager abilityManager = AbilityGetter.getUnwrap(serverPlayer);
        if (abilityManager != null) {
            ManaData manaData = abilityManager.takeAbility(ManaData.class);
            if (manaData != null && !manaData.wasteMana(spell.getManacost())) return false;
        }
        if (this.spell instanceof SelfSpell selfSpell) {
            flag = SpellHelper.trySelfSpell(serverPlayer, selfSpell);
        }
        if (this.spell instanceof TargetedSpell<?> targetedSpell) {
            flag = SpellHelper.tryTargetedSpell(serverPlayer, targetedSpell);
        }
        if (flag) {
            serverPlayer.getCooldowns().addCooldown(this, spell.getRecharge());
            return true;
        } else return false;
    }
    @Nullable
    public Spell getSpell() {
        return spell;
    }

    public SpellCaster setSpell(Spell spell) {
        this.spell = spell;
        return this;
    }
    public SpellCaster setAura(BasicAura<? extends LivingEntity, ? extends Entity> basicAura) {
        this.basicAura = basicAura;
        return this;
    }
    public @Nullable BasicAura<? extends LivingEntity, ? extends Entity> getBasicAura() {
        return basicAura;
    }
    public List<Component> buildTooltip() {
        List<Component> components = Lists.newArrayList();
        if (spell != null) components.addAll(spell.buildTooltip(null));
        if (basicAura != null) components.addAll(basicAura.itemTooltip());
        return components;
    }
    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if (this == ItemRegistry.LEADERS_HORN.get() && !pPlayer.getCooldowns().isOnCooldown(this)) {
            pPlayer.startUsingItem(pUsedHand);
            pPlayer.getCooldowns().addCooldown(this, 400);
            pPlayer.awardStat(Stats.ITEM_USED.get(this));
            pPlayer.level().playSound(null, pPlayer.blockPosition(), SoundRegistry.LEADERS_HORN.get(), SoundSource.NEUTRAL);

            AABB aabb = pPlayer.getBoundingBox().inflate(128);
            if (pLevel instanceof ServerLevel serverLevel) {
                List<TamableAnimal> tamableAnimals = serverLevel.getEntitiesOfClass(TamableAnimal.class, aabb);
                tamableAnimals.removeIf(tamableAnimal -> tamableAnimal.getOwner() != pPlayer || !tamableAnimal.isInSittingPose());
                tamableAnimals.forEach(tamableAnimal -> tamableAnimal.interact(pPlayer, InteractionHand.MAIN_HAND));
            }
            return InteractionResultHolder.success(pPlayer.getItemInHand(pUsedHand));
        } else {
            return super.use(pLevel, pPlayer,pUsedHand);
        }
    }

    @Override
    public void registerVariants() {
        ItemProperties.register(ItemRegistry.LEADERS_HORN.get(), new ResourceLocation("tooting"),
                ((pStack, pLevel, pEntity, pSeed) -> pEntity != null && pEntity.isUsingItem() && pEntity.getUseItem() == pStack ? 1f : 0f));
    }
}
