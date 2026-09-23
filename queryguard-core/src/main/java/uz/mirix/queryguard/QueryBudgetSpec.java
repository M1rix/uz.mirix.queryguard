package uz.mirix.queryguard;

public record QueryBudgetSpec(int maxQueries, int maxDuplicateQueries, long maxTotalDurationMs, long maxSingleQueryDurationMs, boolean failOnNPlusOne) {
    public static QueryBudgetSpec unlimited() { return new QueryBudgetSpec(-1, -1, -1, -1, false); }
    public boolean isUnlimited() { return maxQueries < 0 && maxDuplicateQueries < 0 && maxTotalDurationMs < 0 && maxSingleQueryDurationMs < 0 && !failOnNPlusOne; }
    public static QueryBudgetSpec from(QueryBudget annotation) { return new QueryBudgetSpec(annotation.max(), annotation.maxDuplicates(), annotation.maxTotalTimeMs(), annotation.maxQueryTimeMs(), annotation.failOnNPlusOne()); }
}
