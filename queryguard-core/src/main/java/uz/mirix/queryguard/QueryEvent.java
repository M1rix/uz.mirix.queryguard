package uz.mirix.queryguard;

import java.time.Instant;
import java.util.Objects;

public record QueryEvent(String normalizedSql, SqlOperation operation, long durationNanos, Instant startedAt, String dataSource, boolean successful, String errorType, int batchSize, String instanceFingerprint) {
    public QueryEvent {
        normalizedSql = Objects.requireNonNullElse(normalizedSql, "");
        operation = Objects.requireNonNullElse(operation, SqlOperation.OTHER);
        startedAt = Objects.requireNonNullElseGet(startedAt, Instant::now);
        dataSource = Objects.requireNonNullElse(dataSource, "dataSource");
        errorType = errorType == null ? "" : errorType;
        batchSize = Math.max(1, batchSize);
        instanceFingerprint = Objects.requireNonNullElse(instanceFingerprint, "");
    }
    public static QueryEvent of(String rawSql, String bindingFingerprint, long durationNanos, Instant startedAt, String dataSource, boolean successful, Throwable error, int batchSize) {
        String normalized = SqlNormalizer.normalize(rawSql);
        String instance = Fingerprints.sha256(Objects.requireNonNullElse(rawSql, "") + "|" + Objects.requireNonNullElse(bindingFingerprint, ""));
        return new QueryEvent(normalized, SqlOperation.detect(normalized), Math.max(0, durationNanos), startedAt, dataSource, successful, error == null ? "" : error.getClass().getName(), batchSize, instance);
    }
    public double durationMillis() { return durationNanos / 1_000_000.0d; }
}
