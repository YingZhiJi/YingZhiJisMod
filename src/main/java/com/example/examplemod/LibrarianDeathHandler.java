package com.example.examplemod;

import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID)
public class LibrarianDeathHandler {

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof LibrarianEntity librarian)) return;
        if (librarian.level().isClientSide()) return;

        event.setCanceled(true);
        librarian.respawnAtHome();
    }
}