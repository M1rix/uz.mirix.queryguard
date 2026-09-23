package uz.mirix.queryguard.test;

import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;
import uz.mirix.queryguard.*;
import java.lang.reflect.Method;
import java.util.Optional;

public final class QueryGuardExtension implements BeforeTestExecutionCallback,AfterTestExecutionCallback {
    private static final ExtensionContext.Namespace NAMESPACE=ExtensionContext.Namespace.create(QueryGuardExtension.class);private static final String SCOPE_KEY="scope",REPORT_KEY="report";
    @Override public void beforeTestExecution(ExtensionContext context){QueryBudget budget=findBudget(context).orElse(null);QueryBudgetSpec spec=budget==null?QueryBudgetSpec.unlimited():QueryBudgetSpec.from(budget);String name=budget!=null&&!budget.name().isBlank()?budget.name():context.getRequiredTestClass().getSimpleName()+"."+context.getRequiredTestMethod().getName();context.getStore(NAMESPACE).put(SCOPE_KEY,QueryGuard.openScope(name,spec));}
    @Override public void afterTestExecution(ExtensionContext context){QueryScope scope=context.getStore(NAMESPACE).remove(SCOPE_KEY,QueryScope.class);if(scope==null)return;QueryReport report=scope.closeAndReport();context.getStore(NAMESPACE).put(REPORT_KEY,report);QueryBudget budget=findBudget(context).orElse(null);if(context.getExecutionException().isEmpty()&&budget!=null&&budget.action()==BudgetAction.THROW&&report.budgetExceeded())throw new QueryBudgetExceededException(report);}
    public static Optional<QueryReport> report(ExtensionContext context){return Optional.ofNullable(context.getStore(NAMESPACE).get(REPORT_KEY,QueryReport.class));}
    private Optional<QueryBudget> findBudget(ExtensionContext context){Method method=context.getRequiredTestMethod();Optional<QueryBudget> onMethod=AnnotationSupport.findAnnotation(method,QueryBudget.class);return onMethod.isPresent()?onMethod:AnnotationSupport.findAnnotation(context.getRequiredTestClass(),QueryBudget.class);}
}
