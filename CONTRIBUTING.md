# Contributing

1. Use Java 17+ and Maven 3.9+.
2. Run `./mvnw verify` before opening a pull request.
3. Keep `queryguard-core` free of Spring, Hibernate and JDBC dependencies.
4. Never log or persist raw JDBC bind values.
5. New detection heuristics must include tests for false-positive boundaries.
