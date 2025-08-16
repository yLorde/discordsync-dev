package br.com.ylorde.commands;

import br.com.ylorde.utils.ConvertToColoredText;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ReloadConfigCommand {
    public static int execute(@NotNull CommandContext<CommandSourceStack> ctx) {
        final Plugin plugin = Bukkit.getPluginManager().getPlugin("DiscordSync");
        assert plugin != null;

        CommandSender sender = ctx.getSource().getSender();

        ConvertToColoredText convertToColoredText = new ConvertToColoredText();

        plugin.reloadConfig();
        sender.sendMessage(convertToColoredText.convert(
                Objects.requireNonNull(plugin.getConfig().getString("commands.reloadConfig.onUseMessage"))
        ));

        return Command.SINGLE_SUCCESS;
    }
}
