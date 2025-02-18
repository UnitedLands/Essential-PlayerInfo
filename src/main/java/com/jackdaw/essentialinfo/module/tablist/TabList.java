package com.jackdaw.essentialinfo.module.tablist;

import com.jackdaw.essentialinfo.EssentialInfo;
import com.jackdaw.essentialinfo.utils.Utils;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.player.TabListEntry;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class TabList {
    // class server
    private final ProxyServer proxyServer;
    private final Logger logger;

    // connect the module to the plugin and server
    public TabList(ProxyServer proxyServer, EssentialInfo plugin, Logger logger) {
        this.proxyServer = proxyServer;
        this.logger = logger;
        this.proxyServer.getScheduler().buildTask(plugin, this::pingUpdate)
                .repeat(50L, TimeUnit.MILLISECONDS).schedule();
    }

    // listener of player login
    @Subscribe
    public void connect(ServerConnectedEvent event) {
        connectUpdate(event);
    }

    // listener of player disconnect
    @Subscribe
    public void disconnect(@NotNull DisconnectEvent event) {
        disconnectUpdate(event.getPlayer());
    }

    // pingUpdate tab list
    private void connectUpdate(@NotNull ServerConnectedEvent event) {
        Player playerOfEvent = event.getPlayer();
        // pingUpdate list of current players
        for (Player player : this.proxyServer.getAllPlayers()) {
            String serverName = event.getServer().getServerInfo().getName();
            addTabListEntry(player, playerOfEvent, serverName);
        }
        // pingUpdate list of new connecting player
        for (Player player : this.proxyServer.getAllPlayers()) {
            if (!player.getUniqueId().equals(playerOfEvent.getUniqueId())) {
                if (player.getCurrentServer().isPresent()) {
                    String serverName = player.getCurrentServer().get().getServerInfo().getName();
                    addTabListEntry(playerOfEvent, player, serverName);
                }
            }
        }
    }

    // remove disconnected player from list
    private void disconnectUpdate(Player fromPlayer) {
        for (Player toPlayer : this.proxyServer.getAllPlayers()) {
            if (toPlayer.getTabList().containsEntry(fromPlayer.getUniqueId())) {
                toPlayer.getTabList().removeEntry(fromPlayer.getUniqueId());
            }
        }
    }

    // normal pingUpdate
    private void pingUpdate() {
        for (Player toPlayer : this.proxyServer.getAllPlayers()) {
            for (Player fromPlayer : this.proxyServer.getAllPlayers()) {
                String serverName;
                if (fromPlayer.getCurrentServer().isPresent()) {
                    serverName = fromPlayer.getCurrentServer().get().getServerInfo().getName();
                } else {
                    continue;
                }
                addTabListEntry(toPlayer, fromPlayer, serverName);
            }
        }
    }

    // add TabList entry
    private void addTabListEntry(@NotNull Player toPlayer, @NotNull Player fromPlayer, String serverName) {
        Component groupName = Utils.getGroupDisplayName(fromPlayer);
        if (!toPlayer.getUniqueId().equals(fromPlayer.getUniqueId()) && !toPlayer.getTabList().containsEntry(fromPlayer.getUniqueId())) {
            toPlayer.getTabList().addEntry(TabListEntry.builder()
                    .displayName(groupName
                            .append(Component.text(" " + fromPlayer.getUsername())))
                    .latency((int) fromPlayer.getPing())
                    .profile(fromPlayer.getGameProfile())
                    .tabList(toPlayer.getTabList())
                    .build());
        }
    }
}