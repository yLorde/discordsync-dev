package br.com.ylorde.events;

import br.com.ylorde.Main;
import br.com.ylorde.utils.ConvertToColoredText;
import com.mojang.brigadier.Message;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.jetbrains.annotations.NotNull;
import org.sqlite.core.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class PlayerLogin implements Listener {
    private final Main plugin;

    public PlayerLogin(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(@NotNull PlayerJoinEvent event) {
        Connection connection = this.plugin.getSQLiteConnect();
        Player player = event.getPlayer();

        ConvertToColoredText convertToColoredText = new ConvertToColoredText();
        String discord_id = null;
        String random_code = null;

        try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM players WHERE uuid = ?;")) {
            stmt.setString(1, player.getUniqueId().toString());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    discord_id = rs.getString("discord_id");
                    random_code = rs.getString("random_code");

                    if (!rs.getString("nickname").equals(player.getName())) {
                        try (PreparedStatement updateNickname = connection.prepareStatement("UPDATE players SET nickname = ? WHERE uuid = ?;")) {
                            stmt.setString(1, player.getName());
                            stmt.setString(2, player.getUniqueId().toString());
                            updateNickname.executeUpdate();
                        }
                    }
                } else {
                    try (PreparedStatement insert = connection.prepareStatement("INSERT OR REPLACE INTO players (uuid, nickname) VALUES (?, ?);")) {
                        insert.setString(1, player.getUniqueId().toString());
                        insert.setString(2, player.getName());
                        insert.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
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

        if (!plugin.getConfig().getBoolean("syncOptions.allowUnsynchronizedPlayers") && discord_id == null) {
            player.kick(Component.text(convertToColoredText.convert(
                    Objects.requireNonNull(plugin.getConfig().getString("syncOptions.kickMessage"))
                            .replace("%guild_invite_url", Objects.requireNonNull(plugin.getConfig().getString("discord.guild.inviteUrl")))
                            .replace("%link_code", random_code)
            )), PlayerKickEvent.Cause.TIMEOUT);
        }
    }
}
