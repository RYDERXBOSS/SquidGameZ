package com.yourname.squidgame.managers;

import com.yourname.squidgame.SquidGame;
import com.yourname.squidgame.enums.GameState;
import com.yourname.squidgame.games.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class GameManager {

    private final SquidGame plugin;
    private GameState currentState = GameState.LOBBY;
    private int lobbyCountdown = 30;

    private RedLightGreenLight rlgl;
    private Honeycomb honeycomb;
    private TugOfWar tugOfWar;
    private Marbles marbles;
    private GlassBridge glassBridge;
    private Finale finale;

    public GameManager(SquidGame plugin) {
        this.plugin = plugin;
        startLobbySystem();
    }

    public void setGameState(GameState newState) {
        this.currentState = newState;
        stopAllGames();

        switch (newState) {
            case RED_LIGHT_GREEN_LIGHT:
                broadcastSystemMessage("Game 1: Red Light, Green Light is beginning!");
                rlgl = new RedLightGreenLight(plugin);
                rlgl.start();
                break;
            case HONEYCOMB:
                broadcastSystemMessage("Game 2: Sugar Honeycombs is beginning!");
                honeycomb = new Honeycomb(plugin);
                honeycomb.start();
                break;
            case TUG_OF_WAR:
                broadcastSystemMessage("Game 3: Tug of War is beginning!");
                tugOfWar = new TugOfWar(plugin);
                tugOfWar.start();
                break;
            case MARBLES:
                broadcastSystemMessage("Game 4: Marbles is beginning!");
                marbles = new Marbles(plugin);
                marbles.start();
                break;
            case GLASS_BRIDGE:
                broadcastSystemMessage("Game 5: Glass Stepping Stones is beginning!");
                glassBridge = new GlassBridge(plugin);
                glassBridge.start();
                break;
            case FINALE:
                broadcastSystemMessage("Game 6: Squid Game (The Finale) is starting!");
                finale = new Finale(plugin);
                finale.start();
                break;
            case WON:
                payoutWinner();
                break;
        }
    }

    private void stopAllGames() {
        if (rlgl != null) rlgl.stop();
        if (honeycomb != null) honeycomb.stop();
        if (tugOfWar != null) tugOfWar.stop();
        if (marbles != null) marbles.stop();
        if (glassBridge != null) glassBridge.stop();
        if (finale != null) finale.stop();
    }

    private void startLobbySystem() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (currentState != GameState.LOBBY) { cancel(); return; }

                int count = Bukkit.getOnlinePlayers().size();
                if (count >= 2) {
                    if (lobbyCountdown > 0) {
                        if (lobbyCountdown % 10 == 0 || lobbyCountdown <= 5) {
                            broadcastSystemMessage("Tournament begins in " + lobbyCountdown + " seconds!");
                        }
                        lobbyCountdown--;
                    } else {
                        Bukkit.getOnlinePlayers().forEach(p -> plugin.getPlayerManager().getAlivePlayers().add(p.getUniqueId()));
                        setGameState(GameState.RED_LIGHT_GREEN_LIGHT);
                        cancel();
                    }
                } else {
                    lobbyCountdown = 30;
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    public void checkWinConditions() {
        int alive = plugin.getPlayerManager().getAlivePlayers().size();
        if (alive == 0) {
            broadcastSystemMessage("Everyone has died. No winners this round.");
            setGameState(GameState.LOBBY);
        } else if (alive == 1 && currentState != GameState.LOBBY && currentState != GameState.WON) {
            setGameState(GameState.WON);
        }
    }

    private void payoutWinner() {
        if (plugin.getPlayerManager().getAlivePlayers().size() == 1) {
            Player winner = Bukkit.getPlayer(plugin.getPlayerManager().getAlivePlayers().get(0));
            if (winner != null) {
                broadcastSystemMessage("🏆 " + winner.getName() + " has won the Squid Game tournament!");
                if (plugin.getEconomy() != null) {
                    plugin.getEconomy().depositPlayer(winner, 456000.0);
                    winner.sendMessage(ChatColor.GOLD + "💰 $456,000 has been added to your balance!");
                }
            }
        }
        setGameState(GameState.LOBBY);
    }

    public void broadcastSystemMessage(String msg) {
        Bukkit.broadcastMessage(ChatColor.DARK_RED + "[" + ChatColor.RED + "GUARDS" + ChatColor.DARK_RED + "] " + ChatColor.YELLOW + msg);
    }

    public GameState getCurrentState() { return currentState; }
}