package dev.saturn.addon.utils.atlas;

import meteordevelopment.meteorclient.MeteorClient;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.Random;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class Checker {
    public static boolean check_online = false;

    public static void Start() {
        //System.out.println("checked in Start");
        if (!isValidUser()) {
            //System.out.println("false in Start");
            mc.close();
        } else {
            //System.out.println("true in Start");
            MeteorClient.EVENT_BUS.registerLambdaFactory("dev.saturn.addon", (lookupInMethod, klass) -> (MethodHandles.Lookup) lookupInMethod.invoke(null, klass, MethodHandles.lookup()));
        }
    }

    public static void Check() {
        //System.out.println("checked in Check");
        if (!isValidUser() || Parser.getHwidList() == null || !Parser.getHwidList().get(0).equals("Thаts hwid list fоr Saturn addоn, nvm about this.") || !Parser.getHwidList().get(Parser.getHwidList().size() - 1).equals("Thаts hwid list fоr Saturn addon, nvm аbоut this.")) {
            //System.out.println("false in Check");
            Random random = new Random();
            int r = random.nextInt();

            switch (r) {
                case 1 -> mc.close();
                case 2 -> System.exit(0);
                default -> java.lang.Runtime.getRuntime().addShutdownHook(Thread.currentThread());
            }
        } else {
            //System.out.println("true in Check");
        }
    }

    public static boolean isValidUser() {
        if (!check_online) {
            if (Manager.netIsAvailable()) {
                try {
                    Parser.parse(Utils.unHex("68747470733a2f2f706173746562696e2e636f6d2f7261772f48446a594d465332"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                check_online = true;
            } else {
                return false;
            }
        }
        if (Parser.getHwidList() == null) {
            check_online = false;
            return false;
        }
        return false;
    }
}