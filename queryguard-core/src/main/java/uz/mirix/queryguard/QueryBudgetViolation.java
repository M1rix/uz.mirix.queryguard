package uz.mirix.queryguard;
import java.util.List;
public record QueryBudgetViolation(List<String> reasons) {
    public QueryBudgetViolation { reasons = List.copyOf(reasons); }
    public boolean violated() { return !reasons.isEmpty(); }
    public static QueryBudgetViolation none() { return new QueryBudgetViolation(List.of()); }
}
