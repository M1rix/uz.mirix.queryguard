package uz.mirix.queryguard.jdbc;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.*;

final class ConnectionInvocationHandler implements InvocationHandler, QueryGuardInvocationHandler {
    private final Connection delegate; private final String dataSourceName;
    ConnectionInvocationHandler(Connection delegate, String dataSourceName) { this.delegate = delegate; this.dataSourceName = dataSourceName; }
    @Override public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String name = method.getName();
        if (name.equals("toString") && (args == null || args.length == 0)) return "QueryGuardConnection[" + delegate + "]";
        if (name.equals("unwrap") && args != null && args.length == 1 && args[0] instanceof Class<?> iface) { if (iface.isInstance(proxy)) return proxy; return delegate.unwrap(iface); }
        if (name.equals("isWrapperFor") && args != null && args.length == 1 && args[0] instanceof Class<?> iface) return iface.isInstance(proxy) || delegate.isWrapperFor(iface);
        Object result = ReflectionSupport.invoke(method, delegate, args);
        if (result instanceof PreparedStatement prepared && (name.equals("prepareStatement") || name.equals("prepareCall"))) {
            String sql = args != null && args.length > 0 && args[0] instanceof String value ? value : ""; return QueryGuardJdbc.wrapPreparedStatement(prepared, dataSourceName, sql);
        }
        if (result instanceof Statement statement && name.equals("createStatement")) return QueryGuardJdbc.wrapStatement(statement, dataSourceName);
        return result;
    }
}
