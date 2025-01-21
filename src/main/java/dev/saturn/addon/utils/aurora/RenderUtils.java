package dev.saturn.addon.utils.aurora;

import com.mojang.blaze3d.systems.RenderSystem;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class RenderUtils {

    public static final Matrix4f lastProjMat = new Matrix4f();
    public static final Matrix4f lastModMat = new Matrix4f();
    public static final Matrix4f lastWorldSpaceMatrix = new Matrix4f();
    public static TextureColorProgram TEXTURE_COLOR_PROGRAM;
    public static GradientGlowProgram GRADIENT_GLOW_PROGRAM;
    private static float prevCircleStep;
    private static float circleStep;

    public static void initShaders() {
        if (GRADIENT_GLOW_PROGRAM == null)
            GRADIENT_GLOW_PROGRAM = new GradientGlowProgram();
        if (TEXTURE_COLOR_PROGRAM == null)
            TEXTURE_COLOR_PROGRAM = new TextureColorProgram();
    }



    public static Color injectAlpha(Color color, int alpha) {
        return new Color(color.r, color.g, color.b, MathHelper.clamp(alpha, 0, 255));
    }

    public static void drawTexture(DrawContext context, Identifier icon, int x, int y, int width, int height) {
        RenderSystem.blendEquation(32774);
        RenderSystem.blendFunc(770, 1);
        RenderSystem.enableBlend();
        RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
    }

    public static void rounded(MatrixStack stack, float x, float y, float w, float h, float radius, int p, int color) {

        Matrix4f matrix4f = stack.peek().getPositionMatrix();

        RenderSystem.enableBlend();

        RenderSystem.disableBlend();
    }

    public static void corner(float x, float y, float radius, int angle, float p, float r, float g, float b, float a, BufferBuilder bufferBuilder, Matrix4f matrix4f) {
        for (float i = angle; i > angle - 90; i -= 90 / p) {
        }
    }

    public static void text(String text, MatrixStack stack, float x, float y, int color) {
    }

    public static void quad(MatrixStack stack, float x, float y, float w, float h, int color) {
        Matrix4f matrix4f = stack.peek().getPositionMatrix();

        RenderSystem.enableBlend();

        RenderSystem.disableBlend();
    }

    public static Vec3d interpolatePos(float prevposX, float prevposY, float prevposZ, float posX, float posY, float posZ) {
        return null;
    }


    public static Vec3d worldSpaceToScreenSpace(Vec3d pos) {
        int viewport[] = new int[4];
        GL11.glGetIntegerv(GL11.GL_VIEWPORT, viewport);
        Vector3f target = new Vector3f();
        Matrix4f matrixProj = new Matrix4f(lastProjMat);
        Matrix4f matrixModel = new Matrix4f(lastModMat);
        return pos;
    }

    public static void updateJello() {
        prevCircleStep = circleStep;
        circleStep += 0.15f;
    }


    public static void drawJello(MatrixStack matrix, Entity target, Color color) {

        matrix.push();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        Tessellator tessellator = Tessellator.getInstance();

        float cos;
        float sin;
        for (int i = 0; i <= 30; i++) {
        }

        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        matrix.pop();
    }

    public static void drawRect(MatrixStack matrices, float x, float y, float width, float height, int color) {
        float f = (color >> 24 & 0xFF) / 255.0F;
        float g = (color >> 16 & 0xFF) / 255.0F;
        float h = (color  >> 8 & 0xFF) / 255.0F;
        float k = (color & 0xFF) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
    }

    private static double absSinAnimation(double input) {
        return Math.abs(1 + Math.sin(input)) / 2;
    }

}