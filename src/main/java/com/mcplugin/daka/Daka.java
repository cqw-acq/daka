package com.mcplugin.daka;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public final class Daka extends JavaPlugin {
    private Map<UUID, Boolean> dakaStatus;
    @Override
    public void onEnable() {

        this.dakaStatus = new HashMap<>();
        this.getCommand("daka").setExecutor(this);
        System.out.println("[ServerMessage] dakaPlugin has been enabled!");

        // Schedule the task to reset dakaStatus every day at 0:00
        long currentTime = System.currentTimeMillis();
        long midnight = getNextMidnight(currentTime);
        long delay = midnight - currentTime;

        new BukkitRunnable() {
            @Override
            public void run() {
                dakaStatus.clear();
            }
        }.runTaskTimer(this, delay, TimeUnit.DAYS.toMillis(1)); // 每天的计时

        // Plugin startup logic

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("daka")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                UUID playerUUID = player.getUniqueId();

                if (!dakaStatus.getOrDefault(playerUUID, false)) {
                    dakaStatus.put(playerUUID, true);
                    ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
                    String commandToExecute = String.format("money give %s 350", player.getName());
                    Bukkit.dispatchCommand(console, commandToExecute);
                    player.sendMessage("你已经成功打卡并获得350金币！");
                } else {
                    player.sendMessage("你今天已经打过卡了，请明天再试！");
                }
                return true;
            } else {
                sender.sendMessage("该命令只能由玩家执行！");
                return true;
            }
        }
        return false;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
    private long getNextMidnight(long currentTime) {
        long oneDayMillis = TimeUnit.DAYS.toMillis(1);
        return (currentTime / oneDayMillis + 1) * oneDayMillis;
    }
}
