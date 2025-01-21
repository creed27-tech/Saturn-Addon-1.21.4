package dev.saturn.addon.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

public class SeedCommand extends Command {
    private final static SimpleCommandExceptionType NO_SEED = new SimpleCommandExceptionType(Text.literal("No seed for current world saved."));

    public SeedCommand() {
        super("seed", "Get or set seed for the current world.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {

    }
}