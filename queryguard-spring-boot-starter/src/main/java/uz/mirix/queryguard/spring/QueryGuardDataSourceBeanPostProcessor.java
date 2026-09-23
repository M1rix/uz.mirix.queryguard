package uz.mirix.queryguard.spring;

import org.aopalliance.intercept.MethodInterceptor;
import org.slf4j.*;
import org.springframework.aop.framework.*;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.*;
import uz.mirix.queryguard.jdbc.*;
import javax.sql.DataSource;
import java.lang.reflect.Modifier;
import java.sql.Connection;

final class QueryGuardDataSourceBeanPostProcessor implements BeanPostProcessor, PriorityOrdered {
    private static final Logger log=LoggerFactory.getLogger(QueryGuardDataSourceBeanPostProcessor.class);
    @Override public Object postProcessAfterInitialization(Object bean,String beanName){
        if(!(bean instanceof DataSource)||bean instanceof QueryGuardDataSource)return bean;
        Class<?> targetClass=bean.getClass();
        if(Modifier.isFinal(targetClass.getModifiers())){log.warn("QueryGuard could not auto-instrument final DataSource bean '{}' of type {}. Wrap it explicitly with QueryGuardDataSource.",beanName,targetClass.getName());return bean;}
        ProxyFactory factory=new ProxyFactory(bean); factory.setProxyTargetClass(true); factory.addAdvice((MethodInterceptor)invocation->{Object result=invocation.proceed(); if(result instanceof Connection connection&&invocation.getMethod().getName().equals("getConnection"))return QueryGuardJdbc.wrapConnection(connection,beanName); return result;});
        try{return factory.getProxy(targetClass.getClassLoader());}catch(AopConfigException ex){log.warn("QueryGuard failed to auto-instrument DataSource bean '{}': {}",beanName,ex.getMessage());return bean;}
    }
    @Override public int getOrder(){return Ordered.LOWEST_PRECEDENCE-100;}
}
