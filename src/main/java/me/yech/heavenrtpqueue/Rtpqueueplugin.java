package me.yech.heavenrtpqueue;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;

public final class Rtpqueueplugin extends JavaPlugin {

    @Override
    public void onEnable() {
        RtpQCommand rtpQCommand = new RtpQCommand(this);
        RtpqReloadCommand rtpqReloadCommand = new RtpqReloadCommand(this, rtpQCommand);

        registerCommand(rtpQCommand);
        registerCommand(rtpqReloadCommand);

        getConfig().options().copyDefaults(true);
        saveConfig();

        int flushInterval = getConfig().getInt("queue-flush-interval", 300);
        new QueueFlusher(this, rtpQCommand.getPlayersInQueue()).runTaskTimer(this, flushInterval * 20L, flushInterval * 20L);
    }

    private void registerCommand(BukkitCommand command) {
        try {
            Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            CommandMap commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());

            commandMap.register(command.getName(), command);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}