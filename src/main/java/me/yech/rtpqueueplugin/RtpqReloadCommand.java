package me.yech.rtpqueueplugin;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.jetbrains.annotations.NotNull;

public class RtpqReloadCommand extends BukkitCommand {
    private final Rtpqueueplugin plugin;
    private final RtpQCommand rtpQCommand;

    public RtpqReloadCommand(Rtpqueueplugin plugin, RtpQCommand rtpQCommand) {
        super("rtpqreload");
        this.plugin = plugin;
        this.rtpQCommand = rtpQCommand;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        plugin.reloadConfig();
        rtpQCommand.updateConfigValues();
        String configreload = this.plugin.getConfig().getString("config-reloaded");
        assert configreload != null;
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', configreload));
        return true;
    }
}
