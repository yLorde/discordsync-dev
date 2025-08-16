package br.com.ylorde.commands;

import br.com.ylorde.utils.ConvertToColoredText;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import jdk.jfr.consumer.RecordingStream;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.Objects;

public class UnSyncCommand {
    public static int execute(@NotNull CommandContext<CommandSourceStack> ctx) {
        final Plugin plugin = Bukkit.getPluginManager().getPlugin("DiscordSync");
        assert plugin != null;

        CommandSender sender = ctx.getSource().getSender();
        Entity executor = ctx.getSource().getExecutor();

        ConvertToColoredText convertToColoredText = new ConvertToColoredText();

        if (!(executor instanceof Player player)) {
            sender.sendRichMessage("<red><bold>ERRO</bold></red><yellow> Apenas jogadores podem usar esse comando!</yellow>");
            return Command.SINGLE_SUCCESS;
        }

        if (sender == executor) {
            Connection connection;

            try {
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection("jdbc:sqlite:plugins/discordsync/players.db");

                String discord_id = null;

                try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM players WHERE uuid = ?;")) {
                    stmt.setString(1, player.getUniqueId().toString());
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            discord_id = rs.getString("discord_id");
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                if (discord_id != null) {
                    try (PreparedStatement stmt = connection.prepareStatement("UPDATE players SET discord_id = ? WHERE uuid = ?;")) {
                        stmt.setString(1, null);
                        stmt.setString(2, player.getUniqueId().toString());
                        stmt.executeUpdate();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    player.sendMessage(convertToColoredText.convert(
                            Objects.requireNonNull(plugin.getConfig().getString("commands.unsync.onSuccessCommand"))
                    ));
                    return Command.SINGLE_SUCCESS;
                }

                player.sendMessage(convertToColoredText.convert(
                        Objects.requireNonNull(plugin.getConfig().getString("commands.unsync.onErrorCommand"))
                ));
                return Command.SINGLE_SUCCESS;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return Command.SINGLE_SUCCESS;
    }
}
