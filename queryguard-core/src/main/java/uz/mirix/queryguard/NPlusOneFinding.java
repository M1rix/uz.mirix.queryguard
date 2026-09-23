package uz.mirix.queryguard;
public record NPlusOneFinding(String normalizedSql, int executions, int distinctExecutions, double totalDurationMillis) {}
