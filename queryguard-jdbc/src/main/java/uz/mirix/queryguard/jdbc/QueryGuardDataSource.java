package uz.mirix.queryguard.jdbc;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Objects;
import java.util.logging.Logger;

public final class QueryGuardDataSource implements DataSource {
    private final DataSource delegate; private final String name;
    public QueryGuardDataSource(DataSource delegate){this(delegate,"dataSource");}
    public QueryGuardDataSource(DataSource delegate,String name){this.delegate=Objects.requireNonNull(delegate,"delegate");this.name=name==null||name.isBlank()?"dataSource":name;}
    public DataSource delegate(){return delegate;}
    @Override public Connection getConnection() throws SQLException{return QueryGuardJdbc.wrapConnection(delegate.getConnection(),name);}
    @Override public Connection getConnection(String username,String password)throws SQLException{return QueryGuardJdbc.wrapConnection(delegate.getConnection(username,password),name);}
    @Override public PrintWriter getLogWriter()throws SQLException{return delegate.getLogWriter();}
    @Override public void setLogWriter(PrintWriter out)throws SQLException{delegate.setLogWriter(out);}
    @Override public void setLoginTimeout(int seconds)throws SQLException{delegate.setLoginTimeout(seconds);}
    @Override public int getLoginTimeout()throws SQLException{return delegate.getLoginTimeout();}
    @Override public Logger getParentLogger()throws SQLFeatureNotSupportedException{return delegate.getParentLogger();}
    @Override public <T>T unwrap(Class<T> iface)throws SQLException{if(iface.isInstance(this))return iface.cast(this);if(iface.isInstance(delegate))return iface.cast(delegate);return delegate.unwrap(iface);}
    @Override public boolean isWrapperFor(Class<?> iface)throws SQLException{return iface.isInstance(this)||iface.isInstance(delegate)||delegate.isWrapperFor(iface);}
}
