package io.athos.agrocore.plantmonitor;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
    public static String toCamelCase(String snakeOrQuoted) {
        if (snakeOrQuoted == null) return null;
        String snake = snakeOrQuoted.replace("\"", "");
        snake = snake.toLowerCase(Locale.ROOT);
        StringBuilder out = new StringBuilder();
        boolean up = false;
        for (char c : snake.toCharArray()) {
            if (c == '_' || c == ' ') { up = true; }
            else if (up) { out.append(Character.toUpperCase(c)); up = false; }
            else { out.append(c); }
        }
        return out.toString();
    }

    /** Extrai primeiro conteúdo entre aspas "..." (Postgres costuma incluir nomes de coluna/constraint entre aspas) */
    public static String extractQuoted(String msg) {
        Matcher m = Pattern.compile("\"([^\"]+)\"").matcher(msg);
        return m.find() ? m.group(1) : null;
    }

    /** Tira aspas e normaliza enum/número/null em string bonitinha */
    public static String normalizeConstraintValue(String raw) {
        if (raw == null) return "null";
        String v = raw.trim();
        if ((v.startsWith("'") && v.endsWith("'")) || (v.startsWith("\"") && v.endsWith("\""))) {
            v = v.substring(1, v.length() - 1);
        }
        if (v.equalsIgnoreCase("null")) return "null";
        if (v.matches("^[A-Za-z_]+$")) return v.toUpperCase(Locale.ROOT);    // parece enum
        return v; // número, datetime, etc.
    }
}
