package dev.saturn.addon.utils.bbc.security;

import dev.saturn.addon.utils.bbc.world.DamageHelper;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

//a
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.ServerList;
import static meteordevelopment.meteorclient.MeteorClient.mc;
//a
public class Initialization {
    private static final List<Module> moduleList = new ArrayList<>();

    // executor.stop() breaks everything
    public static Thread executor = Thread.currentThread();

    public static void init(){
        modules();
        utils();
        prefix();
    }

    private static void prefix(){
        ChatUtils.registerCustomPrefix("dev.saturn.addon", Initialization::getPrefix);
    }

    private static void utils(){
        DamageHelper.init();
    }

    private static void modules(){
        Set<Class<? extends Module>> reflections = new Reflections("dev.saturn.addon.modules").getSubTypesOf(Module.class);

        reflections.forEach(aClass -> {
            try {
                moduleList.add(aClass.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });

        moduleList.forEach(module -> Modules.get().add(module));
    }

    public static Text getPrefix() {
        MutableText logo = Text.literal("Saturn");
        MutableText prefix = Text.literal("");
        logo.setStyle(logo.getStyle().withFormatting(Formatting.DARK_GRAY));
        prefix.setStyle(prefix.getStyle().withFormatting(Formatting.DARK_GRAY));
        prefix.append("[");
        prefix.append(logo);
        prefix.append("] ");
        return prefix;
            }
        }