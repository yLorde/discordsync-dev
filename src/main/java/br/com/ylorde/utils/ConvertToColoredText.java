package br.com.ylorde.utils;

import org.jetbrains.annotations.NotNull;

public class ConvertToColoredText {
    public String convert(@NotNull String originalText) {
        return originalText.replaceAll("&", "§");
    }
}
