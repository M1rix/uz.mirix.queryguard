package uz.mirix.queryguard;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Inherited
public @interface QueryBudget {
    int max() default -1;
    int maxDuplicates() default -1;
    long maxTotalTimeMs() default -1;
    long maxQueryTimeMs() default -1;
    boolean failOnNPlusOne() default false;
    BudgetAction action() default BudgetAction.THROW;
    String name() default "";
}
