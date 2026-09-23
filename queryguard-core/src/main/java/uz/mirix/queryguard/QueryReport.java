package uz.mirix.queryguard;
import java.util.List;
public record QueryReport(String scopeName, int totalQueries, int successfulQueries, int failedQueries, int uniquePatterns, int duplicateQueries, long totalDurationNanos, long maxDurationNanos, List<QueryPatternStats> patterns, List<NPlusOneFinding> nPlusOneFindings, QueryBudgetViolation budgetViolation) {
    public QueryReport { patterns = List.copyOf(patterns); nPlusOneFindings = List.copyOf(nPlusOneFindings); budgetViolation = budgetViolation == null ? QueryBudgetViolation.none() : budgetViolation; }
    public double totalDurationMillis() { return totalDurationNanos / 1_000_000.0d; }
    public double maxDurationMillis() { return maxDurationNanos / 1_000_000.0d; }
    public boolean hasNPlusOne() { return !nPlusOneFindings.isEmpty(); }
    public boolean budgetExceeded() { return budgetViolation.violated(); }
}
