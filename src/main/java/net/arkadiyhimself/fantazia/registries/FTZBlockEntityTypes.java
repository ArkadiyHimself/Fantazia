package net.arkadiyhimself.fantazia.registries;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.entities.AmplificationBenchBlockEntity;
import net.arkadiyhimself.fantazia.entities.ObscureHangingSignBlockEntity;
import net.arkadiyhimself.fantazia.entities.ObscureSignBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class FTZBlockEntityTypes {
    public static final DeferredRegister<BlockEntityType<?>> REGISTER = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Fantazia.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ObscureSignBlockEntity>> OBSCURE_SIGN;
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ObscureHangingSignBlockEntity>> OBSCURE_HANGING_SIGN;
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AmplificationBenchBlockEntity>> AMPLIFICATION_BENCH;

    public static void register(IEventBus eventBus) {
        REGISTER.register(eventBus);
    }

    static {
        OBSCURE_SIGN = REGISTER.register("obscure_sign",
                () -> BlockEntityType.Builder.of(ObscureSignBlockEntity::new, FTZBlocks.OBSCURE_SIGN.get(), FTZBlocks.OBSCURE_WALL_SIGN.get()).build(null));

        OBSCURE_HANGING_SIGN = REGISTER.register("obscure_hanging_sign",
                () -> BlockEntityType.Builder.of(ObscureHangingSignBlockEntity::new, FTZBlocks.OBSCURE_HANGING_SIGN.get(), FTZBlocks.OBSCURE_WALL_HANGING_SIGN.get()).build(null));

        AMPLIFICATION_BENCH = REGISTER.register("amplification_bench",
                () -> BlockEntityType.Builder.of(AmplificationBenchBlockEntity::new, FTZBlocks.AMPLIFICATION_BENCH.get()).build(null));
    }
}
