package com.example.examplemod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = ExampleMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommanderMusicHandler {

    private static CommanderMusicInstance current = null;
    private static int currentId = Integer.MIN_VALUE;
    private static ClientLevel lastLevel = null;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        if (mc.player == null || level == null) {
            stopIfAny();
            lastLevel = null;
            return;
        }

        if (level != lastLevel) {
            stopIfAny();
            lastLevel = level;
        }

        if (current != null && !current.isStopped()) {
            int foundId = findCommanderId(level);
            if (foundId != Integer.MIN_VALUE && foundId != currentId) {
                stopIfAny();
                currentId = foundId;
                current = new CommanderMusicInstance(currentId);
                mc.getSoundManager().play(current);
            }
            return;
        }

        current = null;
        currentId = Integer.MIN_VALUE;

        int foundId = findCommanderId(level);
        if (foundId != Integer.MIN_VALUE) {
            currentId = foundId;
            current = new CommanderMusicInstance(currentId);
            mc.getSoundManager().play(current);
        }
    }

    private static int findCommanderId(ClientLevel level) {
        for (Entity e : level.entitiesForRendering()) {
            if (e instanceof ZomperiumCommander c && !c.isRemoved() && c.isAlive()) {
                return c.getId();
            }
        }
        return Integer.MIN_VALUE;
    }

    private static void stopIfAny() {
        if (current != null) {
            Minecraft.getInstance().getSoundManager().stop(current);
            current = null;
        }
        currentId = Integer.MIN_VALUE;
    }
}