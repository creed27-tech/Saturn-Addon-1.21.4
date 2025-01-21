package dev.saturn.addon.utils.lemon.misc;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class JSONUtils {
    private static String readAll(Reader rd) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();

        int cp;
        while ((cp = rd.read()) != -1) {
            stringBuilder.append((char) cp);
        }

        return stringBuilder.toString();
        }
    }