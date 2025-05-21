package net.arkadiyhimself.fantazia.entities.magic_projectile;

import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.LivingEffectHelper;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.PlayerAbilityHelper;
import net.arkadiyhimself.fantazia.api.attachment.level.LevelAttributesHelper;
import net.arkadiyhimself.fantazia.api.attachment.level.holders.DamageSourcesHolder;
import net.arkadiyhimself.fantazia.client.render.ParticleMovement;
import net.arkadiyhimself.fantazia.client.render.VisualHelper;
import net.arkadiyhimself.fantazia.registries.FTZEntityTypes;
import net.arkadiyhimself.fantazia.registries.FTZSoundEvents;
import net.arkadiyhimself.fantazia.registries.custom.Spells;
import net.arkadiyhimself.fantazia.util.library.RandomList;
import net.arkadiyhimself.fantazia.util.wheremagichappens.FantazicCombat;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class SimpleChasingProjectile extends ChasingProjectile {

    public static final Map<String, BiConsumer<SimpleChasingProjectile, LivingEntity>> ON_IMPACT_MAP;
    public static final Map<String, BiConsumer<SimpleChasingProjectile, Entity>> ON_DFELECT_MAP;

    public static final EntityDataAccessor<Float> ROT_X0 = SynchedEntityData.defineId(SimpleChasingProjectile.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> ROT_X1 = SynchedEntityData.defineId(SimpleChasingProjectile.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> ROT_Y0 = SynchedEntityData.defineId(SimpleChasingProjectile.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> ROT_Y1 = SynchedEntityData.defineId(SimpleChasingProjectile.class, EntityDataSerializers.FLOAT);

    public static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(SimpleChasingProjectile.class, EntityDataSerializers.INT);

    private final RandomList<SimpleParticleType> particleTypes = RandomList.emptyRandomList();
    private String mapString;

    public SimpleChasingProjectile(EntityType<? extends SimpleChasingProjectile> entityType, Level level) {
        super(entityType, level);
        this.mapString = "";
    }

    public SimpleChasingProjectile(Level level, @Nullable Entity cachedOwner, String mapString, int lifeSpan, float velocity, int color) {
        super(FTZEntityTypes.SIMPLE_CHASING_PROJECTILE.get(), level, cachedOwner, lifeSpan, velocity);
        this.entityData.set(COLOR, color);
        this.mapString = mapString;
    }

    public SimpleChasingProjectile(Level level, @Nullable Entity cachedOwner, String mapString, int lifeSpan, float velocity) {
        this(level, cachedOwner, mapString, lifeSpan, velocity, 0);
    }

    public SimpleChasingProjectile(Level level, @Nullable Entity cachedOwner, String mapString, int lifeSpan) {
        this(level, cachedOwner, mapString, lifeSpan,1);
    }

    public SimpleChasingProjectile(Level level, @Nullable Entity cachedOwner, String mapString) {
        this(level, cachedOwner, mapString,-1);
    }

    public int getColor() {
        return entityData.get(COLOR);
    }

    public void addParticle(SimpleParticleType particleType) {
        this.particleTypes.add(particleType);
    }

    private void rotate() {
        float rotX = entityData.get(ROT_X1);
        entityData.set(ROT_X0, rotX);
        entityData.set(ROT_X1, rotX - 2.5f);

        float rotY = entityData.get(ROT_Y1);
        entityData.set(ROT_Y0, rotY);
        entityData.set(ROT_Y1, rotY - 2.5f);
    }

    @Override
    public void deflect(Entity attacker) {
        setOwner(attacker);
        setTarget(null);
        BiConsumer<SimpleChasingProjectile, Entity> impact = ON_DFELECT_MAP.get(mapString);
        if (impact != null) impact.accept(this, attacker);
        super.deflect(attacker);
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putString("mapString", mapString);
        compoundTag.putInt("Color", this.entityData.get(COLOR));

        ListTag listTag = new ListTag();
        for (SimpleParticleType particleType : particleTypes) {
            ResourceLocation location = BuiltInRegistries.PARTICLE_TYPE.getKey(particleType);
            if (location != null) listTag.add(StringTag.valueOf(location.toString()));
        }

        compoundTag.put("Particles", listTag);
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        mapString = compoundTag.getString("mapString");
        entityData.set(COLOR, compoundTag.getInt("Color"));

        ListTag listTag = compoundTag.getList("Particles", Tag.TAG_STRING);
        for (int i = 0; i < listTag.size(); i++) {
            ResourceLocation location = ResourceLocation.parse(listTag.getString(i));
            ParticleType<?> type = BuiltInRegistries.PARTICLE_TYPE.get(location);
            if (type instanceof SimpleParticleType simpleParticleType) particleTypes.add(simpleParticleType);
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(COLOR, 0);
        builder.define(ROT_X0, 0f);
        builder.define(ROT_X1, 0f);
        builder.define(ROT_Y0, 0f);
        builder.define(ROT_Y1, 0f);
    }

    @Override
    public void tick() {
        super.tick();
        rotate();
        if (level().isClientSide()) VisualHelper.particleOnEntityClient(this, particleTypes.random(), ParticleMovement.REGULAR);
    }

    @Override
    protected void onHitEntity(Entity entity) {
        BiConsumer<SimpleChasingProjectile, LivingEntity> impact = ON_IMPACT_MAP.get(mapString);
        if (impact != null && entity instanceof LivingEntity livingEntity) impact.accept(this, livingEntity);
        super.onHitEntity(entity);
    }

    static {
        ON_IMPACT_MAP = new HashMap<>(){{
            put("knock_out", (projectile,target) -> {
                if (FantazicCombat.isInvulnerable(target) || target.isDeadOrDying()) return;
                VisualHelper.particleOnEntityServer(projectile, ParticleTypes.EXPLOSION, ParticleMovement.REGULAR, 3);
                if (!LevelAttributesHelper.hurtEntity(target, projectile, 7f, DamageSourcesHolder::simpleChasingProjectile)) return;

                target.playSound(FTZSoundEvents.KNOCK_OUT_IMPACT.value());
                if (target.isDeadOrDying() && projectile.getOwner() instanceof Player player) PlayerAbilityHelper.reduceRecharge(player, Spells.KNOCK_OUT,100);
                if (target.getType() != EntityType.ENDERMAN) LivingEffectHelper.makeStunned(target,80);
            });

            put("", (projectile, target) -> {

            });
        }};

        ON_DFELECT_MAP = new HashMap<>(){{
            put("knock_out", ((projectile, deflecter) -> {
                 VisualHelper.particleOnEntityServer(projectile, ParticleTypes.EXPLOSION, ParticleMovement.REGULAR,3);

                AABB aabb = projectile.getBoundingBox().inflate(0.85);
                for (LivingEntity livingEntity : projectile.level().getEntitiesOfClass(LivingEntity.class, aabb)) {
                    if (livingEntity == deflecter) continue;
                    LivingEffectHelper.microStun(livingEntity);
                    LevelAttributesHelper.hurtEntity(livingEntity, projectile,2f, DamageSourcesHolder::simpleChasingProjectile);
                }
            }));
        }};
    }
}
