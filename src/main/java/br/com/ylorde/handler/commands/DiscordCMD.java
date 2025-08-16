package br.com.ylorde.handler.commands;

import br.com.ylorde.Main;
import br.com.ylorde.commands.DiscordCommand;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

import java.util.Objects;

public record DiscordCMD(Main plugin) {
    public LiteralCommandNode<CommandSourceStack> build(String commandName) {
        return Commands.literal(commandName)
                .requires(sender -> sender.getSender().hasPermission(
                        Objects.requireNonNull(plugin.getConfig().getString("commands.discord.permission"))
                )).executes(DiscordCommand::execute).build();
    }
}
