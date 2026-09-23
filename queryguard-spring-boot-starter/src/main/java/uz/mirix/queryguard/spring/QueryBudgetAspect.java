package uz.mirix.queryguard.spring;

import org.aspectj.lang.*;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;
import uz.mirix.queryguard.*;
import java.lang.reflect.Method;

@Aspect
final class QueryBudgetAspect {
    private final QueryGuardProperties properties; private final QueryGuardDiagnostics diagnostics;
    QueryBudgetAspect(QueryGuardProperties properties,QueryGuardDiagnostics diagnostics){this.properties=properties;this.diagnostics=diagnostics;}
    @Around("@annotation(uz.mirix.queryguard.QueryBudget) || @within(uz.mirix.queryguard.QueryBudget)")
    Object enforce(ProceedingJoinPoint joinPoint)throws Throwable{
        QueryBudget budget=resolveBudget(joinPoint); if(budget==null)return joinPoint.proceed();
        String scopeName=budget.name().isBlank()?joinPoint.getSignature().toShortString():budget.name(); QueryScope scope=QueryGuard.openScope(scopeName,properties.analysisOptions(),QueryBudgetSpec.from(budget));
        Object result; try{result=joinPoint.proceed();}catch(Throwable applicationFailure){QueryReport report=scope.closeAndReport();diagnostics.report(report);throw applicationFailure;}
        QueryReport report=scope.closeAndReport();diagnostics.report(report);if(report.budgetExceeded()&&budget.action()==BudgetAction.THROW)throw new QueryBudgetExceededException(report);return result;
    }
    private QueryBudget resolveBudget(ProceedingJoinPoint joinPoint){Method method=((MethodSignature)joinPoint.getSignature()).getMethod();Class<?> targetClass=joinPoint.getTarget()==null?method.getDeclaringClass():joinPoint.getTarget().getClass();Method specific=AopUtils.getMostSpecificMethod(method,targetClass);QueryBudget budget=AnnotatedElementUtils.findMergedAnnotation(specific,QueryBudget.class);if(budget!=null)return budget;budget=AnnotatedElementUtils.findMergedAnnotation(method,QueryBudget.class);if(budget!=null)return budget;return AnnotatedElementUtils.findMergedAnnotation(targetClass,QueryBudget.class);}
}
