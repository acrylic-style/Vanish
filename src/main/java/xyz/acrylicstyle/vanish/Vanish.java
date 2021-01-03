package xyz.acrylicstyle.vanish;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import util.CollectionList;
import util.ICollectionList;
import xyz.acrylicstyle.tomeito_api.TomeitoAPI;
import xyz.acrylicstyle.tomeito_api.providers.ConfigProvider;
import xyz.acrylicstyle.vanish.commands.VanishCommand;

import java.util.UUID;

public class Vanish extends JavaPlugin implements Listener {
    public static Vanish instance;
    public Vanish() { instance = this; }

    public static ConfigProvider config = null;
    public static CollectionList<UUID> vanishedPlayers = new CollectionList<>();

    @Override
    public void onEnable() {
        config = ConfigProvider.getConfig("./plugins/Vanish/config.yml");
        vanishedPlayers = (CollectionList<UUID>) ICollectionList.asList(config.getStringList("vanishedPlayers")).map(UUID::fromString);
        TomeitoAPI.registerCommand("vanish", new VanishCommand());
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        config.setThenSave("vanishedPlayers", vanishedPlayers.map(UUID::toString));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        vanishedPlayers.forEach(uuid -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) e.getPlayer().hidePlayer(this, player);
        });
        if (vanishedPlayers.contains(e.getPlayer().getUniqueId())) {
            e.setJoinMessage(null);
            try {
                e.getRecipients().clear();
            } catch (NoSuchMethodError ignore) {} // normal thing doesn't support it
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                if (vanishedPlayers.contains(e.getPlayer().getUniqueId())) {
                    Bukkit.getOnlinePlayers().forEach(player -> player.hidePlayer(Vanish.this, e.getPlayer()));
                } else {
                    Bukkit.getOnlinePlayers().forEach(player -> player.showPlayer(Vanish.this, e.getPlayer()));
                }
            }
        }.runTaskLater(this, 1);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        if (vanishedPlayers.contains(e.getPlayer().getUniqueId())) {
            e.setQuitMessage(null);
            try {
                e.getRecipients().clear();
            } catch (NoSuchMethodError ignore) {}
        }
    }
}
