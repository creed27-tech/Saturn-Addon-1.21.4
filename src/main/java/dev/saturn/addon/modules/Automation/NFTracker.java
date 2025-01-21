package dev.saturn.addon.modules.Automation;

import dev.saturn.addon.Saturn;
import dev.saturn.addon.utils.NF.WHhandler;
import meteordevelopment.meteorclient.systems.modules.Module;

import static dev.saturn.addon.utils.NF.NFUtils.getNetherrack;

public class NFTracker extends Module {

    int initialAmount;

    public NFTracker() {
        super(Saturn.Automation, "NF-tracker", "tracks the amount of netherrack mined and sends it to the discord chat.");
    }

    @Override
    public void onActivate(){
        initialAmount = getNetherrack();

    }

    @Override
    public void onDeactivate() {
        if (mc.player == null) return;

        int finalAmount = getNetherrack();
        int amount = finalAmount - initialAmount;
        if (amount > 1000){
            WHhandler.sendMessage("```" + mc.player.getName() + " mined " + amount + " blocks of netherrack this session" + "```");
        }
    }
}