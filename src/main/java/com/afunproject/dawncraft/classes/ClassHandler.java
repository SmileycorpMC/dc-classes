package com.afunproject.dawncraft.classes;

import com.afunproject.dawncraft.classes.data.DCClass;
import com.google.common.collect.Maps;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ClassHandler {

    private static final Map<ResourceLocation, DCClass> CLASSES = Maps.newHashMap();

    public static void clear() {
        CLASSES.clear();
    }

    public static void addClass(DCClass clazz) {
        CLASSES.put(clazz.getRegistryName(), clazz);
        ClassesLogger.logInfo("Registered class clazz " + clazz);
    }

    public static DCClass getClass(ResourceLocation loc) {
        return CLASSES.get(loc);
    }

    public static Collection<ResourceLocation> getClassNames() {
        return CLASSES.keySet();
    }

    public static Collection<DCClass> getClasses() {
        return CLASSES.values();
    }

    public static Map<ResourceLocation, DCClass> getClassMap() {
        return CLASSES;
    }

    public static CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> ctx, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggestResource(getClassNames(), builder);
    }

}
