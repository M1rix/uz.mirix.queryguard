package uz.mirix.queryguard.jdbc;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.*;
import uz.mirix.queryguard.*;
import javax.sql.DataSource;
import java.sql.*;
import static org.junit.jupiter.api.Assertions.*;

class QueryGuardDataSourceTest {
    private DataSource dataSource;
    @BeforeEach void setUp()throws Exception{JdbcDataSource raw=new JdbcDataSource();raw.setURL("jdbc:h2:mem:qg;DB_CLOSE_DELAY=-1");dataSource=new QueryGuardDataSource(raw,"test");try(Connection connection=raw.getConnection();Statement statement=connection.createStatement()){statement.execute("drop table if exists users");statement.execute("create table users(id bigint primary key, name varchar(100))");for(int i=1;i<=10;i++)statement.execute("insert into users(id,name) values ("+i+", 'u"+i+"')");}}
    @Test void capturesPreparedStatementsAndDetectsNPlusOneWithoutRetainingValues()throws Exception{QueryScope scope=QueryGuard.openScope("jdbc",new QueryAnalysisOptions(true,5,2,false),QueryBudgetSpec.unlimited());try(Connection connection=dataSource.getConnection();PreparedStatement statement=connection.prepareStatement("select name from users where id = ?")){for(int i=1;i<=6;i++){statement.setLong(1,i);try(var ignored=statement.executeQuery()) {}}}QueryReport report=scope.closeAndReport();assertEquals(6,report.totalQueries());assertTrue(report.hasNPlusOne());assertTrue(report.patterns().get(0).normalizedSql().contains("where id = ?"));}
}
