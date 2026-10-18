package com.example.examplemod;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID)
public class CommanderDamageHandler {

    /**
     * 指挥官对玩家的伤害 × 0.6
     */
    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        // 攻击者必须是指挥官
        if (!(event.getSource().getEntity() instanceof ZomperiumCommander)) return;

        // 目标必须是玩家
        if (!(event.getEntity() instanceof Player)) return;

        // 伤害 × 0.6
        event.setAmount(event.getAmount() * 0.6F);
    }
}