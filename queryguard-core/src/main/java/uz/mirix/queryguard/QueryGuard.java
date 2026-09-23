package uz.mirix.queryguard;

import java.util.*;

public final class QueryGuard {
    private static final ThreadLocal<Deque<QueryScope>> SCOPES = ThreadLocal.withInitial(ArrayDeque::new);
    private QueryGuard() {}
    public static QueryScope openScope(String name) { return openScope(name, QueryAnalysisOptions.defaults(), QueryBudgetSpec.unlimited()); }
    public static QueryScope openScope(String name, QueryBudgetSpec budget) { return openScope(name, QueryAnalysisOptions.defaults(), budget); }
    public static QueryScope openScope(String name, QueryAnalysisOptions options, QueryBudgetSpec budget) { QueryScope scope = new QueryScope(name, options, budget); SCOPES.get().addLast(scope); return scope; }
    public static Optional<QueryScope> currentScope() { return Optional.ofNullable(SCOPES.get().peekLast()); }
    public static boolean isActive() { return !SCOPES.get().isEmpty(); }
    public static void record(QueryEvent event) { if (event != null) for (QueryScope scope : SCOPES.get()) scope.record(event); }
    public static QueryGuardContextSnapshot captureContext() { return new QueryGuardContextSnapshot(new ArrayList<>(SCOPES.get())); }
    static void closeScope(QueryScope scope) { Deque<QueryScope> deque = SCOPES.get(); if (deque.isEmpty()) return; if (deque.peekLast() == scope) deque.removeLast(); else deque.remove(scope); if (deque.isEmpty()) SCOPES.remove(); }
    static Deque<QueryScope> installContext(List<QueryScope> scopes) { Deque<QueryScope> previous = SCOPES.get(); SCOPES.set(new ArrayDeque<>(scopes)); return previous; }
    static void restoreContext(Deque<QueryScope> previous) { if (previous == null || previous.isEmpty()) SCOPES.remove(); else SCOPES.set(previous); }
}
