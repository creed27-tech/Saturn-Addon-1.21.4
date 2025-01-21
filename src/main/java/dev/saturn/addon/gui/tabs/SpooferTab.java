package dev.saturn.addon.gui.tabs;

import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.tabs.Tab;
import meteordevelopment.meteorclient.gui.tabs.TabScreen;
import meteordevelopment.meteorclient.gui.widgets.WLabel;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.input.WTextBox;
import meteordevelopment.meteorclient.gui.widgets.pressable.WMinus;
import meteordevelopment.meteorclient.gui.widgets.pressable.WPlus;
import meteordevelopment.meteorclient.gui.widgets.pressable.WTriangle;
import meteordevelopment.meteorclient.gui.tabs.WindowTabScreen;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import net.minecraft.client.gui.screen.Screen;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.utils.misc.NbtUtils;
import meteordevelopment.meteorclient.utils.network.MeteorExecutor;

public class SpooferTab extends Tab {
    public SpooferTab() {
        super("Spoofer");
    }

    @Override
    public TabScreen createScreen(GuiTheme theme) {
        return new SpooferScreen(theme, this);
    }

    @Override
    public boolean isScreen(Screen screen) {
        return false;
    }

    private static class SpooferScreen extends WindowTabScreen {
        public SpooferScreen(GuiTheme theme, Tab tab) {
            super(theme, tab);
        }

        @Override
        public void initWidgets() {

            add(theme.label("Account")).expandX().center().widget();

            WTextBox accountInput = add(theme.textBox("Username")).expandX().widget();
            accountInput.setFocused(true);

            // Create a horizontal list (left to right)
            WHorizontalList horizontalList = add(theme.horizontalList()).expandX().widget();

            WTable table = add(theme.table()).expandX().minWidth(400).widget();

            // Add widgets (e.g., labels) to the horizontal list
            horizontalList.add(theme.label("Account")).widget();
            horizontalList.add(theme.label("Device")).widget();
            horizontalList.add(theme.label("Connection")).widget();
            horizontalList.add(theme.label("Advanced")).widget();
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