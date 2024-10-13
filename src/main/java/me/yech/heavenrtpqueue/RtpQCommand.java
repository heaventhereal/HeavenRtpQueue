package me.yech.heavenrtpqueue;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
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
    private final HeavenRtpQueue plugin;
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);
    private final List<UUID> playersInQueue = new ArrayList<>();
    private int xMin;
    private int xMax;
    private int zMin;
    private int zMax;

    public RtpQCommand(HeavenRtpQueue plugin) {
        super("rtpqueue");
        this.setAliases(Arrays.asList("rtpq", "1v1"));
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
        String noperms = this.plugin.getConfig().getString("no-perms");
        assert noperms != null;
        if (sender.hasPermission("yech.rtpq")) {
            if (playersInQueue.contains(player.getUniqueId())) {
                playersInQueue.remove(player.getUniqueId());
                String leftRtp = this.plugin.getConfig().getString("left-rtpq");
                assert leftRtp != null;
                Component componentMessage = LegacyComponentSerializer.legacyAmpersand().deserialize(leftRtp);
                player.sendMessage(componentMessage);

                String globalleftRtpqmessage = this.plugin.getConfig().getString("global-left-rtpq");
                assert globalleftRtpqmessage != null;
                globalleftRtpqmessage = globalleftRtpqmessage.replace("%player%", player.getName());
                Component globalComponentMessage = LegacyComponentSerializer.legacyAmpersand().deserialize(globalleftRtpqmessage);
                Bukkit.getServer().sendMessage(globalComponentMessage);

                String actionbarleftrtpqueue = this.plugin.getConfig().getString("actionbar-left-rtpq");
                assert actionbarleftrtpqueue != null;
                Component actionBarComponent = LegacyComponentSerializer.legacyAmpersand().deserialize(actionbarleftrtpqueue);
                player.sendActionBar(actionBarComponent);

                return true;
            }
            playersInQueue.add(player.getUniqueId());
            String joinRtpq = this.plugin.getConfig().getString("joined-rtpq");
            assert joinRtpq != null;
            Component componentJoinMessage = LegacyComponentSerializer.legacyAmpersand().deserialize(joinRtpq);
            player.sendMessage(componentJoinMessage);

            String globalRtpqmessage = this.plugin.getConfig().getString("global-joined-rtpq");
            assert globalRtpqmessage != null;
            globalRtpqmessage = globalRtpqmessage.replace("%player%", player.getName());
            Component globalJoinMessage = LegacyComponentSerializer.legacyAmpersand().deserialize(globalRtpqmessage);
            Bukkit.getServer().sendMessage(globalJoinMessage);

            String actionbarjoinedrtpqueue = this.plugin.getConfig().getString("actionbar-joined-rtpq");
            assert actionbarjoinedrtpqueue != null;
            Component actionBarJoinMessage = LegacyComponentSerializer.legacyAmpersand().deserialize(actionbarjoinedrtpqueue);
            player.sendActionBar(actionBarJoinMessage);

            if (playersInQueue.size() == 2) {
                executorService.submit(() -> {
                    Location loc = getRandomLocation();
                    Player player1 = Bukkit.getPlayer(playersInQueue.get(0));
                    Player player2 = Bukkit.getPlayer(playersInQueue.get(1));
                    String teleportation = this.plugin.getConfig().getString("being-teleported");
                    assert teleportation != null;
                    Component teleportMessage = LegacyComponentSerializer.legacyAmpersand().deserialize(teleportation);

                    String actionbarbeingteleported = this.plugin.getConfig().getString("actionbar-being-teleported");
                    assert actionbarbeingteleported != null;
                    Component actionBarTeleportMessage = LegacyComponentSerializer.legacyAmpersand().deserialize(actionbarbeingteleported);

                    assert player1 != null;
                    assert player2 != null;
                    BukkitScheduler scheduler = Bukkit.getScheduler();
                    scheduler.runTaskAsynchronously(plugin, () -> {
                        player1.sendMessage(teleportMessage);
                        player2.sendMessage(teleportMessage);
                        player1.sendActionBar(actionBarTeleportMessage);
                        player2.sendActionBar(actionBarTeleportMessage);
                        player1.playSound(player1.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 5.0F, 1F);
                        player2.playSound(player2.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 5.0F, 1F);
                    });
                    scheduler.runTaskLaterAsynchronously(plugin, () -> {
                        player1.teleportAsync(loc);
                        player2.teleportAsync(loc);
                        player1.playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 5.0F, 1F);
                        player2.playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 5.0F, 1F);

                    }, 20);
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

    public List<UUID> getPlayersInQueue() {
        return playersInQueue;
    }
}
