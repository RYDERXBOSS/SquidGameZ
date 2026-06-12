package com.yourname.squidgame.games;

import com.yourname.squidgame.SquidGame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class Finale implements Listener {

    private final SquidGame plugin;

    public Finale(SquidGame plugin) { this.plugin = plugin; }

    public void start() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        plugin.getGameManager().broadcastSystemMessage("PVP is now active! Fight to the death!");
    }

    public void stop() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onPvP(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player) || !(e.getDamager() instanceof Player)) return;

        Player victim = (Player) e.getEntity();
        Player attacker = (Player) e.getDamager();

        if (!plugin.getPlayerManager().getAlivePlayers().contains(victim.getUniqueId()) ||
            !plugin.getPlayerManager().getAlivePlayers().contains(attacker.getUniqueId())) {
            e.setCancelled(true);
        }
    }
}