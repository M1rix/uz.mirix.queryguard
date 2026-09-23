package uz.mirix.queryguard;

import java.util.*;

public final class QueryAnalyzer {
    private QueryAnalyzer() {}
    public static QueryReport analyze(String scopeName, List<QueryEvent> sourceEvents, QueryAnalysisOptions options, QueryBudgetSpec budget) {
        List<QueryEvent> events = sourceEvents.stream().filter(event -> options.includeFailedQueries() || event.successful()).toList();
        Map<String, MutablePattern> grouped = new LinkedHashMap<>();
        long totalDuration = 0, maxDuration = 0; int successful = 0, failed = 0;
        for (QueryEvent event : events) {
            totalDuration += event.durationNanos(); maxDuration = Math.max(maxDuration, event.durationNanos()); if (event.successful()) successful++; else failed++;
            String key = event.operation() + "|" + event.normalizedSql();
            grouped.computeIfAbsent(key, ignored -> new MutablePattern(event.normalizedSql(), event.operation())).add(event);
        }
        List<QueryPatternStats> patterns = grouped.values().stream().map(MutablePattern::freeze).sorted(Comparator.comparingInt(QueryPatternStats::executions).reversed().thenComparing(QueryPatternStats::normalizedSql)).toList();
        int duplicates = patterns.stream().mapToInt(pattern -> Math.max(0, pattern.executions() - 1)).sum();
        List<NPlusOneFinding> findings = options.detectNPlusOne() ? patterns.stream().filter(pattern -> pattern.operation() == SqlOperation.SELECT).filter(pattern -> pattern.executions() >= options.nPlusOneThreshold()).filter(pattern -> pattern.distinctExecutions() >= options.minDistinctExecutions()).map(pattern -> new NPlusOneFinding(pattern.normalizedSql(), pattern.executions(), pattern.distinctExecutions(), pattern.totalDurationMillis())).toList() : List.of();
        List<String> violations = new ArrayList<>();
        if (budget.maxQueries() >= 0 && events.size() > budget.maxQueries()) violations.add("query count " + events.size() + " exceeds max " + budget.maxQueries());
        if (budget.maxDuplicateQueries() >= 0 && duplicates > budget.maxDuplicateQueries()) violations.add("duplicate query count " + duplicates + " exceeds max " + budget.maxDuplicateQueries());
        long totalMsCeil = (long) Math.ceil(totalDuration / 1_000_000.0d); if (budget.maxTotalDurationMs() >= 0 && totalMsCeil > budget.maxTotalDurationMs()) violations.add("total query time " + totalMsCeil + "ms exceeds max " + budget.maxTotalDurationMs() + "ms");
        long maxMsCeil = (long) Math.ceil(maxDuration / 1_000_000.0d); if (budget.maxSingleQueryDurationMs() >= 0 && maxMsCeil > budget.maxSingleQueryDurationMs()) violations.add("slowest query " + maxMsCeil + "ms exceeds max " + budget.maxSingleQueryDurationMs() + "ms");
        if (budget.failOnNPlusOne() && !findings.isEmpty()) violations.add("possible N+1 detected in " + findings.size() + " query pattern(s)");
        return new QueryReport(scopeName, events.size(), successful, failed, patterns.size(), duplicates, totalDuration, maxDuration, patterns, findings, new QueryBudgetViolation(violations));
    }
    private static final class MutablePattern {
        private final String sql; private final SqlOperation operation; private int executions, failed; private long totalDuration, maxDuration; private final Set<String> instances = new LinkedHashSet<>();
        private MutablePattern(String sql, SqlOperation operation) { this.sql = sql; this.operation = operation; }
        private void add(QueryEvent event) { executions++; if (!event.successful()) failed++; totalDuration += event.durationNanos(); maxDuration = Math.max(maxDuration, event.durationNanos()); instances.add(event.instanceFingerprint()); }
        private QueryPatternStats freeze() { return new QueryPatternStats(sql, operation, executions, instances.size(), totalDuration, maxDuration, failed); }
    }
}
