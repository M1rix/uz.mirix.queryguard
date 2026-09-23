package uz.mirix.queryguard.spring;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.autoconfigure.*;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;
import uz.mirix.queryguard.QueryGuard;
import javax.sql.DataSource;

@AutoConfiguration(afterName="org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration")
@EnableConfigurationProperties(QueryGuardProperties.class)
@EnableAspectJAutoProxy
@ConditionalOnClass(QueryGuard.class)
@ConditionalOnProperty(prefix="queryguard",name="enabled",havingValue="true",matchIfMissing=true)
public class QueryGuardAutoConfiguration {
    @Bean @ConditionalOnMissingBean QueryGuardDiagnostics queryGuardDiagnostics(QueryGuardProperties properties){return new QueryGuardDiagnostics(properties);}
    @Bean @ConditionalOnClass(Aspect.class) @ConditionalOnMissingBean QueryBudgetAspect queryBudgetAspect(QueryGuardProperties properties,QueryGuardDiagnostics diagnostics){return new QueryBudgetAspect(properties,diagnostics);}
    @Bean @ConditionalOnClass(DataSource.class) @ConditionalOnProperty(prefix="queryguard.data-source",name="enabled",havingValue="true",matchIfMissing=true) static QueryGuardDataSourceBeanPostProcessor queryGuardDataSourceBeanPostProcessor(){return new QueryGuardDataSourceBeanPostProcessor();}
    @Bean @ConditionalOnClass(name="jakarta.servlet.Filter") @ConditionalOnProperty(prefix="queryguard.request",name="enabled",havingValue="true",matchIfMissing=true) @ConditionalOnMissingBean QueryGuardRequestFilter queryGuardRequestFilter(QueryGuardProperties properties,QueryGuardDiagnostics diagnostics){return new QueryGuardRequestFilter(properties,diagnostics);}
}
