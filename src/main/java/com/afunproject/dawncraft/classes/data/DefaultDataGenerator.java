package com.afunproject.dawncraft.classes.data;

import com.afunproject.dawncraft.classes.ClassesLogger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class DefaultDataGenerator {
    
    private static Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final Path CONFIG_FOLDER = FMLPaths.CONFIGDIR.get().resolve("dcclasses");
    
    public static boolean tryGenerateDataFiles() {
        if (CONFIG_FOLDER.toFile().exists()) return false;
        copyFile("pack.mcmeta");
        copyFile("assets/dawncraft/lang/en_us.json");
        copyFile("assets/minecraft/attributes.json");
        copyFile("assets/minecraft/textures/attribute/forge.entity_gravity.png");
        copyFile("assets/minecraft/textures/attribute/forge.reach_distance.png");
        copyFile("assets/minecraft/textures/attribute/forge.swim_speed.png");
        copyFile("assets/minecraft/textures/attribute/generic.armor.png");
        copyFile("assets/minecraft/textures/attribute/generic.armor_toughness.png");
        copyFile("assets/minecraft/textures/attribute/generic.attack_damage.png");
        copyFile("assets/minecraft/textures/attribute/generic.attack_knockback.png");
        copyFile("assets/minecraft/textures/attribute/generic.attack_speed.png");
        copyFile("assets/minecraft/textures/attribute/generic.knockback_resistance.png");
        copyFile("assets/minecraft/textures/attribute/generic.luck.png");
        copyFile("assets/minecraft/textures/attribute/generic.max_health.png");
        copyFile("assets/minecraft/textures/attribute/generic.movement_speed.png");
        copyFile("assets/paraglider/attributes.json");
        copyFile("assets/paraglider/textures/attribute/max_stamina.png");
        copyFile("data/dawncraft/classes/astrologer.json");
        copyFile("data/dawncraft/classes/berserker.json");
        copyFile("data/dawncraft/classes/brawler.json");
        copyFile("data/dawncraft/classes/lancer.json");
        copyFile("data/dawncraft/classes/roamer.json");
        copyFile("data/dawncraft/classes/ronin.json");
        copyFile("data/dawncraft/classes/tank.json");
        copyFile("data/dawncraft/classes/warrior.json");
        return true;
    }
    
    private static void copyFile(String path) {
        try {
            ModFile mod = FMLLoader.getLoadingModList().getModFileById("dcclasses").getFile();
            File directory = CONFIG_FOLDER.toFile();
            File output = new File(directory, path);
            File dir = output.getParentFile();
            if (dir != null) dir.mkdirs();
            FileUtils.copyInputStreamToFile(Files.newInputStream(mod.findResource("config_defaults/" + path), StandardOpenOption.READ), new File(directory, path));
            ClassesLogger.logInfo("Copied file " + path);
        } catch (Exception e) {
            ClassesLogger.logError("Failed to copy file " + path, e);
        }
    }
    
}
