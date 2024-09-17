package me.yech.rtpqueueplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.UUID;

public class QueueFlusher extends BukkitRunnable {
    private final Rtpqueueplugin plugin;
    private final List<UUID> playersInQueue;

    public QueueFlusher(Rtpqueueplugin plugin, List<UUID> playersInQueue) {
        this.plugin = plugin;
        this.playersInQueue = playersInQueue;
    }

    @Override
    public void run() {
        if (!playersInQueue.isEmpty()) {
            playersInQueue.clear();
            String flushMessage = plugin.getConfig().getString("queue-flushed-message", "&cLa coda è stata svuotata automaticamente.");
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', flushMessage));
        }
    }
}
