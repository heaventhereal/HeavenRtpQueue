package me.yech.heavenrtpqueue;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.jetbrains.annotations.NotNull;

public class RtpqReloadCommand extends BukkitCommand {
    private final HeavenRtpQueue plugin;
    private final RtpQCommand rtpQCommand;

    public RtpqReloadCommand(HeavenRtpQueue plugin, RtpQCommand rtpQCommand) {
        super("rtpqreload");
        this.plugin = plugin;
        this.rtpQCommand = rtpQCommand;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        String configreload = this.plugin.getConfig().getString("config-reloaded");
        assert configreload != null;
        String noperms = this.plugin.getConfig().getString("no-perms");
        assert noperms != null;
        if (sender.hasPermission("yech.rtpqreload")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', configreload));
            plugin.reloadConfig();
            rtpQCommand.updateConfigValues();
        }else{
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', noperms));
        return false;
    }
        return true;
    }
}
