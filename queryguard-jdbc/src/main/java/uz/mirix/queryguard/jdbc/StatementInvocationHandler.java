package uz.mirix.queryguard.jdbc;

import uz.mirix.queryguard.QueryEvent;
import uz.mirix.queryguard.QueryGuard;
import java.lang.reflect.*;
import java.sql.*;
import java.time.Instant;
import java.util.*;

final class StatementInvocationHandler implements InvocationHandler, QueryGuardInvocationHandler {
    private final Statement delegate; private final String dataSourceName; private final String preparedSql;
    private final Map<Integer,Object> bindings = new LinkedHashMap<>(); private final List<String> statementBatch = new ArrayList<>(); private int preparedBatchSize;
    StatementInvocationHandler(Statement delegate, String dataSourceName, String preparedSql) { this.delegate = delegate; this.dataSourceName = dataSourceName; this.preparedSql = preparedSql; }
    @Override public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String name = method.getName();
        if (name.equals("toString") && (args == null || args.length == 0)) return "QueryGuardStatement[" + delegate + "]";
        if (name.equals("unwrap") && args != null && args.length == 1 && args[0] instanceof Class<?> iface) { if (iface.isInstance(proxy)) return proxy; return delegate.unwrap(iface); }
        if (name.equals("isWrapperFor") && args != null && args.length == 1 && args[0] instanceof Class<?> iface) return iface.isInstance(proxy) || delegate.isWrapperFor(iface);
        if (preparedSql != null) {
            captureBinding(name,args); if (name.equals("clearParameters")) bindings.clear(); if (name.equals("addBatch") && (args == null || args.length == 0)) preparedBatchSize++;
            if (isPreparedExecution(name,args)) { int batchSize = isBatchExecution(name) ? Math.max(1,preparedBatchSize) : 1; try { return observe(method,args,preparedSql,BindingFingerprint.of(bindings),batchSize); } finally { if (isBatchExecution(name)) preparedBatchSize=0; } }
        } else {
            if (name.equals("addBatch") && args != null && args.length == 1 && args[0] instanceof String sql) statementBatch.add(sql);
            if (isStatementExecution(name,args)) return observe(method,args,sqlFrom(args),"",1);
            if (isBatchExecution(name)) { String sql = statementBatch.isEmpty() ? "statement batch" : statementBatch.get(0); int batchSize = Math.max(1,statementBatch.size()); try { return observe(method,args,sql,"batch",batchSize); } finally { statementBatch.clear(); } }
        }
        if (name.equals("clearBatch")) { statementBatch.clear(); preparedBatchSize=0; }
        return ReflectionSupport.invoke(method,delegate,args);
    }
    private Object observe(Method method,Object[] args,String sql,String bindingFingerprint,int batchSize) throws Throwable {
        if (!QueryGuard.isActive()) return ReflectionSupport.invoke(method,delegate,args);
        Instant startedAt=Instant.now(); long started=System.nanoTime();
        try { Object result=ReflectionSupport.invoke(method,delegate,args); QueryGuard.record(QueryEvent.of(sql,bindingFingerprint,System.nanoTime()-started,startedAt,dataSourceName,true,null,batchSize)); return result; }
        catch(Throwable error){ QueryGuard.record(QueryEvent.of(sql,bindingFingerprint,System.nanoTime()-started,startedAt,dataSourceName,false,error,batchSize)); throw error; }
    }
    private void captureBinding(String name,Object[] args){ if(!name.startsWith("set")||args==null||args.length<2||!(args[0] instanceof Integer index)) return; bindings.put(index,name.equals("setNull")?null:args[1]); }
    private static boolean isPreparedExecution(String name,Object[] args){ if(args!=null&&args.length>0)return false; return name.equals("execute")||name.equals("executeQuery")||name.equals("executeUpdate")||name.equals("executeLargeUpdate")||name.equals("executeBatch")||name.equals("executeLargeBatch"); }
    private static boolean isStatementExecution(String name,Object[] args){ if(args==null||args.length==0||!(args[0] instanceof String))return false; return name.equals("execute")||name.equals("executeQuery")||name.equals("executeUpdate")||name.equals("executeLargeUpdate"); }
    private static boolean isBatchExecution(String name){ return name.equals("executeBatch")||name.equals("executeLargeBatch"); }
    private static String sqlFrom(Object[] args){ return args!=null&&args.length>0&&args[0] instanceof String sql ? sql : ""; }
}
