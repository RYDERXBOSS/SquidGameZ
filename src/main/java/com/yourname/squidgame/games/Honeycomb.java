package com.yourname.squidgame.games;

import com.yourname.squidgame.SquidGame;
import com.yourname.squidgame.enums.GameState;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class Honeycomb implements Listener {

    private final SquidGame plugin;
    private BukkitTask timerTask;

    public Honeycomb(SquidGame plugin) { this.plugin = plugin; }

    public void start() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        timerTask = new BukkitRunnable() {
            int duration = 60;
            @Override
            public void run() {
                if (duration <= 0) {
                    plugin.getGameManager().setGameState(GameState.TUG_OF_WAR);
                    cancel();
                }
                duration--;
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    public void stop() {
        HandlerList.unregisterAll(this);
        if (timerTask != null) timerTask.cancel();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player p = event.getPlayer();
        if (!plugin.getPlayerManager().getAlivePlayers().contains(p.getUniqueId())) return;

        Block b = event.getBlock();
        if (b.getType() == Material.OBSIDIAN) {
            event.setCancelled(true);
            plugin.getPlayerManager().eliminate(p);
        } else if (b.getType() == Material.GOLD_BLOCK) {
            p.sendMessage("§aPattern piece successfully carved out!");
        }
    }
}