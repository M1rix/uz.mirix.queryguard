package uz.mirix.queryguard.test;

import uz.mirix.queryguard.QueryReport;
import java.util.Objects;

public final class QueryGuardAssertions {
    private QueryGuardAssertions(){}
    public static QueryReportAssert assertThat(QueryReport report){return new QueryReportAssert(Objects.requireNonNull(report,"report"));}
    public static final class QueryReportAssert {private final QueryReport report;QueryReportAssert(QueryReport report){this.report=report;}public QueryReportAssert hasAtMostQueries(int max){if(report.totalQueries()>max)fail("Expected at most "+max+" queries but got "+report.totalQueries());return this;}public QueryReportAssert hasAtMostDuplicateQueries(int max){if(report.duplicateQueries()>max)fail("Expected at most "+max+" duplicate queries but got "+report.duplicateQueries());return this;}public QueryReportAssert hasNoNPlusOne(){if(report.hasNPlusOne())fail("Expected no N+1 findings but got "+report.nPlusOneFindings().size());return this;}public QueryReportAssert isWithinBudget(){if(report.budgetExceeded())fail("Expected query budget to pass but violations were: "+String.join("; ",report.budgetViolation().reasons()));return this;}private static void fail(String message){throw new AssertionError(message);}}
}
