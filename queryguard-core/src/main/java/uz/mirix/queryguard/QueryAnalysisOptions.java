package uz.mirix.queryguard;

public record QueryAnalysisOptions(boolean detectNPlusOne, int nPlusOneThreshold, int minDistinctExecutions, boolean includeFailedQueries) {
    public QueryAnalysisOptions {
        if (nPlusOneThreshold < 2) throw new IllegalArgumentException("nPlusOneThreshold must be >= 2");
        if (minDistinctExecutions < 1) throw new IllegalArgumentException("minDistinctExecutions must be >= 1");
    }
    public static QueryAnalysisOptions defaults() { return new QueryAnalysisOptions(true, 5, 2, false); }
}
