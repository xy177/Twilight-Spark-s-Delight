package xy177.twilightsparksdelight.registry;

import com.mojang.serialization.Codec;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import xy177.twilightsparksdelight.TwilightSparksDelight;
import xy177.twilightsparksdelight.common.loot.BrackenLootModifier;

public final class TSDLootModifiers {
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS,
                    TwilightSparksDelight.MOD_ID);
    public static final RegistryObject<Codec<BrackenLootModifier>> BRACKEN =
            SERIALIZERS.register("bracken_harvest", () -> BrackenLootModifier.CODEC);

    private TSDLootModifiers() {}
}
