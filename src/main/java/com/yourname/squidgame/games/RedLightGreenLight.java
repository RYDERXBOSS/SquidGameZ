package com.yourname.squidgame.games;

import com.yourname.squidgame.SquidGame;
import com.yourname.squidgame.enums.GameState;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Random;

public class RedLightGreenLight implements Listener {

    private final SquidGame plugin;
    private boolean isRedLight = false;
    private BukkitTask loopTask;
    private final Random random = new Random();

    public RedLightGreenLight(SquidGame plugin) { this.plugin = plugin; }

    public void start() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        runGame();
    }

    public void stop() {
        HandlerList.unregisterAll(this);
        if (loopTask != null) loopTask.cancel();
    }

    private void runGame() {
        loopTask = new BukkitRunnable() {
            int timeLimit = 45;

            @Override
            public void run() {
                if (timeLimit <= 0) {
                    plugin.getGameManager().setGameState(GameState.HONEYCOMB);
                    cancel();
                    return;
                }

                isRedLight = random.nextBoolean();
                if (isRedLight) {
                    plugin.getGameManager().broadcastSystemMessage(ChatColor.RED + "🔴 RED LIGHT! STOP MOVING!");
                } else {
                    plugin.getGameManager().broadcastSystemMessage(ChatColor.GREEN + "🟢 GREEN LIGHT! ADVANCE!");
                }
                timeLimit -= 3;
            }
        }.runTaskTimer(plugin, 0L, 60L);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (!isRedLight) return;
        if (!plugin.getPlayerManager().getAlivePlayers().contains(e.getPlayer().getUniqueId())) return;

        Location f = e.getFrom();
        Location t = e.getTo();
        if (t != null && (f.getX() != t.getX() || f.getZ() != t.getZ())) {
            Bukkit.getScheduler().runTask(plugin, () -> plugin.getPlayerManager().eliminate(e.getPlayer()));
        }
    }
}