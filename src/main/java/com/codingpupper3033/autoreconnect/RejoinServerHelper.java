package com.codingpupper3033.autoreconnect;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import org.apache.logging.log4j.Logger;

public class RejoinServerHelper {
    public static final int SUCCESSFULLY_CONNECTED_DURATION_MILLISECOND = 2/*mins*/ * 60 * 1000;

    private ServerAddress address;
    private boolean rejoinServer;
    private boolean wasSocketException;
    private long lastJoinTime;
    public static final Logger LOGGER = AutoReconnect.LOGGER;

    public RejoinServerHelper(ServerAddress address, boolean rejoinServer) {
        this.address = address;
        this.rejoinServer = rejoinServer;
        this.wasSocketException = false;
    }

    public RejoinServerHelper(ServerAddress address) {
        this(address, false);
    }

    public RejoinServerHelper() {
        this(new ServerAddress("",25565));
    }

    public void updateAddress(ServerAddress address) {
        this.wasSocketException = true;
        if (!this.address.equals(address)) {
            this.address = address;

            LOGGER.info("Rejoin server helper has updated to " + getServerString());
        }

        // Logged in long enough that it isn't the boot loop
        /*
        if (this.rejoinServer && System.currentTimeMillis() > lastJoinTime+SUCCESSFULLY_CONNECTED_DURATION_MILLISECOND) {
            this.rejoinServer = false;
            LOGGER.info("Player has been in server for over " + SUCCESSFULLY_CONNECTED_DURATION_MILLISECOND + "mils. Player successfully joined, so disabling autorejoin");

       }
         */
    }

    public void joinServer() {
        System.out.println(rejoinServer + " join server " + wasSocketException);
        if (rejoinServer && wasSocketException) {
            wasSocketException = false;
            //lastJoinTime = System.currentTimeMillis();
            ConnectScreen.startConnecting(new JoinMultiplayerScreen(new TitleScreen()), Minecraft.getInstance(), address, null);
        } else {
            rejoinServer = false;
        }
    }

    public boolean isRejoinServer() {
        return rejoinServer;
    }

    public void setRejoinServer(boolean rejoinServer) {
        this.rejoinServer = rejoinServer;
    }

    public String getServerString() {
        return address.getHost() + ":" + address.getPort();
    }

    public boolean isWasSocketException() {
        return wasSocketException;
    }

    public void setWasSocketException(boolean wasSocketException) {
        this.wasSocketException = wasSocketException;
    }
}
