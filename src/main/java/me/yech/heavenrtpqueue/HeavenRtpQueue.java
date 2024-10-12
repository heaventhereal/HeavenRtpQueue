package me.yech.heavenrtpqueue;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class HeavenRtpQueue extends JavaPlugin {
    private static final Logger logger = Logger.getLogger(HeavenRtpQueue.class.getName());

    @Override
    public void onEnable() {
        RtpQCommand rtpQCommand = new RtpQCommand(this);
        RtpqReloadCommand rtpqReloadCommand = new RtpqReloadCommand(this, rtpQCommand);

        registerCommand(rtpQCommand);
        registerCommand(rtpqReloadCommand);

        getConfig().options().copyDefaults(true);
        saveConfig();

        int flushInterval = getConfig().getInt("queue-flush-interval", 300);
        new QueueFlusher(this, rtpQCommand.getPlayersInQueue()).runTaskTimerAsynchronously(this, flushInterval * 20L, flushInterval * 20L);
    }

    private void registerCommand(BukkitCommand command) {
        try {
            Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            CommandMap commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());
            commandMap.register(command.getName(), command);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            logger.log(Level.SEVERE, "Error registering command: " + command.getName(), e);
        }
    }
}
