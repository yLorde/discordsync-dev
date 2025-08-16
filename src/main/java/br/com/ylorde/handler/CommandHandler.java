package br.com.ylorde.handler;

import br.com.ylorde.Main;
import br.com.ylorde.handler.commands.DiscordCMD;
import br.com.ylorde.handler.commands.SyncCMD;
import br.com.ylorde.handler.commands.UnSyncCMD;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;

public record CommandHandler(Main plugin) {
    public void handle() {
        this.plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            if (plugin.getConfig().getBoolean("commands.sync.enabled"))
                commands.registrar().register(new SyncCMD(plugin).build("sync"));

            if (plugin.getConfig().getBoolean("commands.unsync.enabled"))
                commands.registrar().register(new UnSyncCMD(plugin).build("unsync"));

            if (plugin.getConfig().getBoolean("commands.discord.enabled"))
                commands.registrar().register(new DiscordCMD(plugin).build("discord"));
        });
    }
}
