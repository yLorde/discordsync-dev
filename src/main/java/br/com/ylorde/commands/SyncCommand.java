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

import java.sql.*;
import java.util.Objects;

public class SyncCommand {
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

                String random_code = null;
                String discord_id = null;

                try (PreparedStatement stmt = connection.prepareStatement("SELECT * players WHERE uuid = ?;")) {
                    stmt.setString(1, player.getUniqueId().toString());
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            random_code = rs.getString("random_code");
                            discord_id = rs.getString("discord_id");
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                if (discord_id != null) {
                    player.sendMessage(convertToColoredText.convert(
                            Objects.requireNonNull(plugin.getConfig().getString("commands.sync.alreadyLinkedMessage"))
                                    .replace("%player_discord_id", discord_id)
                    ));

                    return Command.SINGLE_SUCCESS;
                }

                if (random_code == null) {
                    random_code = String.valueOf(1000 + (int)(Math.random() * 9999));

                    try (PreparedStatement updateRandomCode = connection.prepareStatement("UPDATE players SET random_code = ? WHERE uuid = ?;")) {
                        updateRandomCode.setString(1, random_code);
                        updateRandomCode.setString(2, player.getUniqueId().toString());
                        updateRandomCode.executeUpdate();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

                player.sendMessage(convertToColoredText.convert(
                        Objects.requireNonNull(plugin.getConfig().getString("commands.sync.onUseMessage"))
                                .replace("%link_code", random_code)
                ));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return Command.SINGLE_SUCCESS;
    }
}
