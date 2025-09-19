package me.scarletleaf1000.shadow_ring.listener;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import me.scarletleaf1000.shadow_ring.ShadowRing;
import me.scarletleaf1000.shadow_ring.effect.ModEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

@Mod.EventBusSubscriber(modid = ShadowRing.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EnshroudedEffectHandler {

    @SubscribeEvent
    public static void onAttack(AttackEntityEvent event) {
        if (event.getEntity().hasEffect(ModEffects.ENSHROUDED_EFFECT.get())) {
            if (event.isCancelable()){
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onRightClick(PlayerInteractEvent.RightClickItem event) {
        if (!event.getEntity().hasEffect(ModEffects.ENSHROUDED_EFFECT.get())) return;

        ItemStack stack = event.getItemStack();
        Item item = stack.getItem();

        // Allow eating/drinking items
        if (item.getUseAnimation(stack).equals(UseAnim.EAT) ||
                item.getUseAnimation(stack).equals(UseAnim.DRINK)) {
            return; // do NOT cancel
        }

        // Block all other item uses
        if (event.isCancelable()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getEntity().hasEffect(ModEffects.ENSHROUDED_EFFECT.get()) && event.isCancelable()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        if (event.getEntity().hasEffect(ModEffects.ENSHROUDED_EFFECT.get()) && event.isCancelable()) {
            event.setCanceled(true);
        }
    }


    @SubscribeEvent
    public static void onRightClickEntity(PlayerInteractEvent.EntityInteract event) {
        if (event.getEntity().hasEffect(ModEffects.ENSHROUDED_EFFECT.get()) && event.isCancelable()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onTarget(LivingChangeTargetEvent event) {
        LivingEntity target = event.getNewTarget();
        if (target != null && target.hasEffect(ModEffects.ENSHROUDED_EFFECT.get())) {
            if (!(event.getEntity().hasEffect(ModEffects.ENSHROUDED_EFFECT.get()))) {
                if (event.isCancelable()){
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onRenderLiving(RenderLivingEvent event) {
        if (!(event.getEntity() instanceof Player target)) return;

        // If the target does NOT have Enshrouded, render normally
        if (!target.hasEffect(ModEffects.ENSHROUDED_EFFECT.get())) return;

        // Get the viewer (the player doing the rendering)
        Player viewer = Minecraft.getInstance().player;
        if (viewer == null) return;

        // If viewer is also Enshrouded, allow rendering
        if (viewer.hasEffect(ModEffects.ENSHROUDED_EFFECT.get())) {
            return; // don’t cancel → enshrouded can see each other
        }

        // Otherwise, hide the enshrouded player
        if (event.isCancelable()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onPickup(EntityItemPickupEvent event) {
        if (event.getEntity().hasEffect(ModEffects.ENSHROUDED_EFFECT.get())) {
            if (event.isCancelable()) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onCollision(LivingAttackEvent event) {
        LivingEntity target = event.getEntity();
        if (target.hasEffect(ModEffects.ENSHROUDED_EFFECT.get())) {
            var source = event.getSource();
            var type = source.type();

            boolean allowed =
                    type.equals(target.level().damageSources().wither().type()) ||
                            type.equals(target.level().damageSources().magic().type()) ||
                            type.equals(target.level().damageSources().starve().type());

            if (!allowed && event.isCancelable()) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        if (event.side.isClient() && event.phase == TickEvent.Phase.END) {
            player.getActiveEffects().removeIf(inst -> inst.getDuration() <= 0);
        }
        if (player.hasEffect(ModEffects.ENSHROUDED_EFFECT.get())) {
            player.setSilent(true);  // no sounds

            int ticksInShadow = player.getPersistentData().getInt("ticksInShadow");
            ticksInShadow++;
            player.getPersistentData().putInt("ticksInShadow", ticksInShadow);


            applyEnshroudedDebuffs(player, ticksInShadow);
        } else {
            player.setSilent(false);
            int ticksInShadow = player.getPersistentData().getInt("ticksInShadow");
            if (ticksInShadow > 0){
                if (player.tickCount % 5 == 0){
                    ticksInShadow -= 2;
                    player.getPersistentData().putInt("ticksInShadow", ticksInShadow);
                }
            }else{
                player.getPersistentData().putInt("ticksInShadow", 0);
            }
        }
    }

    public static void applyEnshroudedDebuffs(Player p, int ticks) {
        if (p.tickCount % 100 == 0){
            Random rand = new Random();
            if (ticks > 1000 && ticks <= 3000){
                switch (rand.nextInt(18)) {
                    case 0:
                        p.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 100, 0, true, false, true));
                        break;
                    case 1:
                        p.addEffect(new MobEffectInstance(MobEffects.WITHER, 100, 0, true, false, true));
                        break;
                    case 2:
                        p.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 100, 0, true, false, true));
                        break;
                    case 3:
                        p.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 100, 0, true, false, true));
                        break;
                    case 4:
                        p.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 0, true, false, true));
                        break;
                    case 5:
                        p.addEffect(new MobEffectInstance(MobEffects.HUNGER, 100, 0, true, false, true));
                        break;
                    case 6:
                        p.hurt(p.damageSources().magic(), 1.0F);
                        break;
                }
            }else if (ticks > 3000 && ticks <= 5000){
                switch (rand.nextInt(10)) {
                    case 0:
                        p.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 120, 0, true, false, true));
                        break;
                    case 1:
                        p.addEffect(new MobEffectInstance(MobEffects.WITHER, 120, 0, true, false, true));
                        break;
                    case 2:
                        p.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 120, 0, true, false, true));
                        break;
                    case 3:
                        p.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 120, 0, true, false, true));
                        break;
                    case 4:
                        p.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 120, 0, true, false, true));
                        break;
                    case 5:
                        p.addEffect(new MobEffectInstance(MobEffects.HUNGER, 120, 0, true, false, true));
                        break;
                    case 6:
                        p.hurt(p.damageSources().magic(), 2.0F);
                        break;
                }
            }else if (ticks > 5000 && ticks <= 10000){
                int i = 2;
                while (i > 0) {
                    switch (rand.nextInt(12)) {
                        case 0:
                            p.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 120, 0, true, false, true));
                            break;
                        case 1:
                            p.addEffect(new MobEffectInstance(MobEffects.WITHER, 120, 0, true, false, true));
                            break;
                        case 2:
                            p.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 120, 0, true, false, true));
                            break;
                        case 3:
                            p.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 120, 0, true, false, true));
                            break;
                        case 4:
                            p.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 120, 0, true, false, true));
                            break;
                        case 5:
                            p.addEffect(new MobEffectInstance(MobEffects.HUNGER, 120, 0, true, false, true));
                            break;
                        case 6:
                            p.hurt(p.damageSources().magic(), 4.0F);
                            break;
                    }
                    i--;
                }
            }else if(ticks > 10000 && ticks <= 25000){
                int i = 3;
                while (i > 0) {
                    switch (rand.nextInt(10)) {
                        case 0:
                            p.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 150, 1, true, false, true));
                            break;
                        case 1:
                            p.addEffect(new MobEffectInstance(MobEffects.WITHER, 150, 1, true, false, true));
                            break;
                        case 2:
                            p.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 150, 1, true, false, true));
                            break;
                        case 3:
                            p.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 150, 1, true, false, true));
                            break;
                        case 4:
                            p.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 150, 1, true, false, true));
                            break;
                        case 5:
                            p.addEffect(new MobEffectInstance(MobEffects.HUNGER, 150, 1, true, false, true));
                            break;
                        case 6:
                            p.hurt(p.damageSources().magic(), 8.0F);
                            break;
                    }
                    i--;
                }
            }
        }
        if(ticks > 25000){
            p.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 200, 2, true, false, true));
            p.addEffect(new MobEffectInstance(MobEffects.WITHER, 200, 2, true, false, true));
            p.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 2, true, false, true));
            p.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 200, 2, true, false, true));
            p.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 2, true, false, true));
            p.addEffect(new MobEffectInstance(MobEffects.HUNGER, 200, 2, true, false, true));
            p.hurt(p.damageSources().magic(), 1.0F);
        }
    }


    @SubscribeEvent
    public static void onEffectStart(MobEffectEvent.Added event){
        if (event.getEntity() instanceof Player player) {
            if (event.getEffectInstance().getEffect().equals(ModEffects.ENSHROUDED_EFFECT.get())){
                if (!player.level().isClientSide) {
                    double radius = 32.0D;

                    List<Mob> mobs = player.level().getEntitiesOfClass(Mob.class, player.getBoundingBox().inflate(radius));

                    for (Mob mob : mobs) {
                        if (mob.getTarget() != null && mob.getTarget().equals(player)) {
                            mob.setTarget(null);

                            mob.getNavigation().stop();
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiOverlayEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.player.hasEffect(ModEffects.ENSHROUDED_EFFECT.get())) {
            //if (event.getOverlay() == VanillaGuiOverlay.ALL.element()) {
            renderOverlay(mc);
            //}
        }
    }

    private static void renderOverlay(Minecraft mc) {
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();

        //colors
        int alpha = 4;
        int red = 85;
        int green = 25;
        int blue = 245;

        // Draw fullscreen quad with blue tint
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        buffer.vertex(0, screenHeight, -90).color(red, green, blue, alpha).endVertex();   // bottom-left
        buffer.vertex(screenWidth, screenHeight, -90).color(red, green, blue, alpha).endVertex(); // bottom-right
        buffer.vertex(screenWidth, 0, -90).color(red, green, blue, alpha).endVertex();    // top-right
        buffer.vertex(0, 0, -90).color(red, green, blue, alpha).endVertex();              // top-left
        tesselator.end();

        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
    }

}
