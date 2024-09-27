package net.arkadiyhimself.fantazia.entities;

import net.arkadiyhimself.fantazia.api.capability.entity.ability.AbilityGetter;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.abilities.Dash;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.abilities.TalentsHolder;
import net.arkadiyhimself.fantazia.client.render.VisualHelper;
import net.arkadiyhimself.fantazia.items.casters.DashStone;
import net.arkadiyhimself.fantazia.registries.FTZItems;
import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.arkadiyhimself.fantazia.registries.FTZSoundEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EnderEyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.AABB;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class DashStoneEntity extends Entity {
    private static final EntityDataAccessor<ItemStack> DASHSTONE = SynchedEntityData.defineId(DashStoneEntity.class, EntityDataSerializers.ITEM_STACK);
    public static final EntityDataAccessor<Float> VISUAL_ROT0 = SynchedEntityData.defineId(DashStoneEntity.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> VISUAL_ROT1 = SynchedEntityData.defineId(DashStoneEntity.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Optional<UUID>> OWNER = SynchedEntityData.defineId(DashStoneEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private @Nullable UUID ownerUUID;
    private int level;
    private int soundRech = 0;
    private int cooldown = 0;
    public DashStoneEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.ownerUUID = null;
        this.level = 0;
    }
    @Override
    protected void defineSynchedData() {
        entityData.define(DASHSTONE, ItemStack.EMPTY);
        entityData.define(VISUAL_ROT0, 0f);
        entityData.define(VISUAL_ROT1, 0f);
        entityData.define(OWNER, Optional.empty());
    }
    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        if (pCompound.contains("owner")) this.ownerUUID = pCompound.getUUID("owner");
        if (pCompound.contains("dashstone")) this.entityData.set(DASHSTONE, ItemStack.of(pCompound.getCompound("dashstone")));
        if (pCompound.contains("level")) this.level = pCompound.getInt("level");
        entityData.set(OWNER, Optional.ofNullable(ownerUUID));

    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        if (ownerUUID != null) pCompound.putUUID("owner", ownerUUID);
        pCompound.put("dashstone", entityData.get(DASHSTONE).save(new CompoundTag()));
        pCompound.putInt("level", level);
    }

    @Override
    public void playerTouch(@NotNull Player pPlayer) {
        TalentsHolder holder = AbilityGetter.takeAbilityHolder(pPlayer, TalentsHolder.class);
        if (this.level().isClientSide() || !pPlayer.getUUID().equals(ownerUUID) || holder == null) return;

        ICuriosItemHandler curiosItemHandler = CuriosApi.getCuriosInventory(pPlayer).orElse(null);

        Optional<SlotResult> slotResult = curiosItemHandler.findCurio("dashstone", 0);
        if (slotResult.isEmpty()) return;
        Item item = slotResult.get().stack().getItem();
        if (!(item instanceof DashStone dashStone)) return;

        if (this.level < dashStone.level) return;

        curiosItemHandler.setEquippedCurio("dashstone", 0, entityData.get(DASHSTONE));
        VisualHelper.circleOfParticles(ParticleTypes.PORTAL, this.position());
        this.reset();
    }

    @Override
    public void tick() {
        super.tick();
        rotate();
        if (cooldown > 0) cooldown--;
        if (this.level().isClientSide()) visualTick();
        if (this.ownerUUID == null && this.level() instanceof ServerLevel serverLevel && cooldown <= 0) tryAdaptToPlayer(serverLevel);
    }

    private void visualTick() {
        if (entityData.get(OWNER).isEmpty()) return;
        if (soundRech <= 0) {
            soundRech = 270;
            Minecraft.getInstance().levelRenderer.playStreamingMusic(FTZSoundEvents.WIND.get(), this.blockPosition(), null);
        }
        soundRech--;
    }
    public ItemStack getDashstone() {
        return entityData.get(DASHSTONE);
    }
    public void rotate() {
        float rot1 = this.entityData.get(VISUAL_ROT1);
        this.entityData.set(VISUAL_ROT0, rot1);
        this.entityData.set(VISUAL_ROT1, rot1 - 3f);
    }
    public void reset() {
        this.ownerUUID = null;
        this.level = 0;
        this.soundRech = 0;
        entityData.set(DASHSTONE, ItemStack.EMPTY);
        entityData.set(OWNER, Optional.empty());
        Minecraft.getInstance().levelRenderer.playStreamingMusic(null, this.blockPosition(), null);
        cooldown = 10;
    }
    private void tryAdaptToPlayer(ServerLevel serverLevel) {
        List<? extends Player> players = serverLevel.getEntitiesOfClass(Player.class, AABB.ofSize(this.position(), 16,16,16));
        for (Player player : players) {
            Dash dash = AbilityGetter.takeAbilityHolder(player, Dash.class);
            if (dash != null && dash.getLevel() == 1 && dash.getDashstoneEntity() == null) {
                serverLevel.sendParticles(ParticleTypes.EXPLOSION, this.getX(), this.getY(), this.getZ(), 2, 0.1,0,0.1,0);
                this.playSound(SoundEvents.WITHER_SPAWN);
                dash.setDashstoneEntity(this);
                this.ownerUUID = player.getUUID();
                this.level = 2;
                this.soundRech = 0;
                entityData.set(DASHSTONE, new ItemStack(FTZItems.DASHSTONE2.get()));
                entityData.set(OWNER, Optional.of(ownerUUID));

                LightningBolt lightningBolt = EntityType.LIGHTNING_BOLT.create(serverLevel);
                if (lightningBolt != null) {
                    lightningBolt.setPos(this.getX(), this.getY() - 3, this.getZ());
                    serverLevel.addFreshEntity(lightningBolt);
                }
                summonProtector(-2.5, -1, -2.5);
                summonProtector(-2.5, -1, +2.5);
                summonProtector(+2.5, -1, +2.5);
                summonProtector(+2.5, -1, -2.5);
                return;
            }
        }
    }
    private void summonProtector(double xOff, double yOff, double zOff) {
        WitherSkeleton protector = EntityType.WITHER_SKELETON.create(this.level());
        if (protector == null) return;
        protector.setPos(this.getX() + xOff, this.getY() + yOff, this.getZ() + zOff);
        protector.addEffect(new MobEffectInstance(FTZMobEffects.BARRIER.get(), -1, 25, true, true));
        this.level().addFreshEntity(protector);
    }
}
