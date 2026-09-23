package uz.mirix.queryguard.jdbc;

import java.lang.reflect.Proxy;
import java.sql.*;

public final class QueryGuardJdbc {
    private QueryGuardJdbc() {}
    public static Connection wrapConnection(Connection connection, String dataSourceName) {
        if (connection == null || isQueryGuardProxy(connection)) return connection;
        ClassLoader loader = connection.getClass().getClassLoader(); if (loader == null) loader = QueryGuardJdbc.class.getClassLoader();
        return (Connection) Proxy.newProxyInstance(loader, new Class<?>[]{Connection.class}, new ConnectionInvocationHandler(connection, dataSourceName));
    }
    static Statement wrapStatement(Statement statement, String dataSourceName) {
        if (statement == null || isQueryGuardProxy(statement)) return statement;
        Class<?> api = statement instanceof CallableStatement ? CallableStatement.class : statement instanceof PreparedStatement ? PreparedStatement.class : Statement.class;
        ClassLoader loader = statement.getClass().getClassLoader(); if (loader == null) loader = QueryGuardJdbc.class.getClassLoader();
        return (Statement) Proxy.newProxyInstance(loader, new Class<?>[]{api}, new StatementInvocationHandler(statement, dataSourceName, null));
    }
    static PreparedStatement wrapPreparedStatement(PreparedStatement statement, String dataSourceName, String sql) {
        if (statement == null || isQueryGuardProxy(statement)) return statement;
        Class<?> api = statement instanceof CallableStatement ? CallableStatement.class : PreparedStatement.class;
        ClassLoader loader = statement.getClass().getClassLoader(); if (loader == null) loader = QueryGuardJdbc.class.getClassLoader();
        return (PreparedStatement) Proxy.newProxyInstance(loader, new Class<?>[]{api}, new StatementInvocationHandler(statement, dataSourceName, sql));
    }
    private static boolean isQueryGuardProxy(Object candidate) { return Proxy.isProxyClass(candidate.getClass()) && Proxy.getInvocationHandler(candidate) instanceof QueryGuardInvocationHandler; }
}
