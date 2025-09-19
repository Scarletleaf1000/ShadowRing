package me.scarletleaf1000.shadow_ring.effect;

import me.scarletleaf1000.shadow_ring.ShadowRing;
import me.scarletleaf1000.shadow_ring.effect.custom.EnshroudedEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEffects {
    // Create the deferred register for mob effects
    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, ShadowRing.MOD_ID);

    // Register the enshrouded effect: supplies a new EnshroudedEffect instance
    public static final RegistryObject<MobEffect> ENSHROUDED_EFFECT =
            MOB_EFFECTS.register("enshrouded", () -> new EnshroudedEffect());

    // Helper to register this DeferredRegister on the mod event bus
    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}