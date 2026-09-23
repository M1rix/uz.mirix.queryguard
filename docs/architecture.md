# Architecture

QueryGuard deliberately separates query observation from framework integration.

## Modules

- `queryguard-core` — scopes, events, SQL normalization, N+1 analysis, budgets and reports. No Spring/JDBC dependency.
- `queryguard-jdbc` — JDBC proxies that measure real database round trips and never retain bind values.
- `queryguard-spring-boot-starter` — zero-config DataSource instrumentation, HTTP request scopes, method budgets and diagnostics.
- `queryguard-test` — JUnit 5 extension and dependency-free report assertions.

## Data flow

```text
HTTP request / test / explicit scope
            |
            v
       QueryGuard scope
            |
            v
Spring DataSource proxy -> JDBC Connection/Statement proxy -> database
            |                         |
            |                         +-> duration / SQL fingerprint
            v
       QueryEvent(s)
            |
            v
       QueryAnalyzer
       /     |      \
   patterns N+1   budget
            |
            v
       QueryReport
```

## Safety principles

QueryGuard does **not** retain JDBC bind values. Parameter sets are reduced to opaque fingerprints used only to distinguish repeated executions. SQL literals are normalized before they enter reports, so diagnostics do not intentionally echo user data.

## Scope model

Scopes are stackable. An HTTP request can own an outer scope while an `@QueryBudget` method creates a nested scope. Every observed query is recorded into all currently active scopes, so the request report remains complete while the method budget sees only its own work.

The default context is thread-local. `QueryGuard.captureContext()` can explicitly propagate active scopes into executor work.
