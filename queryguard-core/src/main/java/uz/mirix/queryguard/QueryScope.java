package uz.mirix.queryguard;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public final class QueryScope implements AutoCloseable {
    private final String name; private final QueryAnalysisOptions options; private final QueryBudgetSpec budget; private final List<QueryEvent> events = Collections.synchronizedList(new ArrayList<>()); private final AtomicBoolean closed = new AtomicBoolean(false); private volatile QueryReport finalReport;
    QueryScope(String name, QueryAnalysisOptions options, QueryBudgetSpec budget) { this.name = Objects.requireNonNullElse(name, "scope"); this.options = Objects.requireNonNull(options); this.budget = Objects.requireNonNull(budget); }
    void record(QueryEvent event) { if (!closed.get()) events.add(event); }
    public String name() { return name; }
    public QueryReport report() { QueryReport report = finalReport; return report != null ? report : QueryAnalyzer.analyze(name, snapshotEvents(), options, budget); }
    public List<QueryEvent> snapshotEvents() { synchronized (events) { return List.copyOf(events); } }
    public QueryReport closeAndReport() { if (closed.compareAndSet(false, true)) { QueryGuard.closeScope(this); finalReport = QueryAnalyzer.analyze(name, snapshotEvents(), options, budget); } return finalReport; }
    @Override public void close() { closeAndReport(); }
}
