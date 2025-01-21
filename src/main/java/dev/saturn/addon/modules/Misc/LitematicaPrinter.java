package dev.saturn.addon.modules.Misc;

import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;

import javax.print.*;

public class LitematicaPrinter extends Module {
    public LitematicaPrinter() {
        super(Categories.Misc, "litematica-printer", "prints litematica");
    }

    @Override
    public void onActivate(){
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        for (PrintService printer : printServices) {
            try {
                DocFlavor flavor = DocFlavor.STRING.TEXT_PLAIN;
                String printerName = "litematica";
                Doc doc = new SimpleDoc(printerName, flavor, null);
                DocPrintJob printJob = printer.createPrintJob();
                printJob.print(doc, null);
            } catch (PrintException e) {
                e.printStackTrace();
            }
        }
        toggle();
        
    }

}