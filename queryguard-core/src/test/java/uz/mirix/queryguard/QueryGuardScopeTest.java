package uz.mirix.queryguard;

import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class QueryGuardScopeTest {
    @Test void nestedScopesBothObserveQuery(){QueryScope outer=QueryGuard.openScope("outer");QueryScope inner=QueryGuard.openScope("inner");QueryGuard.record(QueryEvent.of("select 1","",100,Instant.now(),"main",true,null,1));assertEquals(1,inner.closeAndReport().totalQueries());assertEquals(1,outer.closeAndReport().totalQueries());}
    @Test void capturedContextCanObserveExecutorQueries()throws Exception{QueryScope scope=QueryGuard.openScope("async");QueryGuardContextSnapshot snapshot=QueryGuard.captureContext();ExecutorService executor=Executors.newSingleThreadExecutor();try{executor.submit(snapshot.wrap(()->QueryGuard.record(QueryEvent.of("select 1","",100,Instant.now(),"main",true,null,1)))).get();}finally{executor.shutdownNow();}assertEquals(1,scope.closeAndReport().totalQueries());}
}
