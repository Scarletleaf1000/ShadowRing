package me.scarletleaf1000.shadow_ring.item.custom;

import me.scarletleaf1000.shadow_ring.effect.ModEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class ShadowRingItem extends Item implements ICurioItem {

    public ShadowRingItem(Properties pProperties) {
        super(new Item.Properties()
                .stacksTo(1)
                .defaultDurability(0)
                .rarity(Rarity.RARE));
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        slotContext.entity().addEffect(new MobEffectInstance(ModEffects.ENSHROUDED_EFFECT.get(), 5, 0, true, false, true));
        slotContext.entity().addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 5, 0, true, false, true));
        slotContext.entity().addEffect(new MobEffectInstance(MobEffects.JUMP, 5, 3, true, false, true));
        //slotContext.entity().addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 5, 0, true, false, true));
        //slotContext.entity().addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 30, 0, true, false, true));


        ICurioItem.super.curioTick(slotContext, stack);
    }
}
