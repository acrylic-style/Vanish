package xyz.acrylicstyle.vanish.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import xyz.acrylicstyle.tomeito_api.command.PlayerOpCommandExecutor;
import xyz.acrylicstyle.vanish.Vanish;

public class VanishCommand extends PlayerOpCommandExecutor {
    @Override
    public void onCommand(Player player, String[] args) {
        Player p = player;
        if (args.length != 0) {
            p = Bukkit.getPlayerExact(args[0]);
            if (p == null) {
                player.sendMessage(ChatColor.RED + "正しいプレイヤーを指定してください。");
                return;
            }
        }
        final Player finalP = p;
        if (Vanish.vanishedPlayers.contains(p.getUniqueId())) {
            Vanish.vanishedPlayers.remove(p.getUniqueId());
            Bukkit.getOnlinePlayers().forEach(p2 -> {
                if (!p2.canSee(finalP)) p2.showPlayer(Vanish.instance, finalP);
            });
            player.sendMessage(ChatColor.GREEN + p.getName() + "の姿を表示するようにしました。");
        } else {
            Vanish.vanishedPlayers.add(p.getUniqueId());
            Bukkit.getOnlinePlayers().forEach(p2 -> {
                if (p2.canSee(finalP)) p2.hidePlayer(Vanish.instance, finalP);
            });
            player.sendMessage(ChatColor.GREEN + p.getName() + "の姿を非表示にしました。");
        }
    }
}
