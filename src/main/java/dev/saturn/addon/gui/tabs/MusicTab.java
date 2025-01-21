package dev.saturn.addon.gui.tabs;

import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.tabs.Tab;
import meteordevelopment.meteorclient.gui.tabs.TabScreen;
import meteordevelopment.meteorclient.gui.tabs.WindowTabScreen;
import meteordevelopment.meteorclient.gui.widgets.WLabel;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.input.WTextBox;
import meteordevelopment.meteorclient.gui.widgets.pressable.WMinus;
import meteordevelopment.meteorclient.gui.widgets.pressable.WPlus;
import meteordevelopment.meteorclient.gui.widgets.pressable.WTriangle;
import meteordevelopment.meteorclient.systems.friends.Friend;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.utils.misc.NbtUtils;
import meteordevelopment.meteorclient.utils.network.MeteorExecutor;
import net.minecraft.client.gui.screen.Screen;
import meteordevelopment.meteorclient.gui.tabs.Tab;

import java.util.List;

public class MusicTab extends Tab {
    public MusicTab() {
        super("Music");
    }

    @Override
    public TabScreen createScreen(GuiTheme theme) {
        return new MusicScreen(theme, this);
    }

    @Override
    public boolean isScreen(Screen screen) {
        return screen instanceof MusicScreen;
    }

    private static class MusicScreen extends WindowTabScreen {
        public MusicScreen(GuiTheme theme, Tab tab) {
            super(theme, tab);
        }

        @Override
        public void initWidgets() {

            add(theme.label("Music Player")).expandX().center().widget();

            // New
            WHorizontalList searchList = add(theme.horizontalList()).expandX().widget();

            WTextBox search = searchList.add(theme.textBox("Search")).expandX().widget();
            search.actionOnUnfocused = () -> {
                if (search.get().isEmpty()) search.set("Search");
            };
            search.setFocused(false);

            add(theme.horizontalSeparator()).expandX();

            WTable table = add(theme.table()).expandX().minWidth(400).widget();

            WHorizontalList playlist = add(theme.horizontalList()).expandX().widget();

            WTextBox URL = playlist.add(theme.textBox("URL")).expandX().widget();
            URL.setFocused(false);
            WPlus addPlaylist = playlist.add(theme.plus()).widget();

            addPlaylist.action = () -> {
                String url = URL.get().trim();
                if (url.isEmpty() || url.contains("URL")) return;
                reload();
            };


        }

        @Override
        public boolean toClipboard() {
            return NbtUtils.toClipboard(Friends.get());
        }

        @Override
        public boolean fromClipboard() {
            return NbtUtils.fromClipboard(Friends.get());
        }
    }
}