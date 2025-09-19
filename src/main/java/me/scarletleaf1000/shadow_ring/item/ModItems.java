package me.scarletleaf1000.shadow_ring.item;

import me.scarletleaf1000.shadow_ring.ShadowRing;
import me.scarletleaf1000.shadow_ring.item.custom.ShadowRingItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, ShadowRing.MOD_ID);

    public static final RegistryObject<Item> SHADOW_RING = ITEMS.register("shadow_ring",
            () -> new ShadowRingItem(new Item.Properties()));

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
