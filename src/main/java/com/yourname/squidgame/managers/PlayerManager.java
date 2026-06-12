package com.yourname.squidgame.managers;

import com.yourname.squidgame.SquidGame;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerManager implements Listener {

    private final List<UUID> alivePlayers = new ArrayList<>();
    private final List<UUID> spectators = new ArrayList<>();

    public void eliminate(Player player) {
        if (!alivePlayers.contains(player.getUniqueId())) return;

        alivePlayers.remove(player.getUniqueId());
        spectators.add(player.getUniqueId());

        player.getWorld().strikeLightningEffect(player.getLocation());
        player.setGameMode(GameMode.SPECTATOR);
        
        Bukkit.broadcastMessage(ChatColor.DARK_RED + "⚡ Player " + player.getName() + " has been ELIMINATED!");

        SquidGame.getInstance().getGameManager().checkWinConditions();
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (alivePlayers.contains(player.getUniqueId())) {
            event.setDeathMessage(null);
            eliminate(player);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        alivePlayers.remove(player.getUniqueId());
        spectators.remove(player.getUniqueId());
    }

    public List<UUID> getAlivePlayers() { return alivePlayers; }
    public List<UUID> getSpectators() { return spectators; }
}