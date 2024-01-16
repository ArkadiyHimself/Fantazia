package net.arkadiyhimself.combatimprovement.Registries.Items.Weapons.Melee;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.arkadiyhimself.combatimprovement.Networking.NetworkHandler;
import net.arkadiyhimself.combatimprovement.Networking.packets.PlayAnimationS2C;
import net.arkadiyhimself.combatimprovement.Registries.MobEffects.MobEffectRegistry;
import net.arkadiyhimself.combatimprovement.util.Capability.Abilities.DataSincyng.AttachDataSync;
import net.arkadiyhimself.combatimprovement.util.Capability.Abilities.DataSincyng.DataSync;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.warden.AngerLevel;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.ForgeMod;

import java.util.List;
import java.util.UUID;

public class Murasama extends MeleeWeaponItem {
    public Murasama() {
        super(new Properties().stacksTo(1).defaultDurability(512).fireResistant().rarity(Rarity.EPIC), 10, -2.3f, "murasama");
        this.attackDamage = 10;
        this.attackSpeedModifier = -2.3f;
    }
    public boolean mineBlock(ItemStack pStack, Level pLevel, BlockState pState, BlockPos pPos, LivingEntity pEntityLiving) {
        if (pState.getDestroySpeed(pLevel, pPos) != 0.0F) {
            pStack.hurtAndBreak(2, pEntityLiving, (p_43276_) -> {
                p_43276_.broadcastBreakEvent(EquipmentSlot.MAINHAND);
            });
        }
        return true;
    }
    public boolean isCorrectToolForDrops(BlockState pBlock) {
        return pBlock.is(Blocks.COBWEB);
    }

    @Override
    public boolean canPerformAction(ItemStack stack, net.minecraftforge.common.ToolAction toolAction) {
        return net.minecraftforge.common.ToolActions.DEFAULT_SWORD_ACTIONS.contains(toolAction);
    }
    @Override
    public boolean hasActive() { return true; }
    @Override
    public void activeAbility(ServerPlayer player) {
        NetworkHandler.sendToPlayer(new PlayAnimationS2C("taunt"), player);
        ServerLevel level = player.getLevel();
        AABB aabb = player.getBoundingBox().inflate(10);
        List<Mob> mobs = level.getEntitiesOfClass(Mob.class, aabb);
        mobs.removeIf(mob -> !mob.hasLineOfSight(player));
        for (Mob mob : mobs) {
            mob.addEffect(new MobEffectInstance(MobEffectRegistry.FURY.get(),300, 0, false, false));
            mob.setTarget(player);
            if (mob instanceof NeutralMob neutralMob) {
                neutralMob.setTarget(player);
            }
            if (mob instanceof Warden warden) {
                warden.increaseAngerAt(player, AngerLevel.ANGRY.getMinimumAnger() + 20, false);
                warden.setAttackTarget(player);
            }
        }
        AttachDataSync.get(player).ifPresent(dataSync -> {
            dataSync.tauntTicks = 30;
            dataSync.updateTracking();
        });
    }

    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot pEquipmentSlot) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> modifiers = ImmutableMultimap.builder();
        modifiers.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", this.attackDamage, AttributeModifier.Operation.ADDITION));
        modifiers.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", this.attackSpeedModifier, AttributeModifier.Operation.ADDITION));
        modifiers.put(ForgeMod.ATTACK_RANGE.get(), new AttributeModifier(UUID.fromString("d020cd5d-c050-49e4-a0ea-ef27adf7e6d0"), "Weapon modifier", 0.5, AttributeModifier.Operation.ADDITION));
        return pEquipmentSlot == EquipmentSlot.MAINHAND ? modifiers.build() : super.getDefaultAttributeModifiers(pEquipmentSlot);
    }
}
