package dev.saturn.addon.utils.aurora;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.orbit.listeners.ConsumerListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.render.VertexFormats;
import org.lwjgl.opengl.GL30;

public class GradientGlowProgram extends GlProgram {

    private GlUniform uSize;
    private GlUniform uLocation;
    private GlUniform radius;
    private GlUniform softness;
    private GlUniform color1;
    private GlUniform color2;
    private GlUniform color3;
    private GlUniform color4;

    private Framebuffer input;

    public GradientGlowProgram() {
        super("gradientglow", VertexFormats.POSITION);
        };
    }