# Releasing

The project is prepared as a Maven multi-module library but `0.1.0-SNAPSHOT` is intentionally not configured to publish with repository credentials.

Before the first public release:

1. choose Maven Central / another repository and configure namespace ownership;
2. add signing and deployment credentials as repository secrets;
3. add a release-only workflow that builds sources/javadocs and deploys on a signed tag;
4. replace snapshot examples in the README with the released version;
5. verify consumer smoke tests against supported Spring Boot lines.

Do not put signing keys or repository tokens in the repository.
