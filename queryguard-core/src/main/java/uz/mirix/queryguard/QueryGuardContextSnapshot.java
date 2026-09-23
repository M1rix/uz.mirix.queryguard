package uz.mirix.queryguard;

import java.util.*;
import java.util.concurrent.Callable;

public final class QueryGuardContextSnapshot {
    private final List<QueryScope> scopes;
    QueryGuardContextSnapshot(List<QueryScope> scopes) { this.scopes = List.copyOf(scopes); }
    public Runnable wrap(Runnable task) { return () -> { Deque<QueryScope> previous = QueryGuard.installContext(scopes); try { task.run(); } finally { QueryGuard.restoreContext(previous); } }; }
    public <T> Callable<T> wrap(Callable<T> task) { return () -> { Deque<QueryScope> previous = QueryGuard.installContext(scopes); try { return task.call(); } finally { QueryGuard.restoreContext(previous); } }; }
    public boolean isEmpty() { return scopes.isEmpty(); }
}
