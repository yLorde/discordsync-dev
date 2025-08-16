package br.com.ylorde;

import br.com.ylorde.handler.CommandHandler;
import br.com.ylorde.handler.EventHandler;
import br.com.ylorde.utils.CheckConfig;
import br.com.ylorde.utils.Console;
import br.com.ylorde.utils.SendHelloMessage;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.Connection;

public final class Main extends JavaPlugin {
    public DiscordBot discordBot;
    public SQLiteManager sqLiteManager;

    @Override
    public void onEnable() {
        saveResource("config.yml", false);
        saveDefaultConfig();

        this.sqLiteManager = new SQLiteManager(new File("plugins/discordsync"));
        this.sqLiteManager.setupTables();

        DiscordClient();

        Console console = new Console();

        console.sendColoredMessage("&aDiscordSync iniciado com sucesso!");

        if (!getConfig().getBoolean("plugin.disableHelloMessage")) {
            new SendHelloMessage().send();
        }

        new CommandHandler(this).handle();
        new EventHandler(this).handle();
    }

    public Connection getSQLiteConnect() { return sqLiteManager.connection; }

    @Override
    public void onDisable() {
        discordBot.stop();
        if (sqLiteManager != null) sqLiteManager.close();
        new Console().sendColoredMessage("&cDiscordSync encerrado segurança.");
    }

    public void DiscordClient() {
        this.discordBot = new DiscordBot(this);
        CheckConfig checkConfig = new CheckConfig();

        if (checkConfig.checkString(this, "discord.bot.botToken", "SEU_TOKEN")) return;
        if (checkConfig.checkString(this, "discord.guild.guildId", "ID_DO_SEU_SERVIDOR")) return;
        if (checkConfig.checkString(this, "discord.guild.linkedRoleId", "ID_DO_CARGO_DE_VINCULADO")) return;

        try {
            Console console = new Console();
            discordBot.start();

            console.sendColoredMessage("&a[DiscordSync::DiscordClient] >> Aplicação iniciada e carregada com sucesso!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
    }
}
