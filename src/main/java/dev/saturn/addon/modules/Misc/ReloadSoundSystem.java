package dev.saturn.addon.modules.Misc;

import dev.saturn.addon.utils.bananaplus.ares.ReflectionHelper;
import meteordevelopment.meteorclient.systems.modules.Categories;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.SoundSystem;
import meteordevelopment.meteorclient.systems.modules.Module;

public class ReloadSoundSystem extends Module
{
    public ReloadSoundSystem() {
        super(Categories.Misc, "reload-sounds", "Reloads Minecraft's sound system. | Ported from Banana+");
    }

    public void onActivate() {
        final SoundSystem soundSystem = ReflectionHelper.getPrivateValue(SoundManager.class, this.mc.getSoundManager(), "soundSystem", "soundSystem");
        soundSystem.reloadSounds();
        this.toggle();
    }
}