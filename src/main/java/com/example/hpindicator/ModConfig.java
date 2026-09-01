package com.example.hpindicator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ModConfig {

    public static double scale = 1.0D;
    public static double verticalOffset = 0.35D;
    public static boolean showNumber = true;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FMLPaths.CONFIGDIR.get().resolve("hpindicator-client.json");

    private ModConfig() {
    }

    public static void load() {
        try {
            if (Files.exists(FILE)) {
                Data data = GSON.fromJson(new String(Files.readAllBytes(FILE), StandardCharsets.UTF_8), Data.class);
                if (data != null) {
                    if (data.scale > 0.0D) scale = clamp(data.scale, 0.5D, 3.0D);
                    verticalOffset = clamp(data.verticalOffset, 0.0D, 1.0D);
                    showNumber = data.showNumber;
                }
            }
        } catch (Exception ignored) {
        }
    }

    public static void save() {
        try {
            Data data = new Data();
            data.scale = scale;
            data.verticalOffset = verticalOffset;
            data.showNumber = showNumber;
            Files.write(FILE, GSON.toJson(data).getBytes(StandardCharsets.UTF_8));
        } catch (Exception ignored) {
        }
    }

    private static double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }

    public static class Data {
        public double scale = 1.0D;
        public double verticalOffset = 0.35D;
        public boolean showNumber = true;
    }
}
