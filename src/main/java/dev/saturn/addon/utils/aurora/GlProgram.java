package dev.saturn.addon.utils.aurora;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class GlProgram {
    protected final MinecraftClient mc = MinecraftClient.getInstance();
    private static final List<Pair<Function<ResourceFactory, ShaderProgram>, Consumer<ShaderProgram>>> REGISTERED_PROGRAMS = new ArrayList<>();

    public ShaderProgram backingProgram;

    public GlProgram(String id, VertexFormat vertexFormat) {
        REGISTERED_PROGRAMS.add(new Pair<>(
                resourceFactory -> {
                    try {
                        throw new RuntimeException("Failed to initialized shader program");
                    } catch (RuntimeException e) {
                        throw new RuntimeException(e);
                    }
                },
                program -> {
                    backingProgram = program;
                    setup();
                }
        ));
    }

    protected GlUniform findUniform(String name) {
        return null;
    }

    protected void setup() {
        }

    protected void use() {

    }
}