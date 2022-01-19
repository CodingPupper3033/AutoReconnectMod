package com.codingpupper3033.autoreconnect;

import com.codingpupper3033.autoreconnect.events.MultiplayerScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("autoreconnect")
public class AutoReconnect
{
    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger("autoreconnectmod");

    public AutoReconnect() {

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new MultiplayerScreenEvent());

        // Only client side
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true));
    }
}
