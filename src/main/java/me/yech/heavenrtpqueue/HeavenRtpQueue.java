package me.yech.heavenrtpqueue;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class HeavenRtpQueue extends JavaPlugin {
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private static final Logger logger = Logger.getLogger(HeavenRtpQueue.class.getName());
    private CommandMap commandMap;

    @Override
    public void onEnable() {
        RtpQCommand rtpQCommand = new RtpQCommand(this);
        RtpqReloadCommand rtpqReloadCommand = new RtpqReloadCommand(this, rtpQCommand);

        registerCommand(rtpQCommand);
        registerCommand(rtpqReloadCommand);

        getConfig().options().copyDefaults(true);
        saveConfig();

        int flushInterval = getConfig().getInt("queue-flush-interval", 300);

        executorService.submit(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    new QueueFlusher(this, rtpQCommand.getPlayersInQueue()).run();
                    TimeUnit.SECONDS.sleep(flushInterval);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
    }

    private void initCommandMap() {
        if (commandMap == null) {
            try {
                Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
                commandMapField.setAccessible(true);
                commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());
            } catch (NoSuchFieldException | IllegalAccessException e) {
                logger.log(Level.SEVERE, "Error accessing commandMap", e);
            }
        }
    }

    private void registerCommand(BukkitCommand command) {
        initCommandMap();
        if (commandMap != null) {
            commandMap.register(command.getName(), command);
        } else {
            logger.log(Level.SEVERE, "CommandMap is null, cannot register command: " + command.getName());
        }
    }
}
