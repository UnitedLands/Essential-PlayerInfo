package com.jackdaw.essentialinfo.utils;

import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;

public class Utils {

    static LuckPerms luckPerms = LuckPermsProvider.get();

    public static Component getGroupDisplayName(Player player) {
        User user = luckPerms.getUserManager().getUser(player.getUniqueId()); // Luckperms user
        String userGroup = user.getPrimaryGroup();
        String groupName = luckPerms.getGroupManager().getGroup(userGroup).getDisplayName();
        Component component = LegacyComponentSerializer.legacySection().deserialize(groupName);
        return component;
    }
}
