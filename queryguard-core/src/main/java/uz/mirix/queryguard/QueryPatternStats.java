package uz.mirix.queryguard;

public record QueryPatternStats(String normalizedSql, SqlOperation operation, int executions, int distinctExecutions, long totalDurationNanos, long maxDurationNanos, int failedExecutions) {
    public double totalDurationMillis() { return totalDurationNanos / 1_000_000.0d; }
    public double maxDurationMillis() { return maxDurationNanos / 1_000_000.0d; }
}
