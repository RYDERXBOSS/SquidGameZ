package com.yourname.squidgame;

import com.yourname.squidgame.enums.GameState;
import com.yourname.squidgame.managers.GameManager;
import com.yourname.squidgame.managers.PlayerManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class SquidGame extends JavaPlugin implements CommandExecutor {

    private static SquidGame instance;
    private GameManager gameManager;
    private PlayerManager playerManager;
    private Economy econ = null;

    @Override
    public void onEnable() {
        instance = this;
        
        if (!setupEconomy()) {
            getLogger().warning("Vault Economy not found! Cash rewards disabled.");
        }

        this.playerManager = new PlayerManager();
        this.gameManager = new GameManager(this);

        getServer().getPluginManager().registerEvents(playerManager, this);
        this.getCommand("squidgame").setExecutor(this);

        getLogger().info("SquidGame plugin has started successfully!");
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) return false;
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return false;
        econ = rsp.getProvider();
        return econ != null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("start")) {
            if (gameManager.getCurrentState() == GameState.LOBBY) {
                getServer().getOnlinePlayers().forEach(p -> playerManager.getAlivePlayers().add(p.getUniqueId()));
                gameManager.setGameState(GameState.RED_LIGHT_GREEN_LIGHT);
                sender.sendMessage(ChatColor.GREEN + "Squid Game tournament force-started!");
            } else {
                sender.sendMessage(ChatColor.RED + "Game is already running!");
            }
            return true;
        }
        sender.sendMessage(ChatColor.RED + "Usage: /squidgame start");
        return true;
    }

    public static SquidGame getInstance() { return instance; }
    public GameManager getGameManager() { return gameManager; }
    public PlayerManager getPlayerManager() { return playerManager; }
    public Economy getEconomy() { return econ; }
}