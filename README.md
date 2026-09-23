# QueryGuard

**Stop N+1 before production.**

QueryGuard is a lightweight Java/Spring Boot library that counts real JDBC executions inside a request, method, test, or explicit scope and turns hidden query regressions into diagnostics or failures.

```text
GET /api/users

⚠ Possible N+1 detected

201 SQL queries
Unique query patterns: 4

100x select role.* from role where user_id = ?
```

No agent. No collector. No Grafana/Jaeger setup. No database-specific extension.

## What it catches

- N+1 query patterns.
- Excessive total query counts.
- Duplicate query executions.
- Total database-time budgets.
- Slow single-query budgets.
- Query regressions in JUnit 5 integration tests.

QueryGuard instruments JDBC, so the same mechanism observes Hibernate/JPA, Spring `JdbcTemplate`, jOOQ and direct JDBC calls that use the instrumented `DataSource`.

## Modules

| Module | Purpose |
| --- | --- |
| `queryguard-core` | Framework-free scopes, reports, SQL normalization, N+1 analysis and budgets. |
| `queryguard-jdbc` | JDBC `DataSource` / `Connection` / `Statement` instrumentation. |
| `queryguard-spring-boot-starter` | Auto-instrumentation, request scopes, `@QueryBudget`, logging and headers. |
| `queryguard-test` | JUnit 5 extension and report assertions. |

Java 17+ is the baseline. The starter is built against Spring Boot 3.5.x.

## Install locally

Until a public Maven repository release exists:

```bash
./mvnw clean install
```

Then add the starter:

```xml
<dependency>
    <groupId>uz.mirix</groupId>
    <artifactId>queryguard-spring-boot-starter</artifactId>
    <version>0.1.0-SNAPSHOT</version>
</dependency>
```

The default setup is zero-config: when a Spring `DataSource` exists, QueryGuard instruments connections and creates a query scope per servlet request.

## Query budgets

```java
@QueryBudget(max = 10)
@GetMapping("/users")
public List<UserDto> users() {
    return userService.findUsers();
}
```

If the method performs 42 SQL executions:

```text
QueryBudgetExceededException
Query budget exceeded for 'UserController.users()': query count 42 exceeds max 10
```

More constraints:

```java
@QueryBudget(
    max = 12,
    maxDuplicates = 4,
    maxTotalTimeMs = 80,
    maxQueryTimeMs = 30,
    failOnNPlusOne = true
)
public Invoice loadInvoice(long id) {
    ...
}
```

Use `action = BudgetAction.LOG` for diagnostics without failing execution.

## JUnit 5 regression tests

```xml
<dependency>
    <groupId>uz.mirix</groupId>
    <artifactId>queryguard-test</artifactId>
    <version>0.1.0-SNAPSHOT</version>
    <scope>test</scope>
</dependency>
```

```java
@QueryGuardTest
@QueryBudget(max = 5, failOnNPlusOne = true)
@Test
void loadsOrderDetailsWithinBudget() {
    orderService.loadDetails(42L);
}
```

In a Spring Boot integration test, the starter instruments the `DataSource` while `queryguard-test` owns the test scope. A budget regression fails the test.

## Programmatic API

QueryGuard does not require Spring:

```java
DataSource guarded = new QueryGuardDataSource(realDataSource, "billing-db");

try (QueryScope scope = QueryGuard.openScope("invoice-generation")) {
    generateInvoice();
    QueryReport report = scope.report();
    System.out.println(QueryReportFormatter.format(report));
}
```

## Async context propagation

Query scopes are thread-local by default. Propagate explicitly when moving work to another executor:

```java
QueryGuardContextSnapshot context = QueryGuard.captureContext();
executor.submit(context.wrap(() -> repository.loadSomething()));
```

## Spring Boot configuration

```yaml
queryguard:
  enabled: true
  data-source:
    enabled: true
  request:
    enabled: true
    excluded-path-prefixes:
      - /actuator
      - /favicon.ico
  detection:
    n-plus-one: true
    n-plus-one-threshold: 5
    min-distinct-executions: 2
    include-failed-queries: false
  headers:
    enabled: false
    server-timing: true
    query-count: true
  logging:
    log-all-requests: false
    warn-on-n-plus-one: true
    max-sql-length: 500
```

When headers are enabled:

```text
X-QueryGuard-Queries: 7
Server-Timing: db;dur=18.42;desc="7 SQL queries"
```

Header emission is disabled by default so production APIs do not expose internal diagnostics unless explicitly enabled.

## N+1 heuristic

A query is considered a possible N+1 when:

1. it is a `SELECT`;
2. the normalized SQL shape repeats at least `n-plus-one-threshold` times;
3. those executions contain at least `min-distinct-executions` different opaque execution fingerprints.

Five identical `select 1` calls are duplicates, but are not automatically labeled N+1. A repeated `select role ... where user_id = ?` with many different bound IDs is much more suspicious.

## Privacy / security

QueryGuard does **not retain JDBC bind values**. Bindings are reduced to opaque fingerprints only for distinguishing repeated executions. Diagnostic SQL is normalized and literal values are replaced before reporting.

## Known boundaries

- JDBC is supported; R2DBC/reactive database drivers are not instrumented in `0.1.x`.
- Request scopes cover servlet applications. Non-web applications can use explicit scopes or method budgets.
- Final/custom `DataSource` implementations that cannot be class-proxied may need explicit `new QueryGuardDataSource(delegate)` wrapping.
- N+1 detection is heuristic. Use `@QueryBudget` when the allowed query count is known and must be enforced deterministically.

## Build

```bash
./mvnw verify
```

CI verifies Java 17 and Java 21 and does not publish build artifacts.

See [`docs/architecture.md`](docs/architecture.md) for internals.

## License

Apache-2.0.
