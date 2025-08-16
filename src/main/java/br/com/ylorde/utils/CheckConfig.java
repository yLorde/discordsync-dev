package br.com.ylorde.utils;

import br.com.ylorde.Main;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class CheckConfig {
    public boolean checkString(@NotNull Main plugin, @NotNull String param1, @NotNull String param2) {
        if (Objects.equals(plugin.getConfig().getString(param1), param2)) {
            Console console = new Console();
            console.sendColoredMessage("&c"+param1+" Não configurado ou ausente!");
            return true;
        };

        if (Objects.requireNonNull(plugin.getConfig().getString(param1)).isBlank()) {
            Console console = new Console();
            console.sendColoredMessage("&c"+param1+" Não configurado ou ausente!");
            return true;
        }

        if (Objects.requireNonNull(plugin.getConfig().getString(param1)).isEmpty()) {
            Console console = new Console();
            console.sendColoredMessage("&c"+param1+" Não configurado ou ausente!");
            return true;
        }

        return  false;
    }
}
