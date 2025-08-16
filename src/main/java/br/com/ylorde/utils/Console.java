package br.com.ylorde.utils;

import br.com.ylorde.Main;
import org.bukkit.Bukkit;

public class Console {
    public void sendMessage(String message) {
        Bukkit.getConsoleSender().sendMessage(message);
    }

    public void sendColoredMessage(String message) {
        Bukkit.getConsoleSender().sendMessage(
                new ConvertToColoredText().convert(message)
        );
    }

    public void dispatchCommand(Main plugin, String command) {
        //Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        Bukkit.getScheduler().runTask(plugin, () -> {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        });
    }
}
