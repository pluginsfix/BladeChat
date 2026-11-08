package pluginsfix.bladechat.text;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TextFormatter {
    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");
    private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)&[0-9a-fk-or]|&#[0-9a-f]{6}");
    private static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.builder()
            .character('&')
            .hexColors()
            .useUnusualXRepeatedCharacterHexFormat()
            .build();

    private TextFormatter() {
    }

    public static Component format(String input) {
        if (input == null || input.isEmpty()) {
            return Component.empty();
        }
        return SERIALIZER.deserialize(translateHex(input));
    }

    public static String translateHex(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        Matcher matcher = HEX_PATTERN.matcher(input);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            String hex = matcher.group(1);
            StringBuilder replacement = new StringBuilder("&x");
            for (char c : hex.toCharArray()) {
                replacement.append('&').append(c);
            }
            matcher.appendReplacement(sb, replacement.toString());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public static String stripColors(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        return STRIP_COLOR_PATTERN.matcher(input).replaceAll("");
    }
}
