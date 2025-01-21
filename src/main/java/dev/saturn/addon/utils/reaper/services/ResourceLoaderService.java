package dev.saturn.addon.utils.reaper.services;

import dev.saturn.addon.Saturn;
import meteordevelopment.meteorclient.utils.network.Http;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class ResourceLoaderService {

    // user lists
    //public static String DEV_DB_URL = "";
    public static String BETA_DB_URL = "https://pastebin.com/raw/2CyKb1Un";
    public static String USER_DB_URL = "https://pastebin.com/raw/rAevfDYC";

    public static ArrayList<String> DEVELOPERS = new ArrayList<>();
    public static ArrayList<String> BETA = new ArrayList<>();
    public static ArrayList<String> USER = new ArrayList<>();

    public static void initUserDB() {
        //DEVELOPERS.addAll(List.of("GhostTypes", "EurekaEffect", "Kiriyaga", "Wide_Cat"));
        //initDB(BETA, BETA_DB_URL);
        //initDB(USER, USER_DB_URL);
            }
        }