package com.yourname.squidgame.games;

import com.yourname.squidgame.SquidGame;
import com.yourname.squidgame.enums.GameState;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class Marbles implements Listener {

    private final SquidGame plugin;
    private BukkitTask task;

    public Marbles(SquidGame plugin) { this.plugin = plugin; }

    public void start() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        task = new BukkitRunnable() {
            int limit = 30;
            @Override
            public void run() {
                if (limit <= 0) {
                    plugin.getGameManager().setGameState(GameState.GLASS_BRIDGE);
                    cancel();
                }
                limit--;
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    public void stop() {
        HandlerList.unregisterAll(this);
        if (task != null) task.cancel();
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Player p = event.getPlayer();
        if (!plugin.getPlayerManager().getAlivePlayers().contains(p.getUniqueId())) return;

        if (event.getItemDrop().getItemStack().getType() == Material.MAGMA_CREAM) {
            int amount = event.getItemDrop().getItemStack().getAmount();
            if (amount % 2 != 0) {
                p.sendMessage("§cYour bet was ODD! Dangerous play...");
            } else {
                p.sendMessage("§aYour bet was EVEN! Safe for now!");
            }
        }
    }
}