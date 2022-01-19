package com.codingpupper3033.autoreconnect.events;

import com.codingpupper3033.autoreconnect.AutoReconnect;
import com.codingpupper3033.autoreconnect.RejoinServerHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.DisconnectedScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.Logger;

import java.net.SocketException;

// Client Side
@Mod.EventBusSubscriber(modid = "autoreconnect", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class MultiplayerScreenEvent {
    private static RejoinServerHelper rejoinServerHelper = new RejoinServerHelper();

    public static final Logger LOGGER = AutoReconnect.LOGGER;

    @SubscribeEvent
    public static void onDisconnectScreen(final ScreenEvent.InitScreenEvent.Post event) {
        if (!(event.getScreen() instanceof DisconnectedScreen)) return;

        DisconnectedScreen screen = (DisconnectedScreen) event.getScreen();

        if (!rejoinServerHelper.isRejoinServer()) {
            event.addListener(new Button(
                    screen.width / 2 - 100,
                    screen.height - 30,
                    200,
                    20,
                    new TextComponent("Enable Auto Relogin"),
                    new Button.OnPress() {
                        @Override
                        public void onPress(Button p_93751_) {
                            rejoinServerHelper.setRejoinServer(true);
                            rejoinServerHelper.joinServer();
                        }
                    }));
        } else {
            rejoinServerHelper.joinServer();
        }
    }

    @SubscribeEvent
    public static void onJoinMultiplayerScreen(final ScreenEvent.InitScreenEvent.Post event) {
        if (!(event.getScreen() instanceof JoinMultiplayerScreen)) return;
        rejoinServerHelper.setRejoinServer(false);
    }

    @SubscribeEvent
    public static void onLeavingDueToSocketException(WorldEvent.Unload event) {
        if (!event.getWorld().isClientSide()) {
            return;
        }

        Connection connection = Minecraft.getInstance().player.connection.getConnection();
        TranslatableComponent disconnectReason = (TranslatableComponent) connection.getDisconnectedReason();

        // No Args, disconnected for a reason
        if (disconnectReason.getArgs().length == 0) {
            return;
        }

        // Not a Socket Exception
        if (!disconnectReason.getArgs()[0].toString().contains(SocketException.class.getName())) {
            return;
        }

        String address = connection.getRemoteAddress().toString();
        address = address.substring(address.indexOf('/')+1);

        String domain = address.substring(0,address.indexOf(':'));
        String portString = address.substring(address.indexOf(':')+1);

        int port;

        try {
            port = Integer.parseInt(portString);
        } catch (NumberFormatException e) {
            AutoReconnect.LOGGER.warn("AutoReconnect could not detect a port, defaulting to 25565");
            port = 25565;
        }

        ServerAddress serverAddress = new ServerAddress(domain, port);

        LOGGER.info("AutoReconnect Detected leaving world due to a Socket Exception, updating address");
        // Update server address
        rejoinServerHelper.updateAddress(serverAddress);


    }

}
