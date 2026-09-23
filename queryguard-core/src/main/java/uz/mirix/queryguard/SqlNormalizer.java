package uz.mirix.queryguard;

import java.util.Locale;
import java.util.regex.Pattern;

/** Produces a stable, redacted SQL shape suitable for grouping and diagnostics. */
public final class SqlNormalizer {
    private static final Pattern BLOCK_COMMENTS = Pattern.compile("/\\*.*?\\*/", Pattern.DOTALL);
    private static final Pattern LINE_COMMENTS = Pattern.compile("--[^\\r\\n]*");
    private static final Pattern STRING_LITERALS = Pattern.compile("'(?:''|[^'])*'");
    private static final Pattern HEX_LITERALS = Pattern.compile("(?i)\\b0x[0-9a-f]+\\b");
    private static final Pattern NUMERIC_LITERALS = Pattern.compile("(?<![A-Za-z0-9_])[-+]?\\d+(?:\\.\\d+)?(?:[eE][-+]?\\d+)?(?![A-Za-z0-9_])");
    private static final Pattern WHITESPACE = Pattern.compile("\\s+");
    private SqlNormalizer() {}
    public static String normalize(String sql) {
        if (sql == null || sql.isBlank()) return "";
        String value = BLOCK_COMMENTS.matcher(sql).replaceAll(" ");
        value = LINE_COMMENTS.matcher(value).replaceAll(" ");
        value = STRING_LITERALS.matcher(value).replaceAll("?");
        value = HEX_LITERALS.matcher(value).replaceAll("?");
        value = NUMERIC_LITERALS.matcher(value).replaceAll("?");
        value = WHITESPACE.matcher(value).replaceAll(" ").trim();
        return value.toLowerCase(Locale.ROOT);
    }
}
