package com.example.examplemod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CommanderMusicInstance extends AbstractTickableSoundInstance {

    private static final float SILENT = 0.0001F;
    private static final float AUDIBLE = 1.0F;

    private final int commanderId;

    public CommanderMusicInstance(int commanderId) {
        super(ModSounds.MUSIC_DISC_INITIAL_PLAY.get(), SoundSource.MUSIC,
                SoundInstance.createUnseededRandom());
        this.commanderId = commanderId;
        this.looping = true;
        this.delay = 0;
        this.volume = SILENT;
        this.pitch = 1.0F;
        this.relative = true;
        this.attenuation = SoundInstance.Attenuation.NONE;
    }

    @Override
    public void tick() {
        Entity e = Minecraft.getInstance().level == null
                ? null
                : Minecraft.getInstance().level.getEntity(commanderId);

        if (e != null && e.isAlive() && !e.isRemoved()) {
            this.volume = AUDIBLE;
        } else {
            this.volume = SILENT;
        }
    }
}