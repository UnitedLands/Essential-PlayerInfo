package com.jackdaw.essentialinfo.module.message;

import com.google.inject.Inject;
import com.jackdaw.essentialinfo.configuration.SettingManager;
import com.jackdaw.essentialinfo.utils.Utils;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import javax.inject.Named;
import java.util.HashMap;
import java.util.Objects;

public class Message {
    // class for Server
    private final ProxyServer proxyServer;
    private final Logger logger;
    private final Parser parser = MessageParser.getParser();
    private final boolean isCommandToBroadcast;

    // connect the module to the plugin and server
    @Inject
    public Message(ProxyServer proxyServer, Logger logger, SettingManager setting) {
        this.isCommandToBroadcast = setting.isCommandToBroadcastEnabled();
        this.proxyServer = proxyServer;
        this.logger = logger;
    }

    // listener of player chat
    @Subscribe(order = PostOrder.EARLY)
    public void onPlayerChat(PlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();
        if (this.isCommandToBroadcast) {
            HashMap parsedMessage = parser.parse(message);
            if (parsedMessage.get("broadcastTag").equals(true)) {
                broadcast(player, parsedMessage.get("content").toString());
            }
        } else {
             broadcast(player, message);
        }

    }

    // broadcast the message
    private void broadcast(Player player, String message) {
        String playerName = player.getUsername();
        Component groupName = Utils.getGroupDisplayName(player);
        Component prefix = Utils.getPlayerPrefix(player);

        @NotNull TextComponent sendMessage;
        TextComponent textComponent = null;
        // Audience message
        if (player.getCurrentServer().isPresent()) {
            @NotNull TextComponent server = getServerPrefix(player);

                sendMessage = Component.text("[", NamedTextColor.DARK_GRAY)
                        .append(server)
                        .append(Component.text("] ", NamedTextColor.DARK_GRAY))
                        .append(groupName)
                        .append(Component.text(" [", NamedTextColor.DARK_GRAY))
                        .append(prefix)
                        .append(Component.text("] ", NamedTextColor.DARK_GRAY))
                        .append(Component.text(playerName + ": ", NamedTextColor.GRAY))
                        .append(Component.text(message, NamedTextColor.WHITE));

             textComponent = sendMessage;
        }
        // send message to other server
        for (RegisteredServer s : this.proxyServer.getAllServers()) {
            if (!Objects.equals(s, player.getCurrentServer().get().getServer())) {
                s.sendMessage(textComponent);
            }
        }
    }

    private @NotNull TextComponent getServerPrefix(Player player) {
        String server = player.getCurrentServer().get().getServerInfo().getName();
        if (server.equals("main")) {
            return Component.text("G", NamedTextColor.DARK_GREEN);
        }
        return Component.text("E", NamedTextColor.YELLOW);
    }

}



