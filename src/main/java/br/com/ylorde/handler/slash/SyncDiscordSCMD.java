package br.com.ylorde.handler.slash;

import br.com.ylorde.Main;
import br.com.ylorde.utils.Console;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class SyncDiscordSCMD {
    private final Main plugin;
    private final SlashCommandInteractionEvent event;

    public SyncDiscordSCMD(Main plugin, SlashCommandInteractionEvent event) {
        this.plugin = plugin;
        this.event = event;
    }

    public void execute(String commandName) {
        if (event.getName().equals(commandName)) {
            String random_code = Objects.requireNonNull(event.getOption("código")).getAsString();

            String uuid = null;
            String nickname = null;
            String discord_id = event.getUser().getId();

            Console console = new Console();

            String linked_role_id = Objects.requireNonNull(plugin.getConfig().getString("discord.guild.linkedRoleId"));

            Guild guild = event.getGuild();
            assert guild != null;

            Role role = guild.getRoleById(linked_role_id);
            if (role == null) {
                console.sendColoredMessage("&c[DiscordSync::DiscordClient] >> Cheque se o campo &elinkedRoleId &cestá configurado corretamente.");
                event.reply("Erro ao vincular a conta, informe a administração do servidor.").setEphemeral(true).queue();
                return;
            }

            User user = event.getUser();

            try (PreparedStatement stmt = plugin.getSQLiteConnect().prepareStatement("SELECT * FROM players WHERE random_code = ?;")) {
                stmt.setString(1, random_code);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        uuid = rs.getString("uuid");
                        nickname = rs.getString("nickname");

                        if (rs.getString("discord_id") != null) {
                            event.reply(Objects.requireNonNull(
                                    plugin.getConfig().getString("discord.messages.minecraftAccountalreadyLinkedMessage")
                            )).setEphemeral(true).queue();

                            try (PreparedStatement removeRandomCode = plugin.getSQLiteConnect().prepareStatement("UPDATE players SET random_code = ? WHERE uuid = ?;")) {
                                removeRandomCode.setString(1, null);
                                removeRandomCode.executeUpdate();
                            }
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            if (uuid != null) {
                guild.addRoleToMember(user, role).queue();

                String commandWhenSync = Objects.requireNonNull(plugin.getConfig().getString("console.commandWhenSync"));
                commandWhenSync = commandWhenSync.replace("%player_nickname", nickname);

                try (PreparedStatement stmt = plugin.getSQLiteConnect().prepareStatement("UPDATE players SET random_code = ?, discord_id = ? WHERE uuid = ?")) {
                    stmt.setString(1, null);
                    stmt.setString(2, discord_id);
                    stmt.setString(3, uuid);
                    stmt.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                event.reply(Objects.requireNonNull(
                        plugin.getConfig().getString("discord.messages.onSuccessCommand")
                ).replace("%player_nickname", nickname)).setEphemeral(true).queue();

                try {
                    console.dispatchCommand(plugin, commandWhenSync);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                event.reply(Objects.requireNonNull(plugin.getConfig().getString("discord.messages.invalidCode"))).setEphemeral(true).queue();
            }
        }
    }
}
