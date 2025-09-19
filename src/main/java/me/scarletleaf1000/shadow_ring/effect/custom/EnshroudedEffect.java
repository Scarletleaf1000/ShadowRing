package me.scarletleaf1000.shadow_ring.effect.custom;

import me.scarletleaf1000.shadow_ring.effect.ModEffects;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.player.Player;

public class EnshroudedEffect extends MobEffect {
    private int timeEnshrouded = 0;

    public EnshroudedEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x2B2B2B);
    }

}
