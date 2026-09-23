package uz.mirix.queryguard;

import java.util.Locale;

public enum SqlOperation {
    SELECT, INSERT, UPDATE, DELETE, MERGE, CALL, DDL, OTHER;
    public static SqlOperation detect(String normalizedSql) {
        if (normalizedSql == null || normalizedSql.isBlank()) return OTHER;
        String sql = normalizedSql.stripLeading().toUpperCase(Locale.ROOT);
        if (sql.startsWith("SELECT") || sql.startsWith("WITH")) return SELECT;
        if (sql.startsWith("INSERT")) return INSERT;
        if (sql.startsWith("UPDATE")) return UPDATE;
        if (sql.startsWith("DELETE")) return DELETE;
        if (sql.startsWith("MERGE")) return MERGE;
        if (sql.startsWith("CALL") || sql.startsWith("EXEC")) return CALL;
        if (sql.startsWith("CREATE") || sql.startsWith("ALTER") || sql.startsWith("DROP") || sql.startsWith("TRUNCATE")) return DDL;
        return OTHER;
    }
}
