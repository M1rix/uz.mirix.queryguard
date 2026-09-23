package uz.mirix.queryguard;

import java.util.Locale;

public final class QueryReportFormatter {
    private QueryReportFormatter() {}
    public static String format(QueryReport report) {
        StringBuilder out = new StringBuilder(256); out.append("QueryGuard [").append(report.scopeName()).append("] ").append(report.totalQueries()).append(" queries, ").append(report.uniquePatterns()).append(" patterns, ").append(String.format(Locale.ROOT, "%.2f ms SQL time", report.totalDurationMillis()));
        if (report.budgetExceeded()) { out.append(System.lineSeparator()).append("Budget violations:"); report.budgetViolation().reasons().forEach(reason -> out.append(System.lineSeparator()).append("  - ").append(reason)); }
        if (report.hasNPlusOne()) { out.append(System.lineSeparator()).append("Possible N+1:"); for (NPlusOneFinding finding : report.nPlusOneFindings()) out.append(System.lineSeparator()).append("  - ").append(finding.executions()).append("x ").append(abbreviate(finding.normalizedSql(), 500)); }
        return out.toString();
    }
    public static String abbreviate(String value, int maxLength) { if (value == null || value.length() <= maxLength) return value; return value.substring(0, Math.max(0, maxLength - 3)) + "..."; }
}
