package me.yech.rtpqueueplugin;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

import java.util.*;
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
            String actionbarleftrtpqueue = this.plugin.getConfig().getString("actionbar-left-rtpq");
            assert actionbarleftrtpqueue != null;
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(actionbarleftrtpqueue));
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
        String actionbarjoinedrtpqueue = this.plugin.getConfig().getString("actionbar-joined-rtpq");
        assert actionbarjoinedrtpqueue != null;
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(actionbarjoinedrtpqueue));
        if (playersInQueue.size() == 2) {
            executorService.submit(() -> {
                Location loc = getRandomLocation();
                Player player1 = Bukkit.getPlayer(playersInQueue.get(0));
                Player player2 = Bukkit.getPlayer(playersInQueue.get(1));
                String teleportation = this.plugin.getConfig().getString("being-teleported");
                assert teleportation != null;
                String actionbarbeingteleported = this.plugin.getConfig().getString("actionbar-being-teleported");
                assert actionbarbeingteleported != null;
                assert player1 != null;
                assert player2 != null;
                BukkitScheduler scheduler = Bukkit.getScheduler();
                scheduler.runTask(plugin, () -> {
                    player1.sendMessage(ChatColor.translateAlternateColorCodes('&', teleportation));
                    player2.sendMessage(ChatColor.translateAlternateColorCodes('&', teleportation));
                    player1.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(actionbarbeingteleported));
                    player2.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(actionbarbeingteleported));
                    player1.playSound(player1.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 5.0F, 1F);
                    player2.playSound(player2.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 5.0F, 1F);
                });
                scheduler.runTaskLater(plugin, () -> {
                    player1.teleport(loc);
                    player2.teleport(loc);
                    player1.playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 5.0F, 1F);
                    player2.playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 5.0F, 1F);

                }, 60);
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