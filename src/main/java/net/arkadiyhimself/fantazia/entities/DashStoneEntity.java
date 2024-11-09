package net.arkadiyhimself.fantazia.entities;

import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.LivingEffectHelper;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.PlayerAbilityGetter;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders.DashHolder;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders.TalentsHolder;
import net.arkadiyhimself.fantazia.client.render.VisualHelper;
import net.arkadiyhimself.fantazia.items.casters.DashStoneItem;
import net.arkadiyhimself.fantazia.registries.FTZItems;
import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.arkadiyhimself.fantazia.registries.FTZSoundEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
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
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder pBuilder) {
        pBuilder.define(DASHSTONE, ItemStack.EMPTY);
        pBuilder.define(VISUAL_ROT0, 0f);
        pBuilder.define(VISUAL_ROT1, 0f);
        pBuilder.define(OWNER, Optional.empty());
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        if (pCompound.contains("owner")) this.ownerUUID = pCompound.getUUID("owner");
        if (pCompound.contains("dashstone")) this.entityData.set(DASHSTONE, ItemStack.parse(this.registryAccess(), pCompound.getCompound("dashstone")).orElse(ItemStack.EMPTY));
        if (pCompound.contains("level")) this.level = pCompound.getInt("level");
        entityData.set(OWNER, Optional.ofNullable(ownerUUID));

    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        if (ownerUUID != null) pCompound.putUUID("owner", ownerUUID);

        ItemStack stack = entityData.get(DASHSTONE);
        if (stack.getItem() instanceof DashStoneItem) pCompound.put("dashstone", stack.save(this.registryAccess()));

        pCompound.putInt("level", level);
    }

    @Override
    public void playerTouch(@NotNull Player pPlayer) {
        TalentsHolder holder = PlayerAbilityGetter.takeHolder(pPlayer, TalentsHolder.class);
        if (this.level().isClientSide() || !pPlayer.getUUID().equals(ownerUUID) || holder == null) return;

        ICuriosItemHandler curiosItemHandler = CuriosApi.getCuriosInventory(pPlayer).orElse(null);
        if (curiosItemHandler == null) return;

        Optional<SlotResult> slotResult = curiosItemHandler.findCurio("dashstone", 0);
        if (slotResult.isEmpty()) return;
        Item item = slotResult.get().stack().getItem();
        if (!(item instanceof DashStoneItem dashStoneItem)) return;

        if (this.level < dashStoneItem.level) return;

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
            Minecraft.getInstance().getSoundManager().play(new SimpleSoundInstance(FTZSoundEvents.DASHSTONE_WIND.get(), SoundSource.AMBIENT, 1f, 1f, RandomSource.create(), this.blockPosition()));
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
        Minecraft.getInstance().getSoundManager().stop(FTZSoundEvents.DASHSTONE_WIND.getId(), SoundSource.AMBIENT);
        cooldown = 10;
    }

    private void tryAdaptToPlayer(ServerLevel serverLevel) {
        List<? extends Player> players = serverLevel.getEntitiesOfClass(Player.class, AABB.ofSize(this.position(), 16,16,16));
        for (Player player : players) {
            DashHolder dashHolder = PlayerAbilityGetter.takeHolder(player, DashHolder.class);
            if (dashHolder != null && dashHolder.getLevel() == 1 && dashHolder.getDashstoneEntity(level()) == null) {
                serverLevel.sendParticles(ParticleTypes.EXPLOSION, this.getX(), this.getY(), this.getZ(), 2, 0.1,0,0.1,0);
                this.playSound(SoundEvents.WITHER_SPAWN);
                dashHolder.setDashstoneEntity(this);
                this.ownerUUID = player.getUUID();
                this.level = 2;
                this.soundRech = 0;
                entityData.set(DASHSTONE, new ItemStack(FTZItems.DASHSTONE2.get()));
                entityData.set(OWNER, Optional.of(ownerUUID));


                summonProtector(-2.5, -2.5);
                summonProtector(-2.5, +2.5);
                summonProtector(+2.5, +2.5);
                summonProtector(+2.5, -2.5);

                LightningBolt lightningBolt = EntityType.LIGHTNING_BOLT.create(serverLevel);
                if (lightningBolt != null) {
                    lightningBolt.setPos(this.getX(), this.getY() - 3, this.getZ());
                    serverLevel.addFreshEntity(lightningBolt);
                }
                return;
            }
        }
    }

    private void summonProtector(double xOff, double zOff) {
        WitherSkeleton protector = EntityType.WITHER_SKELETON.create(this.level());
        if (protector == null) return;
        protector.setPos(this.getX() + xOff, this.getY() + (double) -1, this.getZ() + zOff);
        LivingEffectHelper.giveBarrier(protector, 30);
        protector.addEffect(new MobEffectInstance(FTZMobEffects.BARRIER, -1, 35, true, true));
        this.level().addFreshEntity(protector);
    }
}
