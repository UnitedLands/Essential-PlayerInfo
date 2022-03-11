package com.jackdaw.essentialinfo.utils;

import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import org.jetbrains.annotations.NotNull;

public class Utils {

    static LuckPerms luckPerms = LuckPermsProvider.get();

    public static Component getGroupDisplayName(Player player) {
        User user = luckPerms.getUserManager().getUser(player.getUniqueId()); // Luckperms user
        String userGroup = user.getPrimaryGroup();
        String groupName = luckPerms.getGroupManager().getGroup(userGroup).getDisplayName();
        Component component = LegacyComponentSerializer.legacySection().deserialize(groupName);
        return component;
    }

    public static Component getPlayerPrefix(Player player) {
        User user = luckPerms.getUserManager().getUser(player.getUniqueId());
        String prefix = user.getCachedData().getMetaData().getPrefix();

        if (prefix == null) {
            return null;
        }

        String rawPrefix = prefix
                .replace("&8[", "") // Remove any open brackets [
                .replace("&8]", "") // Remove any close brackets ]
                .replace("&r", "") // Remove any reset colors
                .replace(" ", ""); // Remove any extra whitespace

        return LegacyComponentSerializer.legacySection().deserialize(rawPrefix);
    }

}
