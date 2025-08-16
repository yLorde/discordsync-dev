package br.com.ylorde;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

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
                .addEventListeners(this)
                .build();

        bot.awaitReady();
    }

    public void stop() {
        if (bot != null) {
            bot.shutdown();
        }
    }
}
