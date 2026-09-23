package uz.mirix.queryguard;

public class QueryBudgetExceededException extends RuntimeException {
    private final QueryReport report;
    public QueryBudgetExceededException(QueryReport report) { super("Query budget exceeded for '" + report.scopeName() + "': " + String.join("; ", report.budgetViolation().reasons())); this.report = report; }
    public QueryReport report() { return report; }
}
