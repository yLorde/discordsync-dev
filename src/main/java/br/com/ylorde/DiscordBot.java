package br.com.ylorde;

import br.com.ylorde.handler.DiscordHandler;
import br.com.ylorde.utils.Console;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class DiscordBot extends ListenerAdapter {
    public JDA bot;
    private final Main plugin;

    public DiscordBot(Main plugin) {
        this.plugin = plugin;
    }

    public void start() throws Exception {
        bot = JDABuilder
                .createDefault(plugin.getConfig().getString("discord.bot.botToken"))
                .setStatus(OnlineStatus.IDLE)
                .setActivity(
                        Activity.playing(Objects.requireNonNull(plugin.getConfig().getString("discord.bot.botStatus")))
                )
                .enableIntents(
                        GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.DIRECT_MESSAGES,
                        GatewayIntent.MESSAGE_CONTENT,
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.GUILD_MODERATION,
                        GatewayIntent.GUILD_INVITES
                )
                .addEventListeners(this, new DiscordHandler(plugin))
                .addEventListeners(this)
                .build();

        bot.awaitReady();
    }

    public void onReady(@NotNull ReadyEvent event) {
        Console console = new Console();
        Guild guild = bot.getGuildById(Objects.requireNonNull(plugin.getConfig().getString("discord.guild.guildId")));
        if (guild == null) {
            console.sendColoredMessage("&c[DiscordSync::DiscordClient] >> Nenhum servidor encontrado!");
            return;
        }

        guild.updateCommands().addCommands(
                Commands.slash("sync", "Vincular conta do minecraft ao discord.")
                        .addOption(OptionType.STRING, "código", "Insira aqui o código informado pelo comando /sync usado no servidor.", true)
        ).queue();
    }

    public void stop() {
        if (bot != null) {
            bot.shutdown();
        }
    }
}
