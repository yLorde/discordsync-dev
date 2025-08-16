package br.com.ylorde.utils;

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
}
