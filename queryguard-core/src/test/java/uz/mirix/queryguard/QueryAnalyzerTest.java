package uz.mirix.queryguard;

import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class QueryAnalyzerTest {
    @Test void detectsNPlusOneWhenSameSelectShapeHasDifferentExecutions(){List<QueryEvent> events=new ArrayList<>();for(int i=0;i<7;i++)events.add(QueryEvent.of("select * from role where user_id = ?","bind-"+i,1_000_000,Instant.now(),"main",true,null,1));QueryReport report=QueryAnalyzer.analyze("users",events,new QueryAnalysisOptions(true,5,2,false),new QueryBudgetSpec(10,-1,-1,-1,true));assertEquals(7,report.totalQueries());assertEquals(1,report.uniquePatterns());assertTrue(report.hasNPlusOne());assertTrue(report.budgetExceeded());}
    @Test void repeatedExactQueryIsDuplicateButNotNPlusOne(){QueryEvent event=QueryEvent.of("select 1","same",10,Instant.now(),"main",true,null,1);QueryReport report=QueryAnalyzer.analyze("health",List.of(event,event,event,event,event),QueryAnalysisOptions.defaults(),QueryBudgetSpec.unlimited());assertEquals(4,report.duplicateQueries());assertFalse(report.hasNPlusOne());}
}
