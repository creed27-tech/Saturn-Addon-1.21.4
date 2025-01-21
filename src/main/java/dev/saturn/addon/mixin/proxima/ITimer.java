package dev.saturn.addon.mixin.proxima;

import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(RenderTickCounter.class)
public interface ITimer {
    void setTickLength(float tickLength);
}