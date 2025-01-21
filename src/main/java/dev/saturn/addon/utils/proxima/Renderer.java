package dev.saturn.addon.utils.proxima;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;

import java.awt.*;

public class Renderer {
    public static void gradientLineScreen(Color start, Color end, double x, double y, double x1, double y1) {
        float g = start.getRed() / 255f;
        float h = start.getGreen() / 255f;
        float k = start.getBlue() / 255f;
        float f = start.getAlpha() / 255f;
        float g1 = end.getRed() / 255f;
        float h1 = end.getGreen() / 255f;
        float k1 = end.getBlue() / 255f;
        float f1 = end.getAlpha() / 255f;
        Matrix4f m = new MatrixStack().peek().getPositionMatrix();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableBlend();
        RenderSystem.disableBlend();
    }

    public static void fill(Color c, double x1, double y1, double x2, double y2) {
        fill(new MatrixStack(), c, x1, y1, x2, y2);
    }

    public static void fill(MatrixStack matrices, Color c, double x1, double y1, double x2, double y2) {
        int color = c.getRGB();
        double j;
        if (x1 < x2) {
            j = x1;
            x1 = x2;
            x2 = j;
        }

        if (y1 < y2) {
            j = y1;
            y1 = y2;
            y2 = j;
        }
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        float f = (float) (color >> 24 & 255) / 255.0F;
        float g = (float) (color >> 16 & 255) / 255.0F;
        float h = (float) (color >> 8 & 255) / 255.0F;
        float k = (float) (color & 255) / 255.0F;
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableBlend();
        RenderSystem.disableBlend();
    }

    public static Color modify(Color original, int redOverwrite, int greenOverwrite, int blueOverwrite, int alphaOverwrite) {
        return new Color(redOverwrite == -1 ? original.getRed() : redOverwrite, greenOverwrite == -1 ? original.getGreen() : greenOverwrite, blueOverwrite == -1 ? original.getBlue() : blueOverwrite, alphaOverwrite == -1 ? original.getAlpha() : alphaOverwrite);
    }
}