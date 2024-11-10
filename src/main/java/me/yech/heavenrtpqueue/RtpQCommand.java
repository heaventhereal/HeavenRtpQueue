package me.yech.heavenrtpqueue;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RtpQCommand extends BukkitCommand {
    private final HeavenRtpQueue plugin;
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final ObjectArrayList<UUID> playersInQueue = new ObjectArrayList<>();
    private int xMin;
    private int xMax;
    private int zMin;
    private int zMax;

    public RtpQCommand(HeavenRtpQueue plugin) {
        super("rtpqueue");
        this.setAliases(ObjectArrayList.of("rtpq"));
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
        String noperms = this.plugin.getConfig().getString("messages.no-perms");
        assert noperms != null;
        if (sender.hasPermission("heaven.rtpq")) {
            if (playersInQueue.contains(player.getUniqueId())) {
                playersInQueue.remove(player.getUniqueId());
                String leftRtp = this.plugin.getConfig().getString("messages.left-rtpq");
                assert leftRtp != null;
                Component componentMessage = LegacyComponentSerializer.legacyAmpersand().deserialize(leftRtp);
                player.sendMessage(componentMessage);

                String globalleftRtpqmessage = this.plugin.getConfig().getString("messages.global-left-rtpq");
                assert globalleftRtpqmessage != null;
                globalleftRtpqmessage = globalleftRtpqmessage.replace("%player%", player.getName());
                Component globalComponentMessage = LegacyComponentSerializer.legacyAmpersand().deserialize(globalleftRtpqmessage);
                for (Player a : Bukkit.getOnlinePlayers()) {
                    if (!a.equals(sender)) {
                        a.sendMessage(globalComponentMessage);
                    }
                }

                String actionbarleftrtpqueue = this.plugin.getConfig().getString("messages.actionbar-left-rtpq");
                assert actionbarleftrtpqueue != null;
                Component actionBarComponent = LegacyComponentSerializer.legacyAmpersand().deserialize(actionbarleftrtpqueue);
                player.sendActionBar(actionBarComponent);

                return true;
            }
            playersInQueue.add(player.getUniqueId());
            String joinRtpq = this.plugin.getConfig().getString("messages.joined-rtpq");
            assert joinRtpq != null;
            Component componentJoinMessage = LegacyComponentSerializer.legacyAmpersand().deserialize(joinRtpq);
            player.sendMessage(componentJoinMessage);

            String globalRtpqmessage = this.plugin.getConfig().getString("messages.global-joined-rtpq");
            assert globalRtpqmessage != null;
            globalRtpqmessage = globalRtpqmessage.replace("%player%", player.getName());
            Component globalJoinMessage = LegacyComponentSerializer.legacyAmpersand().deserialize(globalRtpqmessage);
            for (Player a : Bukkit.getOnlinePlayers()) {
                if (!a.equals(sender)) {
                    a.sendMessage(globalJoinMessage);
                }
            }

            String actionbarjoinedrtpqueue = this.plugin.getConfig().getString("messages.actionbar-joined-rtpq");
            assert actionbarjoinedrtpqueue != null;
            Component actionBarJoinMessage = LegacyComponentSerializer.legacyAmpersand().deserialize(actionbarjoinedrtpqueue);
            player.sendActionBar(actionBarJoinMessage);

            int queueSize = this.plugin.getConfig().getInt("queue-size", 2);
            if (playersInQueue.size() == queueSize) {
                executorService.submit(() -> {
                    Location loc = getRandomLocation();
                    Player player1 = Bukkit.getPlayer(playersInQueue.get(0));
                    Player player2 = Bukkit.getPlayer(playersInQueue.get(1));
                    String teleportation = this.plugin.getConfig().getString("messages.being-teleported");
                    assert teleportation != null;
                    Component teleportMessage = LegacyComponentSerializer.legacyAmpersand().deserialize(teleportation);

                    String actionbarbeingteleported = this.plugin.getConfig().getString("messages.actionbar-being-teleported");
                    assert actionbarbeingteleported != null;
                    Component actionBarTeleportMessage = LegacyComponentSerializer.legacyAmpersand().deserialize(actionbarbeingteleported);

                    assert player1 != null;
                    assert player2 != null;

                    player1.sendMessage(teleportMessage);
                    player2.sendMessage(teleportMessage);
                    player1.sendActionBar(actionBarTeleportMessage);
                    player2.sendActionBar(actionBarTeleportMessage);
                    player1.playSound(player1.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 5.0F, 1F);
                    player2.playSound(player2.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 5.0F, 1F);

                    int teleportDelay = this.plugin.getConfig().getInt("teleport-delay", 1);
                    Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
                        player1.teleportAsync(loc);
                        player2.teleportAsync(loc);
                        player1.playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 5.0F, 1F);
                        player2.playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 5.0F, 1F);

                    }, teleportDelay);

                    playersInQueue.remove(player1.getUniqueId());
                    playersInQueue.remove(player2.getUniqueId());
                });
            }
        } else {
            Component noPermsMessage = LegacyComponentSerializer.legacyAmpersand().deserialize(noperms);
            sender.sendMessage(noPermsMessage);
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

    public ObjectArrayList<UUID> getPlayersInQueue() {
        return playersInQueue;
    }
}
