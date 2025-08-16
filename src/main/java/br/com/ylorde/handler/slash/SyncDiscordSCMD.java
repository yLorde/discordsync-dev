package br.com.ylorde.handler.slash;

import br.com.ylorde.Main;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class SyncDiscordSCMD {
    private final Main plugin;
    private final SlashCommandInteractionEvent event;

    public SyncDiscordSCMD(Main plugin, SlashCommandInteractionEvent event) {
        this.plugin = plugin;
        this.event = event;
    }

    public void execute(String commandName) {
        if (event.getName().equals(commandName)) {
            event.reply("Hello, world!").setEphemeral(true).queue();
        }
    }
}
