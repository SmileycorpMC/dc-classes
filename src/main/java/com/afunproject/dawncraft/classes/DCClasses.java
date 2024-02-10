package com.afunproject.dawncraft.classes;

import com.afunproject.dawncraft.classes.client.AttributeProperties;
import com.afunproject.dawncraft.classes.data.DefaultDataGenerator;
import com.afunproject.dawncraft.classes.network.NetworkHandler;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.packs.FolderPackResources;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

@Mod(Constants.MODID)
@Mod.EventBusSubscriber(modid = Constants.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DCClasses {

    public static Capability<PickedClass> PICKED_CLASS = CapabilityManager.get(new CapabilityToken<>(){});

    public DCClasses() {
        ClassesLogger.clearLog();
        MinecraftForge.EVENT_BUS.register(new EventHandler());
        NetworkHandler.initPackets();
        DefaultDataGenerator.tryGenerateDataFiles();
    }
    
    @SubscribeEvent
    public static void addPacks(AddPackFindersEvent event) {
        Path path = FMLPaths.CONFIGDIR.get().resolve("dcclasses");
        ClassesLogger.logInfo(path);
        event.addRepositorySource((consumer, constructor) -> consumer.accept(constructor.create(path.toString(), new TextComponent("DC Classes Config"), true,
                    ()-> new FolderPackResources(path.toFile()), new PackMetadataSection(new TextComponent("DC Classes Config"), 8),
                    Pack.Position.TOP, PackSource.BUILT_IN, false))
        );
    }
    
    @SubscribeEvent
    public static void addResourceReload(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(AttributeProperties.INSTANCE);
    }

}
