package me.yech.heavenrtpqueue;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.UUID;

public class QueueFlusher extends BukkitRunnable {
    private final HeavenRtpQueue plugin;
    private final List<UUID> playersInQueue;

    public QueueFlusher(HeavenRtpQueue plugin, List<UUID> playersInQueue) {
        this.plugin = plugin;
        this.playersInQueue = playersInQueue;
    }

    @Override
    public void run() {
        if (!playersInQueue.isEmpty()) {
            playersInQueue.clear();
            String flushMessage = plugin.getConfig().getString("queue-flushed-message");
            if (flushMessage != null) {
                Component messageComponent = LegacyComponentSerializer.legacyAmpersand().deserialize(flushMessage);

                Bukkit.getServer().sendMessage(messageComponent);
            }
        }
    }
}