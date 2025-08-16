package br.com.ylorde.handler;

import br.com.ylorde.Main;
import br.com.ylorde.handler.slash.SyncDiscordSCMD;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class DiscordHandler extends ListenerAdapter {
    private final Main plugin;

    public DiscordHandler(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        SyncDiscordSCMD sync = new SyncDiscordSCMD(plugin, event);
        sync.execute("sync");
    }
}
