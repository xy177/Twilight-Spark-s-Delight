package xy177.twilightsparksdelight.registry;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Objects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import xy177.twilightsparksdelight.TwilightSparksDelight;

public final class TSDComponents {
    public static final Key<String> NAGA_INGREDIENT = new Key<>("naga_ingredient", Codec.STRING);
    public static final Key<Boolean> COOKED_ADVANCEMENT = new Key<>("cooked_advancement", Codec.BOOL);
    public static final Key<Integer> EXPERIMENT_LEVEL = new Key<>("experiment_level", Codec.INT);
    public static final Key<Double> EXPERIMENT_ACTIVITY = new Key<>("experiment_activity", Codec.DOUBLE);
    public static final Key<String> EXPERIMENT_BOUND_MEAT = new Key<>("experiment_bound_meat", Codec.STRING);
    public static final Key<Integer> EXPERIMENT_REVIVES = new Key<>("experiment_revives", Codec.INT);
    public static final Key<Integer> COMPANION_USES = new Key<>("companion_uses", Codec.INT);
    public static final Key<Integer> HERMIT_CRAB_BITES = new Key<>("hermit_crab_bites", Codec.INT);
    public static final Key<String> COMPANION_CHEF = new Key<>("companion_chef", Codec.STRING);
    public static final Key<List<String>> COMPANION_DINERS = new Key<>("companion_diners", Codec.STRING.listOf());

    private TSDComponents() {
    }

    /** Typed, namespaced item NBT. These are not vanilla data components in 1.20.1. */
    public record Key<T>(String name, Codec<T> codec) {
        public T get(ItemStack stack) {
            CompoundTag data = stack.getTagElement(TwilightSparksDelight.MOD_ID);
            Tag value = data == null ? null : data.get(name);
            return value == null ? null : codec.parse(NbtOps.INSTANCE, value).result().orElse(null);
        }

        public T getOrDefault(ItemStack stack, T fallback) {
            T value = get(stack);
            return value == null ? fallback : value;
        }

        public boolean has(ItemStack stack) {
            return get(stack) != null;
        }

        public void set(ItemStack stack, T value) {
            if (stack.isEmpty()) return;
            Tag encoded = codec.encodeStart(NbtOps.INSTANCE, Objects.requireNonNull(value))
                    .result().orElseThrow(() -> new IllegalArgumentException("Invalid item data: " + name));
            stack.getOrCreateTagElement(TwilightSparksDelight.MOD_ID).put(name, encoded);
        }

        public void remove(ItemStack stack) {
            CompoundTag data = stack.getTagElement(TwilightSparksDelight.MOD_ID);
            if (data == null) return;
            data.remove(name);
            if (data.isEmpty()) stack.removeTagKey(TwilightSparksDelight.MOD_ID);
        }
    }
}
