package me.yech.rtpqueueplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RtpQCommand extends BukkitCommand {
    private final Rtpqueueplugin plugin;
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);
    private final List<UUID> playersInQueue = new ArrayList<>();
    private int xMin;
    private int xMax;
    private int zMin;
    private int zMax;

    public RtpQCommand(Rtpqueueplugin plugin) {
        super("rtpqueue");
        this.plugin = plugin;
        updateConfigValues();
    }

    public void updateConfigValues() {
        xMin = this.plugin.getConfig().getInt("Min x");
        xMax = this.plugin.getConfig().getInt("Max x");
        zMin = this.plugin.getConfig().getInt("Min z");
        zMax = this.plugin.getConfig().getInt("Max z");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) return false;
        if (playersInQueue.contains(player.getUniqueId())) {
            playersInQueue.remove(player.getUniqueId());
            String leftRtp = this.plugin.getConfig().getString("left-rtpq");
            assert leftRtp != null;
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', leftRtp));
            String globalleftRtpqmessage = this.plugin.getConfig().getString("global-left-rtpq");
            assert globalleftRtpqmessage != null;
            globalleftRtpqmessage = globalleftRtpqmessage.replace("%player%", player.getDisplayName());
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', globalleftRtpqmessage));
            return true;
        }
        playersInQueue.add(player.getUniqueId());
        String joinRtpq = this.plugin.getConfig().getString("joined-rtpq");
        assert joinRtpq != null;
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', joinRtpq));
        String globalRtpqmessage = this.plugin.getConfig().getString("global-joined-rtpq");
        assert globalRtpqmessage != null;
        globalRtpqmessage = globalRtpqmessage.replace("%player%", player.getDisplayName());
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', globalRtpqmessage));
        if (playersInQueue.size() == 2) {
            executorService.submit(() -> {
                Location loc = getRandomLocation();
                Player player1 = Bukkit.getPlayer(playersInQueue.get(0));
                Player player2 = Bukkit.getPlayer(playersInQueue.get(1));
                String teleportation = this.plugin.getConfig().getString("being-teleported");
                assert teleportation != null;
                assert player1 != null;
                assert player2 != null;
                BukkitScheduler scheduler = Bukkit.getScheduler();
                scheduler.runTask(plugin, () -> {
                    player1.sendMessage(ChatColor.translateAlternateColorCodes('&', teleportation));
                    player2.sendMessage(ChatColor.translateAlternateColorCodes('&', teleportation));
                });
                scheduler.runTask(plugin, () -> {
                    player1.teleport(loc);
                    player2.teleport(loc);
                });
                playersInQueue.remove(player1.getUniqueId());
                playersInQueue.remove(player2.getUniqueId());
            });
        }
        return true;
    }

    public Location getRandomLocation() {
        Random randomSource = new Random();
        World hopeFullyExistingDefaultWorld = Bukkit.getWorld("world");
        int randomX = randomSource.nextInt(xMax - xMin + 1) + xMin;
        int randomZ = randomSource.nextInt(zMax - zMin + 1) + zMin;
        assert hopeFullyExistingDefaultWorld != null;
        int highestY = hopeFullyExistingDefaultWorld.getHighestBlockYAt(randomX, randomZ) + 3;
        return new Location(hopeFullyExistingDefaultWorld, randomX, highestY, randomZ).add(0, 2, 0);
    }
    public List<UUID> getPlayersInQueue() {
        return playersInQueue;
    }
}
