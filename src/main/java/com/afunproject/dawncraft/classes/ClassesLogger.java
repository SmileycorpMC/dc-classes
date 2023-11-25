package com.afunproject.dawncraft.classes;

import com.google.common.collect.Lists;
import net.minecraftforge.fml.util.thread.SidedThreadGroups;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

public class ClassesLogger {
    private static Logger logger = LogManager.getLogger(Constants.MODID);

    private static Path log_file = Paths.get("logs/dcclasses.log");

    public static void clearLog() {
        try {
            Files.write(log_file, Lists.newArrayList(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            logger.error("Failed to write to log file", e);
            e.printStackTrace();
        }
    }

    public static void logInfo(Object message) {
        writeToFile(message);
        logger.info(message);
    }

    public static void logError(Object message, Exception e) {
        writeToFile(e);
        writeToFile(message);
        logger.error(message, e);
        e.printStackTrace();
    }

    private static boolean writeToFile(Object message) {
        return writeToFile(Lists.newArrayList(String.valueOf(message)));
    }

    private static boolean writeToFile(List<String> out) {
        if (out.size() > 0) out.set(0, Timestamp.valueOf(LocalDateTime.now()) + "[" +
                (Thread.currentThread().equals(SidedThreadGroups.SERVER) ? "SERVER" : "CLIENT") + "]" + out.get(0));
        try {
            Files.write(log_file, out, StandardCharsets.UTF_8, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            return true;
        } catch (Exception e) {
            logger.error("Failed to write to log file", e);
            e.printStackTrace();
            return false;
        }
    }

}
