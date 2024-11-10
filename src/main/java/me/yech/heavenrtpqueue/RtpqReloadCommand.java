package me.yech.heavenrtpqueue;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
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
        String configReloadMessage = this.plugin.getConfig().getString("messages.config-reloaded");
        assert configReloadMessage != null;
        String noPermsMessage = this.plugin.getConfig().getString("messages.no-perms");
        assert noPermsMessage != null;

        if (sender.hasPermission("yech.rtpqreload")) {
            Component reloadMessageComponent = LegacyComponentSerializer.legacyAmpersand().deserialize(configReloadMessage);
            sender.sendMessage(reloadMessageComponent);

            plugin.reloadConfig();
            rtpQCommand.updateConfigValues();
        } else {
            Component noPermsComponent = LegacyComponentSerializer.legacyAmpersand().deserialize(noPermsMessage);
            sender.sendMessage(noPermsComponent);
            return false;
        }
        return true;
    }
}
