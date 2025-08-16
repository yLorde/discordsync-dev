package br.com.ylorde.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.DriverManager;

public class UnSyncCommand {
    public static int execute(@NotNull CommandContext<CommandSourceStack> ctx) {
        final Plugin plugin = Bukkit.getPluginManager().getPlugin("DiscordSync");
        assert plugin != null;

        CommandSender sender = ctx.getSource().getSender();
        Entity executor = ctx.getSource().getExecutor();

        if (!(executor instanceof Player player)) {
            sender.sendRichMessage("<red><bold>ERRO</bold></red><yellow> Apenas jogadores podem usar esse comando!</yellow>");
            return Command.SINGLE_SUCCESS;
        }

        if (sender == executor) {
            Connection connection;

            try {
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection("jdbc:sqlite:plugins/discordsync/players.db");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return Command.SINGLE_SUCCESS;
    }
}
