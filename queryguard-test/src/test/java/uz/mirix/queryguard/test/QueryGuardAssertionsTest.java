package uz.mirix.queryguard.test;
import org.junit.jupiter.api.Test;
import uz.mirix.queryguard.*;
import java.time.Instant;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertThrows;
class QueryGuardAssertionsTest {@Test void failsOnTooManyQueries(){QueryEvent event=QueryEvent.of("select 1","",1,Instant.now(),"test",true,null,1);QueryReport report=QueryAnalyzer.analyze("test",List.of(event,event),QueryAnalysisOptions.defaults(),QueryBudgetSpec.unlimited());assertThrows(AssertionError.class,()->QueryGuardAssertions.assertThat(report).hasAtMostQueries(1));}}
