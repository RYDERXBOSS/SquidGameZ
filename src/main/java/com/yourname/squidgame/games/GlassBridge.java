package com.yourname.squidgame.games;

import com.yourname.squidgame.SquidGame;
import com.yourname.squidgame.enums.GameState;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class GlassBridge implements Listener {

    private final SquidGame plugin;
    private BukkitTask task;

    public GlassBridge(SquidGame plugin) { this.plugin = plugin; }

    public void start() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        task = new BukkitRunnable() {
            int time = 45;
            @Override
            public void run() {
                if (time <= 0) {
                    plugin.getGameManager().setGameState(GameState.FINALE);
                    cancel();
                }
                time--;
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    public void stop() {
        HandlerList.unregisterAll(this);
        if (task != null) task.cancel();
    }

    @EventHandler
    public void onStep(PlayerMoveEvent e) {
        if (!plugin.getPlayerManager().getAlivePlayers().contains(e.getPlayer().getUniqueId())) return;

        Block steppingOn = e.getPlayer().getLocation().subtract(0, 1, 0).getBlock();
        
        if (steppingOn.getType() == Material.GLASS) {
            steppingOn.setType(Material.AIR);
            e.getPlayer().sendMessage("§cThe glass shattered beneath your feet!");
        }
    }
}