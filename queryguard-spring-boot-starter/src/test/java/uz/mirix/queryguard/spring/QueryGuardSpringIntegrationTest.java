package uz.mirix.queryguard.spring;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import uz.mirix.queryguard.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes=QueryGuardSpringIntegrationTest.TestApplication.class,properties={"spring.datasource.url=jdbc:h2:mem:springqg;DB_CLOSE_DELAY=-1","spring.datasource.username=sa","spring.datasource.password=","queryguard.request.enabled=false"})
class QueryGuardSpringIntegrationTest {
    @Autowired JdbcTemplate jdbcTemplate; @Autowired BudgetedService service;
    @Test void autoInstrumentsSpringDataSource(){QueryScope scope=QueryGuard.openScope("spring");jdbcTemplate.queryForObject("select 1",Integer.class);assertEquals(1,scope.closeAndReport().totalQueries());}
    @Test void annotationTurnsQueryRegressionIntoException(){assertThrows(QueryBudgetExceededException.class,service::tooManyQueries);}
    @SpringBootConfiguration @EnableAutoConfiguration static class TestApplication {@Bean BudgetedService budgetedService(JdbcTemplate jdbcTemplate){return new BudgetedService(jdbcTemplate);}}
    static class BudgetedService {private final JdbcTemplate jdbcTemplate;BudgetedService(JdbcTemplate jdbcTemplate){this.jdbcTemplate=jdbcTemplate;}@QueryBudget(max=1,name="budgeted-service") public void tooManyQueries(){jdbcTemplate.queryForObject("select 1",Integer.class);jdbcTemplate.queryForObject("select 2",Integer.class);}}
}
