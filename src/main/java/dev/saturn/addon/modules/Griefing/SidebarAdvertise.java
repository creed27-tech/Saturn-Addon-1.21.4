package dev.saturn.addon.modules.Griefing;

import dev.saturn.addon.Saturn;
import dev.saturn.addon.utils.griefingutils.MiscUtil;
import meteordevelopment.meteorclient.gui.utils.StarscriptTextBoxRenderer;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.utils.misc.MeteorStarscript;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.starscript.Script;
import meteordevelopment.meteorclient.systems.modules.Module;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

public class SidebarAdvertise extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<String> title = sgGeneral.add(new StringSetting.Builder()
            .name("title")
            .description("The title of the scoreboard.")
            .defaultValue("Saturn Addon On Top!")
            .wide()
            .renderer(StarscriptTextBoxRenderer.class)
            .build()
    );

    private final Setting<SettingColor> titleColor = sgGeneral.add(new ColorSetting.Builder()
            .name("title-color")
            .description("The color of the title. (alpha is ignored)")
            .defaultValue(new Color(255, 150, 50))
            .build()
    );

    private final Setting<List<String>> lines = sgGeneral.add(new StringListSetting.Builder()
            .name("lines")
            .description("The lines (content) of the scoreboard.")
            .defaultValue(
                    "Griefed by {player}!",
                    "{date}"
            )
            .renderer(StarscriptTextBoxRenderer.class)
            .build()
    );

    private final Setting<SettingColor> linesColor = sgGeneral.add(new ColorSetting.Builder()
            .name("lines-color")
            .description("The color of the lines. (alpha is ignored)")
            .defaultValue(new Color(255, 75, 0))
            .build()
    );

    public SidebarAdvertise() {
        super(Saturn.Griefing, "sidebar-advertise", "Creates a scoreboard with some content. (requires OP)");
    }

    @Override
    public void onActivate() {
        if (isActive()) {
            warning("You don't have OP");
            toggle();
            return;
        }

        String title = parseTitle();
        if (title == null) return;

        List<String> lines = parseLines();
        if (lines == null) return;
    }

    @Nullable
    private String parseTitle() {
        Script compiledTitle = MeteorStarscript.compile(title.get());
        if (compiledTitle == null) {
            warning("Title is malformed!");
            toggle();
            return null;
        }
        return MeteorStarscript.run(compiledTitle);
    }

    @Nullable
    private List<String> parseLines() {
        List<String> uncompiledLines = this.lines.get();
        List<String> lines = new ArrayList<>(uncompiledLines.size());
        for (int i = 0; i < uncompiledLines.size(); i++) {
            String str = uncompiledLines.get(i);
            Script script = MeteorStarscript.compile(str);
            if (script == null) {
                warning("Content line #%d is malformed!".formatted(i + 1));
                toggle();
                return null;
            }
            lines.add(MeteorStarscript.run(script));
        }
        return lines;
    }

    public boolean sendCommandChecked(String command, Function<String, String> tooLongMessageGenerator) {
        if (command.length() >= 256) {
            String tooLongMessage = tooLongMessageGenerator.apply(command);
            warning(tooLongMessage);
            return false;
        }
        return true;
    }
}