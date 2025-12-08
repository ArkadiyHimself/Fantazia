package net.arkadiyhimself.fantazia.common.registries;

import com.mojang.serialization.MapCodec;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.client.particless.options.EntityChasingParticleOption;
import net.arkadiyhimself.fantazia.util.library.RandomList;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.function.Supplier;

public class FTZParticleTypes {

    public static final RandomList<ParticleType<EntityChasingParticleOption<?>>> DOOMED_SOULS = RandomList.emptyRandomList();

    private static final DeferredRegister<ParticleType<?>> REGISTER = DeferredRegister.create(Registries.PARTICLE_TYPE, Fantazia.MODID);
    
    private static <T extends ParticleType<?>> DeferredHolder<ParticleType<?>, T> registerAndList(String name, Supplier<T> particleTypeSupplier, RandomList<T> particleTypes) {
        T t = particleTypeSupplier.get();
        if (!particleTypes.contains(particleTypeSupplier.get())) particleTypes.add(t);
        return REGISTER.register(name, () -> t);
    }

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BARRIER_PIECE = REGISTER.register("barrier_piece", () -> new SimpleParticleType(true)); // finished and implemented
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BARRIER_PIECE_FURY = REGISTER.register("barrier_piece_fury", () -> new SimpleParticleType(true)); // finished and implemented
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BLOOD = REGISTER.register("blood", () -> new SimpleParticleType(true)); // finished and implemented
    public static final DeferredHolder<ParticleType<?>, ParticleType<BlockParticleOption>> CRUSH_ON = register("crush_on",false, BlockParticleOption::codec, BlockParticleOption::streamCodec);
    public static final DeferredHolder<ParticleType<?>, ParticleType<EntityChasingParticleOption<?>>> ELECTRO = REGISTER.register("electro", () -> particleTypeCodec(EntityChasingParticleOption::basicCodec, EntityChasingParticleOption::basicStreamCodec)); // finished and implemented
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> LIFESTEAL = REGISTER.register("lifesteal", () -> new SimpleParticleType(true)); // finished and implemented
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> FALLEN_SOUL = REGISTER.register("fallen_soul", () -> new SimpleParticleType(true)); // finished and implemented
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> METAL_SCRAP = REGISTER.register("metal_scrap", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> REGEN = REGISTER.register("regen", () -> new SimpleParticleType(true)); // finished and implemented
    public static final DeferredHolder<ParticleType<?>, ParticleType<EntityChasingParticleOption<?>>> DOOMED_SOUL1 = registerAndList("doomed_soul1", () -> FTZParticleTypes.particleTypeCodec(EntityChasingParticleOption::basicCodec, EntityChasingParticleOption::basicStreamCodec), DOOMED_SOULS); // finished and implemented
    public static final DeferredHolder<ParticleType<?>, ParticleType<EntityChasingParticleOption<?>>> DOOMED_SOUL2 = registerAndList("doomed_soul2", () -> FTZParticleTypes.particleTypeCodec(EntityChasingParticleOption::basicCodec, EntityChasingParticleOption::basicStreamCodec), DOOMED_SOULS); // finished and implemented
    public static final DeferredHolder<ParticleType<?>, ParticleType<EntityChasingParticleOption<?>>> DOOMED_SOUL3 = registerAndList("doomed_soul3", () -> FTZParticleTypes.particleTypeCodec(EntityChasingParticleOption::basicCodec, EntityChasingParticleOption::basicStreamCodec), DOOMED_SOULS); // finished and implemented
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> TIME_TRAVEL = REGISTER.register("time_travel", () -> new SimpleParticleType(true)); // finished and implemented
    public static final DeferredHolder<ParticleType<?>, ParticleType<EntityChasingParticleOption<?>>> WITHER = REGISTER.register("wither", () -> particleTypeCodec(EntityChasingParticleOption::basicCodec, EntityChasingParticleOption::basicStreamCodec)); // finished and implemented
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> CHAINED = REGISTER.register("chained", () -> new SimpleParticleType(true)); // finished and implemented

    private static <T extends ParticleOptions> ParticleType<T> particleTypeCodec(final Function<ParticleType<T>, MapCodec<T>> codecGetter, final Function<ParticleType<T>, StreamCodec<? super RegistryFriendlyByteBuf, T>> streamCodecGetter) {
        return new ParticleType<>(true) {
            public @NotNull MapCodec<T> codec() {
                return codecGetter.apply(this);
            }

            public @NotNull StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec() {
                return streamCodecGetter.apply(this);
            }
        };
    }

    private static <T extends ParticleOptions> DeferredHolder<ParticleType<?>,ParticleType<T>> register(String name, boolean overrideLimiter, final Function<ParticleType<T>, MapCodec<T>> codecGetter, final Function<ParticleType<T>, StreamCodec<? super RegistryFriendlyByteBuf, T>> streamCodecGetter) {
        return REGISTER.register(name, () -> new ParticleType<T>(overrideLimiter) {
            public @NotNull MapCodec<T> codec() {
                return codecGetter.apply(this);
            }

            public @NotNull StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec() {
                return streamCodecGetter.apply(this);
            }
        });
    }

    public static void register(IEventBus modEventBus) {
        REGISTER.register(modEventBus);
    }
}
