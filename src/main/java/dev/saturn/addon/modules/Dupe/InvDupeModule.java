package dev.saturn.addon.modules.Dupe;

import meteordevelopment.meteorclient.systems.modules.Module;
import dev.saturn.addon.Saturn;

public class InvDupeModule extends Module {
    public InvDupeModule() {
        super(Saturn.Dupe, "1.17-InventoryDupe", "InventoryDupe only works on servers with the version 1.17. (Not any version after or before.)");
    }
}