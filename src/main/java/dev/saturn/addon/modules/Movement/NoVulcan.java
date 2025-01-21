package dev.saturn.addon.modules.Movement;

import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Categories;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class NoVulcan extends Module {
    private final SettingGroup generalSettings;
    private final Setting<Mode> modeSetting;

    public NoVulcan() {
        super(meteordevelopment.meteorclient.systems.modules.Categories.Movement, "no-vulcan", "Prevents fall damage");
        this.generalSettings = this.settings.getDefaultGroup();
        this.modeSetting = this.generalSettings.add(new EnumSetting.Builder<Mode>()
                .name("mode")
                .description("Which NoFall mode to use.")
                .defaultValue(Mode.Bounce)
                .build());
    }

    public static enum Mode {
        Bounce("Bounce"),
        Clip("Clip"),
        Dev("Dev");

        private final String title;

        private Mode(String title) {
            this.title = title;
        }

        public String toString() {
            return this.title;
        }

        // $FF: synthetic method
        private static Mode[] $values() {
            return new Mode[]{Bounce, Clip, Dev};
        }
    }
}