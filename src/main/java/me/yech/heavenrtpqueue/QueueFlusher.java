package me.yech.heavenrtpqueue;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class QueueFlusher extends BukkitRunnable {
    private final HeavenRtpQueue plugin;
    private final ObjectArrayList<UUID> playersInQueue;

    public QueueFlusher(HeavenRtpQueue plugin, ObjectArrayList<UUID> playersInQueue) {
        this.plugin = plugin;
        this.playersInQueue = playersInQueue;
    }

    @Override
    public void run() {
        if (!playersInQueue.isEmpty()) {
            playersInQueue.clear();
            String flushMessage = plugin.getConfig().getString("messages.queue-flushed-message");
            if (flushMessage != null) {
                Component messageComponent = LegacyComponentSerializer.legacyAmpersand().deserialize(flushMessage);

                Bukkit.getServer().sendMessage(messageComponent);
            }
        }
    }
}