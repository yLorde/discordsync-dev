package br.com.ylorde.commands;

import br.com.ylorde.utils.ConvertToColoredText;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class DiscordCommand {
    public static int execute(@NotNull CommandContext<CommandSourceStack> ctx) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("DiscordSync");
        assert plugin != null;

        CommandSender sender = ctx.getSource().getSender();
        Entity executor = ctx.getSource().getExecutor();

        ConvertToColoredText convertToColoredText = new ConvertToColoredText();

        if (!(executor instanceof Player player)) {
            sender.sendRichMessage("<red><bold>ERRO</bold></red><yellow> Apenas jogadores podem usar esse comando!</yellow>");
            return Command.SINGLE_SUCCESS;
        }

        if (sender == executor) {
            player.sendMessage(convertToColoredText.convert(
                    Objects.requireNonNull(plugin.getConfig().getString("commands.discord.onUseMessage"))
                            .replace("%guild_invite_url", Objects.requireNonNull(plugin.getConfig().getString("discord.guild.inviteUrl")))
            ));
        }

        return Command.SINGLE_SUCCESS;
    }
}
