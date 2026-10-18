package com.example.examplemod;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {

    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, ExampleMod.MODID);

    public static final RegistryObject<SoundEvent> APOCALYPSE_LAUNCH = register("apocalypse_launch");
    public static final RegistryObject<SoundEvent> APOCALYPSE_HIT = register("apocalypse_hit");
    public static final RegistryObject<SoundEvent> LIBRARIAN_SQUISH = register("librarian_squish");

    // ===== 8 张唱片 =====
    public static final RegistryObject<SoundEvent> MUSIC_DISC_UNIVERSAL_COLLAPSE = register("music_disc_universal_collapse");
    public static final RegistryObject<SoundEvent> MUSIC_DISC_THE_FINALS          = register("music_disc_the_finals");
    public static final RegistryObject<SoundEvent> MUSIC_DISC_ARKNIGHTS_OST3      = register("music_disc_arknights_ost3");
    public static final RegistryObject<SoundEvent> MUSIC_DISC_X_COOL              = register("music_disc_x_cool");
    public static final RegistryObject<SoundEvent> MUSIC_DISC_SOMNIOMANCER        = register("music_disc_somniomancer");
    public static final RegistryObject<SoundEvent> MUSIC_DISC_BE_MY_STAR          = register("music_disc_be_my_star");
    public static final RegistryObject<SoundEvent> MUSIC_DISC_BATTLEPLAN_ARCLIGHT = register("music_disc_battleplan_arclight");
    public static final RegistryObject<SoundEvent> MUSIC_DISC_INITIAL_PLAY = register("music_disc_initial_play");

    private static RegistryObject<SoundEvent> register(String name) {
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(
                new ResourceLocation(ExampleMod.MODID, name)));
    }
}