package xyz.acrylicstyle.vanish.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.acrylicstyle.shared.BaseMojangAPI;
import xyz.acrylicstyle.vanish.Vanish;

import java.util.UUID;

public class VanishCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(Vanish.instance, () -> {
            Player p = sender instanceof Player ? (Player) sender : null;
            boolean online = true;
            UUID uuid = p == null ? null : p.getUniqueId();
            String name = p == null ? null : p.getName();
            if (uuid == null && args.length == 0) {
                sender.sendMessage(ChatColor.RED + "プレイヤーを指定してください。");
                return;
            }
            if (args.length != 0) {
                p = Bukkit.getPlayerExact(args[0]);
                if (p == null) {
                    uuid = BaseMojangAPI.getUniqueId(args[0]);
                    if (uuid == null) {
                        sender.sendMessage(ChatColor.RED + "正しいプレイヤーを指定してください。");
                        return;
                    }
                    name = args[0];
                    online = false;
                } else {
                    uuid = p.getUniqueId();
                    name = p.getName();
                }
            }
            @Nullable final Player finalP = p;
            @NotNull final UUID finalU = uuid;
            @NotNull final String finalN = name;
            if (Vanish.vanishedPlayers.contains(finalU)) {
                Vanish.vanishedPlayers.remove(finalU);
                if (online) {
                    run(() -> Bukkit.getOnlinePlayers().forEach(p2 -> {
                        if (!p2.canSee(finalP)) p2.showPlayer(Vanish.instance, finalP);
                    }));
                }
                sender.sendMessage(ChatColor.GREEN + finalN + "の姿を表示するようにしました。");
            } else {
                Vanish.vanishedPlayers.add(finalU);
                if (online) {
                    run(() -> Bukkit.getOnlinePlayers().forEach(p2 -> {
                        if (p2.canSee(finalP)) p2.hidePlayer(Vanish.instance, finalP);
                    }));
                }
                sender.sendMessage(ChatColor.GREEN + finalN + "の姿を非表示にしました。");
            }
        });
        return true;
    }

    public static void run(Runnable runnable) {
        Bukkit.getScheduler().runTask(Vanish.instance, runnable);
    }
}
