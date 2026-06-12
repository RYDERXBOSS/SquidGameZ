package com.yourname.squidgame.games;

import com.yourname.squidgame.SquidGame;
import com.yourname.squidgame.enums.GameState;
import org.bukkit.Bukkit;
import org.bukkit.UUID;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class TugOfWar implements Listener {

    private final SquidGame plugin;
    private BukkitTask task;
    private int teamAClicks = 0;
    private int teamBClicks = 0;
    private final List<UUID> teamA = new ArrayList<>();
    private final List<UUID> teamB = new ArrayList<>();

    public TugOfWar(SquidGame plugin) { this.plugin = plugin; }

    public void start() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        
        List<UUID> alive = plugin.getPlayerManager().getAlivePlayers();
        for (int i = 0; i < alive.size(); i++) {
            if (i % 2 == 0) teamA.add(alive.get(i));
            else teamB.add(alive.get(i));
        }

        task = new BukkitRunnable() {
            int timer = 15;
            @Override
            public void run() {
                if (timer <= 0) {
                    evaluateResults();
                    cancel();
                    return;
                }
                timer--;
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private void evaluateResults() {
        if (teamAClicks >= teamBClicks) {
            plugin.getGameManager().broadcastSystemMessage("Team A wins Tug of War!");
            new ArrayList<>(teamB).forEach(uuid -> {
                Player p = Bukkit.getPlayer(uuid);
                if (p != null) plugin.getPlayerManager().eliminate(p);
            });
        } else {
            plugin.getGameManager().broadcastSystemMessage("Team B wins Tug of War!");
            new ArrayList<>(teamA).forEach(uuid -> {
                Player p = Bukkit.getPlayer(uuid);
                if (p != null) plugin.getPlayerManager().eliminate(p);
            });
        }
        plugin.getGameManager().setGameState(GameState.MARBLES);
    }

    public void stop() {
        HandlerList.unregisterAll(this);
        if (task != null) task.cancel();
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() != Action.LEFT_CLICK_AIR) return;
        UUID uuid = e.getPlayer().getUniqueId();
        
        if (teamA.contains(uuid)) teamAClicks++;
        else if (teamB.contains(uuid)) teamBClicks++;
    }
}